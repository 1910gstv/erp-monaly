ALTER TABLE companies
    ADD CONSTRAINT uq_companies_tax_id UNIQUE (tax_id);