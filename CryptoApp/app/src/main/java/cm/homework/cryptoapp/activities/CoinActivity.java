package cm.homework.cryptoapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cm.homework.cryptoapp.R;

public class CoinActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_coin);

        Bundle b = getIntent().getExtras();
        String symbol = b.getString("symbol");

        Toast.makeText(getApplicationContext(), "Recycle click" + symbol,Toast.LENGTH_SHORT).show();
    }
}
