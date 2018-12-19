package apps.codette.geobuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import apps.codette.forms.Product;
import apps.codette.forms.User;
import apps.codette.geobuy.Constants.OrgService;
import apps.codette.geobuy.adapters.CartProductsAdapter;
import apps.codette.geobuy.adapters.WishlistProductsAdapter;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

public class WishListActivity extends AppCompatActivity {

    SessionManager sessionManager;
    ProgressDialog pd;
    Map<String, ?> userDetails;
    LinearLayout wishlist_products_ll, empty_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sessionManager = new SessionManager(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        userDetails = sessionManager.getUserDetails();
        String wishProducts = (String) userDetails.get("wishlist");
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        getProductsById(wishProducts);
    }



    private void getProductsById(String products) {
        wishlist_products_ll = findViewById(R.id.wishlist_products_ll);
        empty_ll = findViewById(R.id.empty_ll);
        if(products != null && !products.isEmpty()) {
            hideView(empty_ll);
            showView(wishlist_products_ll);
            pd.show();
            RequestParams requestParams = new RequestParams();
            RestCall.get("products?products="+products, requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    updateWishListInRecyclerView(new String(responseBody));
                    pd.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    pd.dismiss();
                    toast(getResources().getString(R.string.try_later));
                }
            });
        } else {
            hideView(wishlist_products_ll);
            showView(empty_ll);
        }
    }

    private void updateWishListInRecyclerView(String response) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        List<Product> productList = gson.fromJson(response, type);
        WishlistProductsAdapter productAdapter = new WishlistProductsAdapter(this, productList);
        RecyclerView recyclerView = findViewById(R.id.wishlist_products);
        LinearLayoutManager ll = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(ll);
        recyclerView.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
      //  toast(productList == null ? "empty":productList.size()+"");
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

    @Override
    public void onResume(){
        super.onResume();
        sessionManager = null;
        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUserDetails();
        String wishProducts = (String) userDetails.get("wishlist");
        getProductsById(wishProducts);
    }

    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }
}
