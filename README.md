# Simple Relational Database Management System (RDBMS)

A minimal yet functional RDBMS implementation in Java with SQL-like interface, REPL mode, and a web application demo.

## Features

### Core Database Features
- **Table Management**: CREATE TABLE, DROP TABLE
- **Data Types**: INT, VARCHAR, BOOLEAN
- **CRUD Operations**: INSERT, SELECT, UPDATE, DELETE
- **Constraints**: PRIMARY KEY, UNIQUE
- **Indexing**: Automatic indexing on primary and unique keys
- **Joins**: Basic INNER JOIN support
- **SQL-like Interface**: Familiar SQL syntax

### Modes of Operation
1. **REPL Mode**: Interactive command-line interface
2. **Web Mode**: REST API with HTML/JavaScript frontend

## Architecture

### Core Components

1. **DataType.java**: Enum defining supported data types
2. **Column.java**: Represents table column with type and constraints
3. **Row.java**: Represents a single row of data
4. **Table.java**: Manages rows, enforces constraints, maintains indexes
5. **Database.java**: Manages multiple tables and join operations
6. **SQLParser.java**: Parses and executes SQL-like commands
7. **REPL.java**: Interactive command-line interface
8. **WebServer.java**: HTTP server with REST API and web UI
9. **Main.java**: Entry point supporting both modes

## SQL Syntax

### CREATE TABLE
```sql
CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR, email VARCHAR UNIQUE)
```

### INSERT
```sql
INSERT INTO users (id, name, email) VALUES (1, 'John Doe', 'john@example.com')
```

### SELECT
```sql
SELECT * FROM users
SELECT * FROM users WHERE id=1
```

### UPDATE
```sql
UPDATE users SET name='Jane Doe' WHERE id=1
```

### DELETE
```sql
DELETE FROM users WHERE id=1
DELETE FROM users
```

### JOIN
```sql
SELECT * FROM orders JOIN users ON orders.user_id = users.id
```

### SHOW TABLES
```sql
SHOW TABLES
```

## Usage

### Compile
```bash
cd src
javac *.java
```

### Run REPL Mode
```bash
java Main
```

Example session:
```
sql> CREATE TABLE products (id INT PRIMARY KEY, name VARCHAR, price INT)
Table created: products

sql> INSERT INTO products (id, name, price) VALUES (1, 'Laptop', 1200)
1 row inserted

sql> SELECT * FROM products
id | name | price
--------------------------------------------------
{price=1200, name=Laptop, id=1}

1 row(s)

sql> exit
```

### Run Web Mode
```bash
java Main web
```

Then visit http://localhost:8080 in your browser.

## Web Application Demo

The web application demonstrates CRUD operations with a user management interface:

- **Create**: Add new users with ID, name, and email
- **Read**: View all users in a table
- **Update**: (via SQL console)
- **Delete**: Remove users by ID
- **SQL Console**: Execute arbitrary SQL commands

### API Endpoints

- `GET /api/users` - List all users
- `POST /api/users` - Create user (JSON body: `{id, name, email}`)
- `DELETE /api/users?id=X` - Delete user by ID
- `POST /api/sql` - Execute SQL query (plain text body)

## Implementation Details

### Indexing
- Automatic index creation for PRIMARY KEY and UNIQUE columns
- Hash-based indexing for O(1) lookup on indexed columns
- Enforces uniqueness constraints at insert/update time

### Data Storage
- In-memory storage using Java collections
- Rows stored as HashMap<String, Object>
- Tables stored in Database's HashMap<String, Table>

### Constraint Enforcement
- Primary key: NOT NULL and UNIQUE
- Unique key: UNIQUE values only
- Type validation on insert/update

### Join Implementation
- Nested loop join algorithm
- Supports equi-joins on common columns
- Returns combined rows with prefixed column names

## Limitations & Future Enhancements

Current limitations:
- In-memory only (no persistence)
- Limited data types (INT, VARCHAR, BOOLEAN)
- Basic JOIN (only INNER JOIN on single column)
- No aggregate functions (COUNT, SUM, etc.)
- No ORDER BY, GROUP BY, LIMIT
- No transactions or concurrency control

Potential enhancements:
- File-based persistence
- More data types (DATE, FLOAT, etc.)
- Complex queries (subqueries, multiple joins)
- Query optimization
- B-tree indexes
- Transaction support with ACID properties

## Credits

This project was created as a demonstration of RDBMS fundamentals. The implementation is original work, drawing on standard database concepts from computer science literature.

## License

This is a demonstration project for educational purposes.
# Pesapal_Junior-Developer-26
