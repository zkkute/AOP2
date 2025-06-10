CREATE TABLE time_limit_exceed_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    method_name VARCHAR(255),
    execution_time BIGINT,
    timestamp TIMESTAMP
);