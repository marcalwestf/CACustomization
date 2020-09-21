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

package org.spin.process;

import org.compiere.process.SvrProcess;

/** Generated Process for (Print Cheques to Fiscal Printer)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.3
 */
public abstract class PaySelectionChequePrintAbstract extends SvrProcess {
	/** Process Value 	*/
	private static final String VALUE_FOR_PROCESS = "C_PaySelectionCheque_Print";
	/** Process Name 	*/
	private static final String NAME_FOR_PROCESS = "Print Cheques to Fiscal Printer";
	/** Process Id 	*/
	private static final int ID_FOR_PROCESS = 1000125;
	/**	Parameter Name for Fiscal Printer	*/
	public static final String FISCALPRINTER_ID = "FiscalPrinter_ID";
	/**	Parameter Name for Pay Selection Check	*/
	public static final String C_PAYSELECTIONCHECK_ID = "C_PaySelectionCheck_ID";
	/**	Parameter Value for Fiscal Printer	*/
	private int fiscalPrinterId;
	/**	Parameter Value for Pay Selection Check	*/
	private int paySelectionCheckId;

	@Override
	protected void prepare() {
		fiscalPrinterId = getParameterAsInt(FISCALPRINTER_ID);
		paySelectionCheckId = getParameterAsInt(C_PAYSELECTIONCHECK_ID);
	}

	/**	 Getter Parameter Value for Fiscal Printer	*/
	protected int getFiscalPrinterId() {
		return fiscalPrinterId;
	}

	/**	 Setter Parameter Value for Fiscal Printer	*/
	protected void setFiscalPrinterId(int fiscalPrinterId) {
		this.fiscalPrinterId = fiscalPrinterId;
	}

	/**	 Getter Parameter Value for Pay Selection Check	*/
	protected int getPaySelectionCheckId() {
		return paySelectionCheckId;
	}

	/**	 Setter Parameter Value for Pay Selection Check	*/
	protected void setPaySelectionCheckId(int paySelectionCheckId) {
		this.paySelectionCheckId = paySelectionCheckId;
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