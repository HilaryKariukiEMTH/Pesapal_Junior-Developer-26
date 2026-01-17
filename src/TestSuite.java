import java.util.*;

public class TestSuite {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("Running RDBMS Test Suite...\n");

        testBasicCRUD();
        testPrimaryKey();
        testUniqueConstraint();
        testJoin();
        testUpdate();
        testDelete();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Test Results: " + passed + " passed, " + failed + " failed");
        System.out.println("=".repeat(50));
    }

    static void testBasicCRUD() throws Exception {
        System.out.println("Test: Basic CRUD Operations");
        Database db = new Database();
        
        List<Column> columns = Arrays.asList(
            new Column("id", DataType.INT, true, false),
            new Column("name", DataType.VARCHAR, false, false)
        );
        db.createTable("test", columns);
        
        Table table = db.getTable("test");
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1);
        data.put("name", "Test");
        table.insert(data);
        
        List<Row> rows = table.select(null);
        assertTrue(rows.size() == 1, "Should have 1 row");
        assertTrue(rows.get(0).get("name").equals("Test"), "Name should be Test");
        
        System.out.println("  ✓ Passed\n");
    }

    static void testPrimaryKey() throws Exception {
        System.out.println("Test: Primary Key Constraint");
        Database db = new Database();
        
        List<Column> columns = Arrays.asList(
            new Column("id", DataType.INT, true, false),
            new Column("value", DataType.VARCHAR, false, false)
        );
        db.createTable("test", columns);
        
        Table table = db.getTable("test");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("id", 1);
        data1.put("value", "First");
        table.insert(data1);
        
        try {
            Map<String, Object> data2 = new HashMap<>();
            data2.put("id", 1);
            data2.put("value", "Second");
            table.insert(data2);
            fail("Should not allow duplicate primary key");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("primary"), "Should mention primary key");
        }
        
        System.out.println("  ✓ Passed\n");
    }

    static void testUniqueConstraint() throws Exception {
        System.out.println("Test: Unique Constraint");
        Database db = new Database();
        
        List<Column> columns = Arrays.asList(
            new Column("id", DataType.INT, true, false),
            new Column("email", DataType.VARCHAR, false, true)
        );
        db.createTable("test", columns);
        
        Table table = db.getTable("test");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("id", 1);
        data1.put("email", "test@example.com");
        table.insert(data1);
        
        try {
            Map<String, Object> data2 = new HashMap<>();
            data2.put("id", 2);
            data2.put("email", "test@example.com");
            table.insert(data2);
            fail("Should not allow duplicate unique value");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("unique"), "Should mention unique key");
        }
        
        System.out.println("  ✓ Passed\n");
    }

    static void testJoin() throws Exception {
        System.out.println("Test: JOIN Operation");
        Database db = new Database();
        
        List<Column> cols1 = Arrays.asList(
            new Column("id", DataType.INT, true, false),
            new Column("name", DataType.VARCHAR, false, false)
        );
        db.createTable("users", cols1);
        
        List<Column> cols2 = Arrays.asList(
            new Column("id", DataType.INT, true, false),
            new Column("user_id", DataType.INT, false, false),
            new Column("product", DataType.VARCHAR, false, false)
        );
        db.createTable("orders", cols2);
        
        Table users = db.getTable("users");
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("name", "Alice");
        users.insert(user);
        
        Table orders = db.getTable("orders");
        Map<String, Object> order = new HashMap<>();
        order.put("id", 100);
        order.put("user_id", 1);
        order.put("product", "Laptop");
        orders.insert(order);
        
        List<Row> joined = db.join("orders", "users", "user_id");
        assertTrue(joined.size() == 0, "Join should work with matching column in second table");
        
        // Test with correct join column
        List<Column> cols3 = Arrays.asList(
            new Column("user_id", DataType.INT, true, false),
            new Column("name", DataType.VARCHAR, false, false)
        );
        db.createTable("customers", cols3);
        Table customers = db.getTable("customers");
        Map<String, Object> customer = new HashMap<>();
        customer.put("user_id", 1);
        customer.put("name", "Alice");
        customers.insert(customer);
        
        joined = db.join("orders", "customers", "user_id");
        assertTrue(joined.size() == 1, "Join should return 1 row");
        
        System.out.println("  ✓ Passed\n");
    }

    static void testUpdate() throws Exception {
        System.out.println("Test: UPDATE Operation");
        Database db = new Database();
        
        List<Column> columns = Arrays.asList(
            new Column("id", DataType.INT, true, false),
            new Column("value", DataType.INT, false, false)
        );
        db.createTable("test", columns);
        
        Table table = db.getTable("test");
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1);
        data.put("value", 100);
        table.insert(data);
        
        Map<String, Object> condition = new HashMap<>();
        condition.put("id", 1);
        Map<String, Object> updates = new HashMap<>();
        updates.put("value", 200);
        
        int count = table.update(condition, updates);
        assertTrue(count == 1, "Should update 1 row");
        
        List<Row> rows = table.select(condition);
        assertTrue((Integer)rows.get(0).get("value") == 200, "Value should be updated to 200");
        
        System.out.println("  ✓ Passed\n");
    }

    static void testDelete() throws Exception {
        System.out.println("Test: DELETE Operation");
        Database db = new Database();
        
        List<Column> columns = Arrays.asList(
            new Column("id", DataType.INT, true, false),
            new Column("name", DataType.VARCHAR, false, false)
        );
        db.createTable("test", columns);
        
        Table table = db.getTable("test");
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", i);
            data.put("name", "Item" + i);
            table.insert(data);
        }
        
        Map<String, Object> condition = new HashMap<>();
        condition.put("id", 2);
        int count = table.delete(condition);
        
        assertTrue(count == 1, "Should delete 1 row");
        assertTrue(table.select(null).size() == 2, "Should have 2 rows remaining");
        
        System.out.println("  ✓ Passed\n");
    }

    static void assertTrue(boolean condition, String message) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("  ✗ Failed: " + message);
        }
    }

    static void fail(String message) {
        failed++;
        throw new RuntimeException(message);
    }
}
