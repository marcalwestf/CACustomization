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
/** Generated Model - DO NOT CHANGE */
package org.adempiere.model;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for GL_CategoryDateAcctAcctNo
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_GL_CategoryDateAcctAcctNo extends PO implements I_GL_CategoryDateAcctAcctNo, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20180524L;

    /** Standard Constructor */
    public X_GL_CategoryDateAcctAcctNo (Properties ctx, int GL_CategoryDateAcctAcctNo_ID, String trxName)
    {
      super (ctx, GL_CategoryDateAcctAcctNo_ID, trxName);
      /** if (GL_CategoryDateAcctAcctNo_ID == 0)
        {
        } */
    }

    /** Load Constructor */
    public X_GL_CategoryDateAcctAcctNo (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_GL_CategoryDateAcctAcctNo[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set accountingNo.
		@param accountingNo accountingNo	  */
	public void setaccountingNo (String accountingNo)
	{
		set_Value (COLUMNNAME_accountingNo, accountingNo);
	}

	/** Get accountingNo.
		@return accountingNo	  */
	public String getaccountingNo () 
	{
		return (String)get_Value(COLUMNNAME_accountingNo);
	}

	/** Set Account Date.
		@param DateAcct 
		Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	public org.compiere.model.I_GL_Category getGL_Category() throws RuntimeException
    {
		return (org.compiere.model.I_GL_Category)MTable.get(getCtx(), org.compiere.model.I_GL_Category.Table_Name)
			.getPO(getGL_Category_ID(), get_TrxName());	}

	/** Set GL Category.
		@param GL_Category_ID 
		General Ledger Category
	  */
	public void setGL_Category_ID (int GL_Category_ID)
	{
		if (GL_Category_ID < 1) 
			set_Value (COLUMNNAME_GL_Category_ID, null);
		else 
			set_Value (COLUMNNAME_GL_Category_ID, Integer.valueOf(GL_Category_ID));
	}

	/** Get GL Category.
		@return General Ledger Category
	  */
	public int getGL_Category_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_Category_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set GL_CategoryDateAcctAcctNo ID.
		@param GL_CategoryDateAcctAcctNo_ID GL_CategoryDateAcctAcctNo ID	  */
	public void setGL_CategoryDateAcctAcctNo_ID (int GL_CategoryDateAcctAcctNo_ID)
	{
		if (GL_CategoryDateAcctAcctNo_ID < 1) 
			set_Value (COLUMNNAME_GL_CategoryDateAcctAcctNo_ID, null);
		else 
			set_Value (COLUMNNAME_GL_CategoryDateAcctAcctNo_ID, Integer.valueOf(GL_CategoryDateAcctAcctNo_ID));
	}

	/** Get GL_CategoryDateAcctAcctNo ID.
		@return GL_CategoryDateAcctAcctNo ID	  */
	public int getGL_CategoryDateAcctAcctNo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_CategoryDateAcctAcctNo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Immutable Universally Unique Identifier.
		@param UUID 
		Immutable Universally Unique Identifier
	  */
	public void setUUID (String UUID)
	{
		set_Value (COLUMNNAME_UUID, UUID);
	}

	/** Get Immutable Universally Unique Identifier.
		@return Immutable Universally Unique Identifier
	  */
	public String getUUID () 
	{
		return (String)get_Value(COLUMNNAME_UUID);
	}
}