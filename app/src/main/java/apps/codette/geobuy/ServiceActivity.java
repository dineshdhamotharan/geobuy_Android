package apps.codette.geobuy;

import android.app.Dialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.HttpGet;
import com.loopj.android.http.RequestParams;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import apps.codette.forms.CategoryMaster;
import apps.codette.forms.Issues;
import apps.codette.geobuy.Constants.GeobuyConstants;
import apps.codette.geobuy.adapters.IssuesAdapter;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class ServiceActivity extends AppCompatActivity {

    Dialog dialog = null;
    EditText phone;
    EditText messsage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        dialog = new Dialog(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getQueries();
        //CardView mail_card_view = findViewById(R.id.mail_card_view);
        CardView callback_card_view = findViewById(R.id.call_back_cardview);
        callback_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(true);
            }
        });
        /*mail_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(false);
            }
        });*/
    }

    private void sendRequest(final boolean callback) {
        dialog.setContentView(R.layout.service_popup);
        phone = dialog.findViewById(R.id.phone);
        messsage = dialog.findViewById(R.id.message);
        dialog.show();
        Button submit = dialog.findViewById(R.id.submit);
        Button cancel = dialog.findViewById(R.id.cancel);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback) {
                    requestCallback(phone.getText().toString());
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void requestCallback(String phoneNo) {
        if(phoneNo != null && !phoneNo.isEmpty() && phoneNo.length() ==10) {
            String msg = messsage.getText().toString();
            RequestParams requestParams = new RequestParams();
            requestParams.put("phone", phoneNo);
            requestParams.put("message",msg);
            RestCall.post("query", requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    toast("Call back initiated successfully");
                    dialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    toast(getResources().getString(R.string.try_later));
                    dialog.dismiss();
                }
            });

        } else {
            phone.setError("Enter valid mobile no");
        }
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

    private void getQueries() {
        RestCall.get("queries", new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                updateQueriesRv(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toast("Failure");
            }
        });
    }

    private void updateQueriesRv(String queries) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Issues>>() {}.getType();
        List<Issues> issues  = gson.fromJson(queries, type);
        RecyclerView recyclerView = findViewById(R.id.issues_rc);
        IssuesAdapter issuesAdapter = new IssuesAdapter(this, issues);
        LinearLayoutManager ll = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(ll);
        recyclerView.setAdapter(issuesAdapter);
        issuesAdapter.notifyDataSetChanged();
    }


    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }
}
