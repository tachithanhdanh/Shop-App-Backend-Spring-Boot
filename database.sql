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

-- Roles for users
CREATE TABLE roles (
    id INT PRIMARY KEY, -- no need to auto increment since roles are predefined and limited
    `name` VARCHAR(50) NOT NULL -- name of the role, cannot be empty
);

ALTER TABLE users ADD COLUMN role_id INT; -- add role_id column to users table
ALTER TABLE users ADD FOREIGN KEY (role_id) REFERENCES roles(id); -- add foreign key constraint to role_id column

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

-- Product images table
CREATE TABLE product_images(
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT, -- foreign key
    FOREIGN KEY (product_id) REFERENCES products(id), -- reference to products table
    CONSTRAINT fk_product_images_product_id
        FOREIGN KEY (product_id) 
        REFERENCES products(id) ON DELETE CASCADE,
        -- when the product is deleted, the images are also deleted
    image_url VARCHAR(300) NOT NULL DEFAULT '' COMMENT 'url of the product image'
)

-- Orders table
CREATE TABLE orders(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT, -- foreign key
    FOREIGN KEY (user_id) REFERENCES users(id), -- reference to users table
    fullname VARCHAR(100) DEFAULT '' COMMENT 'full name of the customer', -- can be different from the user's full name
    email VARCHAR(100) DEFAULT '' COMMENT 'email of the customer', -- can be empty
    phone_number VARCHAR(10) NOT NULL, -- phone number of the customer, cannot be empty
    `address` VARCHAR(200) NOT NULL, -- address of the customer, cannot be empty
    note VARCHAR(100) DEFAULT '' COMMENT 'note for the order', -- can be empty
    order_date DATETIME COMMENT 'date when the order is created' DEFAULT CURRENT_TIMESTAMP,
    `status` VARCHAR(20) COMMENT 'status of the order',
    total_money FLOAT COMMENT 'total money of the order' CHECK (total_money >= 0) -- total money cannot be negative
);

ALTER TABLE orders ADD COLUMN `shipping_method` VARCHAR(100); -- shipping method is the way to deliver the order
ALTER TABLE orders ADD COLUMN `shipping_address` VARCHAR(200); -- shipping address is the address where the order will be delivered
ALTER TABLE orders ADD COLUMN `shipping_date` DATE;
ALTER TABLE orders ADD COLUMN `tracking_number` VARCHAR(100); -- tracking number is used to track the order
ALTER TABLE orders ADD COLUMN `payment_method` VARCHAR(100); -- payment method is the way to pay for the order
-- delete orders -> soft delete
ALTER TABLE orders ADD COLUMN `active` TINYINT(1); -- 1: active, 0: inactive
-- order status can only be one of the following values: pending, processing, shipped, delivered, canceled
ALTER TABLE orders MODIFY COLUMN `status` enum('pending', 'processing', 'shipped', 'delivered', 'canceled') DEFAULT 'pending' COMMENT 'status of the order';

-- Create order details table
CREATE TABLE order_details(
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    price FLOAT CHECK(price >= 0),
    number_of_products INT CHECK(number_of_products > 0),
    total_money FLOAT CHECK(total_money >= 0),
    color VARCHAR(20) DEFAULT '' COMMENT 'color of the product',
);