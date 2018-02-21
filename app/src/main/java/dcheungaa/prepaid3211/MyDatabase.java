package dcheungaa.prepaid3211;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Date;

import dcheungaa.prepaid3211.Daos.HistoryDao;
import dcheungaa.prepaid3211.Records.History;
import dcheungaa.prepaid3211.Records.TypeConverters.DateTypeConverter;

/**
 * Created by Daniel on 5/2/2018.
 */

@Database(entities = {History.class}, version = 1)
@TypeConverters({DateTypeConverter.class})
public abstract class MyDatabase extends RoomDatabase {

    public abstract HistoryDao historyDao();

    private static MyDatabase INSTANCE;

    public static MyDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, "my_db").addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final HistoryDao historyDao;

        PopulateDbAsync(MyDatabase db) {
            historyDao = db.historyDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            historyDao.deleteAll();
            History history = new History("Test Item", 42, new Date());
            historyDao.insert(history);
            return null;
        }
    }

}
