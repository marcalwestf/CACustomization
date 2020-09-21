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

import org.compiere.model.MInOutLine;
import org.compiere.model.MStorage;
import org.compiere.model.MTransaction;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

/** Generated Process for (CreateMTransaction)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.3
 */
public class CreateMTransaction extends CreateMTransactionAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{		
		for(Integer key : getSelectionKeys()) {
			MInOutLine inOutLine = new MInOutLine(getCtx(), key, get_TrxName());
			String MovementType = inOutLine.getParent().getMovementType();
			BigDecimal quantity = inOutLine.getMovementQty();
			if (MovementType.charAt(1) == '-' )	//	C- Customer Shipment - V- Vendor Return
				quantity = quantity.negate();
			MTransaction materialTransaction = new MTransaction (getCtx(), inOutLine.getAD_Org_ID(),
					inOutLine.getParent().getMovementType(), inOutLine.getM_Locator_ID(),
					inOutLine.getM_Product_ID(), inOutLine.getM_AttributeSetInstance_ID(),
					quantity, inOutLine.getParent().getMovementDate(), get_TrxName());
				materialTransaction.setM_InOutLine_ID(inOutLine.getM_InOutLine_ID());
				materialTransaction.saveEx();		
				MStorage.add(getCtx(), inOutLine.getParent().getM_Warehouse_ID(),
						inOutLine.getM_Locator_ID(),
						inOutLine.getM_Product_ID(),
						inOutLine.getM_AttributeSetInstance_ID(), 0,
						quantity,
						Env.ZERO,
						Env.ZERO,
						get_TrxName());
		}

		
		return "";
	}
}