# Design Documentation

## Overview
This is a minimal but functional RDBMS implementation demonstrating core database concepts without external dependencies (except Java's built-in HTTP server for the web demo).

## Architecture Decisions

### 1. In-Memory Storage
**Decision**: Use Java collections (HashMap, ArrayList) for storage.

**Rationale**: 
- Simplifies implementation
- Focuses on core RDBMS logic rather than I/O
- Sufficient for demonstration purposes
- Easy to extend to file-based storage later

### 2. Hash-Based Indexing
**Decision**: Use HashMap for indexes on PRIMARY KEY and UNIQUE columns.

**Rationale**:
- O(1) lookup time for indexed columns
- Simple to implement and understand
- Automatically enforces uniqueness constraints
- Suitable for equality-based queries

**Trade-offs**:
- No support for range queries (would need B-tree)
- Memory overhead for indexes
- Only works for exact matches

### 3. SQL-Like Interface
**Decision**: Implement a subset of SQL using regex parsing.

**Rationale**:
- Familiar syntax for users
- Demonstrates parsing and command interpretation
- Extensible to more complex queries

**Limitations**:
- No full SQL parser (would require lexer/parser generator)
- Limited to simple queries
- No query optimization

### 4. Type System
**Decision**: Support three basic types (INT, VARCHAR, BOOLEAN).

**Rationale**:
- Covers most common use cases
- Simple type checking
- Easy to extend with more types

**Missing**:
- DATE, TIMESTAMP
- FLOAT, DECIMAL
- BLOB, TEXT
- NULL handling (partially implemented)

### 5. Join Implementation
**Decision**: Nested loop join algorithm.

**Rationale**:
- Simplest join algorithm
- Works for all join types
- Easy to understand and debug

**Performance**:
- O(n*m) complexity
- Inefficient for large tables
- Could be optimized with hash join or sort-merge join

### 6. Constraint Enforcement
**Decision**: Check constraints at insert/update time.

**Rationale**:
- Immediate feedback to user
- Maintains data integrity
- Prevents invalid states

**Implementation**:
- PRIMARY KEY: NOT NULL + UNIQUE
- UNIQUE: Enforced via index
- Type checking: Runtime validation

### 7. Web Server
**Decision**: Use Java's built-in HttpServer.

**Rationale**:
- No external dependencies
- Sufficient for demo purposes
- Simple REST API

**Limitations**:
- Not production-ready
- No authentication/authorization
- Basic error handling
- Single-threaded

## Data Flow

### INSERT Operation
1. Parse SQL command
2. Extract table name, columns, values
3. Validate data types
4. Check PRIMARY KEY/UNIQUE constraints
5. Create Row object
6. Add to table's row list
7. Update indexes

### SELECT Operation
1. Parse SQL command
2. Extract table name and WHERE conditions
3. If indexed column in WHERE, use index (future optimization)
4. Otherwise, scan all rows
5. Filter rows matching conditions
6. Format and return results

### UPDATE Operation
1. Parse SQL command
2. Find rows matching WHERE clause
3. Remove from indexes
4. Remove from row list
5. Apply updates to row data
6. Re-insert with validation
7. Update indexes

### DELETE Operation
1. Parse SQL command
2. Find rows matching WHERE clause
3. Remove from indexes
4. Remove from row list

### JOIN Operation
1. Parse SQL command
2. Get both tables
3. For each row in table1:
   - Get join column value
   - Find matching rows in table2
   - Combine row data with prefixed column names
4. Return combined rows

## Testing Strategy

### Unit Tests (TestSuite.java)
- Basic CRUD operations
- Constraint enforcement
- JOIN functionality
- Edge cases

### Integration Tests
- REPL mode with sample commands
- Web server with REST API
- End-to-end workflows

### Manual Testing
- Interactive REPL session
- Web UI interaction
- SQL console in web app

## Future Enhancements

### High Priority
1. **Persistence**: Save/load database to/from disk
2. **Transactions**: BEGIN, COMMIT, ROLLBACK
3. **More data types**: DATE, FLOAT, NULL
4. **Better error messages**: Line numbers, suggestions

### Medium Priority
1. **Query optimization**: Use indexes in WHERE clauses
2. **Aggregate functions**: COUNT, SUM, AVG, MIN, MAX
3. **ORDER BY, GROUP BY, LIMIT**
4. **Multiple column indexes**
5. **Foreign key constraints**

### Low Priority
1. **Subqueries**
2. **Views**
3. **Stored procedures**
4. **User management and permissions**
5. **Query planner and EXPLAIN**

## Performance Characteristics

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| INSERT | O(1) with index, O(n) validation | O(1) |
| SELECT (no index) | O(n) | O(k) where k = result size |
| SELECT (indexed) | O(1) lookup + O(n) scan | O(k) |
| UPDATE | O(n) find + O(1) update | O(1) |
| DELETE | O(n) find + O(1) delete | O(1) |
| JOIN | O(n*m) nested loop | O(n*m) |

## Code Statistics

- **Total Lines**: ~1000 (excluding tests and docs)
- **Classes**: 9 core classes
- **Supported SQL Commands**: 7 (CREATE, DROP, INSERT, SELECT, UPDATE, DELETE, SHOW)
- **Data Types**: 3 (INT, VARCHAR, BOOLEAN)
- **Constraints**: 2 (PRIMARY KEY, UNIQUE)

## Acknowledgments

This implementation draws on standard database concepts from:
- Database Systems textbooks (Ramakrishnan & Gehrke, Silberschatz et al.)
- SQL standard specifications
- Open source database implementations (for inspiration, not code)

All code is original work created for this demonstration.
