package apps.codette.geobuy.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import apps.codette.forms.Organization;
import apps.codette.forms.Product;
import apps.codette.geobuy.CartActivity;
import apps.codette.geobuy.ProductDetailsActivity;
import apps.codette.geobuy.R;

/**
 * Created by user on 25-03-2018.
 */

public class WishlistProductsAdapter extends RecyclerView.Adapter<WishlistProductsAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Product> products;


    public WishlistProductsAdapter(Context ctxt, List<Product> products) {
        this.mCtx = ctxt;
        this.products = products;
    }



    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.wishlist_product_item, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        final Product product = products.get(position);
        if(product.getImage() != null)
            Glide.with(mCtx)
                .load(product.getImage()[0])
                .into(holder.imageView);
        holder.productTitle.setText(product.getTitle());
        Log.i("product", product.toString());
        if(product.getProductDetails() != null && product.getProductDetails().size() > 0) {
            float discount = (Float.valueOf(product.getProductDetails().get(0).getOffer()) / 100) * product.getProductDetails().get(0).getPrice();
            holder.productPrice.setText("₹ " + (product.getProductDetails().get(0).getPrice() - discount));
            holder.preferred_org.setText(product.getProductDetails().get(0).getOrgname());
        } else {
            float discount = (Float.valueOf(product.getOffer() ) / 100) * product.getPrice() *product.getQuanity() ;
            holder.productPrice.setText("₹ " + ((product.getPrice()*product.getQuanity()) - discount));
            holder.preferred_org.setText(product.getOrgname());
        }

        holder.product_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToProductDetailActivity(product.getId(), product.getOrgid());
            }
        });

        holder.product_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToProductDetailActivity(product.getId(), product.getOrgid());
            }
        });


    }


    private List<Organization> getOrgs(List<Product> products) {
        List<Organization> orgs = new ArrayList<>();
        for(Product pr : products)
            orgs.add(new Organization(pr.getOrgname(), pr.getOrgid()));
        return orgs;
    }

    public int getImage(String imageName) {
        int drawableResourceId = mCtx.getResources().getIdentifier(imageName, "drawable", mCtx.getPackageName());
        return drawableResourceId;
    }

    private void formOrgs(List<Organization> organizations, Dialog productOrgDialog){
        LinearLayout ll = productOrgDialog.findViewById(R.id.business_search_layout);
        if(organizations != null && !organizations.isEmpty()){
            RecyclerView rv = productOrgDialog.findViewById(R.id.nearby_business__recycler_view);
            NearByBusinessAdapter rbA = new NearByBusinessAdapter(mCtx,organizations, getImage("store"));
            LinearLayoutManager HorizontalLayout  = new LinearLayoutManager(mCtx, LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(HorizontalLayout);
            rv.setAdapter(rbA);
        }
    }

    private void moveToProductDetailActivity(String id, String orgid) {
        Intent intent = new Intent(mCtx, ProductDetailsActivity.class);
        intent.putExtra("productId", id);
        intent.putExtra("orgId", orgid);
        mCtx.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        if(products != null)
            return products.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }




    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productTitle;
        ImageView imageView;
        TextView productPrice;
        CardView product_card;
        TextView preferred_org;
        RelativeLayout product_relative_layout;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.productTitle);
            imageView = itemView.findViewById(R.id.product_image);
            productPrice = itemView.findViewById(R.id.product_price);
            product_card = itemView.findViewById(R.id.product_card);
            preferred_org = itemView.findViewById(R.id.preferred_org);
            product_relative_layout = itemView.findViewById(R.id.product_relative_layout);

        }

    }
}
