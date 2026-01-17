import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Database db = new Database();
        
        // Initialize demo table
        List<Column> columns = Arrays.asList(
            new Column("id", DataType.INT, true, false),
            new Column("name", DataType.VARCHAR, false, false),
            new Column("email", DataType.VARCHAR, false, true)
        );
        db.createTable("users", columns);
        
        if (args.length > 0 && args[0].equals("web")) {
            // Start web server
            WebServer server = new WebServer(db, 8080);
            server.start();
            System.out.println("Visit http://localhost:8080");
        } else {
            // Start REPL
            REPL repl = new REPL(db);
            repl.start();
        }
    }
}