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
            "\tFROM\n" +
            "\t\tapp\n" +
            "\t\tLEFT JOIN (\n" +
            "\t\t\tWITH\n" +
            "\t\t\t\tdMin AS ( SELECT time(:referenceDate, 'unixepoch', '-30 minutes') as val),\n" +
            "\t\t\t\tdMax AS ( SELECT time(:referenceDate, 'unixepoch', '+30 minutes') as val)\n" +
            "\t\t\tSELECT appId, CAST(COUNT(*) as float) / (\n" +
            "\t\t\t\tSELECT COUNT(*) as nb FROM dMin, dMax, event where time(event.date, 'unixepoch') >= dMin.val and time(event.date, 'unixepoch') <= dMax.val\n" +
            "\t\t\t) as scoreWindow, datetime(:referenceDate, 'unixepoch') as date FROM dMin, dMax, event where time(event.date, 'unixepoch') >= dMin.val and time(event.date, 'unixepoch') <= dMax.val\n" +
            "\t\t\tgroup by appId\n" +
            "\t\t) as windowQuery on windowQuery.appId = app.uid\n" +
            "\t\tLEFT JOIN (\n" +
            "\t\t\tWITH\n" +
            "\t\t\t\tcurDOW AS ( SELECT strftime('%w',:referenceDate, 'unixepoch') as val)\n" +
            "\t\t\tSELECT appId, CAST(COUNT(*) as float) / (\n" +
            "\t\t\t\tSELECT COUNT(*) FROM event, curDOW where strftime('%w', event.date, 'unixepoch') = curDOW.val\n" +
            "\t\t\t) as scoreDOW, datetime(:referenceDate, 'unixepoch') as date FROM curDOW, event where strftime('%w', event.date, 'unixepoch') = curDOW.val\n" +
            "\t\t\tgroup by appId\n" +
            "\t\t) as dowQuery on dowQuery.appId = app.uid\n" +
            "\t\tLEFT JOIN (\n" +
            "\t\t\tSELECT appId, CAST(COUNT(*) as float) / (\n" +
            "\t\t\t\tSELECT COUNT(*) FROM event\n" +
            "\t\t\t) as scoreAll, datetime(:referenceDate, 'unixepoch') as date FROM event\n" +
            "\t\t\tgroup by appId\n" +
            "\t\t) as allQuery on allQuery.appId = app.uid")
    List<AppModel> getAppsWithUsage(Instant referenceDate);
}
