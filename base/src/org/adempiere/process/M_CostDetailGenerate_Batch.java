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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MCostDetail;
import org.compiere.process.ProcessInfo;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.eevolution.process.GenerateCostDetailAbstract;
import org.eevolution.service.dsl.ProcessBuilder;

/** Generated Process for (M_CostDetailGenerate_Batch)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.3
 */
public class M_CostDetailGenerate_Batch extends M_CostDetailGenerate_BatchAbstract
{
	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		StringBuffer sql = new StringBuffer();
		sql.append(" select p.value, p.m_Product_ID, "); //1,2
				sql.append(" c_Acctschema_ID, m_Costelement_ID, m_Costtype_ID , min(dateAcct) as dateacct ");
				sql.append(" from m_Product p ");
				sql.append(" INNER JOIN (");
				sql.append(" select seqno, count(*) as count ,dateacct, cd.m_product_ID, m_Costelement_ID, m_Costtype_ID,c_Acctschema_ID ");
				sql.append(" from m_Costdetail cd ");
				sql.append(" group by dateacct, cd.m_product_ID, m_Costelement_ID, m_Costtype_ID,seqno, c_Acctschema_ID) cd on p.m_Product_ID=cd.m_Product_ID ");
				sql.append(" where p.ad_Client_ID=? and count > 1 ");
				sql.append(" group by p.value, p.m_Product_ID, cd.c_AcctSchema_ID, m_Costelement_ID, m_Costtype_ID ");
				sql.append(" order by p.value");
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			pstmt.setInt (1, Env.getAD_Client_ID(getCtx()));
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ()) {
				 ProcessInfo GenerateCostDetail = ProcessBuilder.create(getCtx())
			                .process(53223)
			                .withTitle("Generate Cost Transaction")
			                .withParameter(MCostDetail.COLUMNNAME_M_Product_ID, rs.getInt(2))
			                .withParameter(MCostDetail.COLUMNNAME_C_AcctSchema_ID, rs.getInt(3))
			                .withParameter(MCostDetail.COLUMNNAME_M_CostElement_ID, rs.getInt(4))
			                .withParameter(MCostDetail.COLUMNNAME_M_CostType_ID, rs.getInt(5))
			                .withParameter(MCostDetail.COLUMNNAME_DateAcct, rs.getTimestamp(6), Env.getContextAsDate(getCtx(), "#Date"))
			                .withoutTransactionClose()
			                .execute(get_TrxName());
				 log.info(rs.getString(1));
				 if (GenerateCostDetail.isError())
						throw new AdempiereException(GenerateCostDetail.getSummary());

			}		
				
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		return "";
	}
}