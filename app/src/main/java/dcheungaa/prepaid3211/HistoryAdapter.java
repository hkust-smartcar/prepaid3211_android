package dcheungaa.prepaid3211;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.List;

import dcheungaa.prepaid3211.Records.History;

/**
 * Created by Daniel on 6/2/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

    class HistoryHolder extends RecyclerView.ViewHolder {

        private final TextView historyItemName;
        private final TextView historyItemCost;
        private final TextView historyPurchasedAt;

        HistoryHolder(View itemView) {
            super(itemView);
            historyItemName = itemView.findViewById(R.id.textHistoryItemItemName);
            historyItemCost = itemView.findViewById(R.id.textHistoryItemItemCost);
            historyPurchasedAt = itemView.findViewById(R.id.textHistoryItemPurchasedAt);
        }
    }

    private final LayoutInflater inflater;
    private List<History> histories;

    HistoryAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.history_view_item, parent, false);
        return new HistoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {
        if (histories != null) {
            History current = histories.get(position);
            holder.historyItemName.setText(current.getItemName());
            int balanceChange = -current.getItemCost();
            boolean isPurchase = balanceChange <= 0;
            holder.historyItemCost.setText(MessageFormat.format("{0}${1}", isPurchase ? "-" : "", Math.abs(balanceChange)));
            holder.historyItemCost.setTextColor(isPurchase ? 0xFFB71C1C : 0xFF1B5E20);
            holder.historyPurchasedAt.setText(MainActivity.dateFormat.format(current.getPurchasedAt()));
        } else {
            holder.historyItemName.setText(R.string.no_item);
            holder.historyItemCost.setText("");
            holder.historyPurchasedAt.setText("");
        }
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (histories != null)
            return histories.size();
        return 0;
    }
}
