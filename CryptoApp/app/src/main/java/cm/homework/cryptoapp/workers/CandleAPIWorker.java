package cm.homework.cryptoapp.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cm.homework.cryptoapp.db.CandleDao;
import cm.homework.cryptoapp.db.CandleRoomDatabase;
import cm.homework.cryptoapp.models.Candle;

public class CandleAPIWorker extends Worker {
    public static final String TAG = "Candle Worker";

    public CandleAPIWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        CandleDao candleDao = CandleRoomDatabase.getDatabase(getApplicationContext()).candleDao();
        candleDao.deleteAll();

        String period = getInputData().getString("period");
        String symbol = getInputData().getString("symbol");
        Log.d("Candle Worker", "Retriving candles for pair: "+symbol+", period: "+period);

        String res = "";
        try{
            URL url = new URL("https://api.binance.com/api/v3/klines?symbol="+symbol.toUpperCase()+"&interval="+period+"&limit=100");
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
                Log.e(TAG,"Failed to connect with Binance API");
            }
            Log.d(TAG,"Successfully retrieved candle info from Binance API");
        }catch(Exception e){
            Log.e(TAG,"ERROR! Error updating repository/database with Binance API info");
        }

        String[] items = res.substring(1,res.length()-1).replaceAll("\\[", "").split("\\]");

        for(int i=0;i<items.length;i++ ){
            String temp;
            if (i!=0) temp = items[i].substring(1);
            else temp = items[i];
            String[] items2 = temp.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

            Candle c = new Candle(i,Float.parseFloat(items2[2].replaceAll("\"","")),Float.parseFloat(items2[3].replaceAll("\"","")),Float.parseFloat(items2[1].replaceAll("\"","")),Float.parseFloat(items2[4].replaceAll("\"","")));

            candleDao.insert(c);
        }

        return Result.success();
    }
}
