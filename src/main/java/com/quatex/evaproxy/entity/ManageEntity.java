package com.quatex.evaproxy.entity;

public class ManageEntity<T> {

    private T currentValue;
    private T newValue;

    public ManageEntity(T currentValue, T newValue) {
        this.currentValue = currentValue;
        this.newValue = newValue;
    }

    public static <T> ManageEntity<T> fromCurrentValue(T value) {
        return new ManageEntity<>(value, null);
    }

     public static <T> ManageEntity<T> fromNewValue(T value) {
        return new ManageEntity<>(null, value);
    }

    public T getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(T currentValue) {
        this.currentValue = currentValue;
    }

    public T getNewValue() {
        return newValue;
    }

    public void setNewValue(T newValue) {
        this.newValue = newValue;
    }
}
