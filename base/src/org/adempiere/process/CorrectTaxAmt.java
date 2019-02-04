/******************************************************************************
 * Product: ADempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2006-2017 ADempiere Foundation, All Rights Reserved.         *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * or (at your option) any later version.										*
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * or via info@adempiere.net or http://www.adempiere.net/license.html         *
 *****************************************************************************/

package org.adempiere.process;

import java.math.BigDecimal;

import org.compiere.model.I_C_Invoice;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceTax;
import org.compiere.model.Query;
import org.compiere.util.Env;

/** Generated Process for (CorrectTaxAmt)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.1
 */
public class CorrectTaxAmt extends CorrectTaxAmtAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		int C_Invoice_ID= getInvoiceId();
		int C_Tax_ID =  Env.getContextAsInt(getCtx(), "C_Tax_ID");
		BigDecimal newTaxAmt = getTaxAmt();
		MInvoiceTax invoiceTax = new Query(getCtx(), MInvoiceTax.Table_Name, "C_Tax_ID=? and c_Invoice_ID=?", get_TrxName())
				.setParameters(C_Tax_ID, C_Invoice_ID)
				.first();
		BigDecimal oldTaxAmt = invoiceTax.getTaxAmt();
		BigDecimal difference = getTaxAmt().subtract(oldTaxAmt);
		invoiceTax.setTaxAmt(getTaxAmt());
		invoiceTax.saveEx();
		MInvoice invoice = (MInvoice)invoiceTax.getC_Invoice();
		invoice.setGrandTotal(invoice.getGrandTotal().add(difference));
		invoice.saveEx();
		return "";
	}
}