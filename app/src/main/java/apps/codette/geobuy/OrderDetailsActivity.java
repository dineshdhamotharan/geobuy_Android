package apps.codette.geobuy;

import android.app.ProgressDialog;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import apps.codette.forms.Order;
import apps.codette.forms.Product;
import apps.codette.forms.User;
import apps.codette.geobuy.adapters.OrderItemsAdapter;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

public class OrderDetailsActivity extends AppCompatActivity {

    SessionManager sessionManager;
    ProgressDialog pd;
    Map<String, ?> userDetails;
    //getOrders
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUserDetails();
        formOrders();
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void formOrders() {
        pd.show();
        RequestParams requestParams = new RequestParams();
        RestCall.get("orders?useremail="+userDetails.get("useremail"), requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                updateOrdersInRecyclerView(new String(responseBody));
                pd.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });

    }

    private void updateOrdersInRecyclerView(String responseBody) {
        RecyclerView recyclerView = findViewById(R.id.order_items);
        LinearLayout empty_ll = findViewById(R.id.empty_ll);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Order>>() {}.getType();
        List<Order> orders = gson.fromJson(responseBody, type);
        List<Order> individualOrders = null;
        try {
            individualOrders = getIndividualOrders(orders);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if(individualOrders != null && !individualOrders.isEmpty()) {
            showView(recyclerView);
            hideView(empty_ll);
            OrderItemsAdapter orderItemsAdapter = new OrderItemsAdapter(this, orders);
            LinearLayoutManager ll = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(ll);
            recyclerView.setAdapter(orderItemsAdapter);
            orderItemsAdapter.notifyDataSetChanged();
        } else {
            showView(empty_ll);
            hideView(recyclerView);
        }

    }

    private List<Order> getIndividualOrders(List<Order> orders) throws CloneNotSupportedException {
        List<Order> nOrders = new ArrayList<Order>();
        if(orders != null && !orders.isEmpty()) {
           for(Order order : orders) {
               if(order.getProducts().size() > 1){
                    for(Product product : order.getProducts()) {
                        List<Product> products = new ArrayList<>();
                        Order norder = (Order)order.clone();
                        products.add(product);
                        norder.setProducts(products);
                        nOrders.add(norder);
                    }
               } else
                   nOrders.add(order);
           }
        }
        return nOrders;
    }

    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
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
