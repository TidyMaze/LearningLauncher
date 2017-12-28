package fr.yaro.learninglauncher;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arnab Chakraborty
 */
public class AppsGridFragment extends GridFragment implements LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {

    AppListAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("LAUNCHER", "Creating appsGridFragment!");

        super.onActivityCreated(savedInstanceState);

        setEmptyText("No Applications");

        mAdapter = new AppListAdapter(getActivity());
        setGridAdapter(mAdapter);

        // till the data is loaded display a spinner
        setGridShown(false);

        // create the loader to load the apps list in background
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle bundle) {
        Log.d("LAUNCHER", "creating new Loader!");
        return new AppsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppModel>> loader, ArrayList<AppModel> apps) {
        List<AppModel> appModels = apps;
        mAdapter.setData(appModels);

        if (isResumed()) {
            setGridShown(true);
        } else {
            setGridShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AppModel>> loader) {
        mAdapter.setData(null);
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        final AppModel app = (AppModel) getGridAdapter().getItem(position);
        if (app != null) {
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(app.getApplicationPackageName());

            if (intent != null) {
                Log.d("LAUNCHER","Starting intent " + app.getApplicationPackageName());
                final UsageEventDao usageEventDao = LauncherDatabase.getInstance(AppsGridFragment.this.getContext()).getUsageEventDao();
                final AppModelDao appModelDao = LauncherDatabase.getInstance(AppsGridFragment.this.getContext()).getAppModelDao();
                new AddEventTask(appModelDao, usageEventDao, app, this).execute();
                startActivity(intent);
            }
        }
    }
}

class AddEventTask extends AsyncTask<Void, Void, Void>{

    private AppModelDao appModelDao;
    private AppModel app;
    private AppsGridFragment appsGridFragment;
    private UsageEventDao usageEventDao;

    AddEventTask(AppModelDao appModelDao, UsageEventDao usageEventDao, AppModel app, AppsGridFragment appsGridFragment){
        this.appModelDao = appModelDao;
        this.usageEventDao = usageEventDao;
        this.app = app;
        this.appsGridFragment = appsGridFragment;
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<AppModel> storedAppModels = appModelDao.findAppModelsByUid(app.getMAppId());
        Log.d("LAUNCHER", storedAppModels.toString());

        if (storedAppModels.isEmpty()) {
            AppModel am = new AppModel(app.getMAppLabel(), app.getMAppId());
            appModelDao.insert(am);
        }

        Instant time = Instant.now();
        UsageEvent usageEvent = new UsageEvent();
        usageEvent.setAppId(app.getMAppId());
        usageEvent.setDate(time);

        Log.d("LAUNCHER", usageEvent.toString());

        usageEventDao.insert(usageEvent);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i("LAUNCHER", "Reloading after inserted usage event");
        appsGridFragment.getLoaderManager().restartLoader(0, null, appsGridFragment);
    }
}