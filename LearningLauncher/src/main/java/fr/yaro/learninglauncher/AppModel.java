package fr.yaro.learninglauncher;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * @credit http://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */

@Entity(tableName = "app")
public class AppModel {
    @Ignore
    private Context mContext;

    @Ignore
    private ApplicationInfo mInfo;

    @ColumnInfo(name="label")
    private String mAppLabel;

    @Ignore
    private Drawable mIcon;

    @Ignore
    private boolean mMounted;

    @Ignore
    private File mApkFile;

    @PrimaryKey
    @ColumnInfo(name = "uid")
    private int mAppId;

    private float scoreWindow;
    private float scoreDOW;
    private float scoreAll;


    private float score;

    public AppModel(Context context, ApplicationInfo info) {
        mContext = context;
        mInfo = info;

        mApkFile = new File(info.sourceDir);
    }

    public AppModel(String mAppLabel, int mAppId) {
        this.mAppLabel = mAppLabel;
        this.mAppId = mAppId;
    }

    public ApplicationInfo getAppInfo() {
        return mInfo;
    }

    public String getApplicationPackageName() {
        return getAppInfo().packageName;
    }

    public String getLabel() {
        return mAppLabel;
    }

    public Drawable getIcon() {
        if (mIcon == null) {
            if (mApkFile.exists()) {
                mIcon = mInfo.loadIcon(mContext.getPackageManager());
                return mIcon;
            } else {
                mMounted = false;
            }
        } else if (!mMounted) {
            // If the app wasn't mounted but is now mounted, reload
            // its icon.
            if (mApkFile.exists()) {
                mMounted = true;
                mIcon = mInfo.loadIcon(mContext.getPackageManager());
                return mIcon;
            }
        } else {
            return mIcon;
        }

        return mContext.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
    }


    void loadLabel(Context context) {
        if (mAppLabel == null || !mMounted) {
            if (!mApkFile.exists()) {
                mMounted = false;
                mAppLabel = mInfo.packageName;
                mAppId = mInfo.uid;
            } else {
                mMounted = true;
                CharSequence label = mInfo.loadLabel(context.getPackageManager());
                mAppLabel = label != null ? label.toString() : mInfo.packageName;
                mAppId = mInfo.uid;
            }
        }
    }

    public String getMAppLabel() {
        return mAppLabel;
    }

    public void setMAppLabel(String mAppLabel) {
        this.mAppLabel = mAppLabel;
    }

    public int getMAppId() {
        return mAppId;
    }

    public void setMAppId(int mAppId) {
        this.mAppId = mAppId;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getScoreWindow() {
        return scoreWindow;
    }

    public void setScoreWindow(float scoreWindow) {
        this.scoreWindow = scoreWindow;
    }

    public float getScoreDOW() {
        return scoreDOW;
    }

    public void setScoreDOW(float scoreDOW) {
        this.scoreDOW = scoreDOW;
    }

    public float getScoreAll() {
        return scoreAll;
    }

    public void setScoreAll(float scoreAll) {
        this.scoreAll = scoreAll;
    }

    @Override
    public String toString() {
        return "AppModel{" +
                "mAppLabel='" + mAppLabel + '\'' +
                ", mAppId=" + mAppId +
                ", scoreWindow=" + String.format("%.2f", scoreWindow) +
                ", scoreDOW=" + String.format("%.2f", scoreDOW) +
                ", scoreAll=" + String.format("%.2f", scoreAll) +
                ", score=" + String.format("%.2f", score) +
                '}';
    }
}
