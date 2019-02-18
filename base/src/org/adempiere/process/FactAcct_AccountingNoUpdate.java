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
import java.util.List;

import org.adempiere.model.MGLCategoryDateAcctAcctNo;
import org.compiere.model.I_C_AllocationLine;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MClientInfo;
import org.compiere.model.MFactAcct;
import org.compiere.model.MGLCategory;
import org.compiere.model.Query;
import org.compiere.model.X_C_AllocationLine;
import org.compiere.model.X_C_PaymentAllocate;
import org.compiere.util.DB;

import it.businesslogic.ireport.gui.docking.GenericDragTargetListener;

/** Generated Process for (FactAcct_AccountingNoUpdate)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.0
 */
public class FactAcct_AccountingNoUpdate extends FactAcct_AccountingNoUpdateAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception{
		if (getCategoryId()==0 || getCategoryId()==MAllocationHdr.Table_ID)
			resetCategoryAllocation();
		String whereClause = "C_AcctSchema_ID= ? and dateacct between ? and ?";
		ArrayList<Object> params = new ArrayList<>();
		params.add(MClientInfo.get(getCtx()).getC_AcctSchema1_ID());
		params.add(getDateAcct());
		params.add(getDateAcctTo());
		if (getCategoryId()!= 0){
			whereClause = whereClause + " and gl_category_ID=?";
			params.add(getCategoryId());
		}
		String documentNo = "";
		List<MFactAcct> factAccts = new Query(getCtx(), MFactAcct.Table_Name, whereClause, get_TrxName())
				.setParameters(params)
				.setOrderBy("GL_Category_ID , dateacct,fact_Acct_ID")
				.list();
		for (MFactAcct factAcct:factAccts){
			MGLCategory glCategory = new MGLCategory(getCtx(), factAcct.getGL_Category_ID(), get_TrxName());
			MGLCategoryDateAcctAcctNo cat = MGLCategoryDateAcctAcctNo.get(
					glCategory.getGL_Category_ID(), factAcct.getDateAcct());
			if (cat != null)
				documentNo = cat.getaccountingNo();
			else
			{
				//String innerTrxName = Trx.createTrxName("ACNO");
				//Trx innerTrx = Trx.get(innerTrxName, true);
				String tableName = MFactAcct.Table_Name + "_" + glCategory.get_ValueAsString("Value");
				documentNo = DB.getDocumentNo(factAcct.getAD_Client_ID(), tableName, factAcct.get_TrxName());
				MGLCategoryDateAcctAcctNo cat1 = new MGLCategoryDateAcctAcctNo(getCtx(), 0, get_TrxName());
				cat1.setaccountingNo(documentNo);
				cat1.setDateAcct(factAcct.getDateAcct());
				cat1.setGL_Category_ID(glCategory.getGL_Category_ID());
				cat1.saveEx();
				commitEx();
				//innerTrx.commit();
				//innerTrx.close();
				//innerTrx = null;
			}
			factAcct.set_ValueOfColumn("AccountingNo", documentNo);
			factAcct.saveEx();
		}
		return "";
	}
	
	private void resetCategoryAllocation() {
		StringBuffer sql = new StringBuffer("update fact_Acct set gl_Category_ID =");
		sql.append(" (select gl_category_ID from c_Allocationline alo inner join c_Payment p on alo.c_Payment_ID=p.c_Payment_ID");
		sql.append(" INNER JOIN c_Doctype dt on p.c_docType_ID = dt.c_Doctype_ID");
		sql.append(" where alo.c_Allocationline_ID=line_ID)");
		sql.append(" where ad_table_ID=735 and c_AcctSchema_ID=? and dateacct between ? and ? ");
		sql.append(" and (select gl_category_ID from c_Allocationline alo inner join c_Payment p on alo.c_Payment_ID=p.c_Payment_ID");
		sql.append(" INNER JOIN c_Doctype dt on p.c_docType_ID = dt.c_Doctype_ID");
		sql.append(" where alo.c_Allocationline_ID=line_ID) is not null");
		ArrayList<Object> params = new ArrayList<>();
		params.add(MClientInfo.get(getCtx()).getC_AcctSchema1_ID());
		params.add(getDateAcct());
		params.add(getDateAcctTo());
		DB.executeUpdateEx(sql.toString(), params.toArray(), get_TrxName());
	}
}