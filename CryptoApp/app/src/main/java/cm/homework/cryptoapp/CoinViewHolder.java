package cm.homework.cryptoapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import cm.homework.cryptoapp.models.Coin;

class CoinViewHolder extends RecyclerView.ViewHolder {
    private final TextView symbolItemView;
    private final TextView priceItemView;
    private final TextView percentItemView;

    private CoinViewHolder(View itemView) {
        super(itemView);
        symbolItemView = itemView.findViewById(R.id.symbolView);
        priceItemView = itemView.findViewById(R.id.priceView);
        percentItemView = itemView.findViewById(R.id.percentView);
    }

    public void bind(Coin coin) {

        String symbol = coin.getSymbol();
        String text1 = symbol.substring(0,symbol.length()-3);
        String text2 = symbol.substring(symbol.length()-3,symbol.length());

        SpannableString span1 = new SpannableString(text1);
        span1.setSpan(new AbsoluteSizeSpan(25,true), 0, text1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(text2);
        span2.setSpan(new AbsoluteSizeSpan(15,true), 0, text2.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        CharSequence finalText = TextUtils.concat(span1, "/", span2);
        symbolItemView.setText(finalText);

        priceItemView.setText(Double.toString(coin.getAskPrice()));

        if(coin.getPriceChangePercent() > 0){
            //percentItemView.setBackgroundColor(Color.GREEN);
            GradientDrawable background = (GradientDrawable) percentItemView.getBackground();
            background.setColor(Color.GREEN);

            percentItemView.setText("+"+Double.toString(round(coin.getPriceChangePercent(),2))+" %");
        }else if (coin.getPriceChangePercent() < 0){
            GradientDrawable background = (GradientDrawable) percentItemView.getBackground();
            background.setColor(Color.RED);
            percentItemView.setText(Double.toString(round(coin.getPriceChangePercent(),2))+" %");
        }else{
            GradientDrawable background = (GradientDrawable) percentItemView.getBackground();
            background.setColor(Color.GRAY);
            percentItemView.setText(Double.toString(round(coin.getPriceChangePercent(),2))+" %");
        }
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    static CoinViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new CoinViewHolder(view);
    }
}