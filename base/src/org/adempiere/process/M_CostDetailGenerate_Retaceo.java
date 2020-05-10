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
import org.eevolution.service.dsl.ProcessBuilder;

/** Generated Process for (M_CostDetailGenerate_Retaceo)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.3
 */
public class M_CostDetailGenerate_Retaceo extends M_CostDetailGenerate_RetaceoAbstract
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
		sql.append(" SELECT distinct iol.m_Product_ID, io.movementdate "); //1,2
				sql.append("FROM m_inoutline iol  ");
				sql.append(" INNER JOIN M_InOut io on iol.m_inout_id = io.m_inout_id");
				sql.append(" where iol.user4_ID = ? and io.docstatus in ('CO','CL')");
				sql.append(" order by iol.m_Product_ID");
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			pstmt.setInt (1, getRecord_ID());
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ()) {
				 ProcessInfo GenerateCostDetail = ProcessBuilder.create(getCtx())
			                .process(53223)
			                .withTitle("Generate Cost Transaction")
			                .withParameter(MCostDetail.COLUMNNAME_M_Product_ID, rs.getInt(1))
			                .withParameter(MCostDetail.COLUMNNAME_DateAcct, rs.getTimestamp(2), Env.getContextAsDate(getCtx(), "#Date"))
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