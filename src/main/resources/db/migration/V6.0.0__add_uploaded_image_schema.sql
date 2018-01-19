CREATE TABLE uploaded_image
(
    id VARCHAR(255) PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    created_at datetime DEFAULT NULL,
    updated_at datetime DEFAULT NULL,
    created_by varchar(255) DEFAULT NULL,
    modified_by varchar(255) DEFAULT NULL,
    version int(11) DEFAULT 0
);