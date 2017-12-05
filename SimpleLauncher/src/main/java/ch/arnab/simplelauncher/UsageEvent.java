package ch.arnab.simplelauncher;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

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
    @PrimaryKey
    @ColumnInfo
    private Date date;

    @ColumnInfo
    private int appId;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
