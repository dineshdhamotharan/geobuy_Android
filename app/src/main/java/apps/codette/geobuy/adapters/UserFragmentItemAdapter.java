package apps.codette.geobuy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import apps.codette.forms.Product;
import apps.codette.forms.UserFragmentItem;
import apps.codette.geobuy.AddressListAcitivty;
import apps.codette.geobuy.CartActivity;
import apps.codette.geobuy.NotificationActivity;
import apps.codette.geobuy.OrderDetailsActivity;
import apps.codette.geobuy.R;
import apps.codette.geobuy.SearchResultActivity;
import apps.codette.geobuy.ServiceActivity;
import apps.codette.geobuy.WishListActivity;

/**
 * Created by user on 08-04-2018.
 */

public class UserFragmentItemAdapter extends RecyclerView.Adapter<UserFragmentItemAdapter.UserFragmentItemHolder>  {

    private Context mCtx;
    private List<UserFragmentItem> items;

    public UserFragmentItemAdapter(Context mCtx, List<UserFragmentItem> items){
        this.mCtx = mCtx;
        this.items = items;
    }

    @Override
    public UserFragmentItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.user_list_item, null);
        return new UserFragmentItemHolder(view);
    }

    @Override
    public void onBindViewHolder(UserFragmentItemHolder holder, final int position) {
        final UserFragmentItem product = items.get(position);
        holder.textViewTitle.setText(product.getText());
        holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(product.getDrawable()));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSelected(position+1);
            }
        });
    }

    private void openSelected(int position) {
        switch(position) {
            case 1 : {
                Intent intent = new Intent(mCtx, CartActivity.class);
                mCtx.startActivity(intent);
                break;
            }
            case 2 : {
                Intent intent = new Intent(mCtx, OrderDetailsActivity.class);
                mCtx.startActivity(intent);
                break;
            }
            case 3 : {
                Intent intent = new Intent(mCtx, NotificationActivity.class);
                mCtx.startActivity(intent);
                break;
            }
            case 4 : {
                Intent intent = new Intent(mCtx, WishListActivity.class);
                mCtx.startActivity(intent);
                break;
            }
            case 5 : {
                Intent intent = new Intent(mCtx, AddressListAcitivty.class);
                mCtx.startActivity(intent);
                break;
            }
            case 6 : {
                Intent intent = new Intent(mCtx, ServiceActivity.class);
                mCtx.startActivity(intent);
                break;
            }
            case 7 : {
                break;
            }
        }
    }

    private void toast(String s) {
        Toast.makeText(mCtx, ""+s, Toast.LENGTH_SHORT).show();
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    class UserFragmentItemHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        ImageView imageView;
        RelativeLayout relativeLayout;

        public UserFragmentItemHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.item_text);
            imageView = itemView.findViewById(R.id.item_image);
            relativeLayout = itemView.findViewById(R.id.item_rl);
        }
    }
}
