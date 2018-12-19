package apps.codette.geobuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import apps.codette.forms.Address;
import apps.codette.forms.Order;
import apps.codette.forms.Product;
import apps.codette.geobuy.service.MyFirebaseInstanceIDService;

public class OrderSuccessActivity extends AppCompatActivity {

    Order order;

    TextView view_details;

    Button subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
        String orderJson = getIntent().getStringExtra("order");
        Type type = new TypeToken<Order>() {}.getType();
        order =  new Gson().fromJson(orderJson, type);;
        formSuccess();
        manageClicks();
    }

    private int i=0;

    private void manageClicks() {
        view_details = findViewById(R.id.view_details);
        subscribe = findViewById(R.id.subscribe);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i %2 ==0) {
                    subscribeOrder(true);
                    subscribe.setText("Subscribed");
                    subscribe.setBackground(getResources().getDrawable(R.drawable.selectedbutton));
                    subscribe.setTextColor(getResources().getColor(R.color.white));
                } else {
                    subscribeOrder(false);
                    subscribe.setText("Subscribe");
                    subscribe.setBackground(getResources().getDrawable(R.drawable.selected));
                    subscribe.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                i++;

            }
        });
        view_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOrders();
            }
        });
    }

    private void goToOrders() {
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        startActivity(intent);
    }

    private void subscribeOrder(boolean doSubs) {
        if(doSubs) {
            MyFirebaseInstanceIDService.subscribeTopic(order.getOrderNo());
            toast("Subscribed");
        } else {
            MyFirebaseInstanceIDService.unSubscribeTopic(order.getOrderNo());
            toast("UnSubscribed");
        }

    }


    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }

    private void formSuccess() {
        //price
        int noOf = order.getProducts().size();
        TextView textView = findViewById(R.id.price);
        textView.setText("Total price for "+noOf+" item "+"₹ "+Math.round((order.getTotalamount())));
        setAddressSelected(order.getAddress());
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


    private void closeActivity() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
