package dcheungaa.prepaid3211.Daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import dcheungaa.prepaid3211.Records.History;

/**
 * Created by Daniel on 6/2/2018.
 *
 * History data access object
 */
@Dao
public interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(History... histories);

    @Update
    public void update(History... histories);

    @Delete
    public void delete(History... histories);

    @Query("SELECT * FROM histories ORDER BY purchasedAt DESC")
    public LiveData<List<History>> all();

    @Query("SELECT * FROM histories WHERE uid = :uid LIMIT 1")
    public LiveData<List<History>> find(int uid);

    @Query("DELETE FROM histories")
    public void deleteAll();
}
