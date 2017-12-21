package fr.yaro.learninglauncher;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.time.Instant;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by GrosK on 27/11/2017.
 */

@Entity(tableName = "event",
        foreignKeys = @ForeignKey(entity = AppModel.class,
        parentColumns = "uid",
        childColumns = "appId",
        onDelete = CASCADE))
public class UsageEvent {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private Instant date;

    @ColumnInfo
    private int appId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    @Override
    public String toString() {
        return "UsageEvent{" +
                "date=" + date +
                ", appId=" + appId +
                '}';
    }
}
