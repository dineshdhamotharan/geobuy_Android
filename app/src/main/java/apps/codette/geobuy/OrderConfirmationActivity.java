package apps.codette.geobuy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import apps.codette.forms.Address;
import apps.codette.forms.Order;
import apps.codette.forms.Product;
import apps.codette.forms.Shipping;
import apps.codette.forms.User;
import apps.codette.geobuy.Constants.OrgService;
import apps.codette.geobuy.service.MyFirebaseInstanceIDService;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;



public class OrderConfirmationActivity extends AppCompatActivity implements PaymentResultListener {

    SessionManager sessionManager;
    ProgressDialog pd;
    Map<String, ?> userDetails;
    private int ADDRESS_REQ =1;
    Button cod;
    Button pay;
    Button takeaway;

    Order order = null;
    Button placeOrderButton;

    ScrollView scroll_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
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
        sessionManager = new SessionManager(this);
        scroll_view = findViewById(R.id.scroll_view);
        userDetails = sessionManager.getUserDetails();

        if(userDetails.get("useremail") != null) {
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setIndeterminate(true);
            pd.setMessage("Loading");
            getUserAddress();
            manageButtonClicks();
            Intent intent = getIntent();
            String products = intent.getStringExtra("products");
            Type type = new TypeToken<List<Product>>() {}.getType();
            List<Product> productList = new Gson().fromJson(new String(products), type);
            TextView amountView = findViewById(R.id.total_amount);
            float total = getTotalAmount(productList);
            amountView.setText(Math.round((total))+"");
            try {
                String email = userDetails.get("useremail").toString();
                String resultString = email.replaceAll("\\P{L}", "");
                SimpleDateFormat sf = new SimpleDateFormat("YYMMddHHmmssSSS");
                String id = "OD"+resultString.substring(0,4)+sf.format(new Date());
                order = new Order(id);
            } catch (Exception ex) {
                SimpleDateFormat sf = new SimpleDateFormat("YYMMddHHmmssSSS");
                String id = "OD"+sf.format(new Date());
                order = new Order(id);
            }
            order.setTotalamount(total);
            order.setProducts(productList);
        } else {
            Intent intent = new Intent(this, SigninActivity.class);
            startActivity(intent);
        }
        Checkout.preload(getApplicationContext());
    }



    private void manageButtonClicks() {
        Button add_address_button = findViewById(R.id.add_address_button);
        cod = findViewById(R.id.cod);
        pay = findViewById(R.id.paytm);
        takeaway = findViewById(R.id.takeaway);
        add_address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToAddAddress();
            }
        });
        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayment("COD");
            }
        });
        takeaway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayment("TA");
            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayment("PAY");
            }
        });
        placeOrderButton = findViewById(R.id.place_order);
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder(order);
            }
        });
    }

    protected void placeOrder(Order order) {
        order.setOrdertime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        order.setUseremail((String) userDetails.get("useremail"));
        order.setUserid((String) userDetails.get("userid"));
        if(order.getAddress() == null) {
            toast("Add Address to place order");
        } else {
            if(order.getPaymentmode() == null) {
                toast("Select payment mode to place order");
            }
            else if(order.getPaymentmode().equalsIgnoreCase("COD") || order.getPaymentmode().equalsIgnoreCase("TA")) {
                order.setPaymentstatus("NOTPAID");
                saveOrder(order);

            }
            else if(order.getPaymentmode().equalsIgnoreCase("PAY")) {
                startPayment();
            }
        }
    }

    private void goToOrderSuccess() {
        Intent intent = new Intent(this, OrderSuccessActivity.class);
        intent.putExtra("order", new Gson().toJson(order));
        startActivityForResult(intent, 500);
    }

    private void goBackToCart() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void moveToAddAddress() {
        Intent intent = new Intent(this, AddressActivity.class);
        startActivityForResult(intent, ADDRESS_REQ);
    }

    private void getUserAddress() {
        String useremail = (String) userDetails.get("useremail");
        pd.show();
        RequestParams requestParams = new RequestParams();
        requestParams.put("useremail", useremail);
        final LinearLayout adressll = findViewById(R.id.delivery_address_ll);
        final LinearLayout payment_address_ll = findViewById(R.id.payment_address_ll);
        RestCall.post("syncUser", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Type type = new TypeToken<User>() {}.getType();
                User user = new Gson().fromJson(new String(responseBody), type);
                Address address;
                if(user.getAddress() != null && !user.getAddress().isEmpty()) {
                    showView(adressll);
                    hideView(payment_address_ll);
                    int pos = 0;
                    final List<Address> addresses = user.getAddress();
                    for(int i=0; i< addresses.size(); i++){
                        if(addresses.get(i).isIsdefault()) {
                            pos = i;
                        }
                    }
                    address = addresses.get(pos);

                    setAddressSelected(address);
                    TextView change_address = findViewById(R.id.change_address);
                    change_address.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openAlert(addresses);
                        }
                    });
                } else {
                    hideView(adressll);
                    showView(payment_address_ll);
                }
                pd.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ADDRESS_REQ) {
            if(resultCode == RESULT_OK){
                getUserAddress();
            }
        }
        if (requestCode == 100) {
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(this, "Payment Success.", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "Payment Cancelled | Failed.", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
        if (requestCode == 500) {
            switch (resultCode) {
                case RESULT_OK:
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                    break;
            }

        }
    }



    private void openAlert(final List<Address> addresses) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        AlertDialog.Builder builderSingle = new android.app.AlertDialog.Builder(this);
        builderSingle.setTitle("Select Address :");
        for(Address address : addresses) {
            arrayAdapter.add(address.toString());
        }
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String address = arrayAdapter.getItem(which);
                setAddressSelected(addresses.get(which));
            }
        });

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setPositiveButton("Add New", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                moveToAddAddress();
            }
        });

        builderSingle.show();
    }

    private void setAddressSelected(Address address) {
        TextView name = findViewById(R.id.name);
        name.setText(address.getName());
        TextView doorno = findViewById(R.id.doorno);
        doorno.setText(address.getDoorno());
        TextView street = findViewById(R.id.street);
        street.setText(address.getStreet());
        TextView city = findViewById(R.id.district);
        city.setText(address.getCity());
        TextView state = findViewById(R.id.state);
        state.setText(address.getState());
        TextView pincode = findViewById(R.id.pincode);
        pincode.setText(address.getPincode());
        order.setAddress(address);
    }

    private void setPayment(String val) {
        order.setPaymentmode(val);
        order.setPaymentstatus("NOTPAID");
        cod.setBackground(getResources().getDrawable(R.drawable.unselected));
        cod.setTextColor(getResources().getColor(R.color.black_overlay));
        pay.setBackground(getResources().getDrawable(R.drawable.unselected));
        pay.setTextColor(getResources().getColor(R.color.black_overlay));
        takeaway.setBackground(getResources().getDrawable(R.drawable.unselected));
        takeaway.setTextColor(getResources().getColor(R.color.black_overlay));
        switch(val) {
            case "COD" : {
                cod.setBackground(getResources().getDrawable(R.drawable.selected));
                cod.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            }
            case "PAY" : {
                pay.setBackground(getResources().getDrawable(R.drawable.selected));
                pay.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            }
            case "TA" : {
                takeaway.setBackground(getResources().getDrawable(R.drawable.selected));
                takeaway.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            }
        }
    }


    public float getTotalAmount(List<Product> products) {
        float totalamount=0;
        for(Product product : products){
            totalamount= totalamount +  product.getAmountToBePaid();
        }
        return totalamount;
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

    private void toast(String text) {

        Snackbar.make(scroll_view, text, Snackbar.LENGTH_SHORT).show();
        //Toast.makeText(this, ""+text, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPaymentSuccess(String s) {
      //  toast(s);
        order.setPaymentId(s);
        order.setPaymentstatus("PAID");
        saveOrder(order);
    }

    private void saveOrder(final Order order) {
            pd.show();
            Map<String, Order> orderMap = getOrgBasedOrders(order);
       // for(Product product : order.getProducts()) {
            Order nOrder = null;
            try {
                nOrder = (Order) order.clone();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            nOrder.setProducts(null);
            List<Product> pr = new ArrayList<>();
        //    pr.add(order.getProducts().get(pos));
            nOrder.setProducts(pr);


            RequestParams requestParams = new RequestParams();
            requestParams.put("order", new Gson().toJson(orderMap));

            RestCall.post("saveOrder", requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    pd.dismiss();
                    goToOrderSuccess();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    pd.dismiss();
                    toast(getResources().getString(R.string.try_later));
                }
            });
      //  }



    }

    private Map<String, Order> getOrgBasedOrders(Order order) {
        Map<String, Order> orgBasedOrder = new HashMap<String, Order>();
        for(Product product : order.getProducts()) {
            if(!orgBasedOrder.containsKey(product.getOrgid())) {
                Order nOrder = null;
                try {
                    nOrder = (Order) order.clone();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                nOrder.setProducts(null);
                List<Product> pr = new ArrayList<Product>();
                pr.add(product);
                SimpleDateFormat sf = new SimpleDateFormat("YYMMddHHmmssSSS");
                String id = "ODSUB"+sf.format(new Date());
                MyFirebaseInstanceIDService.subscribeTopic(id);
                nOrder.setId(id);
                nOrder.setProducts(pr);
                nOrder.setSubtotalamount(product.getAmountToBePaid());
                orgBasedOrder.put(product.getOrgid(), nOrder);
            } else {
                Order ordern = orgBasedOrder.get(product.getOrgid());
                List<Product> productsN = ordern.getProducts();
                productsN.add(product);
                ordern.setProducts(productsN);
                ordern.setSubtotalamount(ordern.getSubtotalamount()+product.getAmountToBePaid());
                orgBasedOrder.put(product.getOrgid(), ordern);
            }
        }
        return orgBasedOrder;
    }

    @Override
    public void onPaymentError(int i, String s) {
        toast(getResources().getString(R.string.payment_cancelled));
    }

    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: Rentomojo || HasGeek etc.
             */
            options.put("name", "Geobuy");

            /**
             * Description can be anything
             * eg: Order #123123
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Order #"+order.getOrderNo());

            options.put("currency", "INR");

            /**
             * Amount is always passed in PAISE
             * Eg: "500" = Rs 5.00
             */
            options.put("amount", order.getTotalamount()*100);

            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e("Error", "Error in starting Razorpay Checkout", e);
        }
    }

}
