package cm.homework.roomwordsample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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
        symbolItemView.setText(coin.getSymbol());
        priceItemView.setText(Double.toString(coin.getAskPrice()));
        percentItemView.setText(Double.toString(coin.getPriceChangePercent()));
    }

    static CoinViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new CoinViewHolder(view);
    }
}