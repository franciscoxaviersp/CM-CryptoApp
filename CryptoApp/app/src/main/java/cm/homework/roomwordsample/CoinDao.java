package cm.homework.roomwordsample;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CoinDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Coin coin);

    @Query("DELETE FROM coin_table")
    void deleteAll();

    @Query("SELECT * FROM coin_table ORDER BY symbol ASC")
    LiveData<List<Coin>> getAscCoins();
}
