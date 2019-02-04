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

import org.compiere.process.SvrProcess;

/** Generated Process for (Print_Payselection)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public abstract class Print_PayselectionAbstract extends SvrProcess {
	/** Process Value 	*/
	private static final String VALUE_FOR_PROCESS = "Print_Payselection";
	/** Process Name 	*/
	private static final String NAME_FOR_PROCESS = "Print_Payselection";
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
	/**	Parameter Value for Payment Rule	*/
	private String paymentRule;
	/**	Parameter Value for Bank Account Document	*/
	private int bankAccountDocId;
	/**	Parameter Value for Current Next	*/
	private int currentNext;
	/**	Parameter Value for PrintRemittance	*/
	private boolean isPrintRemittance;

	@Override
	protected void prepare() {
		paymentRule = getParameterAsString(PAYMENTRULE);
		bankAccountDocId = getParameterAsInt(C_BANKACCOUNTDOC_ID);
		currentNext = getParameterAsInt(CURRENTNEXT);
		isPrintRemittance = getParameterAsBoolean(PRINTREMITTANCE);
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