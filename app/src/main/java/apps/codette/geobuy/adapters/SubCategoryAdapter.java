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
import java.util.List;

import apps.codette.forms.SubCategory;
import apps.codette.geobuy.R;
import apps.codette.geobuy.SearchResultActivity;

public class SubCategoryAdapter  extends RecyclerView.Adapter<SubCategoryAdapter.SubCategoryHolder> {

    List<SubCategory> subCategories;
    Context ctx;

    public SubCategoryAdapter(Context ctx, List<SubCategory> subCategories) {
        this.subCategories = subCategories;
        this.ctx = ctx;
    }

    @Override
    public SubCategoryAdapter.SubCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.sub_category_item, null);
        return new SubCategoryAdapter.SubCategoryHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(SubCategoryAdapter.SubCategoryHolder holder, final int position) {
        String image = subCategories.get(position).getImage();
        Log.i("image",image+"");
        Glide.with(ctx)
        .load(image)
        .into(holder.sub_category_image);
        holder.sub_banner_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProductsForCategory(subCategories.get(position).getCategory(),subCategories.get(position).getId());
            }
        });
        holder.heading.setText(subCategories.get(position).getTittle());

        if(subCategories.get(position).isVisible()) {
            showView(holder.heading_ll);
        }

    }

    private void openProductsForCategory(String categoryId, String subcategory) {
        Intent intent = new Intent(ctx, SearchResultActivity.class);
        intent.putExtra("categoryId", categoryId);
        intent.putExtra("subcategory", subcategory);
        ctx.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return subCategories.size();
    }

    public class SubCategoryHolder  extends RecyclerView.ViewHolder {

        private ImageView sub_category_image;
        private CardView sub_banner_card;
        private LinearLayout heading_ll;
        private TextView heading;

        public SubCategoryHolder(View itemView, int viewType) {
            super(itemView);
            sub_category_image = itemView.findViewById(R.id.sub_category_image);
            sub_banner_card = itemView.findViewById(R.id.sub_banner_card);
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
