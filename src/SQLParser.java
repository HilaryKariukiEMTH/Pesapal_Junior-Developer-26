import java.util.*;
import java.util.regex.*;

public class SQLParser {
    private final Database database;

    public SQLParser(Database database) {
        this.database = database;
    }

    public String execute(String sql) {
        try {
            sql = sql.trim();
            if (sql.toUpperCase().startsWith("CREATE TABLE")) {
                return executeCreateTable(sql);
            } else if (sql.toUpperCase().startsWith("DROP TABLE")) {
                return executeDropTable(sql);
            } else if (sql.toUpperCase().startsWith("INSERT INTO")) {
                return executeInsert(sql);
            } else if (sql.toUpperCase().startsWith("SELECT")) {
                return executeSelect(sql);
            } else if (sql.toUpperCase().startsWith("UPDATE")) {
                return executeUpdate(sql);
            } else if (sql.toUpperCase().startsWith("DELETE FROM")) {
                return executeDelete(sql);
            } else if (sql.toUpperCase().startsWith("SHOW TABLES")) {
                return executeShowTables();
            } else {
                return "Unknown command";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String executeCreateTable(String sql) throws Exception {
        Pattern pattern = Pattern.compile("CREATE TABLE (\\w+) \\((.+)\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (!matcher.find()) {
            throw new Exception("Invalid CREATE TABLE syntax");
        }

        String tableName = matcher.group(1);
        String columnDefs = matcher.group(2);
        
        List<Column> columns = new ArrayList<>();
        for (String colDef : columnDefs.split(",")) {
            colDef = colDef.trim();
            String[] parts = colDef.split("\\s+");
            String colName = parts[0];
            DataType type = DataType.valueOf(parts[1].toUpperCase());
            boolean isPrimary = colDef.toUpperCase().contains("PRIMARY KEY");
            boolean isUnique = colDef.toUpperCase().contains("UNIQUE");
            columns.add(new Column(colName, type, isPrimary, isUnique));
        }

        database.createTable(tableName, columns);
        return "Table created: " + tableName;
    }

    private String executeDropTable(String sql) throws Exception {
        Pattern pattern = Pattern.compile("DROP TABLE (\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (!matcher.find()) {
            throw new Exception("Invalid DROP TABLE syntax");
        }

        String tableName = matcher.group(1);
        database.dropTable(tableName);
        return "Table dropped: " + tableName;
    }

    private String executeInsert(String sql) throws Exception {
        Pattern pattern = Pattern.compile("INSERT INTO (\\w+) \\(([^)]+)\\) VALUES \\(([^)]+)\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (!matcher.find()) {
            throw new Exception("Invalid INSERT syntax");
        }

        String tableName = matcher.group(1);
        String[] columns = matcher.group(2).split(",");
        String[] values = matcher.group(3).split(",");

        Table table = database.getTable(tableName);
        Map<String, Object> data = new HashMap<>();
        
        for (int i = 0; i < columns.length; i++) {
            String colName = columns[i].trim();
            String value = values[i].trim();
            data.put(colName, parseValue(value));
        }

        table.insert(data);
        return "1 row inserted";
    }

    private Object parseValue(String value) {
        value = value.trim();
        if (value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length() - 1);
        }
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private Map<String, Object> parseWhereClause(String whereClause) {
        Map<String, Object> conditions = new HashMap<>();
        if (whereClause == null || whereClause.trim().isEmpty()) {
            return conditions;
        }

        for (String condition : whereClause.split(" AND ")) {
            String[] parts = condition.trim().split("=");
            if (parts.length == 2) {
                conditions.put(parts[0].trim(), parseValue(parts[1].trim()));
            }
        }
        return conditions;
    }

    private Map<String, Object> parseSetClause(String setClause) {
        Map<String, Object> updates = new HashMap<>();
        for (String assignment : setClause.split(",")) {
            String[] parts = assignment.trim().split("=");
            if (parts.length == 2) {
                updates.put(parts[0].trim(), parseValue(parts[1].trim()));
            }
        }
        return updates;
    }

    private String executeSelect(String sql) throws Exception {
        if (sql.toUpperCase().contains(" JOIN ")) {
            return executeJoin(sql);
        }

        Pattern pattern = Pattern.compile("SELECT \\* FROM (\\w+)(?: WHERE (.+))?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (!matcher.find()) {
            throw new Exception("Invalid SELECT syntax");
        }

        String tableName = matcher.group(1);
        String whereClause = matcher.group(2);

        Table table = database.getTable(tableName);
        Map<String, Object> conditions = parseWhereClause(whereClause);
        List<Row> rows = table.select(conditions);

        return formatRows(rows, table.getColumns());
    }

    private String executeJoin(String sql) throws Exception {
        Pattern pattern = Pattern.compile("SELECT \\* FROM (\\w+) JOIN (\\w+) ON (\\w+)\\.(\\w+) = (\\w+)\\.(\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (!matcher.find()) {
            throw new Exception("Invalid JOIN syntax");
        }

        String table1 = matcher.group(1);
        String table2 = matcher.group(2);
        String joinCol = matcher.group(4);

        List<Row> rows = database.join(table1, table2, joinCol);
        return formatRows(rows, null);
    }

    private String executeUpdate(String sql) throws Exception {
        Pattern pattern = Pattern.compile("UPDATE (\\w+) SET (.+?) WHERE (.+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (!matcher.find()) {
            throw new Exception("Invalid UPDATE syntax");
        }

        String tableName = matcher.group(1);
        String setClause = matcher.group(2);
        String whereClause = matcher.group(3);

        Table table = database.getTable(tableName);
        Map<String, Object> updates = parseSetClause(setClause);
        Map<String, Object> conditions = parseWhereClause(whereClause);

        int count = table.update(conditions, updates);
        return count + " row(s) updated";
    }

    private String executeDelete(String sql) throws Exception {
        Pattern pattern = Pattern.compile("DELETE FROM (\\w+)(?: WHERE (.+))?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (!matcher.find()) {
            throw new Exception("Invalid DELETE syntax");
        }

        String tableName = matcher.group(1);
        String whereClause = matcher.group(2);

        Table table = database.getTable(tableName);
        Map<String, Object> conditions = parseWhereClause(whereClause);

        int count = table.delete(conditions);
        return count + " row(s) deleted";
    }

    private String executeShowTables() {
        Set<String> tables = database.getTableNames();
        if (tables.isEmpty()) {
            return "No tables";
        }
        return "Tables:\n" + String.join("\n", tables);
    }

    private String formatRows(List<Row> rows, List<Column> columns) {
        if (rows.isEmpty()) {
            return "0 rows";
        }

        StringBuilder sb = new StringBuilder();
        if (columns != null) {
            sb.append(columns.stream().map(Column::getName).reduce((a, b) -> a + " | " + b).orElse("")).append("\n");
            sb.append("-".repeat(50)).append("\n");
        }

        for (Row row : rows) {
            sb.append(row.toString()).append("\n");
        }
        sb.append("\n").append(rows.size()).append(" row(s)");
        return sb.toString();
    }
}
