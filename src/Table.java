import java.util.*;
import java.util.stream.Collectors;

public class Table {
    private final String name;
    private final List<Column> columns;
    private final List<Row> rows;
    private final Map<String, Map<Object, List<Row>>> indexes;

    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
        this.rows = new ArrayList<>();
        this.indexes = new HashMap<>();
        
        for (Column col : columns) {
            if (col.isPrimaryKey() || col.isUnique()) {
                indexes.put(col.getName(), new HashMap<>());
            }
        }
    }

    public void insert(Map<String, Object> values) throws Exception {
        validateRow(values);
        Row row = new Row(values);
        
        for (Column col : columns) {
            if (col.isPrimaryKey() || col.isUnique()) {
                Object value = values.get(col.getName());
                if (indexes.get(col.getName()).containsKey(value)) {
                    throw new Exception("Duplicate value for " + (col.isPrimaryKey() ? "primary" : "unique") + " key: " + col.getName());
                }
            }
        }
        
        rows.add(row);
        updateIndexes(row);
    }

    public List<Row> select(Map<String, Object> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return new ArrayList<>(rows);
        }
        
        return rows.stream()
            .filter(row -> matchesConditions(row, conditions))
            .collect(Collectors.toList());
    }

    public int update(Map<String, Object> conditions, Map<String, Object> updates) throws Exception {
        List<Row> toUpdate = select(conditions);
        for (Row row : toUpdate) {
            removeFromIndexes(row);
        }
        
        rows.removeAll(toUpdate);
        
        int count = 0;
        for (Row row : toUpdate) {
            Map<String, Object> newData = new HashMap<>(row.getData());
            newData.putAll(updates);
            insert(newData);
            count++;
        }
        return count;
    }

    public int delete(Map<String, Object> conditions) {
        List<Row> toDelete = select(conditions);
        for (Row row : toDelete) {
            removeFromIndexes(row);
        }
        rows.removeAll(toDelete);
        return toDelete.size();
    }

    private void validateRow(Map<String, Object> values) throws Exception {
        for (Column col : columns) {
            Object value = values.get(col.getName());
            if (value == null && col.isPrimaryKey()) {
                throw new Exception("Primary key cannot be null: " + col.getName());
            }
            if (value != null && !isValidType(value, col.getType())) {
                throw new Exception("Invalid type for column " + col.getName());
            }
        }
    }

    private boolean isValidType(Object value, DataType type) {
        switch (type) {
            case INT: return value instanceof Integer;
            case VARCHAR: return value instanceof String;
            case BOOLEAN: return value instanceof Boolean;
            default: return false;
        }
    }

    private boolean matchesConditions(Row row, Map<String, Object> conditions) {
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            Object rowValue = row.get(entry.getKey());
            if (!Objects.equals(rowValue, entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private void updateIndexes(Row row) {
        for (Column col : columns) {
            if (col.isPrimaryKey() || col.isUnique()) {
                Object value = row.get(col.getName());
                indexes.get(col.getName()).computeIfAbsent(value, k -> new ArrayList<>()).add(row);
            }
        }
    }

    private void removeFromIndexes(Row row) {
        for (Column col : columns) {
            if (col.isPrimaryKey() || col.isUnique()) {
                Object value = row.get(col.getName());
                Map<Object, List<Row>> index = indexes.get(col.getName());
                if (index != null) {
                    List<Row> indexedRows = index.get(value);
                    if (indexedRows != null) {
                        indexedRows.remove(row);
                        if (indexedRows.isEmpty()) {
                            index.remove(value);
                        }
                    }
                }
            }
        }
    }

    public String getName() { return name; }
    public List<Column> getColumns() { return columns; }
    public List<Row> getRows() { return new ArrayList<>(rows); }
}
