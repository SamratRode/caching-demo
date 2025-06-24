CREATE DATABASE IF NOT EXISTS caching_demo;
USE caching_demo;

CREATE USER IF NOT EXISTS 'demo_user'@'%' IDENTIFIED BY 'demo_password';
GRANT ALL PRIVILEGES ON caching_demo.* TO 'demo_user'@'%';
FLUSH PRIVILEGES;
