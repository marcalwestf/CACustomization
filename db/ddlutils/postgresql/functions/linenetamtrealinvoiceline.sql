-- Function: linenetamtrealinvoiceline(numeric)

-- DROP FUNCTION linenetamtrealinvoiceline(numeric);

CREATE OR REPLACE FUNCTION linenetamtrealinvoiceline(p_c_invoiceline_id numeric)
  RETURNS numeric AS
$BODY$

DECLARE
          v_amt numeric;  
BEGIN
	select case when pl.istaxincluded = 'Y' then ivl.linenetamt-ivl.taxamt else linenetamt end into v_amt 
	from c_Invoiceline ivl
	inner join c_invoice i on ivl.c_invoice_ID = i.c_invoice_ID
	inner join m_pricelist pl on i.m_pricelist_ID = pl.m_pricelist_ID
	where ivl.c_invoiceline_ID=p_c_invoiceLine_ID;
    RETURN coalesce(v_amt,0);

END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION linenetamtrealinvoiceline(numeric)
  OWNER TO adempiere;
