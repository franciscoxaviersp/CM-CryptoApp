package cm.homework.roomwordsample;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class CoinListAdapter extends ListAdapter<Coin, CoinViewHolder> {

    public CoinListAdapter(@NonNull DiffUtil.ItemCallback<Coin> diffCallback) {
        super(diffCallback);
    }

    @Override
    public CoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CoinViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(CoinViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class CoinDiff extends DiffUtil.ItemCallback<Coin> {

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