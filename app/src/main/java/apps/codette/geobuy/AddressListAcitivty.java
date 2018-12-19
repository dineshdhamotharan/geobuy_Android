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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import apps.codette.forms.Address;
import apps.codette.forms.User;
import apps.codette.geobuy.adapters.AddressAdapter;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

public class AddressListAcitivty extends AppCompatActivity {


    SessionManager sessionManager;
    public ProgressDialog pd;
    public Map<String, ?> userDetails;
    private int UPDATE_REQ = 2;
    private int ADDRESS_REQ =1;
    RecyclerView recyclerView;
    LinearLayout empty_ll;
    Button addButton;

    public int getUPDATE_REQ(){
        return UPDATE_REQ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
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
        userDetails = sessionManager.getUserDetails();
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        recyclerView = findViewById(R.id.address_recycler);
        empty_ll = findViewById(R.id.empty_ll);
        addButton = findViewById(R.id.add_address_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddresActivty();
            }
        });
        getUserAddress();
    }

    private void openAddresActivty() {
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
                List<Address> addresses = user.getAddress();
                if(addresses != null && !addresses.isEmpty()){
                    showView(recyclerView);
                    hideView(empty_ll);
                    updateAddressInRecyclerView(addresses);
                } else {
                    showView(empty_ll);
                    hideView(recyclerView);
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

    private void updateAddressInRecyclerView(List<Address> addresses) {
        AddressAdapter addressAdapter = new AddressAdapter(this, addresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(addressAdapter);
    }


    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

       // if(requestCode == UPDATE_REQ) {
            if(resultCode == RESULT_OK){
                getUserAddress();
            }
     //   } else if(requestCode == ADDRESS_REQ) {
           /* if(resultCode == RESULT_OK){
                getUserAddress();
            }*/
      //  }
    }


    private void moveToAddAddress() {
        Intent intent = new Intent(this, AddressActivity.class);
        startActivityForResult(intent, ADDRESS_REQ);
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
