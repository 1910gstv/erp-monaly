CREATE TABLE stock (
       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       company_id UUID NOT NULL,
       product_id UUID NOT NULL,
       quantity INTEGER NOT NULL DEFAULT 0,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       deleted_at TIMESTAMP,
       CONSTRAINT fk_stock_company
           FOREIGN KEY (company_id)
               REFERENCES companies(id),
       CONSTRAINT fk_stock_product
            FOREIGN KEY (product_id)
                REFERENCES products(id),
       CONSTRAINT chk_stock_quantity_non_negative CHECK(quantity >= 0)
);

CREATE UNIQUE INDEX uq_stock_company_product_ative ON stock (company_id, product_id) WHERE deleted_at IS NULL;
