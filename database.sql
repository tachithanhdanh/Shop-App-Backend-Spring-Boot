CREATE DATABASE shopapp;
USE shopapp;
-- Customers when they want to buy something -> Must be registered -> Users table
-- avoid using null values for columns since they can cause issues on the backend side, which produces NullPointerException
-- data types are written in uppercase
-- table names are written in lowercase, plural form
-- column names are written in lowercase, snake_case
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY, -- Unique identifier, auto increment when new user is created
    fullname VARCHAR(100) DEFAULT '', -- Full name of the user, default is empty string
    phone_number VARCHAR(10) NOT NULL, -- Phone number of the user, cannot be empty
    `address` VARCHAR(200) DEFAULT '', -- Address of the user, default is empty string
    `password` VARCHAR(255) NOT NULL DEFAULT '', -- Password of the user, cannot be empty, hashed by SHA-256
    created_at DATETIME,
    updated_at DATETIME,
    is_active TINYINT(1) DEFAULT 1, -- 1: active, 0: inactive, soft delete, TINYINT(1) is one digit integer
    date_of_birth DATE,
    facebook_account_id VARCHAR(100) DEFAULT '', -- Facebook ID of the user, default is empty string
    google_account_id VARCHAR(100) DEFAULT '' -- Google ID of the user, default is empty string
);

-- Tokens for authentication
-- Token: JWT token
-- tokens and users are related, one user can have multiple tokens
CREATE TABLE tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    expiration_date DATETIME,
    revoked TINYINT(1) NOT NULL,
    expired TINYINT(1) NOT NULL,
    user_id int, -- foreign key
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Support log in with Facebook and Google
-- social_accounts and users are related, one user can have multiple social accounts
CREATE TABLE social_accounts(
    id INT AUTO_INCREMENT PRIMARY KEY,
    `provider` VARCHAR(20) NOT NULL COMMENT 'facebook, google', -- COMMENT is used to describe the column
    `provider_id` VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL COMMENT 'email of the user',
    `name` VARCHAR(100) NOT NULL COMMENT 'name of the user',
    user_id INT, -- foreign key
    FOREIGN KEY (user_id) REFERENCES users(id) -- reference to users table
);

-- Product categories table
CREATE TABLE categories(
    id INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'name of the category, for example: electronics, fashion, etc'
);

-- Products table
CREATE TABLE products(
    id INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(350) NOT NULL DEFAULT '' COMMENT 'name of the product',
    price FLOAT NOT NULL COMMENT 'price of the product' CHECK (price >= 0), -- price cannot be negative
    thumbnail VARCHAR(300) NOT NULL DEFAULT '' COMMENT 'thumbnail url of the product',
    `description` LONGTEXT DEFAULT '' COMMENT 'description of the product',
    created_at DATETIME,
    updated_at DATETIME,
    category_id INT, -- foreign key
    FOREIGN KEY (category_id) REFERENCES categories(id) -- reference to categories table
);