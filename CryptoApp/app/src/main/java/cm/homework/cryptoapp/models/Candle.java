package cm.homework.cryptoapp.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "candle_table")
public class Candle {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    int id;
    float high;
    float low;
    float open;
    float close;

    public Candle(int id, float high, float low, float open, float close) {
        this.id = id;
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    @Override
    public String toString() {
        return "Candle{" +
                "id=" + id +
                ", high=" + high +
                ", low=" + low +
                ", open=" + open +
                ", close=" + close +
                '}';
    }
}
