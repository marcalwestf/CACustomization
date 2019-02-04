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
import org.compiere.model.MProject;
import org.compiere.model.MProjectIssue;
import org.compiere.model.MProjectLine;
import org.compiere.model.MTimeExpense;
import org.compiere.model.MTimeExpenseLine;

/** Generated Process for (SBProcess_ProjetIssueTimeExpense)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.1
 */
public class SBProcess_ProjetIssueTimeExpense extends SBProcess_ProjetIssueTimeExpenseAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		//	Sum all
		for(int timeExpenseLineID : getSelectionKeys()) {
			BigDecimal movementQty = getSelectionAsBigDecimal(timeExpenseLineID, "PI_QtyToDeliver");
			MTimeExpenseLine timeExpenseLine = new MTimeExpenseLine(getCtx(), timeExpenseLineID, get_TrxName());
			if (timeExpenseLine.getM_Product_ID() == 0 || timeExpenseLine.getQty().signum() == 0)
				continue;
			createLine(timeExpenseLine, movementQty);
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
		if (getMovementDate() != null)        //	default today
			projectIssue.setMovementDate(getMovementDate());
		if (getDescription() != null && getDescription().length() > 0)
			projectIssue.setDescription(getDescription());
		else if (timeExpenseLine.getDescription() != null)
			projectIssue.setDescription(timeExpenseLine.getDescription());
		else if (timeExpenseLine.getS_TimeExpense().getDescription() != null)
			projectIssue.setDescription(timeExpenseLine.getS_TimeExpense().getDescription());
		projectIssue.setS_TimeExpenseLine_ID(timeExpenseLine.getS_TimeExpenseLine_ID());
		if (timeExpenseLine.getC_ProjectPhase_ID() != 0)
			projectIssue.set_ValueOfColumn(MInOutLine.COLUMNNAME_C_ProjectPhase_ID, timeExpenseLine.getC_ProjectPhase_ID());
		if (timeExpenseLine.getC_ProjectTask_ID()!=0)
			projectIssue.set_ValueOfColumn(MInOutLine.COLUMNNAME_C_ProjectTask_ID, timeExpenseLine.getC_ProjectTask_ID());
		projectIssue.saveEx();
		projectIssue.process();

		//	Find/Create Project Line
		//	Find/Create Project Line
		MProjectLine projectLine = new MProjectLine(project);
		projectLine.setMProjectIssue(projectIssue);		//	setIssue
		projectLine.saveEx();
		addLog(projectIssue.getLine(), projectIssue.getMovementDate(), projectIssue.getMovementQty(), null);
		//return "@Created@ " + counter.get();		
	}
}
