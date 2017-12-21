package fr.yaro.learninglauncher;

import android.arch.persistence.room.TypeConverter;

import java.time.Instant;

public class Converters {
    @TypeConverter
    public Instant fromTimestamp(Long value) {
        return value == null ? null : Instant.ofEpochSecond(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Instant date) {
        if (date == null) {
            return null;
        } else {
            return date.getEpochSecond();
        }
    }
}