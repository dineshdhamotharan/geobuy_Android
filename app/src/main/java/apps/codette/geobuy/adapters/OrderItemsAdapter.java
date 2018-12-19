package apps.codette.geobuy.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import apps.codette.forms.Order;
import apps.codette.forms.Organization;
import apps.codette.forms.Product;
import apps.codette.geobuy.OrderDetailActivity;
import apps.codette.geobuy.ProductDetailsActivity;
import apps.codette.geobuy.R;

/**
 * Created by user on 25-03-2018.
 */

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.OrderViewHolder> {

    private Context mCtx;
    private List<Order> orders;


    public OrderItemsAdapter(Context ctxt, List<Order> orders) {
        this.mCtx = ctxt;
        this.orders = orders;
    }



    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.order_item, null);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderViewHolder holder, int position) {
        final Order order = orders.get(position);
        if(order.getProducts().size() ==1)
            holder.productTitle.setText(order.getProducts().get(0).getTitle());
        else
            holder.productTitle.setText(order.getProducts().get(0).getTitle()+" & "+ (order.getProducts().size()-1) +" more items");

        if(order.getProducts().get(0).getImage() != null &&  order.getProducts().get(0).getImage().length != 0) {
            Glide.with(mCtx)
                    .load(order.getProducts().get(0).getImage()[0])
                    .into(holder.imageView);
        } else {
            Glide.with(mCtx)
                    .load("https://firebasestorage.googleapis.com/v0/b/pingme-191816.appspot.com/o/test%2Fproduct_nil.gif?alt=media&token=a6ae5d11-d8e7-47f7-a0fd-7d7ea7ea87cb")
                    .into(holder.imageView);
        }

       // hideView(holder.submit);
        /*holder.productRating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(order.getRating() != holder.productRating.getRating())
                    showView(holder.submit);
                else
                    hideView(holder.submit);
                return false;
            }
        });*/
        // O - Ordered,A - Approved,D - Delivered,
        // Y - Yet to dispatch, C - Cancelled, R - Returned
        if(order.getStatus().equalsIgnoreCase("O")) {
            holder.order_status.setText("Ordered");
            holder.order_status.setBackgroundColor(mCtx.getResources().getColor(R.color.yello));
        }
        else if(order.getStatus().equalsIgnoreCase("A")) {
            holder.order_status.setText("Approved");
            holder.order_status.setBackgroundColor(mCtx.getResources().getColor(R.color.green));
        }
        else if(order.getStatus().equalsIgnoreCase("D")) {
            holder.order_status.setText("Delivered");
            holder.order_status.setBackgroundColor(mCtx.getResources().getColor(R.color.green));
        }
        else if(order.getStatus().equalsIgnoreCase("Y")) {
            holder.order_status.setText("Yet to dispatch");
            holder.order_status.setBackgroundColor(mCtx.getResources().getColor(R.color.green));
        }
        else if(order.getStatus().equalsIgnoreCase("C")) {
            holder.order_status.setBackgroundColor(mCtx.getResources().getColor(R.color.colorPrimary));
            holder.order_status.setText("Cancelled");
        }
        else if(order.getStatus().equalsIgnoreCase("RE")) {
            holder.order_status.setBackgroundColor(mCtx.getResources().getColor(R.color.colorPrimary));
            holder.order_status.setText("Returned");
        }
        else if(order.getStatus().equalsIgnoreCase("RR")) {
            holder.order_status.setBackgroundColor(mCtx.getResources().getColor(R.color.colorPrimary));
            holder.order_status.setText("Return Requested");
        }
        else if(order.getStatus().equalsIgnoreCase("R")) {
            holder.order_status.setBackgroundColor(mCtx.getResources().getColor(R.color.colorPrimary));
            holder.order_status.setText("Rejected");
        }
        else if(order.getStatus().equalsIgnoreCase("DI")) {
            holder.order_status.setBackgroundColor(mCtx.getResources().getColor(R.color.colorPrimary));
            holder.order_status.setText("Dispatched");
        }
        else if(order.getStatus().equalsIgnoreCase("RP")) {
            holder.order_status.setBackgroundColor(mCtx.getResources().getColor(R.color.green));
            holder.order_status.setText("Ready");
        }
        if(order.getDeliverytime() !=null ){
            holder.delivery_date.setText("Delivery on "+order.getDeliverytime());
        }
        holder.total_amount.setText(Math.round(order.getSubtotalamount())+"");

        if(order.getPaymentstatus().equalsIgnoreCase("PAID"))
            holder.payment_status.setText("Paid");
        else if(order.getPaymentstatus().equalsIgnoreCase("TA"))
            holder.payment_status.setText("Cash on pickup");
        else
            holder.payment_status.setText("Cash On Delivery");

        holder.quanity.setText(""+order.getProducts().get(0).getQuanity());
        holder.order_item_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToOrderDetail(order.getId(), order.getProducts().get(0).getId());
            }
        });

        if(order.getOrgDetails() != null && order.getOrgDetails().get(0).getOrgname() != null)
            holder.seller.setText(order.getOrgDetails().get(0).getOrgname());
    }

    private void moveToOrderDetail(String id, String productId) {
        Intent intent = new Intent(mCtx, OrderDetailActivity.class);
        intent.putExtra("id", id);
        //intent.putExtra("productId",productId);
        mCtx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if(orders != null)
            return orders.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
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


    class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView productTitle;
        ImageView imageView;
        RatingBar productRating;
        Button submit;
        TextView delivery_date;
        TextView total_amount;
        TextView order_status;
        TextView payment_status;
        TextView quanity;
        CardView order_item_card_view;
        TextView seller;

        public OrderViewHolder(View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_name);
            imageView = itemView.findViewById(R.id.product_image);
         //   productRating = itemView.findViewById(R.id.order_rating_bar);
            delivery_date = itemView.findViewById(R.id.delivery_date);
        //    submit = itemView.findViewById(R.id.order_review);
            total_amount = itemView.findViewById(R.id.total_amount);
            order_status = itemView.findViewById(R.id.order_status);
            payment_status = itemView.findViewById(R.id.payment_status);
            quanity = itemView.findViewById(R.id.quanity);
            seller = itemView.findViewById(R.id.seller);
            order_item_card_view = itemView.findViewById(R.id.order_item_card_view);
        }


    }
}
