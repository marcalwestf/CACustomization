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

import java.sql.Timestamp;
import org.compiere.process.SvrProcess;

/** Generated Process for (SBP_Print_Payselection)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.3
 */
public abstract class SBP_Print_PayselectionAbstract extends SvrProcess {
	/** Process Value 	*/
	private static final String VALUE_FOR_PROCESS = "SBP_Print_Payselection";
	/** Process Name 	*/
	private static final String NAME_FOR_PROCESS = "SBP_Print_Payselection";
	/** Process Id 	*/
	private static final int ID_FOR_PROCESS = 1000002;
	/**	Parameter Name for Payment Rule	*/
	public static final String PAYMENTRULE = "PaymentRule";
	/**	Parameter Name for Bank Account Document	*/
	public static final String C_BANKACCOUNTDOC_ID = "C_BankAccountDoc_ID";
	/**	Parameter Name for Current Next	*/
	public static final String CURRENTNEXT = "CurrentNext";
	/**	Parameter Name for PrintRemittance	*/
	public static final String PRINTREMITTANCE = "PrintRemittance";
	/**	Parameter Name for cmd_PayPrint	*/
	public static final String CMD_PAYPRINT = "cmd_PayPrint";
	/**	Parameter Name for Bank Account	*/
	public static final String C_BANKACCOUNT_ID = "C_BankAccount_ID";
	/**	Parameter Name for isCreateDeposit	*/
	public static final String ISCREATEDEPOSIT = "isCreateDeposit";
	/**	Parameter Name for Business Partner 	*/
	public static final String C_BPARTNER_ID = "C_BPartner_ID";
	/**	Parameter Name for Charge	*/
	public static final String C_CHARGE_ID = "C_Charge_ID";
	/**	Parameter Name for Transaction Date	*/
	public static final String DATETRX = "DateTrx";
	/**	Parameter Name for Direct print	*/
	public static final String ISDIRECTPRINT = "IsDirectPrint";
	/**	Parameter Value for Payment Rule	*/
	private String paymentRule;
	/**	Parameter Value for Bank Account Document	*/
	private int bankAccountDocId;
	/**	Parameter Value for Current Next	*/
	private int currentNext;
	/**	Parameter Value for PrintRemittance	*/
	private boolean isPrintRemittance;
	/**	Parameter Value for cmd_PayPrint	*/
	private String payPrint;
	/**	Parameter Value for Bank Account	*/
	private int bankAccountId;
	/**	Parameter Value for isCreateDeposit	*/
	private boolean isisCreateDeposit;
	/**	Parameter Value for Business Partner 	*/
	private int bPartnerId;
	/**	Parameter Value for Charge	*/
	private int chargeId;
	/**	Parameter Value for Transaction Date	*/
	private Timestamp dateTrx;
	/**	Parameter Value for Direct print	*/
	private boolean isDirectPrint;

	@Override
	protected void prepare() {
		paymentRule = getParameterAsString(PAYMENTRULE);
		bankAccountDocId = getParameterAsInt(C_BANKACCOUNTDOC_ID);
		currentNext = getParameterAsInt(CURRENTNEXT);
		isPrintRemittance = getParameterAsBoolean(PRINTREMITTANCE);
		payPrint = getParameterAsString(CMD_PAYPRINT);
		bankAccountId = getParameterAsInt(C_BANKACCOUNT_ID);
		isisCreateDeposit = getParameterAsBoolean(ISCREATEDEPOSIT);
		bPartnerId = getParameterAsInt(C_BPARTNER_ID);
		chargeId = getParameterAsInt(C_CHARGE_ID);
		dateTrx = getParameterAsTimestamp(DATETRX);
		isDirectPrint = getParameterAsBoolean(ISDIRECTPRINT);
	}

	/**	 Getter Parameter Value for Payment Rule	*/
	protected String getPaymentRule() {
		return paymentRule;
	}

	/**	 Setter Parameter Value for Payment Rule	*/
	protected void setPaymentRule(String paymentRule) {
		this.paymentRule = paymentRule;
	}

	/**	 Getter Parameter Value for Bank Account Document	*/
	protected int getBankAccountDocId() {
		return bankAccountDocId;
	}

	/**	 Setter Parameter Value for Bank Account Document	*/
	protected void setBankAccountDocId(int bankAccountDocId) {
		this.bankAccountDocId = bankAccountDocId;
	}

	/**	 Getter Parameter Value for Current Next	*/
	protected int getCurrentNext() {
		return currentNext;
	}

	/**	 Setter Parameter Value for Current Next	*/
	protected void setCurrentNext(int currentNext) {
		this.currentNext = currentNext;
	}

	/**	 Getter Parameter Value for PrintRemittance	*/
	protected boolean isPrintRemittance() {
		return isPrintRemittance;
	}

	/**	 Setter Parameter Value for PrintRemittance	*/
	protected void setPrintRemittance(boolean isPrintRemittance) {
		this.isPrintRemittance = isPrintRemittance;
	}

	/**	 Getter Parameter Value for cmd_PayPrint	*/
	protected String getPayPrint() {
		return payPrint;
	}

	/**	 Setter Parameter Value for cmd_PayPrint	*/
	protected void setPayPrint(String payPrint) {
		this.payPrint = payPrint;
	}

	/**	 Getter Parameter Value for Bank Account	*/
	protected int getBankAccountId() {
		return bankAccountId;
	}

	/**	 Setter Parameter Value for Bank Account	*/
	protected void setBankAccountId(int bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	/**	 Getter Parameter Value for isCreateDeposit	*/
	protected boolean isisCreateDeposit() {
		return isisCreateDeposit;
	}

	/**	 Setter Parameter Value for isCreateDeposit	*/
	protected void setisCreateDeposit(boolean isisCreateDeposit) {
		this.isisCreateDeposit = isisCreateDeposit;
	}

	/**	 Getter Parameter Value for Business Partner 	*/
	protected int getBPartnerId() {
		return bPartnerId;
	}

	/**	 Setter Parameter Value for Business Partner 	*/
	protected void setBPartnerId(int bPartnerId) {
		this.bPartnerId = bPartnerId;
	}

	/**	 Getter Parameter Value for Charge	*/
	protected int getChargeId() {
		return chargeId;
	}

	/**	 Setter Parameter Value for Charge	*/
	protected void setChargeId(int chargeId) {
		this.chargeId = chargeId;
	}

	/**	 Getter Parameter Value for Transaction Date	*/
	protected Timestamp getDateTrx() {
		return dateTrx;
	}

	/**	 Setter Parameter Value for Transaction Date	*/
	protected void setDateTrx(Timestamp dateTrx) {
		this.dateTrx = dateTrx;
	}

	/**	 Getter Parameter Value for Direct print	*/
	protected boolean isDirectPrint() {
		return isDirectPrint;
	}

	/**	 Setter Parameter Value for Direct print	*/
	protected void setIsDirectPrint(boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

	/**	 Getter Parameter Value for Process ID	*/
	public static final int getProcessId() {
		return ID_FOR_PROCESS;
	}

	/**	 Getter Parameter Value for Process Value	*/
	public static final String getProcessValue() {
		return VALUE_FOR_PROCESS;
	}

	/**	 Getter Parameter Value for Process Name	*/
	public static final String getProcessName() {
		return NAME_FOR_PROCESS;
	}
}