package apps.codette.geobuy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import apps.codette.forms.Product;
import apps.codette.geobuy.R;

public class OrderProductsAdapter  extends RecyclerView.Adapter<OrderProductsAdapter.ViewHolder> {

    List<Product> products;
    Context mCtx;

    public OrderProductsAdapter(Context mCtx, List<Product> products){
        this.products = products;
        this.mCtx = mCtx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.order_item_product, null);
        return new OrderProductsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.product_name.setText(product.getTitle());
        if(product.getImage() != null && product.getImage().length > 0)
            Glide.with(mCtx)
                    .load(product.getImage()[0])
                    .into(holder.product_image);
        holder.quanity.setText(product.getQuanity()+"");
        holder.selling_price.setText("₹ "+(Math.round(product.getAmountToBePaid())));
        holder.shipping_fee.setText("₹ "+0);
        holder.total_amount.setText("₹ "+(Math.round(product.getAmountToBePaid())));

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView product_name, quanity,selling_price, shipping_fee, total_amount;
        ImageView product_image;
        public ViewHolder(View itemView) {
            super(itemView);
            product_name= itemView.findViewById(R.id.product_name);
            product_image = itemView.findViewById(R.id.product_image);
            quanity = itemView.findViewById(R.id.quanity);
            selling_price = itemView.findViewById(R.id.selling_price);
            shipping_fee = itemView.findViewById(R.id.shipping_fee);
            total_amount = itemView.findViewById(R.id.total_amount);

        }
    }
}
