package cm.homework.roomwordsample;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

class CoinRepository {

    private CoinDao coinDao;
    private LiveData<List<Coin>> allCoins;

    CoinRepository(Application application) {
        CoinRoomDatabase db = CoinRoomDatabase.getDatabase(application);
        coinDao = db.coinDao();
        allCoins = coinDao.getAscCoins();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Coin>> getAllCoins() {
        return allCoins;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Coin coin) {
        CoinRoomDatabase.databaseWriteExecutor.execute(() -> {
            coinDao.insert(coin);
        });
    }
}

