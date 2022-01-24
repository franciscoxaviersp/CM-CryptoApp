package cm.homework.cryptoapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cm.homework.cryptoapp.db.CandleRepository;
import cm.homework.cryptoapp.models.Candle;

public class CandleViewModel extends AndroidViewModel {
    private CandleRepository mRepository;

    private final LiveData<List<Candle>> mAllCandles;

    public CandleViewModel (Application application) {
        super(application);
        mRepository = new CandleRepository(application);
        mAllCandles = mRepository.getAllCandles();
    }

    public LiveData<List<Candle>> getAllCandles() { return mAllCandles; }

    public void insert(Candle candle) { mRepository.insert(candle); }
}
