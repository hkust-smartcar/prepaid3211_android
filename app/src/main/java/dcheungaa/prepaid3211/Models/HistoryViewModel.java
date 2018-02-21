package dcheungaa.prepaid3211.Models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import dcheungaa.prepaid3211.HistoryRepo;
import dcheungaa.prepaid3211.Records.History;

/**
 * Created by Daniel on 7/2/2018.
 * History View Model
 */

public class HistoryViewModel extends AndroidViewModel {

    private HistoryRepo historyRepo;
    private LiveData<List<History>> allHistories;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        historyRepo = new HistoryRepo(application);
        allHistories = historyRepo.getAllHistories();
    }

    public LiveData<List<History>> getAllHistories() {
        return allHistories;
    }

    public void insert(History history) {
        historyRepo.insert(history);
    }
}
