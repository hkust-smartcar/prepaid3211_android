package dcheungaa.prepaid3211;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import dcheungaa.prepaid3211.Daos.HistoryDao;
import dcheungaa.prepaid3211.Records.History;

/**
 * Created by Daniel on 6/2/2018.
 * History Repo
 */

public class HistoryRepo {
    private HistoryDao historyDao;
    private LiveData<List<History>> allHistories;

    public HistoryRepo(Application application) {
        MyDatabase db = MyDatabase.getDatabase(application);
        historyDao = db.historyDao();
        allHistories = historyDao.all();
    }

    public LiveData<List<History>> getAllHistories() {
        return allHistories;
    }

    public void insert(History history) {
        new AsyncInsertHistoriesTask(historyDao).execute(history);
    }

    private static class AsyncInsertHistoriesTask extends AsyncTask<History, Void, Void> {

        private HistoryDao asyncHistoryDao;

        AsyncInsertHistoriesTask(HistoryDao historyDao) {
            asyncHistoryDao = historyDao;
        }

        @Override
        protected Void doInBackground(final History... histories) {
            asyncHistoryDao.insert(histories[0]);
            return null;
        }
    }
}
