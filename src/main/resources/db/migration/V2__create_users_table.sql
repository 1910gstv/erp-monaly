CREATE TABLE users (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           username VARCHAR(150) NOT NULL,
                           email VARCHAR(100) NOT NULL,
                           password VARCHAR(255) NOT NULL,
                           role VARCHAR(20) NOT NULL,
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

