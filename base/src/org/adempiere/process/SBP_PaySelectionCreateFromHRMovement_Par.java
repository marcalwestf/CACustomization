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

import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionLine;
import org.compiere.util.Env;

/** Generated Process for (PayselectionFromHRMovement_Parcial)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.1
 */
public class SBP_PaySelectionCreateFromHRMovement_Par extends SBP_PaySelectionCreateFromHRMovement_ParAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	private int				m_SeqNo = 10;


	@Override
	protected String doIt() throws Exception
	{
		//	Instance current Payment Selection
		MPaySelection paySelection = new MPaySelection(getCtx(), getRecord_ID(), get_TrxName());
		m_SeqNo = paySelection.getLastLineNo();
		//	Loop for keys
		for(Integer key : getSelectionKeys()) {
			//	get values from result set
			int HR_Movement_ID = key;
			String PaymentRule = getSelectionAsString(key, "HRM_PaymentRule");
			BigDecimal Amount = getSelectionAsBigDecimal(key, "HRM_OpenAmt");
			if (getPercent().compareTo(Env.ONEHUNDRED) ==-1) {
				BigDecimal percent  = getPercent().divide(Env.ONEHUNDRED);
				Amount = Amount.multiply(percent);
			}
			m_SeqNo += 10;
			MPaySelectionLine line = new MPaySelectionLine(paySelection, m_SeqNo, PaymentRule);
			//	Add Order
			line.setHRMovement(HR_Movement_ID, Amount);
			//	Save
			line.saveEx();
		}
		return "";
	}
}