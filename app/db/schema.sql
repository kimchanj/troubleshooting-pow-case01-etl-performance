WHENEVER SQLERROR EXIT FAILURE ROLLBACK

CREATE SEQUENCE product_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE supplier_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE warehouse_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE shipment_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE shipment_item_seq START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE product (
    product_id NUMBER(19) CONSTRAINT pk_product PRIMARY KEY,
    product_code VARCHAR2(40 CHAR) CONSTRAINT nn_product_code NOT NULL,
    display_name VARCHAR2(100 CHAR) CONSTRAINT nn_product_name NOT NULL,
    active_flag CHAR(1 CHAR) DEFAULT 'Y' CONSTRAINT nn_product_active NOT NULL,
    CONSTRAINT uk_product_code UNIQUE (product_code),
    CONSTRAINT ck_product_active CHECK (active_flag IN ('Y', 'N'))
);

CREATE TABLE supplier (
    supplier_id NUMBER(19) CONSTRAINT pk_supplier PRIMARY KEY,
    supplier_code VARCHAR2(40 CHAR) CONSTRAINT nn_supplier_code NOT NULL,
    display_name VARCHAR2(100 CHAR) CONSTRAINT nn_supplier_name NOT NULL,
    active_flag CHAR(1 CHAR) DEFAULT 'Y' CONSTRAINT nn_supplier_active NOT NULL,
    CONSTRAINT uk_supplier_code UNIQUE (supplier_code),
    CONSTRAINT ck_supplier_active CHECK (active_flag IN ('Y', 'N'))
);

CREATE TABLE warehouse (
    warehouse_id NUMBER(19) CONSTRAINT pk_warehouse PRIMARY KEY,
    warehouse_code VARCHAR2(40 CHAR) CONSTRAINT nn_warehouse_code NOT NULL,
    display_name VARCHAR2(100 CHAR) CONSTRAINT nn_warehouse_name NOT NULL,
    active_flag CHAR(1 CHAR) DEFAULT 'Y' CONSTRAINT nn_warehouse_active NOT NULL,
    CONSTRAINT uk_warehouse_code UNIQUE (warehouse_code),
    CONSTRAINT ck_warehouse_active CHECK (active_flag IN ('Y', 'N'))
);

CREATE TABLE shipment (
    shipment_id NUMBER(19) CONSTRAINT pk_shipment PRIMARY KEY,
    external_id VARCHAR2(60 CHAR) CONSTRAINT nn_shipment_external_id NOT NULL,
    shipped_at TIMESTAMP CONSTRAINT nn_shipment_shipped_at NOT NULL,
    supplier_id NUMBER(19) CONSTRAINT nn_shipment_supplier NOT NULL,
    warehouse_id NUMBER(19) CONSTRAINT nn_shipment_warehouse NOT NULL,
    status VARCHAR2(20 CHAR) CONSTRAINT nn_shipment_status NOT NULL,
    CONSTRAINT uk_shipment_external_id UNIQUE (external_id),
    CONSTRAINT fk_shipment_supplier FOREIGN KEY (supplier_id) REFERENCES supplier (supplier_id),
    CONSTRAINT fk_shipment_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse (warehouse_id),
    CONSTRAINT ck_shipment_status CHECK (status IN ('IMPORTED'))
);

CREATE TABLE shipment_item (
    shipment_item_id NUMBER(19) CONSTRAINT pk_shipment_item PRIMARY KEY,
    shipment_id NUMBER(19) CONSTRAINT nn_item_shipment NOT NULL,
    product_id NUMBER(19) CONSTRAINT nn_item_product NOT NULL,
    quantity NUMBER(10) CONSTRAINT nn_item_quantity NOT NULL,
    unit_price NUMBER(12,2) CONSTRAINT nn_item_unit_price NOT NULL,
    line_amount NUMBER(22,2) CONSTRAINT nn_item_line_amount NOT NULL,
    CONSTRAINT fk_item_shipment FOREIGN KEY (shipment_id) REFERENCES shipment (shipment_id),
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES product (product_id),
    CONSTRAINT ck_item_quantity CHECK (quantity > 0),
    CONSTRAINT ck_item_unit_price CHECK (unit_price >= 0),
    CONSTRAINT ck_item_line_amount CHECK (line_amount >= 0)
);

CREATE INDEX ix_shipment_item_shipment ON shipment_item (shipment_id);
CREATE INDEX ix_shipment_item_product ON shipment_item (product_id);

PROMPT ETL_LAB schema objects created.
EXIT SUCCESS
