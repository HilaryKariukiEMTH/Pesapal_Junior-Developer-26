public class Column {
    private final String name;
    private final DataType type;
    private final boolean isPrimaryKey;
    private final boolean isUnique;

    public Column(String name, DataType type, boolean isPrimaryKey, boolean isUnique) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
        this.isUnique = isUnique;
    }

    public String getName() { return name; }
    public DataType getType() { return type; }
    public boolean isPrimaryKey() { return isPrimaryKey; }
    public boolean isUnique() { return isUnique; }
}
