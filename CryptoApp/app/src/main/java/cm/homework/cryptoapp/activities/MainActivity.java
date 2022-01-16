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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import cm.homework.cryptoapp.db.CoinDao;
import cm.homework.cryptoapp.db.CoinRoomDatabase;
import cm.homework.cryptoapp.models.Coin;
import cm.homework.cryptoapp.CoinListAdapter;
import cm.homework.cryptoapp.CoinViewModel;
import cm.homework.cryptoapp.R;
import cm.homework.cryptoapp.db.UpdateDBWorker;

public class MainActivity extends AppCompatActivity {

    private CoinViewModel mCoinViewModel;
    private Handler APICallHandler;

    ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            });


    ActivityResultLauncher<Intent> launchActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //get result
                        Intent data = result.getData();
                        Coin coin = new Coin(data.getStringExtra(NewCoinActivity.EXTRA_REPLY));
                        mCoinViewModel.insert(coin);
                    }else{
                        Toast.makeText(
                                getApplicationContext(),
                                R.string.empty_not_saved,
                                Toast.LENGTH_LONG).show();
                    }
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener( view -> {
            Intent intent = new Intent(MainActivity.this, NewCoinActivity.class);
            //launch activity with info to be changed
            launchActivity.launch(intent);
        });


    }
    @Override
    public void onStart(){
        super.onStart();
        APICallHandler.postDelayed(new APICallRunnable(),10);
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