package apps.codette.geobuy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.codette.geobuy.FilterActivity;
import apps.codette.geobuy.R;


public class PriceFilterAdapter extends RecyclerView.Adapter<PriceFilterAdapter.ViewHolder> {

    private FilterActivity context;
    private List<String> pojoClassArrayList;
    private Integer[] selected;


    public PriceFilterAdapter(FilterActivity context, List<String> pojoClassArrayList) {
        this.context = context;
        this.pojoClassArrayList = pojoClassArrayList;
        selected = new Integer[pojoClassArrayList.size()];
        for(int j=0 ; j < pojoClassArrayList.size(); j++) {
            selected[j]= 0;
        }
        context.setSelectedPrices(selected);
    }

    @NonNull
    @Override
    public PriceFilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_price, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final PriceFilterAdapter.ViewHolder holder, final int position) {

        holder.title.setText(pojoClassArrayList.get(position));
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected[position] == 0) {
                    holder.title.setBackground(context.getResources().getDrawable(R.drawable.dark_bg_rect));
                    selected[position] = 1;
                } else {
                    holder.title.setBackground(context.getResources().getDrawable(R.drawable.light_dark_rect));
                    selected[position] = 0;
                }
                context.setSelectedPrices(selected);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pojoClassArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        public ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
        }
    }
}
