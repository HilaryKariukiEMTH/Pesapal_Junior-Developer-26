# Example SQL Commands

## Basic Table Operations

### Create a table
```sql
CREATE TABLE employees (id INT PRIMARY KEY, name VARCHAR, salary INT)
```

### Insert data
```sql
INSERT INTO employees (id, name, salary) VALUES (1, 'Alice', 75000)
INSERT INTO employees (id, name, salary) VALUES (2, 'Bob', 65000)
INSERT INTO employees (id, name, salary) VALUES (3, 'Charlie', 80000)
```

### Query data
```sql
SELECT * FROM employees
SELECT * FROM employees WHERE id=2
```

### Update data
```sql
UPDATE employees SET salary=70000 WHERE id=2
```

### Delete data
```sql
DELETE FROM employees WHERE id=3
```

## Advanced Examples

### Unique Constraint
```sql
CREATE TABLE accounts (id INT PRIMARY KEY, username VARCHAR UNIQUE, active BOOLEAN)
INSERT INTO accounts (id, username, active) VALUES (1, 'alice123', true)
INSERT INTO accounts (id, username, active) VALUES (2, 'alice123', true)  -- This will fail!
```

### Join Operation
```sql
CREATE TABLE departments (dept_id INT PRIMARY KEY, dept_name VARCHAR)
CREATE TABLE staff (id INT PRIMARY KEY, name VARCHAR, dept_id INT)

INSERT INTO departments (dept_id, dept_name) VALUES (10, 'Engineering')
INSERT INTO departments (dept_id, dept_name) VALUES (20, 'Sales')

INSERT INTO staff (id, name, dept_id) VALUES (1, 'Alice', 10)
INSERT INTO staff (id, name, dept_id) VALUES (2, 'Bob', 20)
INSERT INTO staff (id, name, dept_id) VALUES (3, 'Charlie', 10)

SELECT * FROM staff JOIN departments ON staff.dept_id = departments.dept_id
```

### Multiple Tables
```sql
CREATE TABLE products (id INT PRIMARY KEY, name VARCHAR, price INT)
CREATE TABLE inventory (product_id INT PRIMARY KEY, quantity INT)

INSERT INTO products (id, name, price) VALUES (1, 'Laptop', 1200)
INSERT INTO products (id, name, price) VALUES (2, 'Mouse', 25)

INSERT INTO inventory (product_id, quantity) VALUES (1, 50)
INSERT INTO inventory (product_id, quantity) VALUES (2, 200)

SELECT * FROM products JOIN inventory ON products.id = inventory.product_id
```

## Utility Commands

### Show all tables
```sql
SHOW TABLES
```

### Drop a table
```sql
DROP TABLE employees
```
