# Quick Start Guide

## Installation

No installation required! Just Java 11+ needed.

## Running the RDBMS

### Option 1: REPL Mode (Interactive)

```bash
./run.sh
```

Or manually:
```bash
cd src
javac *.java
java Main
```

### Option 2: Web Mode

```bash
./run.sh web
```

Or manually:
```bash
cd src
javac *.java
java Main web
```

Then open http://localhost:8080 in your browser.

## Quick Tutorial

### REPL Mode

```
$ ./run.sh

Simple RDBMS - Interactive Mode
Type 'exit' to quit

sql> CREATE TABLE books (id INT PRIMARY KEY, title VARCHAR, price INT)
Table created: books

sql> INSERT INTO books (id, title, price) VALUES (1, 'Database Systems', 89)
1 row inserted

sql> INSERT INTO books (id, title, price) VALUES (2, 'Algorithms', 75)
1 row inserted

sql> SELECT * FROM books
id | title | price
--------------------------------------------------
{price=89, id=1, title=Database Systems}
{price=75, id=2, title=Algorithms}

2 row(s)

sql> UPDATE books SET price=79 WHERE id=2
1 row(s) updated

sql> SELECT * FROM books WHERE id=2
id | title | price
--------------------------------------------------
{price=79, id=2, title=Algorithms}

1 row(s)

sql> DELETE FROM books WHERE id=1
1 row(s) deleted

sql> SELECT * FROM books
id | title | price
--------------------------------------------------
{price=79, id=2, title=Algorithms}

1 row(s)

sql> exit
Goodbye!
```

### Web Mode

1. Start the server:
   ```bash
   ./run.sh web
   ```

2. Open http://localhost:8080

3. Use the web interface to:
   - Create users with ID, name, and email
   - View all users in a table
   - Delete users
   - Execute custom SQL in the console

### Example: JOIN Query

```sql
-- Create tables
CREATE TABLE departments (dept_id INT PRIMARY KEY, name VARCHAR)
CREATE TABLE employees (id INT PRIMARY KEY, name VARCHAR, dept_id INT)

-- Insert data
INSERT INTO departments (dept_id, name) VALUES (1, 'Engineering')
INSERT INTO departments (dept_id, name) VALUES (2, 'Sales')

INSERT INTO employees (id, name, dept_id) VALUES (101, 'Alice', 1)
INSERT INTO employees (id, name, dept_id) VALUES (102, 'Bob', 2)
INSERT INTO employees (id, name, dept_id) VALUES (103, 'Charlie', 1)

-- Join query
SELECT * FROM employees JOIN departments ON employees.dept_id = departments.dept_id
```

## Running Tests

```bash
cd src
javac TestSuite.java
java TestSuite
```

Expected output:
```
Running RDBMS Test Suite...

Test: Basic CRUD Operations
  ✓ Passed

Test: Primary Key Constraint
  ✓ Passed

Test: Unique Constraint
  ✓ Passed

Test: JOIN Operation
  ✓ Passed

Test: UPDATE Operation
  ✓ Passed

Test: DELETE Operation
  ✓ Passed

==================================================
Test Results: 10 passed, 0 failed
==================================================
```

## Supported SQL Commands

| Command | Syntax | Example |
|---------|--------|---------|
| CREATE TABLE | `CREATE TABLE name (col type [PRIMARY KEY\|UNIQUE], ...)` | `CREATE TABLE users (id INT PRIMARY KEY, email VARCHAR UNIQUE)` |
| DROP TABLE | `DROP TABLE name` | `DROP TABLE users` |
| INSERT | `INSERT INTO table (cols) VALUES (vals)` | `INSERT INTO users (id, email) VALUES (1, 'a@b.com')` |
| SELECT | `SELECT * FROM table [WHERE conditions]` | `SELECT * FROM users WHERE id=1` |
| UPDATE | `UPDATE table SET col=val WHERE conditions` | `UPDATE users SET email='new@b.com' WHERE id=1` |
| DELETE | `DELETE FROM table [WHERE conditions]` | `DELETE FROM users WHERE id=1` |
| JOIN | `SELECT * FROM t1 JOIN t2 ON t1.col = t2.col` | `SELECT * FROM orders JOIN users ON orders.user_id = users.user_id` |
| SHOW TABLES | `SHOW TABLES` | `SHOW TABLES` |

## Data Types

- `INT` - Integer numbers
- `VARCHAR` - Text strings
- `BOOLEAN` - true/false values

## Constraints

- `PRIMARY KEY` - Unique identifier, cannot be null
- `UNIQUE` - Must be unique across all rows

## Tips

1. **Column names**: Use simple alphanumeric names (no spaces)
2. **String values**: Wrap in single quotes: `'text'`
3. **Numbers**: No quotes needed: `123`
4. **Booleans**: Use `true` or `false`
5. **WHERE clauses**: Use `AND` to combine conditions: `WHERE id=1 AND active=true`
6. **JOINs**: Both tables must have the same column name for joining

## Troubleshooting

**Error: "Table already exists"**
- Use `DROP TABLE name` first, or choose a different name

**Error: "Duplicate value for primary key"**
- Primary keys must be unique. Use a different ID.

**Error: "Invalid type for column"**
- Check that your value matches the column type (INT, VARCHAR, BOOLEAN)

**Error: "Table does not exist"**
- Create the table first with `CREATE TABLE`

**Web server won't start**
- Check if port 8080 is already in use
- Try: `lsof -i :8080` to see what's using it

## Next Steps

- Read [EXAMPLES.md](EXAMPLES.md) for more SQL examples
- Read [DESIGN.md](DESIGN.md) to understand the implementation
- Read [README.md](README.md) for complete documentation
