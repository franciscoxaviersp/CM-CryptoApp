package cm.homework.cryptoapp.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import cm.homework.cryptoapp.CoinListAdapter;
import cm.homework.cryptoapp.R;
import cm.homework.cryptoapp.viewmodels.CoinViewModel;
import cm.homework.cryptoapp.workers.UpdateDBWorker;

public class MainActivity extends AppCompatActivity {

    private CoinViewModel mCoinViewModel;
    private Handler APICallHandler;
    private APICallRunnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APICallHandler = new Handler();

        runnable = new APICallRunnable();
        APICallHandler.postDelayed(runnable,10);

        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        final CoinListAdapter adapter = new CoinListAdapter(new CoinListAdapter.CoinDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCoinViewModel = new ViewModelProvider(this).get(CoinViewModel.class);

        mCoinViewModel.getAllCoins().observe(this, coins -> {
            // Update the cached copy of the coins in the adapter.
            adapter.submitList(coins);
        });



    }

    private class APICallRunnable implements Runnable{

        @Override
        public void run(){
            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(UpdateDBWorker.class).addTag("UpdateDBWorker").build();
            WorkManager.getInstance(getApplicationContext()).enqueue(oneTimeWorkRequest);

            APICallHandler.postDelayed(this,30000);
        }
    }
}