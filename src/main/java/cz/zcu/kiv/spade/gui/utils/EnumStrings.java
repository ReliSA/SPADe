package cz.zcu.kiv.spade.gui.utils;

public class EnumStrings {

    private String className;
    private String attributeName;
    private String collectionName;

    public EnumStrings(String className, String attributeName, String collectionName) {
        this.className = className;
        this.attributeName = attributeName;
        this.collectionName = collectionName;
    }

    public String getClassName() {
        return className;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getCollectionName() {
        return collectionName;
    }
}
