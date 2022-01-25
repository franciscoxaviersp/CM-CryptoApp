package cm.homework.cryptoapp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.homework.cryptoapp.NotificationTask;
import cm.homework.cryptoapp.R;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import cm.homework.cryptoapp.R;
import cm.homework.cryptoapp.TopUpDialog;

public class wallet extends AppCompatActivity {

    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    ArrayAdapter<String> adapter;
    public Map<String, Object> currencies;
    TextView money;
    FirebaseUser user;
    FirebaseFirestore db;
    Spinner spinner;
    String token;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_wallet);
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("token", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                // Get new FCM registration token
                token = task.getResult();

                // Log and toast
                Log.d("token", token);

            }
        });

        TextView name = findViewById(R.id.name);
        name.setText("Welcome, " + user.getDisplayName());

        TextView wallet_id = findViewById(R.id.walletid);
        wallet_id.setText("Your personal QR Code:");

        ImageView qrcode = findViewById(R.id.qrcode);
        qrgEncoder = new QRGEncoder(user.getUid(), null, QRGContents.Type.TEXT, 5000);
        qrcode.setImageBitmap(qrgEncoder.getBitmap());

        TextView funds = findViewById(R.id.funds);
        funds.setText("Your Available Funds:");

        db = FirebaseFirestore.getInstance();
        spinner = findViewById(R.id.spinner);
        List<String> list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        money = findViewById(R.id.money);
        Button trs = (Button) findViewById(R.id.transferbtn1);
        trs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Ahah", "ahah");
                transferact();
            }
        });

        get_money();

        SpinnerActivity spin = new SpinnerActivity();
        spinner.setOnItemSelectedListener(spin);
        Button top = findViewById(R.id.topupbtn);
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopUpDialog tp = new TopUpDialog(wallet.this);
                tp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tp.show();
                tp.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                       get_money();
                    }
                });

            }
        });



    }

    public void transferact(){
        Intent intent = new Intent(this, TransferActivity.class);
        startActivity(intent);
    }


    private void get_money(){
        adapter.clear();
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot result = task.getResult();
                Log.d("Result", result.toString());
                if (result.getData() != null){
                    currencies = result.getData();
                    for (Map.Entry<String, Object> entry : currencies.entrySet()) {
                        adapter.add(entry.getKey());
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    currencies = new HashMap<String, Object>();
                    currencies.put("EUR", 0);
                    db.collection("users").document(user.getUid()).set(currencies);
                    adapter.add("EUR");
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    private void update_money(){
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot result = task.getResult();
                Log.d("Result", result.toString());
                if (result.getData() != null) {
                    currencies = result.getData();
                    money.setText(currencies.get(spinner.getSelectedItem().toString()).toString());
                }
            }
        });
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            update_money();
            String currency = parent.getItemAtPosition(pos).toString();
            money.setText(currencies.get(currency).toString());
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

}
