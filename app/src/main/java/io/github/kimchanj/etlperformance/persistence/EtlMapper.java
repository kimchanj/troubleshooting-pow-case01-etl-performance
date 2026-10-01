package io.github.kimchanj.etlperformance.persistence;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

@Mapper
public interface EtlMapper {
    @Select("SELECT product_id AS id, product_code AS code, active_flag FROM product WHERE product_code = #{code}")
    ReferenceData findProduct(@Param("code") String code);

    @Select("SELECT supplier_id AS id, supplier_code AS code, active_flag FROM supplier WHERE supplier_code = #{code}")
    ReferenceData findSupplier(@Param("code") String code);

    @Select("SELECT warehouse_id AS id, warehouse_code AS code, active_flag FROM warehouse WHERE warehouse_code = #{code}")
    ReferenceData findWarehouse(@Param("code") String code);

    @Select("SELECT COUNT(*) FROM shipment WHERE external_id = #{externalId}")
    int countShipment(@Param("externalId") String externalId);

    @SelectKey(statement = "SELECT shipment_seq.NEXTVAL FROM dual", keyProperty = "id", before = true, resultType = Long.class)
    @Insert("INSERT INTO shipment (shipment_id, external_id, shipped_at, supplier_id, warehouse_id, status) " +
            "VALUES (#{id}, #{externalId}, #{shippedAt}, #{supplierId}, #{warehouseId}, 'IMPORTED')")
    void insertShipment(ShipmentRecord shipment);

    @SelectKey(statement = "SELECT shipment_item_seq.NEXTVAL FROM dual", keyProperty = "id", before = true, resultType = Long.class)
    @Insert("INSERT INTO shipment_item (shipment_item_id, shipment_id, product_id, quantity, unit_price, line_amount) " +
            "VALUES (#{id}, #{shipmentId}, #{productId}, #{quantity}, #{unitPrice}, #{lineAmount})")
    void insertShipmentItem(ShipmentItemRecord item);
}
