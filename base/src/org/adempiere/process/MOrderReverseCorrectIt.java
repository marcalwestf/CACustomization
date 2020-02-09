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

import java.util.List;

import org.compiere.model.I_M_InOut;
import org.compiere.model.MInOut;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.Query;

/** Generated Process for (MOrderReverseCorrectIt)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.3
 */
public class MOrderReverseCorrectIt extends MOrderReverseCorrectItAbstract
{
	private String processMsg = "";
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		MOrder order = new MOrder(getCtx(), getRecord_ID(), get_TrxName());
		createReversals(order);
		order.closeIt();
		return "";
	}

	private Boolean createReversals(MOrder order) {
		Boolean success = createReversalShipments(order);
		success = createReversalInvoice(order);
		return true;
	}

	private Boolean createReversalShipments(MOrder order) {
		MInOut[] shipments = order.getShipments();
		for (int i = 0; i < shipments.length; i++)
		{
			MInOut ship = shipments[i];
			//	if closed - ignore
			if (MInOut.DOCSTATUS_Closed.equals(ship.getDocStatus())
					|| MInOut.DOCSTATUS_Reversed.equals(ship.getDocStatus())
					|| MInOut.DOCSTATUS_Voided.equals(ship.getDocStatus()) )
				continue;
			ship.set_TrxName(get_TrxName());

			//	If not completed - void - otherwise reverse it
			if (MInOut.DOCSTATUS_Completed.equals(ship.getDocStatus()))
			{
				ship.voidIt();
				MInOut reversal  = (MInOut)ship.getReversal();
				reversal.setMovementDate(getDateOrdered());
				reversal.saveEx();
			}

			else
			{
				processMsg = "Could not reverse Shipment " + ship;
				return false;
			}
			ship.setDocAction(MInOut.DOCACTION_None);
			ship.saveEx();
		}	//	for all shipments


		return true;
	}

	private Boolean createReversalInvoice(MOrder order) {
		MInvoice[] invoices = order.getInvoices();
		for (MInvoice invoice:invoices)
		{
			//	if closed - ignore
			if (MInvoice.DOCSTATUS_Closed.equals(invoice.getDocStatus())
					|| MInvoice.DOCSTATUS_Reversed.equals(invoice.getDocStatus())
					|| MInvoice.DOCSTATUS_Voided.equals(invoice.getDocStatus()) )
				continue;
			invoice.set_TrxName(get_TrxName());

			//	If not completed - void - otherwise reverse it
			if (MInvoice.DOCSTATUS_Completed.equals(invoice.getDocStatus()))
			{
				invoice.voidIt();
				MInvoice reversal  = (MInvoice)invoice.getReversal();
				reversal.setDateInvoiced(getDateOrdered());
				reversal.saveEx();
			}		
			else
			{
				processMsg = "Could not reverse Shipment " + invoice;
				return false;
			}
			invoice.setDocAction(MInOut.DOCACTION_None);
			invoice.saveEx();
		}
		return true;
	}
}