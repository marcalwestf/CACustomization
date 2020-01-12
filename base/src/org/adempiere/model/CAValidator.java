package org.adempiere.model;
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


import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.acct.Doc;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.I_C_PaySelection;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MClient;
import org.compiere.model.MCommissionAmt;
import org.compiere.model.MCommissionDetail;
import org.compiere.model.MConversionRate;
import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInventory;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MMovement;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionCheck;
import org.compiere.model.MPaySelectionLine;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentBatch;
import org.compiere.model.MProduction;
import org.compiere.model.MProject;
import org.compiere.model.MProjectIssue;
import org.compiere.model.MProjectLine;
import org.compiere.model.MRequisition;
import org.compiere.model.MTaxDeclarationLine;
import org.compiere.model.MTimeExpense;
import org.compiere.model.MTimeExpenseLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_C_Payment;
import org.compiere.process.DocAction;
import org.compiere.sqlj.Adempiere;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.TimeUtil;
import org.eevolution.model.MDDOrder;
import org.eevolution.model.MHRAttribute;
import org.eevolution.model.MHRConcept;
import org.eevolution.model.MHREmployee;
import org.eevolution.model.MPPOrder;
import org.eevolution.model.MWMInOutBoundLine;
import org.eevolution.model.X_C_TaxDefinition;


/**
 *	Validator for Customization Central America
 *	
 *  @author Susanne Calderon Systemhaus Westfalia
 */
public class CAValidator implements ModelValidator

{
	public CAValidator ()
	{
		super ();
	}
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(CAValidator.class);
	/** Client			*/
	private int		m_AD_Client_ID = -1;
	
	/**
	 *	Initialize Validation
	 *	@param engine validation engine 
	 *	@param client client
	 */
	public void initialize (ModelValidationEngine engine, MClient client)
	{
		//client = null for global validator
		if (client != null) {	
			m_AD_Client_ID = client.getAD_Client_ID();
			log.info(client.toString());
		}
		else  {
			log.info("Initializing global validator: "+this.toString());
		}

		//	Tables to be monitored
		engine.addModelChange(MOrder.Table_Name, this);
		engine.addModelChange(MOrderLine.Table_Name, this);
		engine.addModelChange(MInvoice.Table_Name, this);
		engine.addModelChange(MInvoiceLine.Table_Name, this);
		engine.addModelChange(MCommissionDetail.Table_Name, this);
		engine.addModelChange(MTimeExpenseLine.Table_Name, this);
		engine.addModelChange(MWMInOutBoundLine.Table_Name, this);
		//engine.addModelChange(MTaxDeclarationLine.Table_Name, this);
		
		engine.addDocValidate(MPaySelection.Table_Name	, this);
		engine.addDocValidate(MBankStatement.Table_Name, this);
		engine.addDocValidate(MOrder.Table_Name, this);
		engine.addDocValidate(MInvoice.Table_Name, this);
		engine.addDocValidate(MPayment.Table_Name, this);
		engine.addDocValidate(MTimeExpense.Table_Name, this);
		engine.addDocValidate(MMovement.Table_Name, this);
		engine.addDocValidate(MInventory.Table_Name, this);
		engine.addDocValidate(MProjectIssue.Table_Name, this);
		engine.addDocValidate(MProduction.Table_Name, this);
		engine.addDocValidate(MAllocationHdr.Table_Name, this);
		engine.addDocValidate(MInOut.Table_Name, this);
		
		
		//	Documents to be monitored
	//	engine.addDocValidate(MInvoice.Table_Name, this);

	}	//	initialize

    /**
     *	Model Change of a monitored Table.
     *	Called after PO.beforeSave/PO.beforeDelete
     *	when you called addModelChange for the table
     *	@param po persistent object
     *	@param type TYPE_
     *	@return error message or null
     *	@exception Exception if the recipient wishes the change to be not accept.
     */
	public String modelChange (PO po, int type) throws Exception
	{
		String error = "";
		log.info(po.get_TableName() + " Type: "+type);
		
		if (type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE ){
			if (po.get_TableName().equals(MOrder.Table_Name)) {
				error = User4Mandatory(po);
				if (po.is_ValueChanged(MOrder.COLUMNNAME_C_BPartner_ID))
					error = upDateControlCredito(po);
			}
			if(po.get_TableName().equals(MInvoice.Table_Name))
				error = InvoiceTypeTaxDeclaration(po);		
			if (po.get_TableName().equals(MTimeExpenseLine.Table_Name))
				error = calculateLabourCost(po);
			if (po.get_TableName().equals(MOrderLine.Table_Name)
					&& (po.is_ValueChanged("M_Product_ID") || po.is_ValueChanged("C_Charge_ID")))
				error = setOrderLineTax(po);
			if (po.get_TableName().equals(MOrderLine.Table_Name))
				error = updatePriceLimitControl(po);
			

			if (po.get_TableName().equals(MInvoiceLine.Table_Name)
					&& (po.is_ValueChanged("M_Product_ID") || po.is_ValueChanged("C_Charge_ID")))
				error = setInvoiceLineTax(po);
			 if (po.get_TableName().equals(MWMInOutBoundLine.Table_Name)) {
				 error = updateM_Locator_TO(po);
				 
			 }
			//if (po.get_TableName().equals(MTaxDeclarationLine.Table_Name) && type == ModelValidator.TYPE_BEFORE_NEW)
			//	error = updateTaxDeclarationLIne(po);
		}

		if (type == ModelValidator.TYPE_AFTER_CHANGE ){
			if (po.get_TableName().equals(MOrder.Table_Name))
				error = updateOrderLines(po);
			if(po.get_TableName().equals(MInvoice.Table_Name))
				error = updateInvoiceLines(po);	
			if (po.get_TableName().equals(MCommissionDetail.Table_Name)) {
				error = commissionAmtUpdate(po);
			}
			
		}
		if (type == ModelValidator.TYPE_BEFORE_NEW && po.get_TableName().equals(MOrder.Table_Name))
			error = UpdatePaymentRule(po);
		
		
		
		return error;
	}	//	modelChange

	private String User4Mandatory(PO po) {
		MOrder order = (MOrder)po;
		if(order.getC_DocTypeTarget().isHasCharges() && order.getUser4_ID() <=0)
		{
			return "EL campo retaceo es obligatorio";
		}
		return "";
	}
	

