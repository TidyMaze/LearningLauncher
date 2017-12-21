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

    @Query("WITH raw AS (\n" +
            "\tSELECT app.label, app.uid, ifnull(scoreWindow, 0) as scoreWindow, ifnull(dowQuery.scoreDOW,0) as scoreDOW, ifnull(scoreAll,0) as scoreAll, (ifnull(scoreWindow, 0)*5 + ifnull(scoreDOW,0)*2 + ifnull(scoreAll,0))/8 as score, :referenceDate as date\n" +
            "\tFROM\n" +
            "\t\tapp\n" +
            "\t\tLEFT JOIN (\n" +
            "\t\t\tSELECT appId, CAST(COUNT(*) as float) / (\n" +
            "\t\t\t\tSELECT COUNT(*) as nb FROM event where time(event.date, 'unixepoch') >= time(:referenceDate,'unixepoch', '-30 minutes') and time(event.date, 'unixepoch') <= time(:referenceDate,'unixepoch','+30 minutes')\n" +
            "\t\t\t) as scoreWindow, :referenceDate as date FROM event where time(event.date, 'unixepoch') >= time(:referenceDate,'unixepoch', '-30 minutes') and time(event.date, 'unixepoch') <= time(:referenceDate,'unixepoch', '+30 minutes')\n" +
            "\t\t\tgroup by date, appId\n" +
            "\t\t) as windowQuery on windowQuery.appId = app.uid and windowQuery.date = :referenceDate\n" +
            "\t\tLEFT JOIN (\n" +
            "\t\t\tSELECT appId, CAST(COUNT(*) as float) / (\n" +
            "\t\t\t\tSELECT COUNT(*) FROM event where strftime('%w', event.date, 'unixepoch') = strftime('%w',:referenceDate, 'unixepoch')\n" +
            "\t\t\t) as scoreDOW, :referenceDate as date FROM event where strftime('%w', event.date, 'unixepoch') = strftime('%w',:referenceDate, 'unixepoch')\n" +
            "\t\t\tgroup by date, appId\n" +
            "\t\t) as dowQuery on dowQuery.appId = app.uid and dowQuery.date = :referenceDate\n" +
            "\t\tLEFT JOIN (\n" +
            "\t\t\tSELECT appId, CAST(COUNT(*) as float) / (\n" +
            "\t\t\t\tSELECT COUNT(*) FROM event\n" +
            "\t\t\t) as scoreAll, :referenceDate as date FROM event\n" +
            "\t\t\tgroup by date, appId\n" +
            "\t\t) as allQuery on allQuery.appId = app.uid and allQuery.date = :referenceDate\n" +
            ")\n" +
            "SELECT * FROM raw\n" +
            "WHERE raw.uid in (\n" +
            "\tSELECT uid FROM raw\n" +
            "\tgroup BY uid\n" +
            "\torder by avg(score) desc\n" +
            "\tLIMIT 10\n" +
            ")\n" +
            "order by score DESC, label, uid ASC")
    List<AppModel> getAppsWithUsage(Instant referenceDate);
}
