package apps.codette.geobuy.adapters;

import android.app.Dialog;
import android.content.Context;
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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import apps.codette.forms.NotificationForm;
import apps.codette.forms.Organization;
import apps.codette.forms.Product;
import apps.codette.geobuy.BusinessActivity;
import apps.codette.geobuy.OrderDetailActivity;
import apps.codette.geobuy.ProductDetailsActivity;
import apps.codette.geobuy.R;
import apps.codette.geobuy.SearchResultActivity;

/**
 * Created by user on 25-03-2018.
 */

public class NotoficationsAdapter extends RecyclerView.Adapter<NotoficationsAdapter.ViewHolder> {

    private Context mCtx;
    private List<NotificationForm> notifications;


    public NotoficationsAdapter(Context ctxt, List<NotificationForm> notifications) {
        this.mCtx = ctxt;
        this.notifications = notifications;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.notification_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final NotificationForm not = notifications.get(position);
        if(not.getImage() != null)
            Glide.with(mCtx).load(not.getImage()).into(holder.notification_image);
        holder.notificationTitle.setText(not.getNotification().getTitle());
        holder.notification_body.setText(not.getNotification().getBody());
        holder.notificationTitle.setOnClickListener(onSelectNotifications(not));
        holder.product_relative_layout.setOnClickListener(onSelectNotifications(not));
        holder.notification_card.setOnClickListener(onSelectNotifications(not));

    }

    private View.OnClickListener onSelectNotifications(final NotificationForm not) {

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String[] linkids = not.getLinkId();
                String link = not.getLink();
                if(link != null && link.equalsIgnoreCase("products")) {
                    Intent intent = new Intent(mCtx, SearchResultActivity.class);
                    intent.putExtra("productIds", linkids);
                    mCtx.startActivity(intent);
                } else if (link != null && link.equalsIgnoreCase("order")) {
                    Intent intent = new Intent(mCtx, OrderDetailActivity.class);
                    intent.putExtra("id", linkids[0]);
                 //   intent.putExtra("productId", linkids[0].split("#")[1]);
                    mCtx.startActivity(intent);
                } else if (link != null && link.equalsIgnoreCase("seller")) {
                    Intent intent = new Intent(mCtx, BusinessActivity.class);
                    intent.putExtra("orgid", linkids[0]);
                    mCtx.startActivity(intent);
                }
            }
        };
        return listener;
    }


    @Override
    public int getItemCount() {
        if(notifications != null)
            return notifications.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }




    class ViewHolder extends RecyclerView.ViewHolder {

        TextView notificationTitle,notification_body;
        ImageView notification_image;
        LinearLayout notification_select;
        CardView notification_card;
        RelativeLayout product_relative_layout;


        public ViewHolder(View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notification_image = itemView.findViewById(R.id.notification_image);
            notification_body = itemView.findViewById(R.id.notification_body);
            notification_card = itemView.findViewById(R.id.notification_card);
            product_relative_layout = itemView.findViewById(R.id.product_relative_layout);
        }

    }
}
