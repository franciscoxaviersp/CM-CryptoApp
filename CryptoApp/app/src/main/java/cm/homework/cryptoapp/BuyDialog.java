package cm.homework.cryptoapp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class BuyDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button yes;
    public TextView money;
    FirebaseFirestore db;
    FirebaseUser user;
    public TextView money_text;
    double askingprice;
    String symbol;
    double max_buy;
    TextView much;


    public BuyDialog(Activity c, double askingprice, String symbol) {
        super(c);
        this.c = c;
        this.askingprice = askingprice;
        this.symbol = symbol.substring(0, symbol.length()-3);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_buy);
        yes = (Button) findViewById(R.id.ok_btn);
        yes.setOnClickListener(this);
        money = findViewById(R.id.topup);
        money_text = findViewById(R.id.money);
        much = findViewById(R.id.much);

        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot result = task.getResult();
                Log.d("Result", result.toString());
                if (result.getData() != null) {
                    if(!result.getData().containsKey("EUR")){
                        dismiss();
                    }else{
                        double eur = Double.parseDouble(result.getData().get("EUR").toString());
                        max_buy = eur/askingprice;
                        much.setText("/"+String.valueOf(eur/askingprice).substring(0,6));
                    }
                }else{
                    dismiss();
                }
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
        String val = money.getText().toString();
        if(Double.parseDouble(val) > max_buy){
            dismiss();
        }

        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot result = task.getResult();
                Log.d("Result", result.toString());
                if (result.getData() != null) {
                    if (!result.getData().containsKey(symbol.toUpperCase())) {
                        Map<String, Object> temp = result.getData();
                        Double a = Double.parseDouble(val);
                        double cost = a * askingprice;
                        temp.put(symbol.toUpperCase(), a);
                        temp.put("EUR", Double.parseDouble(temp.get("EUR").toString())-cost);
                        db.collection("users").document(user.getUid()).set(temp).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dismiss();
                            }
                        });
                    } else {
                        Map<String, Object> temp = result.getData();
                        Double a = Double.parseDouble(val);
                        double cost = a * askingprice;
                        temp.put(symbol.toUpperCase(), Double.parseDouble(temp.get(symbol.toUpperCase()).toString()) + a);
                        temp.put("EUR", Double.parseDouble(temp.get("EUR").toString())-cost);
                        db.collection("users").document(user.getUid()).set(temp).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dismiss();
                            }
                        });
                    }

                }
            }
        });
    }
}
