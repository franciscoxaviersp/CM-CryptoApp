package cm.homework.cryptoapp.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import cm.homework.cryptoapp.models.Candle;

public class CandleRepository {

    private CandleDao candleDao;
    private LiveData<List<Candle>> allCandles;

    public CandleRepository(Application application) {
        CandleRoomDatabase db = CandleRoomDatabase.getDatabase(application);
        candleDao = db.candleDao();
        allCandles = candleDao.getIdAscCandlesLive();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Candle>> getAllCandles() {
        return allCandles;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Candle candle) {
        CoinRoomDatabase.databaseWriteExecutor.execute(() -> {
            candleDao.insert(candle);
        });
    }
}


