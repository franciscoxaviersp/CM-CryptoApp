package cm.homework.cryptoapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.bind.ObjectTypeAdapter;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class NotificationTask extends Worker {
    String auth;
    FirebaseFirestore db;
    boolean not = false;


    public NotificationTask(Context context, WorkerParameters params){
        super(context, params);

    }

    @Override
    public Result doWork(){
        CountDownLatch latch = new CountDownLatch(1);
        auth = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(auth).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.e("aqui","aqui");
                Map<String, Object> result = task.getResult().getData();
                if(result.containsKey("transfer")){
                    if(result.get("transfer").equals("true")){
                        Log.e("AHAHAH","AHAAH");
                        result.put("transfer", "false");
                        db.collection("users").document(auth).set(result).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.e("TRANSFER", "DONE");
                                not = true;
                                createNotification();
                                latch.countDown();
                            }
                        });
                    }
                }
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("AQUI", "AQUI");
        return Result.success();
    }

    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Transfer")
                .setContentText("New Transfer has been received")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

}
