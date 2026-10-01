WHENEVER SQLERROR EXIT FAILURE ROLLBACK

INSERT INTO product VALUES (product_seq.NEXTVAL, 'PROD-001', 'Synthetic Widget', 'Y');
INSERT INTO product VALUES (product_seq.NEXTVAL, 'PROD-002', 'Synthetic Bracket', 'Y');
INSERT INTO product VALUES (product_seq.NEXTVAL, 'PROD-003', 'Synthetic Container', 'Y');
INSERT INTO product VALUES (product_seq.NEXTVAL, 'PROD-INACTIVE', 'Inactive Synthetic Product', 'N');

INSERT INTO supplier VALUES (supplier_seq.NEXTVAL, 'SUP-001', 'Synthetic Supplier North', 'Y');
INSERT INTO supplier VALUES (supplier_seq.NEXTVAL, 'SUP-002', 'Synthetic Supplier South', 'Y');
INSERT INTO supplier VALUES (supplier_seq.NEXTVAL, 'SUP-INACTIVE', 'Inactive Synthetic Supplier', 'N');

INSERT INTO warehouse VALUES (warehouse_seq.NEXTVAL, 'WH-001', 'Synthetic Warehouse East', 'Y');
INSERT INTO warehouse VALUES (warehouse_seq.NEXTVAL, 'WH-002', 'Synthetic Warehouse West', 'Y');
INSERT INTO warehouse VALUES (warehouse_seq.NEXTVAL, 'WH-INACTIVE', 'Inactive Synthetic Warehouse', 'N');

COMMIT;
PROMPT Synthetic reference seed inserted.
EXIT SUCCESS
