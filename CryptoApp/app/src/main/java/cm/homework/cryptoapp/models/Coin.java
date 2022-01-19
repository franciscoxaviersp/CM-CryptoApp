package cm.homework.cryptoapp.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 *
 * @author Xico
 */
@Entity(tableName = "coin_table")
public class Coin implements Comparable{

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "symbol")
    private String symbol;
    private double priceChange;
    private double priceChangePercent;
    private double weightedAvgPrice;
    private double prevClosePrice;
    private double lastPrice;
    private double lastQty;
    private double bidPrice;
    private double bidQty;
    private double askPrice;
    private double askQty;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    @ColumnInfo(name = "volume")
    private double volume;
    private double quoteVolume;
    private Long openTime;
    private Long closeTime;
    private int firstId;
    private int lastId;
    private int count;

    public Coin() {
    }
    @Ignore
    public Coin(String symbol) {
        this.symbol = symbol;
    }

    public Coin(String symbol, double priceChange, double priceChangePercent, double weightedAvgPercent, double prevClosePrice, double lastPrice, double lastQty, double bidPrice, double bidQty, double askPrice, double askQty, double openPrice, double highPrice, double lowPrice, double volume, double quoteVolume, Long openTime, Long closeTime, int firstId, int lastId, int count) {

        this.symbol = symbol;
        this.priceChange = priceChange;
        this.priceChangePercent = priceChangePercent;
        this.weightedAvgPrice = weightedAvgPercent;
        this.prevClosePrice = prevClosePrice;
        this.lastPrice = lastPrice;
        this.lastQty = lastQty;
        this.bidPrice = bidPrice;
        this.bidQty = bidQty;
        this.askPrice = askPrice;
        this.askQty = askQty;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
        this.quoteVolume = quoteVolume;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.firstId = firstId;
        this.lastId = lastId;
        this.count = count;
    }


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public double getPriceChangePercent() {
        return priceChangePercent;
    }

    public void setPriceChangePercent(double priceChangePercent) {
        this.priceChangePercent = priceChangePercent;
    }

    public double getWeightedAvgPrice() { return weightedAvgPrice; }

    public void setWeightedAvgPrice(double weightedAvgPrice) {
        this.weightedAvgPrice = weightedAvgPrice;
    }

    public double getPrevClosePrice() {
        return prevClosePrice;
    }

    public void setPrevClosePrice(double prevClosePrice) {
        this.prevClosePrice = prevClosePrice;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getLastQty() {
        return lastQty;
    }

    public void setLastQty(double lastQty) {
        this.lastQty = lastQty;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public double getBidQty() {
        return bidQty;
    }

    public void setBidQty(double bidQty) {
        this.bidQty = bidQty;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public double getAskQty() {
        return askQty;
    }

    public void setAskQty(double askQty) {
        this.askQty = askQty;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getQuoteVolume() {
        return quoteVolume;
    }

    public void setQuoteVolume(double quoteVolume) {
        this.quoteVolume = quoteVolume;
    }

    public Long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Long openTime) {
        this.openTime = openTime;
    }

    public Long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Long closeTime) {
        this.closeTime = closeTime;
    }

    public int getFirstId() {
        return firstId;
    }

    public void setFirstId(int firstId) {
        this.firstId = firstId;
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, priceChange, priceChangePercent, weightedAvgPrice, prevClosePrice, lastPrice, lastQty, bidPrice, bidQty, askPrice, askQty, openPrice, highPrice, lowPrice, volume, quoteVolume, openTime, closeTime, firstId, lastId, count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coin coin = (Coin) o;
        return Double.compare(coin.priceChange, priceChange) == 0 && Double.compare(coin.priceChangePercent, priceChangePercent) == 0 && Double.compare(coin.weightedAvgPrice, weightedAvgPrice) == 0 && Double.compare(coin.prevClosePrice, prevClosePrice) == 0 && Double.compare(coin.lastPrice, lastPrice) == 0 && Double.compare(coin.lastQty, lastQty) == 0 && Double.compare(coin.bidPrice, bidPrice) == 0 && Double.compare(coin.bidQty, bidQty) == 0 && Double.compare(coin.askPrice, askPrice) == 0 && Double.compare(coin.askQty, askQty) == 0 && Double.compare(coin.openPrice, openPrice) == 0 && Double.compare(coin.highPrice, highPrice) == 0 && Double.compare(coin.lowPrice, lowPrice) == 0 && Double.compare(coin.volume, volume) == 0 && Double.compare(coin.quoteVolume, quoteVolume) == 0 && firstId == coin.firstId && lastId == coin.lastId && count == coin.count && symbol.equals(coin.symbol) && Objects.equals(openTime, coin.openTime) && Objects.equals(closeTime, coin.closeTime);
    }

    @Override
    public String toString() {
        return "Coin{" +
                "symbol='" + symbol + '\'' +
                ", priceChange=" + priceChange +
                ", priceChangePercent=" + priceChangePercent +
                ", weightedAvgPrice=" + weightedAvgPrice +
                ", prevClosePrice=" + prevClosePrice +
                ", lastPrice=" + lastPrice +
                ", lastQty=" + lastQty +
                ", bidPrice=" + bidPrice +
                ", bidQty=" + bidQty +
                ", askPrice=" + askPrice +
                ", askQty=" + askQty +
                ", openPrice=" + openPrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", volume=" + volume +
                ", quoteVolume=" + quoteVolume +
                ", openTime=" + openTime +
                ", closeTime=" + closeTime +
                ", firstId=" + firstId +
                ", lastId=" + lastId +
                ", count=" + count +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        double vol = ((Coin)o).getVolume();

        double diff = vol-this.volume;

        if(diff > 0){
            return (int)Math.ceil(diff);
        }else if(diff < 0){
            return (int)Math.floor(diff);
        }
        return (int)diff;
    }

}
