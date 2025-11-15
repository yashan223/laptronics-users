
-- Create database
CREATE DATABASE IF NOT EXISTS laptronics_users;
USE laptronics_users;

-- ============================================
-- Table: roles
-- Stores different user roles (USER, ADMIN, etc.)
-- ============================================
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- ============================================
-- Table: users
-- Stores user account information
-- ============================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone_number VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- ============================================
-- Table: user_roles
-- Junction table linking users to their roles (Many-to-Many)
-- ============================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ============================================
-- Insert default roles
-- ============================================
INSERT INTO roles (name, description) VALUES 
('USER', 'Default user role'),
('ADMIN', 'Administrator role');

-- ============================================
-- Sample data (optional - for testing)
-- ============================================
-- Insert test users
INSERT INTO users (username, email, password, first_name, last_name, phone_number, active, created_at, updated_at)
VALUES 
('yashan', 'yashan@email.com', 'yashan123', 'Yashan', 'Perera', '1234567890', TRUE, NOW(), NOW()),
('admin', 'admin@email.com', 'admin123', 'Admin', 'User', '0987654321', TRUE, NOW(), NOW());

-- Assign USER role to yashan
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'yashan' AND r.name = 'USER';

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- ============================================
-- Useful queries
-- ============================================

-- View all users with their roles
SELECT 
    u.id,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    u.active,
    GROUP_CONCAT(r.name) as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.id;

-- Count users by role
SELECT r.name, COUNT(ur.user_id) as user_count
FROM roles r
LEFT JOIN user_roles ur ON r.id = ur.role_id
GROUP BY r.id;