	private String updateM_Locator_TO(PO po) {
		MWMInOutBoundLine inOutBoundLine = (MWMInOutBoundLine)po;
		if(inOutBoundLine.getM_LocatorTo_ID() <=0)
			inOutBoundLine.setM_LocatorTo_ID(inOutBoundLine.getWM_InOutBound().getM_Locator_ID());
		
		return "";
	}
	
	private String UpdatePaymentRule(PO po) {
		MOrder order = (MOrder)po;
		if(order.getC_DocTypeTarget().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_POSOrder))
		{
			order.setPaymentRule(MOrder.PAYMENTRULE_Cash);
		}
		return "";
	}
	
	
	private String updateOrderLines(PO po) {   
		MOrder order = (MOrder)po;
		if(order.is_ValueChanged("User4_ID"))
		{
			for(MOrderLine orderLine:order.getLines()) 
			{
				orderLine.setUser4_ID(order.getUser4_ID());
				orderLine.saveEx();            
			}
		}

		if(order.is_ValueChanged(MOrder.COLUMNNAME_C_Project_ID))
		{
			for(MOrderLine orderLine:order.getLines()) 
			{
				orderLine.setC_Project_ID(order.getC_Project_ID());;
				orderLine.saveEx();            
			}
		}
		
		return "";
	}
	
	private String AfterPrepareIsControlLimitPrice(PO po) {   
		String result = "";
		MOrder order = (MOrder)po;
		if (!order.isSOTrx())
			return result;
		
		StringBuffer sqlRole = new StringBuffer();
		sqlRole.append("SELECT count(*) FROM AD_Role r  WHERE r.IsActive='Y' ");
		sqlRole.append("	AND EXISTS (SELECT * FROM AD_User_Roles ur");
		sqlRole.append("	WHERE r.AD_Role_ID=ur.AD_Role_ID AND ur.IsActive='Y' AND ur.AD_User_ID=?) ");
		sqlRole.append("	AND r.overwritepricelimit = 'Y' ");
		int noRole = DB.getSQLValueEx(order.get_TrxName(), sqlRole.toString(), Env.getAD_User_ID(order.getCtx()));
		if (noRole>0) {
			order.set_ValueOfColumn("isControlLimitPrice", false);
			order.set_ValueOfColumn("limitPriceApprovedBy", Env.getAD_User_ID(order.getCtx()));
			order.saveEx();
			return result;			
		}
		if (order.get_ValueAsBoolean("isControlLimitPrice"))
			result = "Excede Limite de Precio";
		return result;
	}
	
	private String updatePriceLimitControl(PO po) {	
		MOrderLine orderLine = (MOrderLine)po;
		if (!orderLine.getParent().isSOTrx())
			return "";	
		if(orderLine.is_ValueChanged(MOrderLine.COLUMNNAME_PriceEntered)) {
		Boolean controlPriceLimit =	orderLine.getPriceActual().compareTo(orderLine.getPriceLimit())< 0?true:false;			
				orderLine.set_ValueOfColumn("isControlLimitPrice", controlPriceLimit);
				orderLine.getParent().set_ValueOfColumn("isControlLimitPrice", controlPriceLimit);
				orderLine.getParent().saveEx();		
							
		}
		return "";
	}
	
	private String AfterPrepareOrderControlCreditStop(PO po) {  
		MOrder order = (MOrder)po;
		String result = "";
		if (!order.isSOTrx())
			return result;
		
		StringBuffer sqlRole = new StringBuffer();
		sqlRole.append("SELECT count(*) FROM AD_Role r  WHERE r.IsActive='Y' ");
		sqlRole.append("	AND EXISTS (SELECT * FROM AD_User_Roles ur");
		sqlRole.append("	WHERE r.AD_Role_ID=ur.AD_Role_ID AND ur.IsActive='Y' AND ur.AD_User_ID=?) ");
		sqlRole.append("	AND r.iscanapprovecreditlimit = 'Y' ");
		int noRole = DB.getSQLValueEx(order.get_TrxName(), sqlRole.toString(), Env.getAD_User_ID(order.getCtx()));
		if (noRole>0) {
			return result;	
			}
		
		if(order.get_ValueAsString("DocStatus_RejectStatus").equals("NO")) {
			result = "Aprobar Credito";
		}
		return result;
	}
	
	private String upDateControlCredito(PO po) {
		
		MOrder order = (MOrder)po;
		if (!order.isSOTrx())
			return "";
		if(order.is_ValueChanged(MOrder.COLUMNNAME_C_BPartner_ID)) {
			String sql = "select count(*) from rv_OpenItem oi " + 
					" INNER JOIN C_Paymentterm pt on oi.c_Paymentterm_ID=pt.c_Paymentterm_ID " + 
					" WHERE IsSotrx = 'Y' and C_BPartner_ID=? " + 
					" AND daysDue > pt.netdays";
			int noInvoicesDue = DB.getSQLValueEx(order.get_TrxName(), sql, order.getC_BPartner_ID());
			order.set_ValueOfColumn("ISpastDueInvoices", noInvoicesDue>0?true:false);
			if (noInvoicesDue>0){
				order.set_ValueOfColumn("DocStatus_RejectStatus", "CL");
			}
		}
		if(order.is_ValueChanged(MOrder.COLUMNNAME_DateOrdered)) {
			if ( (order.getC_DocTypeTarget().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_StandardOrder)
				|| order.getC_DocTypeTarget().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Proposal)
				|| order.getC_DocTypeTarget().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Quotation)))
				return"";
			Timestamp datePromised = order.getDatePromised();
			GregorianCalendar cal = new GregorianCalendar(Language.getLoginLanguage().getLocale());
				cal.setTimeInMillis(datePromised.getTime());
				cal.add(Calendar.DAY_OF_YEAR, +3);	//	next
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
			try {
				datePromised = Adempiere.nextBusinessDay(datePromised);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			order.setDatePromised(datePromised);
		}
		return "";
	}
	
	


	private String updateTaxDeclarationLIne(PO po) {
		MTaxDeclarationLine declarationLine = (MTaxDeclarationLine)po;
		if (declarationLine.getC_Invoice().getC_DocType_ID() != 1000455)
			return "";
		String sql = "select ivl.linenetamt from c_Invoiceline ivl inner join c_INvoice i on ivl.c_Invoice_ID=i.c_Invoice_ID "
				+ "	where docstatus in ('CO','CL') and ivl.ref_Invoice_ID =?";
		BigDecimal taxamt = DB.getSQLValueBDEx(declarationLine.get_TrxName(), sql, declarationLine.getC_Invoice_ID());
		declarationLine.setTaxAmt(taxamt);
		
		return "";
	}
	

	private String InvoiceTypeTaxDeclaration(PO po) {
		MInvoice invoice = (MInvoice)po;
		if (invoice.isSOTrx())
			return"";
		if(invoice.getC_DocTypeTarget().isHasCharges() && invoice.getUser4_ID() <=0)
		{
			return "EL campo retaceo es obligatorio";
		}
		MDocType docType = (MDocType)invoice.getC_DocTypeTarget();
		if(docType.get_ValueAsString("invoiceType").equals("ICD"))
		{
			if (invoice.get_ValueAsInt("Ref_BPartner_ID")<=0)
				return "@No Reference Vendor";
			BigDecimal amount = (BigDecimal)invoice.get_Value("ActualAmt");
			BigDecimal taxaAmt = (BigDecimal)invoice.get_Value("TaxAmt");
			if(amount.longValue()<=0 || taxaAmt.longValue()<=0)
				return "@Invoice Amount and Tax Amount are mandatory";
		}
		return "";
	}
	
	
	private String updateInvoiceLines(PO po) { 
		MInvoice invoice = (MInvoice)po;
		if(invoice.is_ValueChanged("User4_ID")) 
		{
			for(MInvoiceLine invoiceLine:invoice.getLines()) 
			{
				invoiceLine.setUser4_ID(invoice.getUser4_ID());
				invoiceLine.saveEx();            
			}

			if(invoice.is_ValueChanged(MInvoice.COLUMNNAME_C_Project_ID))
			{
				for(MInvoiceLine invoiceLine:invoice.getLines()) 
				{
					invoiceLine.setC_Project_ID(invoice.getC_Project_ID());
					invoiceLine.saveEx();            
				}
			}
		}
		return "";
	}
	

	private String calculateLabourCost(PO A_PO) {
		Boolean changed = false;
		MTimeExpenseLine expenseLine = (MTimeExpenseLine)A_PO;
		if(expenseLine.is_ValueChanged("FeeAmt") || 
				expenseLine.is_ValueChanged("TravelCost") ||
				expenseLine.is_ValueChanged("HR_Concept_ID") ||
				expenseLine.is_ValueChanged(MTimeExpenseLine.COLUMNNAME_Qty) ||
				expenseLine.is_ValueChanged(MTimeExpenseLine.COLUMNNAME_Description)
				)
			changed = true;
		if (!changed)
			return "";
		MHRConcept conceptProduct = new MHRConcept(expenseLine.getCtx(), expenseLine.get_ValueAsInt(MHRConcept.COLUMNNAME_HR_Concept_ID), expenseLine.get_TrxName());
		MHRAttribute productattribute = MHRAttribute.getByConceptAndEmployee(conceptProduct, null, 0, expenseLine.getParent().getDateReport(), expenseLine.getParent().getDateReport());
		BigDecimal factor = productattribute.getAmount();
		
		MHRConcept salaryConcept = MHRConcept.getByValue(expenseLine.getCtx(), "Salarios", expenseLine.get_TrxName());
		MHREmployee employee = MHREmployee.getActiveEmployee(A_PO.getCtx(), expenseLine.getC_BPartner_ID(), A_PO.get_TrxName());
		if (employee == null) {
			employee = new Query(expenseLine.getCtx(), MHREmployee.Table_Name, "C_BPartner_ID=?", expenseLine.get_TrxName())
					.setParameters(expenseLine.getC_BPartner_ID())
					.setOrderBy("HR_Employee_ID desc ")
					.first();
			if (employee == null)
				return expenseLine.getC_BPartner().getName() + " no esta en planilla";
			}
		if (employee.getHR_Payroll_ID() == 0)
			return expenseLine.getC_BPartner().getName() + " sin contrato";
		MHRAttribute salaryAttribute = MHRAttribute.getByConceptAndEmployee(salaryConcept, employee, employee.getHR_Payroll_ID(), 
				expenseLine.getParent().getDateReport(), expenseLine.getParent().getDateReport());
		
		if (salaryAttribute == null || salaryAttribute.getAmount().signum()==0)
			return expenseLine.getC_BPartner().getName() + " Falta definir el sueldo básico para este empleado";
		int precision = salaryConcept.getStdPrecision() > 0? salaryConcept.getStdPrecision()
				: expenseLine.getC_Currency().getStdPrecision();		
		BigDecimal salaryhour = salaryAttribute.getAmount().divide(new BigDecimal(30.00), precision, BigDecimal.ROUND_HALF_UP);
		
		salaryhour = salaryhour.divide(new BigDecimal(8.00), precision, BigDecimal.ROUND_HALF_UP);
		salaryhour = salaryhour.multiply(factor);
		BigDecimal costtotal = salaryhour.multiply(expenseLine.getQty());
		expenseLine.setExpenseAmt(salaryhour);

		if (expenseLine.getC_Currency_ID() == expenseLine.getC_Currency_Report_ID())
			expenseLine.setConvertedAmt(costtotal);
		else
		{
			expenseLine.setConvertedAmt(MConversionRate.convert (expenseLine.getCtx(),
					costtotal, expenseLine.getC_Currency_ID(), expenseLine.getC_Currency_Report_ID(), 
					expenseLine.getDateExpense(), 0, getAD_Client_ID(), expenseLine.getAD_Org_ID()) );
		}
		BigDecimal feeAmt = (BigDecimal)expenseLine.get_Value("FeeAmt");
		BigDecimal travelCost =  (BigDecimal)expenseLine.get_Value("TravelCost");
		BigDecimal prepaymentAmt =  (BigDecimal)expenseLine.get_Value("PrepaymentAmt");
		expenseLine.setQtyReimbursed(Env.ONE);
		expenseLine.setPriceReimbursed(feeAmt.add(travelCost).subtract(prepaymentAmt));
		if (!expenseLine.getParent().getDocStatus().equals("CO"))		
			return "";
		String sqlUpdate = 
				"update c_ProjectLine pl  set committedamt = (select coalesce(convertedamt,0) + coalesce(pricereimbursed,0) from s_timeexpenseline where s_Timeexpenseline_ID=pl.s_Timeexpenseline_ID)" + 
				" where pl.s_timeexpenseline_ID = " + expenseLine.getS_TimeExpenseLine_ID();
		int no = DB.executeUpdateEx(sqlUpdate,  expenseLine.get_TrxName());
		
		return "";
	}
	
	private String PayselectionGeneratePayment(PO po) { 
		
		AtomicInteger lastDocumentNo = new AtomicInteger();
		List<MPaySelectionCheck> paySelectionChecks = MPaySelectionCheck.get(po.getCtx(), po.get_ID(), po.get_TrxName());
		//paySelectionChecks.stream().filter(psc -> psc != null && 
			//	!psc.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_Check))
		//.forEach(paySelectionCheck ->
		for (MPaySelectionCheck paySelectionCheck:paySelectionChecks)
		{
			MPayment payment = new MPayment(paySelectionCheck.getCtx(), paySelectionCheck.getC_Payment_ID(), paySelectionCheck.get_TrxName());
			//	Existing Payment
			if (paySelectionCheck.getC_Payment_ID() != 0
					&& (payment.getDocStatus().equals(DocAction.STATUS_Completed)
								|| payment.getDocStatus().equals(DocAction.STATUS_Closed)))
			{
				//	Update check number
				if (paySelectionCheck.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_Check))
				{
					payment.setCheckNo(paySelectionCheck.getDocumentNo());
					payment.saveEx();
				}
			} else {	//	New Payment
				I_C_PaySelection paySelection =  paySelectionCheck.getC_PaySelection();

				Boolean payInmediate = true;
				if (paySelection.getC_DocType().isSOTrx()) {
					//String TransactionCode = paySelectionCheck.get_ValueAsString("TransactionCode");
					//if (paySelectionCheck.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_Check) && TransactionCode.length()>0)
						payInmediate = true;
				}
				else {
					if ((paySelectionCheck.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_Check) ) ||
							paySelectionCheck.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_DirectDeposit))
						payInmediate = false;
				}
				if (!payInmediate)
					continue;
				MDocType documentType = MDocType.get(paySelectionCheck.getCtx(), paySelection.getC_DocType_ID());
				int docTypeId = documentType.getC_DocTypePayment_ID();
				//	
				payment = new MPayment(paySelectionCheck.getCtx(), 0, paySelectionCheck.get_TrxName());
				payment.setAD_Org_ID(paySelectionCheck.getAD_Org_ID());
				if (paySelectionCheck.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_Check)
						|| paySelectionCheck.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_Cash)) {
					payment.setBankCheck (paySelectionCheck.getParent().getC_BankAccount_ID(), false, paySelectionCheck.getDocumentNo());
					payment.setCheckNo(paySelectionCheck.get_ValueAsString("CheckNo"));
					payment.setVoiceAuthCode(paySelectionCheck.get_ValueAsString("TransactionCode"));
					if (paySelectionCheck.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_Cash))
						payment.setTenderType(MPayment.TENDERTYPE_Cash);
				} else if (paySelectionCheck.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_CreditCard)) {
					payment.setTenderType(X_C_Payment.TENDERTYPE_CreditCard);
				} else if (paySelectionCheck.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_DirectDeposit)
					|| paySelectionCheck.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_DirectDebit)) {
					payment.setBankACH(paySelectionCheck);
				} else {
					//logger.log(Level.SEVERE, "Unsupported Payment Rule=" + paySelectionCheck.getPaymentRule());
					throw  new AdempiereException("Unsupported Payment Rule=" + paySelectionCheck.getPaymentRule());
					//continue;
				}
				payment.setTrxType(X_C_Payment.TRXTYPE_CreditPayment);
				if (docTypeId > 0) {
					payment.setC_DocType_ID(docTypeId);
				}
				payment.setAmount(paySelectionCheck.getParent().getC_Currency_ID(), paySelectionCheck.getPayAmt());
				payment.setDiscountAmt(paySelectionCheck.getDiscountAmt());
				payment.setDateTrx(paySelectionCheck.getParent().getPayDate());
				payment.setDateAcct(payment.getDateTrx()); // globalqss [ 2030685 ]
				payment.setC_BPartner_ID(paySelectionCheck.getC_BPartner_ID());
				List<MPaySelectionLine> paySelectionLines = paySelectionCheck.getPaySelectionLinesAsList(false);
				//logger.fine("confirmPrint - " + paySelectionCheck + " (#SelectionLines=" + (paySelectionLines != null? paySelectionLines.size(): 0) + ")");
				//	For bank Transfer
				if(documentType.isBankTransfer()) {
					payment.setC_Invoice_ID(-1);
					payment.setC_Order_ID(-1);
					//payment.setTenderType(MPayment.TENDERTYPE_DirectDeposit);
					payment.saveEx();
					if(paySelectionLines != null) {
						for(MPaySelectionLine line : paySelectionLines) {
							if (payment.getC_Charge_ID() ==0)
								payment.setC_Charge_ID(line.getC_Charge_ID());
							if(line.getC_BankAccountTo_ID() == 0) {
								throw new AdempiereException("@C_BankAccountTo_ID@ @NotFound@");
							}
							//	For all
							MPayment receiptAccount = new MPayment(paySelectionCheck.getCtx(), 0, paySelectionCheck.get_TrxName());
							PO.copyValues(payment, receiptAccount);
							//	Set default values
							receiptAccount.setC_BankAccount_ID(line.getC_BankAccountTo_ID());
							receiptAccount.setIsReceipt(!payment.isReceipt());
							receiptAccount.setC_DocType_ID(!payment.isReceipt());
							receiptAccount.setRelatedPayment_ID(payment.getC_Payment_ID());
							receiptAccount.setTenderType(payment.getTenderType());
							receiptAccount.setC_Charge_ID(payment.getC_Charge_ID());
							receiptAccount.saveEx();
							receiptAccount.processIt(DocAction.ACTION_Complete);
							receiptAccount.saveEx();
							payment.setRelatedPayment_ID(receiptAccount.getC_Payment_ID());
						}
					}
				} else {
					//	Link to Invoice
					if (paySelectionCheck.getQty() == 1 && paySelectionLines != null && paySelectionLines.size() == 1) {
						MPaySelectionLine paySelectionLine = paySelectionLines.get(0);
						//logger.fine("Map to Invoice " + paySelectionLine);
						//
						//	FR [ 297 ]
						//	For Order
						if(paySelectionLine.getC_Order_ID() != 0) {
							payment.setC_Order_ID (paySelectionLine.getC_Order_ID());
						}
						//	For Charge
						if (paySelectionLine.getC_Charge_ID() != 0) {
							payment.setC_Charge_ID(paySelectionLine.getC_Charge_ID());
							if (paySelectionLine.getHR_Movement_ID() > 0) {
								payment.setC_Project_ID(paySelectionLine.getHRMovement().getC_Project_ID());
							}

						}
						//	For Conversion Type
						if(paySelectionLine.getC_ConversionType_ID() != 0) {
							payment.setC_ConversionType_ID(paySelectionLine.getC_ConversionType_ID());
						}
						//	For Invoice
						if(paySelectionLine.getC_Invoice_ID() != 0) {
							payment.setC_Invoice_ID (paySelectionLine.getC_Invoice_ID());
						}
						//	For all
						payment.setIsPrepayment(paySelectionLine.isPrepayment());
						//	
						payment.setDiscountAmt (paySelectionLine.getDiscountAmt());
						payment.setWriteOffAmt(paySelectionLine.getDifferenceAmt());
						BigDecimal overUnder = paySelectionLine.getOpenAmt().subtract(paySelectionLine.getPayAmt())
							.subtract(paySelectionLine.getDiscountAmt()).subtract(paySelectionLine.getDifferenceAmt());
						payment.setOverUnderAmt(overUnder);
					} else {
						payment.setDiscountAmt(Env.ZERO);
					}
				}
				payment.setWriteOffAmt(Env.ZERO);
				payment.saveEx();
				//	
				paySelectionCheck.setC_Payment_ID (payment.getC_Payment_ID());
				paySelectionCheck.saveEx();	//	Payment process needs it
				//	Should start WF
				payment.processIt(DocAction.ACTION_Complete);
				payment.saveEx();
			}	//	new Payment

			//	Get Check Document No
			try
			{
				int no = Integer.parseInt(paySelectionCheck.getDocumentNo());
				if (lastDocumentNo.get() < no)
					lastDocumentNo.set(no);
			}
			catch (NumberFormatException ex)
			{
				//logger.log(Level.SEVERE, "DocumentNo=" + paySelectionCheck.getDocumentNo(), ex);
			}
			paySelectionCheck.setIsPrinted(true);
			paySelectionCheck.setProcessed(true);
			paySelectionCheck.saveEx();
		}
		//);	//	all checks

		//logger.fine("Last Document No = " + lastDocumentNo.get());
		return "";
	}
	

	public String toString ()
	{
		StringBuffer sb = new StringBuffer ("CAValidator");
		return sb.toString ();
	}	//	toString
	

