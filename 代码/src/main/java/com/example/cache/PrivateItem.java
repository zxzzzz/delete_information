package com.example.cache;

import java.io.Serializable;

/**
 * Created by zx on 16-9-19.
 */
public class PrivateItem implements Serializable {
    String name;
    String value;
    String attributeName;
    String attributeValue;

    public PrivateItem(String name, String value, String attributeName, String attributeValue) {
        super();
        this.name = name;
        this.value = value;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    public PrivateItem() {
        super();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
}
