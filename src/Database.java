import java.util.*;

public class Database {
    private final Map<String, Table> tables;

    public Database() {
        this.tables = new HashMap<>();
    }

    public void createTable(String name, List<Column> columns) throws Exception {
        if (tables.containsKey(name)) {
            throw new Exception("Table already exists: " + name);
        }
        tables.put(name, new Table(name, columns));
    }

    public void dropTable(String name) throws Exception {
        if (!tables.containsKey(name)) {
            throw new Exception("Table does not exist: " + name);
        }
        tables.remove(name);
    }

    public Table getTable(String name) throws Exception {
        Table table = tables.get(name);
        if (table == null) {
            throw new Exception("Table does not exist: " + name);
        }
        return table;
    }

    public List<Row> join(String table1Name, String table2Name, String joinColumn) throws Exception {
        Table table1 = getTable(table1Name);
        Table table2 = getTable(table2Name);
        
        List<Row> result = new ArrayList<>();
        for (Row row1 : table1.getRows()) {
            Object joinValue = row1.get(joinColumn);
            Map<String, Object> condition = new HashMap<>();
            condition.put(joinColumn, joinValue);
            
            List<Row> matchingRows = table2.select(condition);
            for (Row row2 : matchingRows) {
                Map<String, Object> joinedData = new HashMap<>();
                row1.getData().forEach((k, v) -> joinedData.put(table1Name + "." + k, v));
                row2.getData().forEach((k, v) -> joinedData.put(table2Name + "." + k, v));
                result.add(new Row(joinedData));
            }
        }
        return result;
    }

    public Set<String> getTableNames() {
        return tables.keySet();
    }
}
