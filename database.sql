CREATE DATABASE shopapp;
USE shopapp;
-- Customers when they want to buy something -> Must be registered -> Users table
-- avoid using null values for columns since they can cause issues on the backend side, which produces NullPointerException
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY, -- Unique identifier, auto increment when new user is created
    fullname VARCHAR(100) DEFAULT '', -- Full name of the user, default is empty string
    phone_number VARCHAR(10) NOT NULL, -- Phone number of the user, cannot be empty
    [address] VARCHAR(200) DEFAULT '', -- Address of the user, default is empty string
    [password] VARCHAR(255) NOT NULL, -- Password of the user, cannot be empty, hashed by SHA-256
    created_at DATETIME,
    updated_at DATETIME,
    is_active TINYINT DEFAULT 1 -- 1: active, 0: inactive, soft delete
);