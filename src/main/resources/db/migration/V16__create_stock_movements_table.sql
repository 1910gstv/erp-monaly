CREATE TABLE stock_movements (
       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       company_id UUID NOT NULL,
       product_id UUID NOT NULL,
       type VARCHAR(20) NOT NULL CONSTRAINT chk_movement_type CHECK (type IN ('entry', 'exit', 'adjustment')),
       quantity INTEGER NOT NULL,
       user_id UUID NOT NULL,
       reason TEXT NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       CONSTRAINT fk_stock_mov_company
           FOREIGN KEY (company_id)
               REFERENCES companies(id),
       CONSTRAINT fk_stock_mov_product
           FOREIGN KEY (product_id)
               REFERENCES products(id),
       CONSTRAINT fk_stock_mov_user
            FOREIGN KEY (user_id)
                    REFERENCES users(id)
);