-- password for all seed users is "password123"
INSERT INTO users (name, email, password_hash, role) VALUES
('Admin', 'admin@jananimart.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOe0P0N0Vf5b9pQ7lZ3q7f0y0v9s7Y6Xu', 'ADMIN');

INSERT INTO users (name, email, password_hash, role) VALUES
('Seller One', 'seller1@jananimart.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOe0P0N0Vf5b9pQ7lZ3q7f0y0v9s7Y6Xu', 'SELLER');

INSERT INTO users (name, email, password_hash, role) VALUES
('Buyer One', 'buyer1@jananimart.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOe0P0N0Vf5b9pQ7lZ3q7f0y0v9s7Y6Xu', 'BUYER');

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url) VALUES
(2, 'Wireless Mouse', 'Ergonomic 2.4GHz wireless mouse', 599.00, 50, 'Electronics', 'https://via.placeholder.com/200'),
(2, 'Cotton T-Shirt', 'Plain round-neck cotton t-shirt', 349.00, 100, 'Apparel', 'https://via.placeholder.com/200'),
(2, 'Notebook Set', 'Pack of 3 ruled notebooks', 199.00, 200, 'Stationery', 'https://via.placeholder.com/200');
