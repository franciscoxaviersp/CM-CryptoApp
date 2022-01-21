package cm.homework.cryptoapp.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import cm.homework.cryptoapp.models.Coin;
import cm.homework.cryptoapp.CoinListAdapter;
import cm.homework.cryptoapp.CoinViewModel;
import cm.homework.cryptoapp.R;
import cm.homework.cryptoapp.workers.UpdateDBWorker;

public class MainActivity extends AppCompatActivity {

    private CoinViewModel mCoinViewModel;
    private Handler APICallHandler;
    private APICallRunnable runnable;

    ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            });

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.d("LOGIN", "SUCESS");

        } else {
            Log.d("LOGIN", "FAILED");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);

        APICallHandler = new Handler();
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
    @Override
    public void onStart(){
        super.onStart();
        runnable = new APICallRunnable();
        APICallHandler.postDelayed(runnable,10);
    }

    @Override
    public void onDestroy(){
        Log.d("Main Activity","Stopped API call runnable.");
        APICallHandler.removeCallbacksAndMessages(runnable);
        super.onDestroy();
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