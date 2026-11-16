ALTER TABLE products
    ADD sku varchar(100) NOT NULL UNIQUE;

ALTER TABLE products
    ADD CONSTRAINT uq_products_company_sku UNIQUE("company_id", sku);