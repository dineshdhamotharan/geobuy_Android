package apps.codette.geobuy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import apps.codette.forms.Category;
import apps.codette.forms.CategoryMaster;
import apps.codette.geobuy.Constants.GeobuyConstants;
import apps.codette.geobuy.adapters.CategoryMasterAdapter;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        getCategoryFromDB();
    }




    private void getCategoryFromDB() {
        RequestParams requestParams = new RequestParams();
        RestCall.get("categories", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getCategory(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error(getResources().getString(R.string.error_network));
            }
        });
    }

    private void error(String msg) {
        ImageView imageView = findViewById(R.id.logo);
        Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
       // Snackbar.make(imageView, msg, Snackbar.LENGTH_SHORT).show();
        finish();
    }

    private void formUiforCategory(String categoryJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<CategoryMaster>>() {}.getType();
        GeobuyConstants.categoryMasters  = gson.fromJson(categoryJson, type);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void getCategory(String categoryJson) {
        JSONObject jsonObject = null;
        //List<Category> categories = null;
        try {
            jsonObject = new JSONObject(categoryJson);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Category>>() {}.getType();
            GeobuyConstants.categories  = gson.fromJson(jsonObject.get("data").toString(), type);
        } catch (JSONException e) {
            error(getResources().getString(R.string.error_network));
        }
        RestCall.get("categorymaster", new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                formUiforCategory(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error(getResources().getString(R.string.error_network));
            }
        });

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }


}
