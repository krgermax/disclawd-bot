package org.clawd.data;

public abstract class DataObject {
    private final int id;
    private final String name;
    private final String desc;

    public DataObject(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return desc;
    }
}
