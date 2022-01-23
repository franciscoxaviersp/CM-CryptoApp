package cm.homework.cryptoapp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class SellDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button yes;
    public TextView money;
    FirebaseFirestore db;
    FirebaseUser user;
    public TextView money_text;
    double asking_price;
    String symbol;

    public SellDialog(Activity c, double asking_price, String symbol) {
        super(c);
        this.c = c;
        this.asking_price = asking_price;
        this.symbol = symbol;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_sell);
        yes = (Button) findViewById(R.id.ok_btn);
        yes.setOnClickListener(this);
        money = findViewById(R.id.topup);
        money_text = findViewById(R.id.much);


        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> currencies = task.getResult().getData();
                money_text.setText(currencies.get(symbol.substring(0, symbol.length()-3)).toString());
            }
        });

        Button no = findViewById(R.id.cancel);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    @Override
    public void onClick(View v) {
        double gain = asking_price * Double.parseDouble(money.getText().toString());

        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> currencies = task.getResult().getData();
                if (Double.parseDouble(money.getText().toString()) > Double.parseDouble(currencies.get(symbol.substring(0, symbol.length()-3)).toString())){
                    dismiss();
                }
                double qt = Double.parseDouble(currencies.get(symbol.substring(0, symbol.length()-3)).toString());
                double now = qt - Double.parseDouble(money.getText().toString());
                double now_eur = Double.parseDouble(currencies.get("EUR").toString()) + gain;
                currencies.put(symbol.substring(0, symbol.length()-3), now);
                currencies.put("EUR", now_eur);
                db.collection("users").document(user.getUid()).set(currencies).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dismiss();
                    }
                });

            }
        });
    }
}
