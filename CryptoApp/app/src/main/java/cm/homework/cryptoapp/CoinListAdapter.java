package cm.homework.cryptoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import cm.homework.cryptoapp.activities.CoinActivity;
import cm.homework.cryptoapp.models.Coin;

public class CoinListAdapter extends ListAdapter<Coin, CoinViewHolder> {

    public CoinListAdapter(@NonNull DiffUtil.ItemCallback<Coin> diffCallback) {
        super(diffCallback);
    }

    @Override
    public CoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CoinViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(CoinViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(getItem(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                Intent intent = new Intent(v.getContext(), CoinActivity.class);
                intent.putExtra("symbol",getItem(position).getSymbol());
                intent.putExtra("askPrice",getItem(position).getAskPrice());
                intent.putExtra("priceChange",getItem(position).getPriceChange());
                intent.putExtra("priceChangePercent",getItem(position).getPriceChangePercent());
                intent.putExtra("volume",getItem(position).getVolume());

                //launch activity with info to be changed
                v.getContext().startActivity(intent);
            }
        });
    }

    public static class CoinDiff extends DiffUtil.ItemCallback<Coin> {

        @Override
        public boolean areItemsTheSame(@NonNull Coin oldItem, @NonNull Coin newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Coin oldItem, @NonNull Coin newItem) {
            return oldItem.getSymbol().equals(newItem.getSymbol());
        }
    }
}