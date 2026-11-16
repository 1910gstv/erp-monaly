ALTER TABLE products DROP CONSTRAINT products_sku_key;

ALTER TABLE products
    ALTER COLUMN sku DROP NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL,
    ALTER COLUMN deleted_at SET NOT NULL;