package cm.homework.roomwordsample;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainActivity extends AppCompatActivity {

    private CoinViewModel mCoinViewModel;
    private Handler APICallHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(UpdateDBWorker.class).addTag("UpdateDBWorker").build();
        WorkManager.getInstance(getApplicationContext()).enqueue(oneTimeWorkRequest);
        */

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
            Log.i("asd","AAAAAAAAAAAAAAAAAAAAA");
            String res = "";
            try{
                URL url = new URL("https://api.binance.com/api/v3/ticker/24hr");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();


                if (responseCode == HttpURLConnection.HTTP_OK) {
                    StringBuilder response;
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()))) {
                        response = new StringBuilder();
                        String readLine;
                        while ((readLine = in.readLine()) != null) {
                            response.append(readLine);
                        }
                    }
                    // print result
                    res = response.toString();
                    //GetAndPost.POSTRequest(response.toString());
                } else {
                    Log.e("e","Failed to connect with Binance API");
                }
                Log.i("i","Successfully retrieved coin pairs info from Binance API");
            }catch(Exception e){
                Log.e("e","ERROR! Error updating repository/database with Binance API info");
            }

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Coin[] coins = null;

            try {
                coins = objectMapper.readValue(res, Coin[].class);

            } catch (JsonProcessingException ex) {
                Log.e("e","Error processing JSON to object");
            }
            List<Coin> coinList = new ArrayList(Arrays.asList(coins));
            List<Coin> finalList = new ArrayList();
            System.out.println(coinList.size());
            for(int i=0; i<coinList.size(); i++) {
                Coin temp = coinList.get(i);

                if (temp.getSymbol().endsWith("EUR")) {
                    finalList.add(temp);
                }
            }

            CoinDao coinDao = CoinRoomDatabase.getDatabase(getApplicationContext()).coinDao();

            for(int i = 0; i< finalList.size();i++){
                //Log.i("i",finalList.get(i).getSymbol());
                coinDao.insert(finalList.get(i));
            }

            Collections.sort(finalList);
            coinList = finalList;
            APICallHandler.postDelayed(this,30000);
        }
    }
}