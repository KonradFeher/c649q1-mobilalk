package com.example.currensee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> implements Filterable {

    private ArrayList<Currency> currencies;
    private ArrayList<Currency> currenciesAll;
    private Context context;
    private int lastPosition = -1;

    CurrencyAdapter(Context context, ArrayList<Currency> items) {
        this.currencies = items;
        this.currenciesAll = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CurrencyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_currency, parent, false));
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Currency current = currencies.get(position);
        holder.bindTo(current);

        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.scroll_in);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence query) {
            ArrayList<Currency> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(query == null || query.toString().isEmpty()) {
                // if nothing is queried show all
                results.count = currenciesAll.size();
                results.values = currenciesAll;
            } else {
                // otherwise look at name and abbr
                String filterPattern = query.toString().toLowerCase().trim();
                for(Currency curr : currenciesAll) {
                    if(curr.getFullName().toLowerCase().contains(filterPattern) ||
                            curr.getAbbreviation().toLowerCase().contains(filterPattern)){
                        filteredList.add(curr);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            currencies = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fullNameText;
        private TextView abbreviationText;
        private TextView symbolText;
        private TextView valueText;
        private TextView updatedAtText;

        ViewHolder(View itemView) {
            super(itemView);
            fullNameText = itemView.findViewById(R.id.full_name);
            abbreviationText = itemView.findViewById(R.id.abbreviation);
            symbolText = itemView.findViewById(R.id.symbol);
            valueText = itemView.findViewById(R.id.value);
            updatedAtText = itemView.findViewById(R.id.updated_at);

            itemView.findViewById(R.id.edit).setOnClickListener(view -> ((CurrenciesActivity)context).editCurrency(abbreviationText.getText().toString()));
            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((CurrenciesActivity)context).deleteCurrency(abbreviationText.getText().toString()));

        }

        @SuppressLint("SetTextI18n")
        void bindTo(Currency curr){
            fullNameText.setText(curr.getFullName());
            abbreviationText.setText(curr.getAbbreviation());
            symbolText.setText(curr.getSymbol());
            valueText.setText("1 " + curr.getAbbreviation() + " = " + String.format(Locale.ROOT, "%.4f ", curr.getValue()) + "EUR");
            updatedAtText.setText(new SimpleDateFormat("HH:mm MM-dd", Locale.ROOT).format(curr.getUpdatedAt()));
        }
    }
}
