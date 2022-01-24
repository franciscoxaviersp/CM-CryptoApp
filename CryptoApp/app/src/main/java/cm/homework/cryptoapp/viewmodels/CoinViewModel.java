package cm.homework.cryptoapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cm.homework.cryptoapp.db.CoinRepository;
import cm.homework.cryptoapp.models.Coin;

public class CoinViewModel extends AndroidViewModel {

    private CoinRepository mRepository;

    private final LiveData<List<Coin>> mAllCoins;

    public CoinViewModel (Application application) {
        super(application);
        mRepository = new CoinRepository(application);
        mAllCoins = mRepository.getAllCoins();
    }

    public LiveData<List<Coin>> getAllCoins() { return mAllCoins; }

    public void insert(Coin coin) { mRepository.insert(coin); }
}
