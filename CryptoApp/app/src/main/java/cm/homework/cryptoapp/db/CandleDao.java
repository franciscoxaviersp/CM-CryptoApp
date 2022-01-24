package cm.homework.cryptoapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cm.homework.cryptoapp.models.Candle;

@Dao
public interface CandleDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Candle candle);

    @Query("DELETE FROM candle_table")
    void deleteAll();

    @Query("SELECT * FROM candle_table ORDER BY id ASC")
    LiveData<List<Candle>> getIdAscCandlesLive();

    @Query("SELECT * FROM candle_table ORDER BY id ASC")
    List<Candle> getIdAscCandles();
}