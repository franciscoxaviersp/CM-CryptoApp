package cm.homework.cryptoapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import cm.homework.cryptoapp.R;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TopUpDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button yes;
    public TextView money;
    FirebaseFirestore db;
    FirebaseUser user;
    public TextView money_text;

    public TopUpDialog(Activity c) {
        super(c);
        this.c = c;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_top_up);
        yes = (Button) findViewById(R.id.ok_btn);
        yes.setOnClickListener(this);
        money = findViewById(R.id.topup);
        money_text = findViewById(R.id.money);


    }

    @Override
    public void onClick(View v) {
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot result = task.getResult();
                Log.d("Result", result.toString());
                if (result.getData() != null) {
                    if (!result.getData().containsKey("EUR")) {
                        Map<String, Object> temp = result.getData();
                        Double a = Double.parseDouble(money.getText().toString());
                        temp.put("EUR", a);
                        db.collection("users").document(user.getUid()).set(temp).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dismiss();
                            }
                        });
                    } else {
                        Map<String, Object> temp = result.getData();
                        Double a = Double.parseDouble(money.getText().toString());
                        temp.put("EUR", Double.parseDouble(temp.get("EUR").toString()) + a);
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
