package fr.yaro.learninglauncher;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.time.Instant;
import java.util.List;

/**
 * Created by GrosK on 27/11/2017.
 */

@Dao
public interface AppModelDao {
    @Query("SELECT * from app")
    List<AppModel> loadAppModels();

    @Insert
    void insert(AppModel appModel);

    @Update
    void update(AppModel... appModels);

    @Delete
    void delete(AppModel... appModels);

    @Query("SELECT * from app where app.uid = :uid limit 1")
    List<AppModel> findAppModelsByUid(int uid);

    @Query("SELECT app.label, app.uid, windowQuery.scoreWindow, dowQuery.scoreDOW, allQuery.scoreAll, (ifnull(windowQuery.scoreWindow, 0)*5 + ifnull(dowQuery.scoreDOW,0)*2 + ifnull(allQuery.scoreAll,0))/8 as score, :referenceDate as date\n" +
            "FROM\n" +
            "    app\n" +
            "    LEFT JOIN (\n" +
            "        SELECT appId, CAST(COUNT(*) as float) / (\n" +
            "            SELECT COUNT(*) as nb FROM event where time(event.date, 'unixepoch') >= time(:referenceDate,'unixepoch', '-30 minutes') and time(event.date, 'unixepoch') <= time(:referenceDate,'unixepoch','+30 minutes')\n" +
            "        ) as scoreWindow, :referenceDate as date FROM event where time(event.date, 'unixepoch') >= time(:referenceDate,'unixepoch', '-30 minutes') and time(event.date, 'unixepoch') <= time(:referenceDate,'unixepoch', '+30 minutes')\n" +
            "        group by appId\n" +
            "    ) as windowQuery on windowQuery.appId = app.uid and windowQuery.date = :referenceDate\n" +
            "    LEFT JOIN (\n" +
            "        SELECT appId, CAST(COUNT(*) as float) / (\n" +
            "            SELECT COUNT(*) FROM event where strftime('%w', event.date, 'unixepoch') = strftime('%w',:referenceDate, 'unixepoch')\n" +
            "        ) as scoreDOW, :referenceDate as date FROM event where strftime('%w', event.date, 'unixepoch') = strftime('%w',:referenceDate, 'unixepoch')\n" +
            "        group by appId\n" +
            "    ) as dowQuery on dowQuery.appId = app.uid and dowQuery.date = :referenceDate\n" +
            "    LEFT JOIN (\n" +
            "        SELECT appId, CAST(COUNT(*) as float) / (\n" +
            "            SELECT COUNT(*) FROM event\n" +
            "        ) as scoreAll, :referenceDate as date FROM event\n" +
            "        group by appId\n" +
            "    ) as allQuery on allQuery.appId = app.uid and allQuery.date = :referenceDate\n" +
            "    order by score DESC, label, uid ASC")
    List<AppModel> getAppsWithUsage(Instant referenceDate);
}
