package cm.homework.cryptoapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferActivity extends AppCompatActivity {

    String wallet_addr;
    FirebaseUser user;
    FirebaseFirestore db;
    public Map<String, Object> currencies;
    ArrayAdapter<String> adapter;
    FirebaseMessaging inst;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer2);
        getSupportActionBar().setTitle("Transfers");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LogoBlue)));



    }

    @Override
    protected void onStart(){
        super.onStart();


        ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            wallet_addr = data.getStringExtra("wallet");
                            EditText edit =  findViewById(R.id.wallet_address);
                            edit.setText(wallet_addr);

                        }
                    }
                });
        Button camera = findViewById(R.id.buttoncamera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), QRScanning.class );
                launchSomeActivity.launch(intent);

            }
        });
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Spinner spinner = findViewById(R.id.spinner);
        List<String> list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        get_money();

        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button transfer = findViewById(R.id.transferok);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText values = findViewById(R.id.value);
                double value = Double.parseDouble(values.getText().toString());
                String curr = spinner.getSelectedItem().toString();
                EditText edit = findViewById(R.id.wallet_address);
                if (Double.parseDouble(currencies.get(curr).toString()) - value >= 0) {
                    HashMap<String, Object> temp = new HashMap<>();
                    temp.put(curr, Double.parseDouble(currencies.get(curr).toString()) - value);
                    db.collection("users").document(user.getUid()).set(temp);

                    db.collection("users").document(edit.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            boolean made = false;
                            DocumentSnapshot result = task.getResult();
                            Log.d("Result", result.toString());
                            if (result.getData() != null) {
                                Map<String, Object> temp_currencies = result.getData();
                                if(temp_currencies.containsKey(curr)){
                                    double temp_value = Double.parseDouble(temp_currencies.get(curr).toString());
                                    temp_currencies.put(curr, temp_value + value);
                                    db.collection("users").document(edit.getText().toString()).set(temp_currencies).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            finish();
                                        }
                                    });
                                }else{
                                    temp_currencies.put(curr, value);
                                    db.collection("users").document(edit.getText().toString()).set(temp_currencies).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            finish();
                                        }
                                    });
                                }
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Wallet does not exist",
                                        Toast.LENGTH_LONG);
                                toast.show();

                            }

                        }
                    });
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Not enough funds",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

    }

    private void get_money(){
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

}