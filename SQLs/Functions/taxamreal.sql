-- Function: adempiere.taxamtreal(numeric)

-- DROP FUNCTION adempiere.taxamtreal(numeric);

CREATE OR REPLACE FUNCTION taxamtreal(p_c_orderline_id numeric)
  RETURNS numeric AS
$BODY$

DECLARE
          v_taxamt numeric;  
BEGIN

select case when pl.istaxincluded = 'Y' 
	then 
	   case 
		when o.docstatus in ('CL')
		then ((ol.qtyinvoiced * ol.priceactual)- (ol.qtyinvoiced * ol.priceactual)/(1+(t.rate/100)))   
		else (ol.linenetamt- ol.linenetamt/(1+(t.rate/100))) 
            end 
         else 0 end into v_taxamt 

	from c_Orderline ol
	inner join c_order o on ol.c_order_ID = o.c_order_ID
	inner join m_pricelist pl on o.m_pricelist_ID = pl.m_pricelist_ID
	inner join c_tax t on ol.c_tax_ID = t.c_tax_ID
	where ol.c_orderline_ID=p_c_orderLine_ID;

    RETURN coalesce(v_taxamt,0);

END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION taxamtreal(numeric)
  OWNER TO adempiere;
