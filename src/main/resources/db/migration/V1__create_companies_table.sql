CREATE TABLE companies (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           name VARCHAR(150) NOT NULL,
                           tax_id VARCHAR(20) UNIQUE,
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_companies_name ON companies (name);