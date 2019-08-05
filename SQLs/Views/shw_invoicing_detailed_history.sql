﻿-- Materialized View: adempiere.shw_invoicing_detailed_history

-- DROP MATERIALIZED VIEW adempiere.shw_invoicing_detailed_history;

CREATE MATERIALIZED VIEW adempiere.shw_invoicing_detailed_history AS 
 SELECT i.ad_client_id,
    i.ad_org_id,
    i.dateinvoiced,
    to_char(i.dateinvoiced::date::timestamp with time zone, 'dd/mm/yyyy'::text) AS dateinvoicedformatted,
    i.issotrx,
    i.documentno,
    i.docstatus,
    usr.name AS salesrepname,
    usr.value AS salesrepvalue,
    p.name AS productname,
    p.value AS productvalue,
    vnd.value AS vendorvalue,
    vnd.name AS vendorname,
    il.qtyinvoiced,
    uomtrl.name AS uomname,
    il.priceactual,
    il.linetotalamt,
    i.taxamt,
    taxtrl.name AS tax,
    adempiere.linenetamtrealinvoiceline(il.c_invoiceline_id) AS linenetamtreal,
    adempiere.linenetamtreturned(il.c_invoiceline_id) AS linenetamtreturned,
    adempiere.linenetamtvoided(il.reversalline_id) AS linenetamtvoided,
    0 AS linenetamtreinvoiced,
    adempiere.linenetamtrealinvoiceline(il.c_invoiceline_id) + adempiere.linenetamtvoided(il.reversalline_id) - adempiere.linenetamtreturned(il.c_invoiceline_id) AS netsales,
    i.c_invoice_id,
    p.m_product_id,
    vnd.c_bpartner_id,
    usr.ad_user_id,
    dtt.name AS doctypename,
    i.c_doctype_id,
    dt.docbasetype,
    il.c_invoiceline_id,
    COALESCE(returnamt.linenetamt, 0::numeric) AS returnamtimport,
    COALESCE(discountamt.linenetamt, 2::numeric) AS discountamtimport,
        CASE
            WHEN adempiere.charat(dt.docbasetype::character varying, 3)::text = 'C'::text THEN '-1'::integer
            ELSE 1
        END AS multiplier,
    i.c_bpartner_id AS customer_id,
    round(il.linenetamt, 2) AS linenetamt,
    adempiere.trunc(i.dateinvoiced::timestamp with time zone, 'MONTH'::character varying) AS dateinvoicedmonth,
    adempiere.trunc(i.dateinvoiced::timestamp with time zone, 'YEAR'::character varying) AS dateinvoicedyear,
    per.c_period_id,
    i.salesrep_id,
    p.m_product_category_id,
    p.user3_id
   FROM adempiere.c_invoice i
     JOIN adempiere.c_invoiceline il ON i.c_invoice_id = il.c_invoice_id
     JOIN adempiere.ad_client cl ON i.ad_client_id = cl.ad_client_id
     JOIN adempiere.c_uom_trl uomtrl ON il.c_uom_id = uomtrl.c_uom_id AND cl.ad_language::text = uomtrl.ad_language::text
     JOIN adempiere.c_tax_trl taxtrl ON il.c_tax_id = taxtrl.c_tax_id AND cl.ad_language::text = taxtrl.ad_language::text
     JOIN adempiere.m_product p ON il.m_product_id = p.m_product_id
     LEFT JOIN adempiere.m_product_po ppo ON p.m_product_id = ppo.m_product_id AND ppo.iscurrentvendor = 'Y'::bpchar
     JOIN adempiere.c_bpartner vnd ON ppo.c_bpartner_id = vnd.c_bpartner_id
     JOIN adempiere.ad_user usr ON i.salesrep_id = usr.ad_user_id
     JOIN adempiere.c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
     LEFT JOIN adempiere.c_doctype_trl dtt ON dtt.c_doctype_id = i.c_doctype_id AND dtt.ad_language::text = cl.ad_language::text
     LEFT JOIN adempiere.c_period per ON i.dateinvoiced >= per.startdate AND i.dateinvoiced <= per.enddate AND per.ad_client_id = i.ad_client_id
     LEFT JOIN LATERAL ( SELECT sum(round(abs(rv_c_invoiceline.linenetamt), 2)) AS linenetamt,
            il.ref_invoiceline_id
           FROM adempiere.rv_c_invoiceline
          WHERE il.ref_invoiceline_id IS NOT NULL
          GROUP BY il.ref_invoiceline_id) discountamt ON discountamt.ref_invoiceline_id = il.c_invoiceline_id
     LEFT JOIN LATERAL ( SELECT sum(round(abs(ivl.linenetamt), 2)) AS linenetamt,
            ivl.m_product_id,
            i_1.ref_invoice_id
           FROM adempiere.rv_c_invoiceline ivl
             JOIN adempiere.c_invoice i_1 ON ivl.c_invoice_id = i_1.c_invoice_id
          WHERE i_1.ref_invoice_id IS NOT NULL
          GROUP BY ivl.m_product_id, i_1.ref_invoice_id) returnamt ON returnamt.ref_invoice_id = il.c_invoice_id AND returnamt.m_product_id = il.m_product_id
WITH NO DATA;

ALTER TABLE adempiere.shw_invoicing_detailed_history
  OWNER TO adempiere;
