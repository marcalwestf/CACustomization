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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.compiere.model.MPaySelectionCheck;
import org.compiere.model.MPaymentBatch;
import org.compiere.print.ServerReportCtl;
import org.compiere.print.ReportEngine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Ini;

/** Generated Process for (Print_Payselection)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public class Print_Payselection extends Print_PayselectionAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		printChecks();
		return "";
	}
	
	private void printChecks(){
		List<MPaySelectionCheck> paySelectionChecks = new ArrayList<MPaySelectionCheck>();
		int C_BankAccount_ID=0;
		MPaymentBatch paymentBatch = null;
		String paymentRule = getPaymentRule();
		log.info(paymentRule);

		AtomicBoolean somethingPrinted = new AtomicBoolean(false);
		boolean directPrint = !Ini.isPropertyBool(Ini.P_PRINTPREVIEW);
		//	for all checks//	Loop for keys
		for(Integer key : getSelectionKeys()) {
			boolean ok = ServerReportCtl.startDocumentPrint(ReportEngine.CHECK, null,
					key, null, null);
			if (!somethingPrinted.get() && ok)
				somethingPrinted.set(true);
			MPaySelectionCheck paySelectionCheck = new MPaySelectionCheck(getCtx(), key, get_TrxName());
			if(paymentBatch == null)
				paymentBatch = MPaymentBatch.getForPaySelection (Env.getCtx(),paySelectionCheck.getC_PaySelection_ID() , null);
			if (C_BankAccount_ID == 0)
					C_BankAccount_ID = paySelectionCheck.getC_PaySelection().getC_BankAccount_ID();
		}

		//	Confirm Print and Update BankAccountDoc
		if (somethingPrinted.get())
		{
			int lastDocumentNo = MPaySelectionCheck.confirmPrint (paySelectionChecks, paymentBatch);
			if (lastDocumentNo != 0)
			{
				StringBuffer sb = new StringBuffer();
				sb.append("UPDATE C_BankAccountDoc SET CurrentNext=").append(++lastDocumentNo)
					.append(" WHERE C_BankAccount_ID=").append(C_BankAccount_ID)
					.append(" AND PaymentRule='").append(paymentRule).append("'");
				DB.executeUpdate(sb.toString(), null);
			}
		}	//	confirm

		if (isPrintRemittance())
		{
			paySelectionChecks.stream()
					.filter(paySelectionCheck -> paySelectionCheck != null)
					.forEach( paySelectionCheck -> {
				ServerReportCtl.startDocumentPrint(ReportEngine.REMITTANCE, 
						   null, // No custom print format
						   paySelectionCheck.getC_PaySelection_ID(),
						   null,  // No custom printer
						   null
						   );	
			});
		}	//	remittance

		
	}
}