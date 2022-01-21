package cm.homework.cryptoapp.activities;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import cm.homework.cryptoapp.R;
import cm.homework.cryptoapp.db.CandleDao;
import cm.homework.cryptoapp.db.CandleRepository;
import cm.homework.cryptoapp.db.CandleRoomDatabase;
import cm.homework.cryptoapp.db.CoinRepository;
import cm.homework.cryptoapp.models.Candle;
import cm.homework.cryptoapp.models.Coin;
import cm.homework.cryptoapp.workers.CandleAPIWorker;

public class CoinActivity extends AppCompatActivity {

    private TextView askPriceView;
    private TextView priceChangeView;
    private TextView priceChangePercentView;
    private TextView volumeView;
    private TextView volumeEurView;
    private TextView volumeTitleView;
    private Button period_button;
    private String symbol;
    private double askPrice;
    private double priceChange;
    private double priceChangePercent;
    private double volume;

    List<Candle> candles;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        period_button = this.findViewById(R.id.period_button);
        period_button.setText("1 hour");

        Bundle b = getIntent().getExtras();
        symbol = b.getString("symbol");
        askPrice = b.getDouble("askPrice");
        priceChange = b.getDouble("priceChange");
        priceChangePercent = b.getDouble("priceChangePercent");
        volume = b.getDouble("volume");

        String volumeEur = formatValue(askPrice*volume);

        askPriceView = this.findViewById(R.id.priceView);
        priceChangeView = this.findViewById(R.id.priceChange);
        priceChangePercentView = this.findViewById(R.id.priceChangePercent);
        volumeView = this.findViewById(R.id.volume);
        volumeEurView = this.findViewById(R.id.volumeEur);
        volumeTitleView = this.findViewById(R.id.volume_title);

        String text1 = symbol.substring(0,symbol.length()-3);
        String text2 = symbol.substring(symbol.length()-3,symbol.length());

        volumeTitleView.setText("Volume 24h("+text1+"):");

