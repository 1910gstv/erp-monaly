ALTER TABLE users
    ADD CONSTRAINT fk_users_company
        FOREIGN KEY (company_id)
            REFERENCES companies(id)
            ON DELETE RESTRICT;

CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_company_id ON users (company_id);