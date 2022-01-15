package cm.homework.cryptoapp.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import cm.homework.cryptoapp.models.Coin;

public class CoinRepository {

    private CoinDao coinDao;
    private LiveData<List<Coin>> allCoins;

    public CoinRepository(Application application) {
        CoinRoomDatabase db = CoinRoomDatabase.getDatabase(application);
        coinDao = db.coinDao();
        allCoins = coinDao.getVolAscCoins();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Coin>> getAllCoins() {
        return allCoins;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Coin coin) {
        CoinRoomDatabase.databaseWriteExecutor.execute(() -> {
            coinDao.insert(coin);
        });
    }
}