        SpannableString span1 = new SpannableString(text1);
        span1.setSpan(new AbsoluteSizeSpan(30,true), 0, text1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(text2);
        span2.setSpan(new AbsoluteSizeSpan(20,true), 0, text2.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        CharSequence finalText = TextUtils.concat(span1, "/", span2);
        setTitle(finalText);
        askPriceView.setText(Double.toString(askPrice)+"€");
        if(priceChange>0) {
            priceChangeView.setText("+"+Double.toString(priceChange)+"€");
            priceChangeView.setTextColor(getResources().getColor(R.color.colorAccent,getApplicationContext().getTheme()));
        } else {
            priceChangeView.setText(Double.toString(priceChange)+"€");
            priceChangeView.setTextColor(Color.RED);
        }

        if(priceChangePercent>0) {
            priceChangePercentView.setText("+"+Double.toString(priceChangePercent)+"%");
            priceChangePercentView.setTextColor(getResources().getColor(R.color.colorAccent,getApplicationContext().getTheme()));
        } else {
            priceChangePercentView.setText(Double.toString(priceChangePercent)+"%");
            priceChangePercentView.setTextColor(Color.RED);
        }
        volumeView.setText(Double.toString(volume));
        volumeEurView.setText(volumeEur);

        CoinRepository mRepository = new CoinRepository(getApplication());
        mRepository.getAllCoins().observe(this, coins -> {
            for(Coin c : coins){
                if(c.getSymbol().equalsIgnoreCase(symbol)){
                    askPriceView.setText(Double.toString(c.getAskPrice())+"€");
                    if(priceChange>0) {
                        priceChangeView.setText("+"+Double.toString(c.getPriceChange())+"€");
                        priceChangeView.setTextColor(getResources().getColor(R.color.colorAccent,getApplicationContext().getTheme()));
                    } else {
                        priceChangeView.setText(Double.toString(c.getPriceChange())+"€");
                        priceChangeView.setTextColor(Color.RED);
                    }

                    if(priceChangePercent>0) {
                        priceChangePercentView.setText("+"+Double.toString(c.getPriceChangePercent())+"%");
                        priceChangePercentView.setTextColor(getResources().getColor(R.color.colorAccent,getApplicationContext().getTheme()));
                    } else {
                        priceChangePercentView.setText(Double.toString(c.getPriceChangePercent())+"%");
                        priceChangePercentView.setTextColor(Color.RED);
                    }
                    volumeView.setText(Double.toString(c.getVolume()));

                    volumeEurView.setText(formatValue(c.getVolume()*c.getAskPrice()));
                }
            }
        });

        period_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(CoinActivity.this, period_button);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        period_button.setText(item.getTitle());
                        drawCandleChart(symbol,getPeriodId(item.getTitle().toString()));
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        drawCandleChart(symbol,"1h");

    }

    public static String formatValue(double value) {
        int power;
        String suffix = " kmbt";
        String formattedNumber = "";

        NumberFormat formatter = new DecimalFormat("#,###.#");
        power = (int)StrictMath.log10(value);
        value = value/(Math.pow(10,(power/3)*3));
        formattedNumber=formatter.format(value);
        formattedNumber = formattedNumber + suffix.charAt(power/3);
        return formattedNumber.length()>4 ?  formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber;
    }

    private void drawCandleChart(String symbol, String period){

        CandleStickChart candleStickChart = findViewById(R.id.candle_stick_chart);
        candleStickChart.setHighlightPerDragEnabled(true);

        candleStickChart.setDrawBorders(true);
        candleStickChart.getDescription().setText("");

        candleStickChart.setBorderColor(getResources().getColor(R.color.lightGray,getApplicationContext().getTheme()));

        YAxis yAxis = candleStickChart.getAxisLeft();
        YAxis rightAxis = candleStickChart.getAxisRight();
        yAxis.setDrawGridLines(true);
        rightAxis.setDrawGridLines(true);
        candleStickChart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = candleStickChart.getXAxis();

        xAxis.setDrawGridLines(true);// disable x axis grid lines
        xAxis.setDrawLabels(false);
        rightAxis.setTextColor(Color.BLACK);
        yAxis.setDrawLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);

        Legend l = candleStickChart.getLegend();
        l.setEnabled(false);

        CandleRepository mRepository = new CandleRepository(getApplication());
        CandleDao candleDao = CandleRoomDatabase.getDatabase(getApplicationContext()).candleDao();
        candleDao.deleteAll();

        mRepository.getAllCandles().observe(this, c -> {
            // Update the cached copy of the candles
            saveCandles(c);
            if (candles.size() == 100){
                ArrayList<CandleEntry> candleEntries = new ArrayList<CandleEntry>();

                for(Candle ca : candles){
                    CandleEntry entry = new CandleEntry(ca.getId(),ca.getHigh(),ca.getLow(),ca.getOpen(),ca.getClose());
                    candleEntries.add(entry);
                }
                CandleDataSet set1 = new CandleDataSet(candleEntries, "DataSet 1");

                set1.setColor(Color.rgb(80, 80, 80));
                set1.setShadowColor(getResources().getColor(R.color.lightGrayMore,getApplicationContext().getTheme()));
                set1.setShadowWidth(0.8f);
                set1.setDecreasingColor(getResources().getColor(R.color.Red,getApplicationContext().getTheme()));
                set1.setDecreasingPaintStyle(Paint.Style.FILL);
                set1.setIncreasingColor(getResources().getColor(R.color.colorAccent,getApplicationContext().getTheme()));
                set1.setIncreasingPaintStyle(Paint.Style.FILL);
                set1.setNeutralColor(Color.LTGRAY);
                set1.setDrawValues(false);


                // create a data object with the datasets
                CandleData data = new CandleData(set1);

                // set data
                candleStickChart.setData(data);
                candleStickChart.invalidate();
            }
        });

        Data data = new Data.Builder().putString("period",period).putString("symbol",symbol).build();
        OneTimeWorkRequest candleWorkRequest = new OneTimeWorkRequest.Builder(CandleAPIWorker.class)
                .setInputData(data).build();

        WorkManager workManager = WorkManager.getInstance(CoinActivity.this);
        workManager.enqueue(candleWorkRequest);

        workManager.getWorkInfoByIdLiveData(candleWorkRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().equals("SUCCEEDED")) {

                            ArrayList<CandleEntry> candleEntries = new ArrayList<CandleEntry>();

                            for(Candle c : candles){
                                CandleEntry entry = new CandleEntry(c.getId(),c.getHigh(),c.getLow(),c.getOpen(),c.getClose());
                                candleEntries.add(entry);
                            }
                            CandleDataSet set1 = new CandleDataSet(candleEntries, "DataSet 1");

                            set1.setColor(Color.rgb(80, 80, 80));
                            set1.setShadowColor(getResources().getColor(R.color.lightGrayMore,getApplicationContext().getTheme()));
                            set1.setShadowWidth(0.8f);
                            set1.setDecreasingColor(getResources().getColor(R.color.Red,getApplicationContext().getTheme()));
                            set1.setDecreasingPaintStyle(Paint.Style.FILL);
                            set1.setIncreasingColor(getResources().getColor(R.color.colorAccent,getApplicationContext().getTheme()));
                            set1.setIncreasingPaintStyle(Paint.Style.FILL);
                            set1.setNeutralColor(Color.LTGRAY);
                            set1.setDrawValues(false);

                            // create a data object with the datasets
                            CandleData data = new CandleData(set1);

                            // set data
                            candleStickChart.setData(data);
                            candleStickChart.invalidate();
                        }
                    }
                });


    }

    private String getPeriodId(String title){
        String id = "";
        switch (title){
            case "1 minute":
                id = "1m";
                break;
            case "3 minutes":
                id = "3m";
                break;
            case "5 minutes":
                id = "5m";
                break;
            case "15 minutes":
                id = "15m";
                break;
            case "30 minutes":
                id = "30m";
                break;
            case "1 hour":
                id = "1h";
                break;
            case "2 hours":
                id = "2h";
                break;
            case "4 hours":
                id = "4h";
                break;
            case "6 hours":
                id = "6h";
                break;
            case "":
                id = "8h";
                break;
            case "12 hours":
                id = "12h";
                break;
            case "1 day":
                id = "1d";
                break;
            case "3 days":
                id = "3d";
                break;
            case "1 week":
                id = "1w";
                break;
            default:
                id = "1h";
                break;
        }
        return id;
    }

    private void saveCandles(List<Candle> c){
        candles = c;
    }

}
