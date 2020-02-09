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

import org.compiere.model.MInOut;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MLandedCost;
import org.compiere.model.MLandedCostAllocation;

/** Generated Process for (SBP_GenerateLandedCost_InvoiceLine)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.3
 */
public class SBP_GenerateLandedCost_InvoiceLine extends SBP_GenerateLandedCost_InvoiceLineAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{ getSelectionKeys().stream().forEach(invoiceLineId -> {
        MInvoiceLine invoiceLine = new MInvoiceLine(getCtx(), invoiceLineId, get_TrxName());
        MLandedCost landedCost = createLandedCost(invoiceLine);
        landedCost.allocateCosts();        
    });

		return "";
	}
	
	public MLandedCost createLandedCost(MInvoiceLine invoiceLine) {
        MInOut document = new MInOut(getCtx(), getInOutId(), get_TrxName());
        for (MLandedCostAllocation landedCostAllocation:MLandedCostAllocation.getOfInvoiceLine(getCtx(), 
        		invoiceLine.get_ID(), get_TrxName())) {
        	landedCostAllocation.deleteEx(true);
        }
        for (MLandedCost landedCost : MLandedCost.getLandedCosts(invoiceLine)){
        	landedCost.deleteEx(true);
        	        }
        MLandedCost landedCost = new MLandedCost(getCtx(), 0, get_TrxName());
        landedCost.setAD_Org_ID(document.getAD_Org_ID());
        landedCost.setC_InvoiceLine_ID(invoiceLine.getC_InvoiceLine_ID());
        landedCost.setM_InOut_ID(document.getM_InOut_ID());
        landedCost.setDescription(document.getPOReference());
        landedCost.setLandedCostDistribution(getLandedCostDistribution());
        landedCost.setM_CostElement_ID(getCostElementId());
        landedCost.setC_LandedCostType_ID(getLandedCostTypeId());        

        landedCost.saveEx();
        return landedCost;
	}

}