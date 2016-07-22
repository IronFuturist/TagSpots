package com.megliosolutions.pobail.Objects;

/**
 * Created by Meglio on 7/20/16.
 */
public class TagProperty {
    public String propertyKey;
    public String propertyValue;

    public TagProperty() {
    }

    public TagProperty(String propertyKey, String propertyValue) {
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
