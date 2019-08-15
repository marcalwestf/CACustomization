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


import java.util.ArrayList;

import org.compiere.util.DB;

/** Generated Process for (FactAcct_DaylyReport)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.1
 */
public class FactAcct_DailyReport extends FactAcct_DailyReportAbstract
{
	

	/**	Insert Statement				*/
	private static String		s_insert = "INSERT INTO T_FactAcct_DailyReport "
		+ "(AD_PInstance_ID, Fact_Acct_ID,"
		+ " AD_Client_ID, AD_Org_ID, Created,CreatedBy, Updated,UpdatedBy,"
		+ " C_AcctSchema_ID, Account_ID, AccountValue, DateTrx, DateAcct, C_Period_ID,"
		+ " AD_Table_ID, Record_ID, Line_ID,"
		+ " GL_Category_ID, GL_Budget_ID, C_Tax_ID, M_Locator_ID, PostingType,"
		+ " C_Currency_ID, AmtSourceDr, AmtSourceCr, AmtSourceBalance,"
		+ " AmtAcctDr, AmtAcctCr, AmtAcctBalance, C_UOM_ID, Qty,"
		+ " M_Product_ID, C_BPartner_ID, AD_OrgTrx_ID, C_LocFrom_ID,C_LocTo_ID,"
		+ " C_SalesRegion_ID, C_Project_ID, C_Campaign_ID, C_Activity_ID,"
		+ " User1_ID, User2_ID,User3_ID, User4_ID, A_Asset_ID, Description)";
	private StringBuffer whereClause = null;
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{	
		createDetailLines();
		return "";
	}
	

	private void createDetailLines()
	{

		whereClause = new StringBuffer(" (C_AcctSchema_ID=? ");
		whereClause.append(" and dateacct between ? and ? ");
		whereClause.append(" and postingtype = 'A'");
		ArrayList<Object> params = new ArrayList<>();
		params.add(getAcctSchemaId());
		params.add(getDateAcct());
		params.add(getDateAcctTo());
		StringBuffer sql = new StringBuffer (s_insert);
		//	(AD_PInstance_ID, Fact_Acct_ID,
		sql.append("SELECT ").append(getAD_PInstance_ID()).append(",Fact_Acct_ID,");
		//	AD_Client_ID, AD_Org_ID, Created,CreatedBy, Updated,UpdatedBy,
		sql.append(getAD_Client_ID()).append(",AD_Org_ID,Created,CreatedBy, Updated,UpdatedBy,");
		//	C_AcctSchema_ID, Account_ID, DateTrx, AccountValue, DateAcct, C_Period_ID,
		sql.append("C_AcctSchema_ID, Account_ID, null, DateTrx, DateAcct, C_Period_ID,");
		//	AD_Table_ID, Record_ID, Line_ID,
		sql.append("AD_Table_ID, Record_ID, Line_ID,");
		//	GL_Category_ID, GL_Budget_ID, C_Tax_ID, M_Locator_ID, PostingType,
		sql.append("GL_Category_ID, GL_Budget_ID, C_Tax_ID, M_Locator_ID, PostingType,");
		//	C_Currency_ID, AmtSourceDr, AmtSourceCr, AmtSourceBalance,
		sql.append("C_Currency_ID, AmtSourceDr,AmtSourceCr, AmtSourceDr-AmtSourceCr,");
		//	AmtAcctDr, AmtAcctCr, AmtAcctBalance, C_UOM_ID, Qty,
		sql.append(" AmtAcctDr,AmtAcctCr, AmtAcctDr-AmtAcctCr, C_UOM_ID,Qty,");
		//	M_Product_ID, C_BPartner_ID, AD_OrgTrx_ID, C_LocFrom_ID,C_LocTo_ID,
		sql.append ("M_Product_ID, C_BPartner_ID, AD_OrgTrx_ID, C_LocFrom_ID,C_LocTo_ID,");
		//	C_SalesRegion_ID, C_Project_ID, C_Campaign_ID, C_Activity_ID,
		sql.append ("C_SalesRegion_ID, C_Project_ID, C_Campaign_ID, C_Activity_ID,");
		//	User1_ID, User2_ID, User3_ID, User4_ID , A_Asset_ID, Description)
		sql.append ("User1_ID, User2_ID, User3_ID, User4_ID, A_Asset_ID, Description");
		//
		sql.append(" FROM Fact_Acct WHERE AD_Client_ID=").append(getAD_Client_ID())
			.append (" AND ").append(whereClause.toString());
		//
		sql.append(")");
		
		int no = DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
		if (no == 0)
			log.fine(sql.toString());
		
		//	Update AccountValue
		String sql2 = "UPDATE T_FactAcct_DailyReport tb SET AccountValue = "
			+ "(SELECT Value FROM C_ElementValue ev WHERE ev.C_ElementValue_ID=tb.Account_ID) "
			+ "WHERE tb.Account_ID IS NOT NULL AND tb.AD_PInstance_ID = " + getAD_PInstance_ID();
		no = DB.executeUpdate(sql2, get_TrxName());
		sql2 = "UPDATE T_FactAcct_DailyReport tb set c_Doctype_ID = getdocType_ID(AD_Table_ID, record_ID, line_ID)"
				+ "WHERE  tb.AD_PInstance_ID = " + getAD_PInstance_ID();
		

		no = DB.executeUpdate(sql2, get_TrxName());

		sql2 = "UPDATE T_FactAcct_DailyReport tb set documentno = getdocumentNo(AD_Table_ID, record_ID, line_ID)"
				+ "WHERE  tb.AD_PInstance_ID = " + getAD_PInstance_ID();
		

		no = DB.executeUpdate(sql2, get_TrxName());
		
		if (no > 0)
			log.fine("Set AccountValue #" + no);
		
	}	//	createDetailLines
}