/**
 *	User Login.
	 *	Called when preferences are set
	 *	@param AD_Org_ID org
	 *	@param AD_Role_ID role
	 *	@param AD_User_ID user
	 *	@return error message or null
	 */
	public String login (int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		log.info("AD_User_ID=" + AD_User_ID);
		return null;
	}	//	login

	
	/**
	 *	Get Client to be monitored
	 *	@return AD_Client_ID client
	 */
	public int getAD_Client_ID()
	{
		return m_AD_Client_ID;
	}	//	getAD_Client_ID
	

	public String docValidate (PO po, int timing)
	{
		String error = "";
        if (ModelValidator.TIMING_AFTER_COMPLETE == timing) {
        	if (po instanceof MPaySelection)
        		error = PayselectionGeneratePayment(po);
        	if (po instanceof MPayment)
        		error = PaymentAutoReconcile(po);
        	if (po instanceof MTimeExpense)
        		error = TimeExpenseReportCreateProjectIssue(po);
        }
        if (ModelValidator.TIMING_AFTER_PREPARE == timing) {
        	if (po instanceof MBankStatement) {
        		//error = bs_AfterPrepare(po);
        		//error = bs_AfterPrepare_CreatePayment(po);
        	}
        	if (po instanceof MOrder) {
        		error = OrderSetPrecision(po);
        		error = AfterPrepareIsControlLimitPrice(po);
        		if (error.isEmpty())
        			error = AfterPrepareOrderControlCreditStop(po);
        	}
        	if (po instanceof MInvoice) {
        		error = InvoiceSetPrecision(po);
        	}
        }
        if (ModelValidator.TIMING_BEFORE_POST == timing) {
        	if (po.get_ColumnIndex("Posted") > 0)        		
        	//error = factAcct_UpdateDocumentNO(po)
        		;
        }
        
		return error;
	}	//	docValidate
	

	private String bs_AfterPrepare(PO A_PO)
	{
		Predicate<MBankStatementLine> createPayment = (bsl)->  		
			bsl != null && bsl.get_ValueAsInt(MPayment.COLUMNNAME_C_BankAccount_ID) != 0 && bsl.getC_Payment_ID() ==0;
		
		MBankStatement bankstatement = (MBankStatement)A_PO;
		Arrays.stream(bankstatement.getLines(true))
		.filter(bsl -> createPayment.test(bsl))
		.forEach(bsl -> {
			        MPaymentBatch pBatch = new MPaymentBatch(A_PO.getCtx(), 0 ,  A_PO.get_TrxName());
			        String description = "Transferencia";
			        pBatch.setName("Transferencia");
			        pBatch.saveEx();
			        MPayment paymentBankFrom = new MPayment(A_PO.getCtx(), 0 ,  A_PO.get_TrxName());
			        paymentBankFrom.setC_BankAccount_ID(bankstatement.getC_BankAccount_ID());
			        paymentBankFrom.setC_DocType_ID(false);
			        String value = DB.getDocumentNo(paymentBankFrom.getC_DocType_ID(),A_PO.get_TrxName(), false,  paymentBankFrom);
			        paymentBankFrom.setDocumentNo(value);
			       // paymentBankFrom.setDocumentNo(P_DocumentNo);
			        
			        paymentBankFrom.setDateAcct(bankstatement.getStatementDate());
			        paymentBankFrom.setDateTrx(bankstatement.getStatementDate());
			        paymentBankFrom.setTenderType(X_C_Payment.TENDERTYPE_Account);
			        paymentBankFrom.setDescription(description);
			        paymentBankFrom.setC_BPartner_ID (1000026);
			        paymentBankFrom.setC_Currency_ID(bankstatement.getC_BankAccount().getC_Currency_ID());
			       // if (P_C_ConversionType_ID > 0)
			        paymentBankFrom.setC_ConversionType_ID(114);    
			        paymentBankFrom.setPayAmt(bsl.getTrxAmt().negate());
			        paymentBankFrom.setOverUnderAmt(Env.ZERO);
			        paymentBankFrom.setC_Charge_ID(1000456);
			        paymentBankFrom.setAD_Org_ID(bankstatement.getAD_Org_ID());
			        paymentBankFrom.setC_PaymentBatch_ID(pBatch.getC_PaymentBatch_ID());
			        paymentBankFrom.saveEx();
			        description = description + " desde" +  paymentBankFrom.getC_BankAccount().getAccountNo();
			        paymentBankFrom.processIt(MPayment.DOCACTION_Complete);
			        paymentBankFrom.saveEx();
			        
			        MPayment paymentBankTo = new MPayment(A_PO.getCtx(), 0 ,  A_PO.get_TrxName());
			        paymentBankTo.setC_BankAccount_ID(bsl.get_ValueAsInt("C_BankAccount_ID"));
			        paymentBankTo.setC_DocType_ID(true);
			        value = DB.getDocumentNo(paymentBankTo.getC_DocType_ID(),A_PO.get_TrxName(), false,  paymentBankTo);
			        paymentBankTo.setDocumentNo(value);        
			        paymentBankTo.setC_PaymentBatch_ID(pBatch.getC_PaymentBatch_ID());
			      //  paymentBankTo.setDocumentNo(P_DocumentNo);
			        paymentBankTo.setDateAcct(bankstatement.getStatementDate());
			        paymentBankTo.setDateTrx(bankstatement.getStatementDate());
			        paymentBankTo.setTenderType(X_C_Payment.TENDERTYPE_Account);
			        paymentBankTo.setDescription(description);
			        paymentBankTo.setC_BPartner_ID (1000026);
			        paymentBankTo.setC_Currency_ID(100);        
			        paymentBankFrom.setC_ConversionType_ID(114);    
			        paymentBankTo.setPayAmt(bsl.getTrxAmt().negate());
			        paymentBankTo.setOverUnderAmt(Env.ZERO);
			        paymentBankTo.setC_Charge_ID(1000456);
			        paymentBankTo.setAD_Org_ID(bankstatement.getAD_Org_ID());
			        paymentBankTo.saveEx();
			        description = description + " a " +  paymentBankTo.getC_BankAccount().getAccountNo();
			        paymentBankTo.processIt(MPayment.DOCACTION_Complete);
			        paymentBankTo.saveEx();
			        
			        pBatch.setName(description);        
			        description = description + " Monto:" +  paymentBankTo.getPayAmt();
			        pBatch.set_ValueOfColumn("Description", description);
			        pBatch.setProcessingDate(bankstatement.getStatementDate());
			        pBatch.saveEx();
			        bsl.setPayment(paymentBankFrom);
			        bsl.saveEx();					
				});    
		return "";
	}
	
	private MPayment createPayment (MBankStatementLine bankStatementLine)
		{
			//	Trx Amount = Payment overwrites Statement Amount if defined
			BigDecimal PayAmt = bankStatementLine.getTrxAmt();
			if (PayAmt == null || Env.ZERO.compareTo(PayAmt) == 0)
				PayAmt = bankStatementLine.getStmtAmt();
			if (bankStatementLine.getC_Invoice_ID() == 0
				&& (PayAmt == null || Env.ZERO.compareTo(PayAmt) == 0))
				throw new IllegalStateException ("@PayAmt@ = 0");
			if (PayAmt == null)
				PayAmt = Env.ZERO;
			//
			MPayment payment = new MPayment (bankStatementLine.getCtx(), 0, bankStatementLine.get_TrxName());
			payment.setAD_Org_ID(bankStatementLine.getAD_Org_ID());
			payment.setC_BankAccount_ID(bankStatementLine.getC_BankStatement().getC_BankAccount_ID());
			payment.setTenderType(MPayment.TENDERTYPE_Check);
			if (bankStatementLine.getStatementLineDate()!= null)
				payment.setDateTrx(bankStatementLine.getStatementLineDate());
			else if (bankStatementLine.getStatementLineDate() != null)
				payment.setDateTrx(bankStatementLine.getStatementLineDate());
			if (bankStatementLine.getDateAcct() != null)
				payment.setDateAcct(bankStatementLine.getDateAcct());
			else
				payment.setDateAcct(payment.getDateTrx());
			payment.setDescription(bankStatementLine.getDescription());
			int C_BPartner_ID=0;
			 if (bankStatementLine.getC_BPartner_ID() != 0){
				 C_BPartner_ID = bankStatementLine.getC_BPartner_ID();
			 }
			 else
				 C_BPartner_ID = bankStatementLine.getC_BankStatement().getC_BankAccount().getC_Bank().getC_BPartner_ID();
			 if (C_BPartner_ID !=0)
			{
				payment.setC_BPartner_ID(bankStatementLine.getC_BPartner_ID());
				payment.setC_Currency_ID(bankStatementLine.getC_Currency_ID());
				if (PayAmt.signum() < 0)	//	Payment
				{
					payment.setPayAmt(PayAmt.abs());
					payment.setC_DocType_ID(false);
				}
				else	//	Receipt
				{
					payment.setPayAmt(PayAmt);
					payment.setC_DocType_ID(true);
				}
			}
			else
				return null;
			payment.saveEx();
			//
			payment.processIt(MPayment.DOCACTION_Complete);
			payment.saveEx();
			return payment;		
		}	//	createPayment
	

	private String bs_AfterPrepare_CreatePayment(PO A_PO)
	{
		Predicate<MBankStatementLine> createPayment = (bsl)->  		
			bsl != null && bsl.get_ValueAsInt(MPayment.COLUMNNAME_C_BankAccount_ID) == 0 && bsl.getC_Payment_ID() ==0 && bsl.getC_BPartner_ID() != 0;
		
		MBankStatement bankstatement = (MBankStatement)A_PO;
		Arrays.stream(bankstatement.getLines(true))
		.filter(bsl -> createPayment.test(bsl))
		.forEach(bsl -> {
			MPayment payment = createPayment(bsl);

			if (payment != null) {
				bsl.setC_Payment_ID(payment.getC_Payment_ID());
				bsl.saveEx();				
			}
		});
		return "";
	}

	private String commissionAmtUpdate(PO A_PO)
	{
		MCommissionDetail commissionDetail = (MCommissionDetail)A_PO;
		MCommissionAmt commissionAmt = new MCommissionAmt(A_PO.getCtx(), commissionDetail.getC_CommissionAmt_ID(),commissionDetail.get_TrxName());
		commissionAmt.updateCommissionAmount();
		commissionAmt.saveEx();
		return "";
	}

	private String OrderSetPrecision(PO A_PO) {
		MOrder order = (MOrder)A_PO;
		if (order.getGrandTotal().scale() > order.getC_Currency().getStdPrecision())
			order.setGrandTotal(order.getGrandTotal().setScale(order.getC_Currency().getStdPrecision(), BigDecimal.ROUND_HALF_UP));
		order.saveEx();
		return "";
	}
	


	private String InvoiceSetPrecision(PO A_PO) {
		MInvoice invoice = (MInvoice)A_PO;
		if (invoice.getGrandTotal().scale() > invoice.getC_Currency().getStdPrecision())
			invoice.setGrandTotal(invoice.getGrandTotal().setScale(invoice.getC_Currency().getStdPrecision(), BigDecimal.ROUND_HALF_UP));
		invoice.saveEx();
		return "";
	}
	
	private String PaymentAutoReconcile(PO A_PO) {
		MPayment payment = (MPayment)A_PO;
		MBankAccount bankAccount = (MBankAccount)payment.getC_BankAccount();
		Boolean isAutoReconciled = bankAccount.get_ValueAsBoolean("isAutoReconciled");
		if(isAutoReconciled) {
			MBankStatementLine bsl = MBankStatement.addPayment(payment);
			}
		return "";
	}
	

	private String TimeExpenseReportCreateProjectIssue(PO A_PO) {
		MTimeExpense timeExpense = (MTimeExpense)A_PO;
		for (MTimeExpenseLine expenseLine:timeExpense.getLines()) {
			createLine(expenseLine, expenseLine.getQty());
		}
		return "";
	}
	
	private void createLine(MTimeExpenseLine timeExpenseLine, BigDecimal movementQty) {
		//	Create Issue
		if (timeExpenseLine.getC_Project_ID() == 0)
			return;
		MProject project = (MProject)timeExpenseLine.getC_Project();
		MTimeExpense timeExpense = (MTimeExpense)timeExpenseLine.getS_TimeExpense();
		MProjectIssue projectIssue = new MProjectIssue(project);
		projectIssue.setMandatory(timeExpense.getM_Locator_ID(), timeExpenseLine.getM_Product_ID(), timeExpenseLine.getQty());
		
		projectIssue.setMovementDate(timeExpenseLine.getDateExpense());
		projectIssue.setDescription(timeExpenseLine.getDescription());
		projectIssue.setS_TimeExpenseLine_ID(timeExpenseLine.getS_TimeExpenseLine_ID());
		if (timeExpenseLine.getC_ProjectPhase_ID() != 0)
			projectIssue.set_ValueOfColumn(MInOutLine.COLUMNNAME_C_ProjectPhase_ID, timeExpenseLine.getC_ProjectPhase_ID());
		if (timeExpenseLine.getC_ProjectTask_ID()!=0)
			projectIssue.set_ValueOfColumn(MInOutLine.COLUMNNAME_C_ProjectTask_ID, timeExpenseLine.getC_ProjectTask_ID());
		projectIssue.saveEx();
		projectIssue.completeIt();

		//	Find/Create Project Line
		//	Find/Create Project Line
		MProjectLine projectLine = new MProjectLine(project);
		projectLine.setMProjectIssue(projectIssue);		//	setIssueBigDecimal feeAmt = (BigDecimal)expenseLine.get_Value("FeeAmt");
		BigDecimal travelCost =  (BigDecimal)timeExpenseLine.get_Value("TravelCost");
		BigDecimal prepaymentAmt =  (BigDecimal)timeExpenseLine.get_Value("PrepaymentAmt");
		BigDecimal feeAmt = (BigDecimal)timeExpenseLine.get_Value("FeeAmt");
		//projectLine.setCommittedAmt(timeExpenseLine.getConvertedAmt().add(timeExpenseLine.getPriceReimbursed()));
		projectLine.setCommittedAmt(timeExpenseLine.getConvertedAmt().add(feeAmt).add(prepaymentAmt).add(travelCost));
		projectLine.setM_Product_ID(projectIssue.getM_Product_ID());
		projectLine.set_ValueOfColumn(MTimeExpenseLine.COLUMNNAME_S_TimeExpenseLine_ID, projectIssue.getS_TimeExpenseLine_ID());
		projectLine.saveEx();
		//return "@Created@ " + counter.get();		
	}
	
	private void updateLine(MTimeExpenseLine timeExpenseLine) {
		//	Create Issue

		if (!timeExpenseLine.getParent().getDocStatus().equals("CO"))		
			return ;
		String sqlUpdate = 
				"update c_ProjectLine pl  set committedamt = (select coalesce(convertedamt,0) + coalesce(pricereimbursed,0) from s_timeexpenseline where s_Timeexpenseline_ID=pl.s_Timeexpenseline_ID)" + 
				" where pl.s_timeexpenseline_ID = " + timeExpenseLine.getS_TimeExpenseLine_ID();
		int no = DB.executeUpdateEx(sqlUpdate,  timeExpenseLine.get_TrxName());
		
	}
	

	private String factAcct_UpdateDocumentNO(PO A_PO)

	{	
		Doc doc = A_PO.getDoc();
		String dateacct = "DateAcct";
		String Documentno = "";
		if (A_PO instanceof MInventory
				|| A_PO instanceof MMovement
				|| A_PO instanceof MProjectIssue
				|| A_PO instanceof MProduction)
			dateacct = "MovementDate";
		else if (A_PO instanceof MBankStatement)
			dateacct = "StatementDate";
		else if (A_PO instanceof MDDOrder)
			dateacct = "DateOrdered";
		else if (A_PO instanceof MRequisition)
			dateacct = "DateRequired";
		else if (A_PO instanceof MPPOrder)
			dateacct = "DateOrdered";
		Timestamp date = (Timestamp)A_PO.get_Value(dateacct);
		String whereClause = "dateacct =? and documentno is not null and documentno <> ''  and postingtype = 'A' ";
		MFactAcct factacct = new Query(A_PO.getCtx(), MFactAcct.Table_Name, whereClause, A_PO.get_TrxName())
				.setParameters(A_PO.get_Value(dateacct))
				.setClient_ID()
				.first();
		if (factacct != null)
			Documentno = factacct.get_ValueAsString("DocumentNo");
		else
		{
			Documentno = DB.getDocumentNo(A_PO.getAD_Client_ID(), MFactAcct.Table_Name, A_PO.get_TrxName());			
		}


		ArrayList<Fact> facts = doc.getFacts();
		// one fact per acctschema
		for (Fact fact:facts)
		{
			for (FactLine fLine:fact.getLines())
			{
				fLine.set_ValueOfColumn("DocumentNo", Documentno);
			}
		}		
		return "";
	}
	
	private String setOrderLineTax(PO A_PO) {

		//import org.compoere.model.MProduct;
		//import org.compoere.model.MBPartner;
		//import org.compoere.model.MCharge;
		//import org.compoere.model.X_C_TaxDefinition;
		//import org.compoere.model.Query;

		//import java.util.ArrayList;
		MOrderLine orderLine = (MOrderLine)A_PO;
		Boolean	isSOTrx = orderLine.getParent().isSOTrx();
		int shipC_BPartner = orderLine.getParent().getC_BPartner_ID();
		if (orderLine.getM_Product_ID() == 0 && orderLine.getC_Charge_ID() == 0)
			return "";		//
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(isSOTrx);
		//	Check Partner Location
		StringBuffer whereClause = new StringBuffer();
		whereClause.append("c_Tax_ID in (select c_Tax_ID from c_Tax t where case when ? = 'Y' then t.sopotype in ('B','S') else t.sopotype in('B','P') end)");
		whereClause.append(" and (c_taxgroup_ID =? or c_taxgroup_ID is null)");
		MBPartner bpartner = new MBPartner(Env.getCtx(), shipC_BPartner, null);
		params.add(bpartner.getC_TaxGroup_ID());
		if (orderLine.getC_Charge_ID() != 0)
		{
			whereClause.append(" AND (c_taxtype_ID =? or c_taxtype_ID is null)");
			params.add(orderLine.getC_Charge().getC_TaxType_ID());
		}
		else if (orderLine.getM_Product_ID() != 0)
		{
			whereClause.append(" AND (C_Taxtype_ID =? or C_TaxType_ID is null)");
			params.add(orderLine.getM_Product().getC_TaxType_ID());
		}
		X_C_TaxDefinition taxdefinition = new Query(Env.getCtx(), X_C_TaxDefinition.Table_Name, whereClause.toString(), null)
				.setClient_ID()
				.setOnlyActiveRecords(true)
				.setParameters(params)
				.setOrderBy("seqNo")
				.first();
		if (taxdefinition == null || taxdefinition.getC_Tax_ID() == 0) {
			return "";		}
		else
			orderLine.setC_Tax_ID(taxdefinition.getC_Tax_ID());
		Integer i = orderLine.get_ID();
		Object object  = (Object)i;
		//
		return "";
	}
	

	
	private String setInvoiceLineTax(PO A_PO) {

		//import org.compoere.model.MProduct;
		//import org.compoere.model.MBPartner;
		//import org.compoere.model.MCharge;
		//import org.compoere.model.X_C_TaxDefinition;
		//import org.compoere.model.Query;

		//import java.util.ArrayList;
		MInvoiceLine invoiceLine = (MInvoiceLine)A_PO;
		if (invoiceLine.getC_OrderLine_ID() >0)
			return "";
		Boolean	isSOTrx = invoiceLine.getParent().isSOTrx();
		int shipC_BPartner = invoiceLine.getParent().getC_BPartner_ID();
		if (invoiceLine.getM_Product_ID() == 0 && invoiceLine.getC_Charge_ID() == 0)
			return "";		//
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(isSOTrx);
		//	Check Partner Location
		StringBuffer whereClause = new StringBuffer();
		whereClause.append("c_Tax_ID in (select c_Tax_ID from c_Tax t where case when ? = 'Y' then t.sopotype in ('B','S') else t.sopotype in('B','P') end)");
		whereClause.append(" and (c_taxgroup_ID =? or c_taxgroup_ID is null)");
		MBPartner bpartner = new MBPartner(Env.getCtx(), shipC_BPartner, null);
		params.add(bpartner.getC_TaxGroup_ID());
		if (invoiceLine.getC_Charge_ID() != 0)
		{
			whereClause.append(" AND (c_taxtype_ID =? or c_taxtype_ID is null)");
			params.add(invoiceLine.getC_Charge().getC_TaxType_ID());
		}
		else if (invoiceLine.getM_Product_ID() != 0)
		{
			whereClause.append(" AND (C_Taxtype_ID =? or C_TaxType_ID is null)");
			params.add(invoiceLine.getM_Product().getC_TaxType_ID());
		}
		X_C_TaxDefinition taxdefinition = new Query(Env.getCtx(), X_C_TaxDefinition.Table_Name, whereClause.toString(), null)
				.setClient_ID()
				.setOnlyActiveRecords(true)
				.setParameters(params)
				.setOrderBy("seqNo")
				.first();
		if (taxdefinition == null || taxdefinition.getC_Tax_ID() == 0) {
			return "";		}
		else
			invoiceLine.setC_Tax_ID(taxdefinition.getC_Tax_ID());
		//
		return "";
	}

	
}	//	Validator