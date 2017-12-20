package fr.yaro.learninglauncher;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by GrosK on 27/11/2017.
 */

@Dao
public interface UsageEventDao {
    @Query("SELECT * from event")
    List<UsageEvent> loadUsageEvents();

    @Insert
    void insert(UsageEvent event);

    @Update
    void update(UsageEvent... events);

    @Delete
    void delete(UsageEvent... events);
}
