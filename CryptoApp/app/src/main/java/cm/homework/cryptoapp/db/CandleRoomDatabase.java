package cm.homework.cryptoapp.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.github.mikephil.charting.data.CandleEntry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.homework.cryptoapp.models.Candle;
import cm.homework.cryptoapp.models.Coin;

@Database(entities = {Candle.class}, version = 1, exportSchema = false)
public abstract class CandleRoomDatabase extends RoomDatabase {

    public abstract CandleDao candleDao();

    private static volatile CandleRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                CandleDao dao = INSTANCE.candleDao();
                dao.deleteAll();

            });
        }
    };



    public static CandleRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CoinRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CandleRoomDatabase.class, "candle_database").addCallback(sRoomDatabaseCallback).allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}