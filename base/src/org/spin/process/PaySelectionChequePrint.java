/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 or later of the                                  *
 * GNU General Public License as published                                    *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2016 E.R.P. Consultores y Asociados, C.A                *
 * All Rights Reserved.                                                       *
 * Contributor(s): Yamel Senih www.erpya.com                                  *
 *****************************************************************************/
package org.spin.process;

import java.sql.Timestamp;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MInvoice;
import org.compiere.model.MPaySelectionCheck;
import org.compiere.model.MPayment;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.model.MADAppRegistration;
//import org.spin.support.fp.FiscalDocument;
//import org.spin.support.fp.IFiscalPrinter;
//import org.spin.util.fp.ColumnsAdded;
import org.spin.util.support.AppSupportHandler;
import org.spin.util.support.IAppSupport;

/** Generated Process for (Print Invoices to Fiscal Printer)
 *  @author ADempiere (generated) 
 *  @version Release 3.8.0
 */
public class PaySelectionChequePrint extends PaySelectionChequePrintAbstract {
	@Override
	protected String doIt() throws Exception {
		MPaySelectionCheck paySelectionCheck = null;
		if(getRecord_ID() == 0) {
			paySelectionCheck = new MPaySelectionCheck(getCtx(), getPaySelectionCheckId(),  get_TrxName());
		}
		else {
			//MPayment payment = new MPayment(getCtx(), getRecord_ID(), get_TrxName());
			paySelectionCheck = MPaySelectionCheck.getOfPayment(Env.getCtx(), getRecord_ID(), null);
			int PaySelectionCheckId = 0;
			if (paySelectionCheck != null)
				PaySelectionCheckId = paySelectionCheck.getC_PaySelectionCheck_ID();
			else
			{
				paySelectionCheck = MPaySelectionCheck.createForPayment(Env.getCtx(), getRecord_ID(), null);
				if (paySelectionCheck != null)
					PaySelectionCheckId = paySelectionCheck.getC_PaySelectionCheck_ID();
			}
		}
		//	
		//	Validate Printing
		//if(paySelectionCheck.get_ValueAsBoolean(ColumnsAdded.COLUMNNAME_PrintFiscalDocument)) {
		//	return "@C_Invoice_ID@ " + paySelectionCheck.getC_Payment().getDocumentNo() + " @Printed@";
		//}
		//	Validate only completed
		if(paySelectionCheck.getC_Payment().getDocStatus().equals(MPayment.STATUS_Reversed)
				|| paySelectionCheck.getC_Payment().getDocStatus().equals(MInvoice.STATUS_Voided)) {
			return "@C_Invoice_ID@ " + paySelectionCheck.getC_Payment().getDocumentNo() + " @Voided@";
		}
		//	Get Fiscal Printer
		//if(getFiscalPrinterId() == 0) {
		//	setFiscalPrinterId(paySelectionCheck.getC_Payment().get_ValueAsInt(ColumnsAdded.COLUMNNAME_FiscalPrinter_ID));
		//}
		//	Validate
		//if(getFiscalPrinterId() == 0)
		//	throw new AdempiereException("@" + ColumnsAdded.COLUMNNAME_FiscalPrinter_ID + "@ @NotFound@");
		//	
		MADAppRegistration registeredApplication = MADAppRegistration.getById(Env.getCtx(), getFiscalPrinterId(), null);
		IAppSupport supportedApplication = AppSupportHandler.getInstance().getAppSupport(registeredApplication);
		//	Exists a Application available for it?
		//if(supportedApplication != null
		//		&& IFiscalPrinter.class.isAssignableFrom(supportedApplication.getClass())) {
			//	Instance of fiscal printer
		//	IFiscalPrinter fiscalPrinter = (IFiscalPrinter) supportedApplication;
		//	FiscalDocument fiscalDocument = new FiscalDocument(invoice);
			//	Send document
		//	String lastDocumentNo = fiscalPrinter.printFiscalDocument(fiscalDocument);
			//	Set Fiscal Printer
		//	invoice.set_ValueOfColumn(ColumnsAdded.COLUMNNAME_FiscalPrinter_ID, registeredApplication.getAD_AppRegistration_ID());
		//	invoice.set_ValueOfColumn(ColumnsAdded.COLUMNNAME_FiscalDocumentNo, lastDocumentNo);
		//	invoice.set_ValueOfColumn(ColumnsAdded.COLUMNNAME_PrintFiscalDocument, "Y");
		//	invoice.set_ValueOfColumn(ColumnsAdded.COLUMNNAME_FiscalPrintDate, new Timestamp(System.currentTimeMillis()) );
		//	invoice.setIsPrinted(true);
		//	if(!Util.isEmpty(lastDocumentNo)) {
		//		invoice.setDocumentNo(lastDocumentNo);
		//	}
		//	invoice.saveEx();
		//}
		//	Ok
		return "Ok";
	}
}