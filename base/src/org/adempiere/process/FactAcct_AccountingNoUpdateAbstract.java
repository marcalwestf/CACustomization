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

/** Generated Process for (FactAcct_AccountingNoUpdate)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public abstract class FactAcct_AccountingNoUpdateAbstract extends SvrProcess {
	/** Process Value 	*/
	private static final String VALUE_FOR_PROCESS = "FactAcct_AccountingNoUpdate";
	/** Process Name 	*/
	private static final String NAME_FOR_PROCESS = "FactAcct_AccountingNoUpdate";
	/** Process Id 	*/
	private static final int ID_FOR_PROCESS = 54106;
	/**	Parameter Name for Account Date	*/
	public static final String DATEACCT = "DateAcct";
	/**	Parameter Name for GL Category	*/
	public static final String GL_CATEGORY_ID = "GL_Category_ID";
	/**	Parameter Name for Recalculate	*/
	public static final String ISRECALCULATE = "IsReCalculate";
	/**	Parameter Name for Date Start	*/
	public static final String DATESTART = "DateStart";
	/**	Parameter Value for Account Date	*/
	private Timestamp dateAcct;
	/**	Parameter Value for Account Date(To)	*/
	private Timestamp dateAcctTo;
	/**	Parameter Value for GL Category	*/
	private int categoryId;
	/**	Parameter Value for Recalculate	*/
	private boolean isReCalculate;
	/**	Parameter Value for Date Start	*/
	private Timestamp dateStart;

	@Override
	protected void prepare() {
		dateAcct = getParameterAsTimestamp(DATEACCT);
		dateAcctTo = getParameterToAsTimestamp(DATEACCT);
		categoryId = getParameterAsInt(GL_CATEGORY_ID);
		isReCalculate = getParameterAsBoolean(ISRECALCULATE);
		dateStart = getParameterAsTimestamp(DATESTART);
	}

	/**	 Getter Parameter Value for Account Date	*/
	protected Timestamp getDateAcct() {
		return dateAcct;
	}

	/**	 Setter Parameter Value for Account Date	*/
	protected void setDateAcct(Timestamp dateAcct) {
		this.dateAcct = dateAcct;
	}

	/**	 Getter Parameter Value for Account Date(To)	*/
	protected Timestamp getDateAcctTo() {
		return dateAcctTo;
	}

	/**	 Setter Parameter Value for Account Date(To)	*/
	protected void setDateAcctTo(Timestamp dateAcctTo) {
		this.dateAcctTo = dateAcctTo;
	}

	/**	 Getter Parameter Value for GL Category	*/
	protected int getCategoryId() {
		return categoryId;
	}

	/**	 Setter Parameter Value for GL Category	*/
	protected void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	/**	 Getter Parameter Value for Recalculate	*/
	protected boolean isReCalculate() {
		return isReCalculate;
	}

	/**	 Setter Parameter Value for Recalculate	*/
	protected void setIsReCalculate(boolean isReCalculate) {
		this.isReCalculate = isReCalculate;
	}

	/**	 Getter Parameter Value for Date Start	*/
	protected Timestamp getDateStart() {
		return dateStart;
	}

	/**	 Setter Parameter Value for Date Start	*/
	protected void setDateStart(Timestamp dateStart) {
		this.dateStart = dateStart;
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