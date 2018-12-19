package apps.codette.geobuy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apps.codette.forms.Brand;
import apps.codette.forms.SubCategory;
import apps.codette.geobuy.FilterActivity;
import apps.codette.geobuy.R;
import apps.codette.geobuy.SearchResultActivity;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {

    List<Brand> brands;
    FilterActivity ctx;
    Integer selected[];
    List<String> selectedBrands;

    public BrandAdapter(FilterActivity ctx, List<Brand> brands, String[] selectedBrands) {
        this.brands = brands;
        this.ctx = ctx;
        if(selectedBrands != null && selectedBrands.length>0) {
            this.selectedBrands  = new ArrayList<String> (Arrays.asList(selectedBrands));;
        }
        if(brands != null && brands.size() > 0 ) {
            selected = new Integer[brands.size()];
            for(int i=0; i<brands.size(); i++)
                selected[i] = 0;
        }
    }

    @Override
    public BrandAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.brand_filter_item, null);
        return new BrandAdapter.ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final BrandAdapter.ViewHolder holder, final int position) {
        String image = brands.get(position).getImage();

        if(image != null) {
            Glide.with(ctx)
                    .load(image)
                    .into(holder.sub_category_image);
        } else{
            String empty ="https://firebasestorage.googleapis.com/v0/b/pingme-191816.appspot.com/o/noimage%2Fnoimage.jpg?alt=media&token=03a8f3fa-9375-48a9-ba3e-9c7468e261c9";
            Glide.with(ctx)
                    .load(empty)
                    .into(holder.sub_category_image);

            holder.heading.setText(brands.get(position).getTittle());
            showView(holder.heading_ll);
        }
        if(selectedBrands != null && selectedBrands.contains(brands.get(position).getId()) ) {
            holder.cart_ll.setBackground(ctx.getResources().getDrawable(R.drawable.selected_black));
            selected[position] = 1;
        } else
            holder.cart_ll.setBackground(ctx.getResources().getDrawable(R.drawable.unselected_white));

        holder.cart_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected[position] == 0) {
                    selected[position] = 1;
                    holder.cart_ll.setBackground(ctx.getResources().getDrawable(R.drawable.selected_black));
                } else {
                    selected[position] = 0;
                    holder.cart_ll.setBackground(ctx.getResources().getDrawable(R.drawable.unselected_white));
                }
                ctx.setSelectedBrands(selected);
            }
        });


    }




    @Override
    public int getItemCount() {
        return brands.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {

        private ImageView sub_category_image;
        private CardView sub_banner_card;
        private LinearLayout heading_ll, cart_ll;
        private TextView heading;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            sub_category_image = itemView.findViewById(R.id.sub_category_image);
      //      sub_banner_card = itemView.findViewById(R.id.sub_banner_card);
            cart_ll = itemView.findViewById(R.id.cart_ll);
            heading_ll = itemView.findViewById(R.id.heading_ll);
            heading = itemView.findViewById(R.id.heading);
        }
    }

    private void showView (View... views){
        for(View v: views){
            v.setVisibility(View.VISIBLE);
        }
    }


    private void hideView (View... views){
        for(View v: views){
            v.setVisibility(View.GONE);
        }
    }
}
