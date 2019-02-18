/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.adempiere.model;

import org.compiere.model.MDocType;
import org.compiere.model.Query;
import org.compiere.model.X_M_PromotionGroup;
import org.compiere.util.Env;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author hengsin
 *
 */
public class MGLCategoryDateAcctAcctNo extends X_GL_CategoryDateAcctAcctNo {

	private static final long serialVersionUID = 4203915332775348579L;

	public MGLCategoryDateAcctAcctNo(Properties ctx, int X_GL_CategoryDateAcctAcctNo_ID,
                                     String trxName) {
		super(ctx, X_GL_CategoryDateAcctAcctNo_ID, trxName);
	}

	public MGLCategoryDateAcctAcctNo(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	static public MGLCategoryDateAcctAcctNo get(int GL_Category_ID, Timestamp dateacct){
		final String whereClause  = "GL_Category_ID=? and dateacct=?";
		MGLCategoryDateAcctAcctNo categoryDateAcctAcctNo= new Query(Env.getCtx(), MGLCategoryDateAcctAcctNo.Table_Name,
				whereClause, null)
				.setParameters(GL_Category_ID, dateacct)
			 	.setOnlyActiveRecords(true)
				.first();
		return categoryDateAcctAcctNo;
	}

}
