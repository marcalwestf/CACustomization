/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.compiere.model.I_C_Period;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceTax;
import org.compiere.model.MPeriod;
import org.compiere.model.MTaxDeclaration;
import org.compiere.model.MTaxDeclarationAcct;
import org.compiere.model.MTaxDeclarationLine;
import org.compiere.model.Query;
import org.compiere.model.X_C_DocType;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.DB;

/**
 * 	Create Tax Declaration
 *	
 *  @author Jorg Janke
 *  @version $Id: TaxDeclarationCreate.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class TaxDeclarationCreate extends TaxDeclarationCreateAbstract
{
	/**	Tax Declaration			*/
	private MTaxDeclaration 	taxDeclaration = null;
	/** TDLines					*/
	private int					m_noLines = 0;
	/** TDAccts					*/
	private int					m_noAccts = 0;
	private int					noInvoices = 0;
	

	
	/**
	 * 	Process
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{

		if (getRecord_ID() == 0)
		throw new AdempiereSystemError("@NotFound@ @C_TaxDeclaration_ID@ = " + getRecord_ID());
		log.info("C_TaxDeclaration_ID=" + getRecord_ID());

		taxDeclaration = new MTaxDeclaration(getCtx(), getRecord_ID(), get_TrxName());
		
		if (isDeleteOld())
		{
			//	Delete old
			String sql = "DELETE C_TaxDeclarationLine WHERE C_TaxDeclaration_ID=?";
			int no = DB.executeUpdate(sql, taxDeclaration.getC_TaxDeclaration_ID(), false, get_TrxName());
			if (no != 0)
				log.config("Delete Line #" + no);
			sql = "DELETE C_TaxDeclarationAcct WHERE C_TaxDeclaration_ID=?";
			no = DB.executeUpdate(sql, taxDeclaration.getC_TaxDeclaration_ID(), false, get_TrxName());
			if (no != 0)
				log.config("Delete Acct #" + no);
		}

		//	Get Invoices
		StringBuffer  whereClause = new StringBuffer();
		I_C_Period period = taxDeclaration.getC_Period();
		ArrayList<Object> params = new ArrayList<>();
		whereClause.append(" dateacct between ? and ? and processed = 'Y' ");
		whereClause.append(" and c_Doctype_ID in (select c_Doctype_ID from c_Doctype dt INNER JOIN C_TaxdeclType_Invoicetype dti on dt.c_InvoiceType_ID=dti.c_Invoicetype_ID where c_TaxDeclarationtype_ID=?)");

		params.add(period.getStartDate());
		params.add(period.getEndDate());
		params.add(taxDeclaration.get_ValueAsInt("C_TaxDeclarationType_ID"));
		List<MInvoice> invoices = new Query(getCtx(), MInvoice.Table_Name, whereClause.toString(), get_TrxName())
				.setParameters(params)
				.setClient_ID()
				.setOrderBy("DateAcct, c_Bpartner_ID")
				.list();		
		invoices.stream().forEach(invoice ->{
			create(invoice);
		});
		
		
		return "@C_Invoice_ID@ #" + noInvoices 
			+ " (" + m_noLines + ", " + m_noAccts + ")";
	}	//	doIt
	
	/**
	 * 	Create Data
	 *	@param invoice invoice
	 */
	private void create (MInvoice invoice)
	{
		MInvoiceTax[] taxes = invoice.getTaxes(false);
		for (MInvoiceTax invoiceTax:taxes)
		{
			if (invoiceTax.getC_Tax().getC_TaxCategory_ID() != taxDeclaration.getC_TaxCategory_ID())
				continue;
			if (TaxDeclarationLineExists(invoiceTax.getC_Invoice_ID()))
				continue;
			MTaxDeclarationLine taxDeclarationLine = new MTaxDeclarationLine (taxDeclaration, invoice, invoiceTax);
			Boolean isCreditMemo = invoice.getC_DocType().getDocBaseType().equals(X_C_DocType.DOCBASETYPE_ARCreditMemo)
					||invoice.getC_DocType().getDocBaseType().equals(X_C_DocType.DOCBASETYPE_APCreditMemo)?true:false; 
			if (isCreditMemo) {
				taxDeclarationLine.setTaxAmt(taxDeclarationLine.getTaxAmt().negate());
				taxDeclarationLine.setTaxBaseAmt(taxDeclarationLine.getTaxBaseAmt().negate());
			}
			taxDeclarationLine.setLine((m_noLines+1) * 10);
			taxDeclarationLine.saveEx();
			m_noLines++;
			createtdlfactAcct(invoiceTax, invoice, taxDeclaration);
			
		}
		noInvoices ++;
	}	//	invoice
	
	private void createtdlfactAcct(MInvoiceTax invoiceTax, MInvoice invoice, MTaxDeclaration taxDeclaration) {
		String whereClause  = " record_ID=? and ad_table_ID=? and c_Tax_ID=? and line_ID is null";
		ArrayList<Object> params = new ArrayList<>();
		params.add(invoice.getC_Invoice_ID());
		params.add(MInvoice.Table_ID);
		params.add(invoiceTax.getC_Tax_ID());
		List<MFactAcct> factAccts = new Query(getCtx(), MFactAcct.Table_Name, whereClause, get_TrxName())
				.setParameters(params)
				.setClient_ID()
				.list();
		factAccts.stream().forEach(factAcct ->{
			MTaxDeclarationAcct taxDeclarationAcct = new MTaxDeclarationAcct (taxDeclaration, factAcct);
			taxDeclarationAcct.setLine((m_noAccts+1) * 10);
			if (taxDeclarationAcct.save())
				m_noAccts++;
		});
	}
	
	private Boolean TaxDeclarationLineExists(int invoiceID) {
		String whereClause = "C_Invoice_ID=? and C_Taxdeclaration_ID=?";
		int no = new Query(getCtx(), MTaxDeclarationLine.Table_Name, whereClause, get_TrxName())
				.setParameters(invoiceID, getRecord_ID())
				.count();
		return  no>0?true:false;
		
	}
	
}	//	TaxDeclarationCreate
