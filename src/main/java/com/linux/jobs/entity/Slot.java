package com.linux.jobs.entity;

import org.jboss.logging.Logger;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Converter(autoApply = true)
public enum Slot implements AttributeConverter<Slot, String> {
    ONE("One"), TWO("Two"), THREE("Three"), MANY("Many"), NONE("None");
    private static final Logger l = Logger.getLogger(Slot.class);

    public final String dbData;
    Slot(String dbData) {
        this.dbData = dbData;
    }


    public static String asString(Slot slot) {
        return slot == null ? "" : slot.dbData;
    }

    public static Slot fromString( String value) {
        try {
            return Slot.valueOf(value);
        } catch (IllegalArgumentException iae) {
            if(value != null && !"".equals(value)) {
                l.warnv(iae, "Unknown value for enum Slot, {0}", value);
            }
        }

        return NONE;
    }

    @Override
    public String convertToDatabaseColumn(Slot attribute) {
        return asString(attribute);
    }

    @Override
    public Slot convertToEntityAttribute(String dbData) {
        return fromString(dbData);
    }
}