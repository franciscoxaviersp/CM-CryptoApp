package cm.homework.roomwordsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

public class NewCoinActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";

    private EditText mEditCoinView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_coin);
        mEditCoinView = findViewById(R.id.edit_coin);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(mEditCoinView.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String coin = mEditCoinView.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, coin);
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}
