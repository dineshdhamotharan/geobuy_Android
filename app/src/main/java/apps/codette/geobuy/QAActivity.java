package apps.codette.geobuy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.List;

import apps.codette.forms.Issues;
import apps.codette.forms.QA;
import apps.codette.geobuy.adapters.QAAdapter;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;

public class QAActivity extends AppCompatActivity {

    Dialog dialog = null;
    EditText phone;
    EditText messsage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);
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
        String qas = intent.getStringExtra("qas");
        Gson gson = new Gson();
        Type type = new TypeToken<List<QA>>() {}.getType();
        List<QA> qasList  = gson.fromJson(qas, type);
        RecyclerView recyclerView = findViewById(R.id.qa_rc);
        QAAdapter qaAdapter = new QAAdapter(this, qasList);
        LinearLayoutManager ll = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(ll);
        recyclerView.setAdapter(qaAdapter);
        CardView callback_card_view = findViewById(R.id.call_back_cardview);
        callback_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(true);
            }
        });
    }

    private void sendRequest(final boolean callback) {
        dialog = new Dialog(this);
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

    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }
}
