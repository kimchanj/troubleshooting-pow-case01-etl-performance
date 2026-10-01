SET PAGESIZE 100
SET LINESIZE 200
SET FEEDBACK ON

SELECT table_name FROM user_tables ORDER BY table_name;
SELECT 'PRODUCT' AS reference_type, COUNT(*) AS row_count FROM product
UNION ALL SELECT 'SUPPLIER', COUNT(*) FROM supplier
UNION ALL SELECT 'WAREHOUSE', COUNT(*) FROM warehouse;

SELECT shipment_id, external_id, shipped_at, status
FROM shipment
ORDER BY shipment_id;

SELECT shipment_id, COUNT(*) AS item_count, SUM(line_amount) AS shipment_amount
FROM shipment_item
GROUP BY shipment_id
ORDER BY shipment_id;

SELECT COUNT(*) AS orphan_item_count
FROM shipment_item item
LEFT JOIN shipment header ON header.shipment_id = item.shipment_id
WHERE header.shipment_id IS NULL;

EXIT SUCCESS
