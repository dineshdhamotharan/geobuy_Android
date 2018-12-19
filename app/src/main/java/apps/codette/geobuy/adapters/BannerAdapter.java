package apps.codette.geobuy.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import apps.codette.forms.Banner;
import apps.codette.forms.Category;
import apps.codette.forms.Product;
import apps.codette.geobuy.BusinessActivity;
import apps.codette.geobuy.R;
import apps.codette.geobuy.SearchResultActivity;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;

/**
 * Created by user on 25-03-2018.
 */

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private Context mCtx;
    private List<Banner> categories;
    private boolean isLocation;
    private LatLng location;

    public BannerAdapter(Context ctxt, List<Banner> categories, boolean isLocation, LatLng location) {
        this.isLocation = isLocation;
        this.mCtx = ctxt;
        this.categories = categories;
        this.location = location;
    }


    @Override
    public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.banner_item, null);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BannerViewHolder holder, int position) {
        final Banner category = categories.get(position);
        Glide.with(mCtx)
                .load(category.getImage())
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProductsByGroup(category);
            }
        });
        if(isLocation) {
            updateDistanceAndTravelDuration(holder, category);
        }

    }

    private void updateDistanceAndTravelDuration(final BannerViewHolder holder, Banner category) {
        StringBuffer query = new StringBuffer("getDistance");
        query.append("?lat1="+location.latitude);
        query.append("&lon1="+location.longitude);
        query.append("&lat2="+category.getLat());
        query.append("&lon2="+category.getLon());
        RestCall.get(query.toString(), new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    holder.distance.setText(jsonObject.get("distance").toString());
                    holder.duration.setText(jsonObject.get("duration").toString());
                    showView(holder.offer_distance_ll);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //toast(mCtx.getResources().getString(R.string.try_later));
            }
        });
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


    private void openProductsByGroup(Banner category) {
        String[] linkId = null;
        if(category.isOrg()) {
            linkId = category.getLinkId();
            Intent intent = new Intent(mCtx, BusinessActivity.class);
            intent.putExtra("orgid", linkId[0]);
            mCtx.startActivity(intent);
        } else if (category.isProducts()) {
            linkId = category.getLinkId();
            Intent intent = new Intent(mCtx, SearchResultActivity.class);
            intent.putExtra("productIds", linkId);
            mCtx.startActivity(intent);
        }

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private void toast(String s) {
        Toast.makeText(mCtx, s, Toast.LENGTH_SHORT).show();
    }


    class BannerViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        LinearLayout offer_distance_ll;

        TextView distance;

        TextView duration;

        public BannerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.category_image);
            offer_distance_ll =itemView.findViewById(R.id.offer_distance_ll);
            distance = itemView.findViewById(R.id.distance);
            duration = itemView.findViewById(R.id.duration);
        }
    }
}
