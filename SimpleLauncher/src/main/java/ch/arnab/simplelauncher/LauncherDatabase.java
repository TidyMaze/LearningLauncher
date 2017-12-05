package ch.arnab.simplelauncher;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by GrosK on 27/11/2017.
 */
@TypeConverters({Converters.class})
@Database(
        entities = { AppModel.class, UsageEvent.class },
        version = 3
)
public abstract class LauncherDatabase extends RoomDatabase {
    private static final String DB_NAME = "launcherDatabase.db";
    private static LauncherDatabase sInstance;

    public abstract AppModelDao getAppModelDao();
    public abstract UsageEventDao getUsageEventDao();

    public static LauncherDatabase getInstance(Context context) {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (LauncherDatabase.class) {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(
                        context,
                        LauncherDatabase.class,
                        DB_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return sInstance;
    }
}
