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
    List<AppModel> loadAppModels();

    @Insert
    void insert(AppModel appModel);

    @Update
    void update(AppModel... appModels);

    @Delete
    void delete(AppModel... appModels);

    @Query("SELECT * from app where app.uid = :uid limit 1")
    List<AppModel> findAppModelsByUid(int uid);

    @Query("SELECT\n" +
            "\tlabel,\n" +
            "\tuid,\n" +
            "\tifnull(scoreWindow, 0) as scoreWindow,\n" +
            "\tifnull(scoreDOW,0) as scoreDOW,\n" +
            "\tifnull(scoreAll,0) as scoreAll,\n" +
            "\t(ifnull(scoreWindow, 0) + ifnull(scoreDOW,0) + ifnull(scoreAll,0))/3 as score\n" +
            "FROM\n" +
            "\tapp\n" +
            "\tleft join (\n" +
            "\t\tSELECT\n" +
            "\t\t\tappId,\n" +
            "\t\t\tCAST(COUNT(*) as float) / (\n" +
            "\t\t\t\tSELECT nb FROM (\n" +
            "\t\t\t\t\tSELECT COUNT(*) as nb,\n" +
            "\t\t\t\t\ttime(date/1000, 'unixepoch') as d,\n" +
            "\t\t\t\t\ttime('now','-30 minutes') as s,\n" +
            "\t\t\t\t\ttime('now','+30 minutes') as e\n" +
            "\t\t\t\t\tFROM event\n" +
            "\t\t\t\t\twhere d >= s and d <= e\n" +
            "\t\t\t\t)\n" +
            "\t\t\t) as scoreWindow,\n" +
            "\t\t\ttime(date/1000, 'unixepoch') as d,\n" +
            "\t\t\ttime('now','-30 minutes') as s,\n" +
            "\t\t\ttime('now','+30 minutes') as e\n" +
            "\t\tFROM event\n" +
            "\t\twhere d >= s and d <= e\n" +
            "\t\tgroup by appId\n" +
            "\t) as windowQuery on windowQuery.appId = app.uid\n" +
            "\tleft join (\n" +
            "\t\tSELECT\n" +
            "\t\t\tappId,\n" +
            "\t\t\tCAST(COUNT(*) as float) / (\n" +
            "\t\t\t\tSELECT COUNT(*)\n" +
            "\t\t\t\tFROM event\n" +
            "\t\t\t\twhere strftime('%w', date/1000) = strftime('%w','now')\n" +
            "\t\t\t) as scoreDOW\n" +
            "\t\tFROM event\n" +
            "\t\twhere strftime('%w', date/1000) = strftime('%w','now')\n" +
            "\t\tgroup by appId\n" +
            "\t) as dowQuery on dowQuery.appId = app.uid\n" +
            "\tleft join (\n" +
            "\t\tSELECT\n" +
            "\t\t\tappId,\n" +
            "\t\t\tCAST(COUNT(*) as float) / (\n" +
            "\t\t\t\tSELECT COUNT(*)\n" +
            "\t\t\t\tFROM event\n" +
            "\t\t\t) as scoreAll\n" +
            "\t\tFROM event\n" +
            "\t\tgroup by appId\n" +
            "\t) as allQuery on allQuery.appId = app.uid\n" +
            "order by score DESC;")
    List<AppModel> getAppsWithUsage();
}
