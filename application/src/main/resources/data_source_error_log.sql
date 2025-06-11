CREATE TABLE data_source_error_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    method_name VARCHAR(255),
    error_message TEXT,
    timestamp TIMESTAMP
);