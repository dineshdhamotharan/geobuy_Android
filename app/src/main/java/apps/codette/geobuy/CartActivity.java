package apps.codette.geobuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apps.codette.forms.Product;
import apps.codette.forms.User;
import apps.codette.geobuy.Constants.OrgService;
import apps.codette.geobuy.adapters.CartProductsAdapter;
import apps.codette.geobuy.adapters.ProductAdapter;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

public class CartActivity extends AppCompatActivity {

    SessionManager sessionManager;
    ProgressDialog pd;
    Map<String, ?> userDetails;
    private List<Product> products;

    private int ORDER_REQ = 100;

    RelativeLayout rll;
    LinearLayout ell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kart);
        sessionManager = new SessionManager(this);
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
        Intent intent = getIntent();
        userDetails = sessionManager.getUserDetails();
        String cartProducts = (String) userDetails.get("cart");
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        getProductsInCart(cartProducts);
        Button continue_cart = findViewById(R.id.continue_cart);
        continue_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToPayment();
            }
        });
    }

    private void moveToPayment() {
        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        String productsList = new Gson().toJson(products);
        intent.putExtra("products", productsList);
        startActivityForResult(intent, ORDER_REQ);
    }

    private void getProductsInCart(String cartProducts) {
        rll = findViewById(R.id.cart_ll);
        ell = findViewById(R.id.empty_ll);
        if(cartProducts != null && !cartProducts.isEmpty()) {
            hideView(ell);
            showView(rll);
            pd.show();
            RequestParams requestParams = new RequestParams();
            requestParams.put("products", cartProducts);
            requestParams.put("useremail", userDetails.get("useremail"));
            RestCall.post("getProductsFromCart", requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    updateCartInRecyclerView(new String(responseBody));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    pd.dismiss();
                    toast(getResources().getString(R.string.try_later));
                }
            });
        } else {
            hideView(rll);
            showView(ell);
        }
    }

    private void updateCartInRecyclerView(String responseBody) {
        //cart_products_layout
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {}.getType();
        User user = gson.fromJson(responseBody, type);
        List<Product> productList = new ArrayList<>();
        if(user.getProducts() != null && !user.getProducts().isEmpty() && user.getCart() != null && !user.getCart().isEmpty() ){
            Map<String, Integer> productQuanityMap = new HashMap<>();
            for(Product product : user.getCart()){
                productQuanityMap.put(product.getId(), product.getQuanity());
            }
            for(Product product : user.getProducts()){
                product.setQuanity(productQuanityMap.get(product.getId()));
                productList.add(product);
            }
            setTotalAmount(productList);
            CartProductsAdapter productAdapter = new CartProductsAdapter(this, productList);
            RecyclerView recyclerView = findViewById(R.id.cart_products);
            LinearLayoutManager ll = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(ll);
            recyclerView.setAdapter(productAdapter);
            productAdapter.notifyDataSetChanged();
        } else {
            hideView(rll);
            showView(ell);
            SharedPreferences.Editor editor = sessionManager.getEditor();
            editor.putString("cart", "");
            sessionManager.put(editor);
            userDetails = sessionManager.getUserDetails();
        }
        pd.dismiss();
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

    public void setTotalAmount(List<Product> products) {
        float totalamount=0;
        this.products = products;
        float amountToBePaid;
        for(Product product : products){
            float discount = (Float.valueOf(product.getOffer()) / 100) * product.getPrice();
            amountToBePaid = (product.getPrice()-discount)* product.getQuanity();
            totalamount = totalamount + amountToBePaid;
            product.setAmountToBePaid(amountToBePaid);
            product.setReviews(null);
            product.setRatings(null);
            product.setHighlights(null);
        }
        TextView cart_total_amount = findViewById(R.id.cart_total_amount);
        cart_total_amount.setText("₹ " + totalamount);
    }


    public void syncWithUserCart(Product product) {
        if(product.getQuanity() == 0){
            StringBuilder prd = new StringBuilder();
            String products = (String) userDetails.get("cart");
            String[] productArry = products.split(",");
            for(int i=0; i<productArry.length; i++){
                if(!product.getId().equalsIgnoreCase(productArry[i]))
                    prd.append(","+productArry[i]);
            }
            SharedPreferences.Editor editor = sessionManager.getEditor();
            editor.putString("cart", productArry.length > 1 ? prd.substring(1) : "");
            sessionManager.put(editor);
            userDetails = sessionManager.getUserDetails();
        }
        RequestParams requestParams = new RequestParams();
        requestParams.put("cart",new Gson().toJson(new Product(product.getId(), product.getQuanity())));
        requestParams.put("useremail",userDetails.get("useremail"));
        RestCall.post("addToCart", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //toast(getResources().getString(R.string.review_done));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toast(getResources().getString(R.string.try_later));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ORDER_REQ || requestCode == 500) {
            if(resultCode == RESULT_OK){
                clearProductsAndSync();
            }
        }
    }

    private void clearProductsAndSync() {
        products = new ArrayList<>();
        SharedPreferences.Editor editor = sessionManager.getEditor();
        editor.putString("cart","");
        sessionManager.put(editor);
        finish();
      //  toast("Go to Order Detail");
    }
}
