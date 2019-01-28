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

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import apps.codette.forms.NotificationForm;
import apps.codette.forms.Product;
import apps.codette.geobuy.adapters.NotoficationsAdapter;
import apps.codette.geobuy.adapters.WishlistProductsAdapter;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

public class NotificationActivity extends AppCompatActivity {
    ProgressDialog pd;

    LinearLayout notification_ll, empty_ll;
    SessionManager sessionManager;
    Map<String, ?> userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Notifications");
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
        notification_ll = findViewById(R.id.notification_ll);
        empty_ll = findViewById(R.id.empty_ll);
        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUserDetails();
        getAllNotifications();

    }

    private void getAllNotifications() {
        pd.show();
        RequestParams requestParams = new RequestParams();
        requestParams.put("useremail", userDetails.get("useremail"));
        RestCall.get("notifications", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                updateNotificationsInRecyclerView(new String(responseBody));
                pd.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    private void updateNotificationsInRecyclerView(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<NotificationForm>>() {}.getType();
        List<NotificationForm> notificationForms = gson.fromJson(json, type);
        if(notificationForms != null && !notificationForms.isEmpty()) {
            NotoficationsAdapter notAdapter = new NotoficationsAdapter(this, notificationForms);
            RecyclerView recyclerView = findViewById(R.id.notifications);
            LinearLayoutManager ll = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(ll);
            recyclerView.setAdapter(notAdapter);
            notAdapter.notifyDataSetChanged();
        } else {
            hideView(notification_ll);
            showView(empty_ll);
        }

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
