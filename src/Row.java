import java.util.*;

public class Row {
    private final Map<String, Object> data;

    public Row(Map<String, Object> data) {
        this.data = new HashMap<>(data);
    }

    public Object get(String columnName) {
        return data.get(columnName);
    }

    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
