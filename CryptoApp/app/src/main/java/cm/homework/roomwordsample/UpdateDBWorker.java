package cm.homework.roomwordsample;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdateDBWorker extends Worker {

    private static final String PROGRESS = "PROGRESS";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Coin> coinList = new ArrayList<>();

    public UpdateDBWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        setProgressAsync(new Data.Builder().putInt(PROGRESS, 1).build());
    }

    @Override
    public Result doWork(){
        try{
            Log.i("work","Doing work");
            getCoins();
            newWorkRequest();
            setProgressAsync(new Data.Builder().putInt(PROGRESS, 100).build());
            return Result.success();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("e","Failed to update db");
            return Result.failure();
        }
    }

    private void newWorkRequest(){
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(UpdateDBWorker.class).setInitialDelay(1, TimeUnit.MINUTES).addTag("UpdateDBWorker").build();
        WorkManager.getInstance(getApplicationContext()).enqueue(oneTimeWorkRequest);
    }

    void getCoins(){

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
        }catch(IOException e){
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
    }
}
