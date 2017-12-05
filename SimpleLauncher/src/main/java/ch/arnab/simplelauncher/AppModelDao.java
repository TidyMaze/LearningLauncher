package ch.arnab.simplelauncher;

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
public interface AppModelDao {
    @Query("SELECT * from app")
    public List<AppModel> loadAppModels();

    @Insert
    void insert(AppModel appModel);

    @Update
    void update(AppModel... appModels);

    @Delete
    void delete(AppModel... appModels);

    @Query("SELECT * from app where app.uid = :uid limit 1")
    public List<AppModel> findAppModelsByUid(int uid);

    @Query("SELECT label, uid, COUNT(*) as times, time(date/1000, 'unixepoch') as d, time('now','-30 minutes') as s, time('now','+30 minutes') as e FROM App inner join event on app.uid = event.appId  where d >= s and d <= e group by app.uid order by times DESC, label")
    public List<AppModel> getAppsWithUsage();
}
