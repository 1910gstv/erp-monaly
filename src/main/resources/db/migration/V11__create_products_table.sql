CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    state VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_company
        FOREIGN KEY (company_id)
            REFERENCES companies(id)
);

CREATE INDEX idx_products_name ON products (name);
CREATE INDEX idx_products_company_id ON products (company_id);
CREATE INDEX idx_products_state ON products (state);
