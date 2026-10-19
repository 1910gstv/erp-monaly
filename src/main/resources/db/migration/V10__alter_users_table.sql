ALTER TABLE users
    ADD CONSTRAINT fk_company
        FOREIGN KEY (company_id)
            REFERENCES companies(id);

    
CREATE INDEX IF NOT EXISTS idx_users_company_id ON users (company_id);
