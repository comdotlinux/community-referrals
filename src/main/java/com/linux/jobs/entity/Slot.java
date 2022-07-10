package com.linux.jobs.entity;

import javax.persistence.*;

@Converter(autoApply = true)
public enum Slot implements AttributeConverter<Slot, String> {
    ONE("1"), TWO("2"), THREE("3"), MANY("Many"), NONE("None");
    public final String value;

    Slot(String value) {
        this.value = value;
    }

    @Override
    public String convertToDatabaseColumn(Slot attribute) {
        return attribute.value;
    }

    @Override
    public Slot convertToEntityAttribute(String dbData) {
        return valueOf(dbData);
    }
}