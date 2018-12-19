package apps.codette.geobuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import apps.codette.forms.Brand;
import apps.codette.geobuy.adapters.BrandAdapter;
import apps.codette.geobuy.adapters.PriceFilterAdapter;
import apps.codette.geobuy.adapters.SubCategoryAdapter;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;

public class FilterActivity extends AppCompatActivity {

    RecyclerView price_rc;
    ProgressDialog pd;
    List<Brand> brands;
    Button apply_filter;
    String selectedBrandsFromPr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        price_rc = findViewById(R.id.price_rc);
        List<String> prices = new ArrayList<String>();
        prices.add("below 1000");
        prices.add("1000 to 2000");
        prices.add("2000 to 5000");
        prices.add("Above 5000");
        PriceFilterAdapter priceFilterAdapter = new PriceFilterAdapter(this, prices);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,  LinearLayoutManager.HORIZONTAL, false);
        price_rc.setLayoutManager(mLayoutManager);
        price_rc.setItemAnimator(new DefaultItemAnimator());
        price_rc.setAdapter(priceFilterAdapter);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        apply_filter = findViewById(R.id.apply_filter);
        Intent intent = getIntent();
        String subcat = intent.getStringExtra("subcategory");
        selectedBrandsFromPr = intent.getStringExtra("brands");
        getAllBrands(subcat);
        apply_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                int c =0;
                StringBuffer brandIds = new StringBuffer("");
                for(Brand brand : brands) {
                    if(selectedBrands[c]==1) {
                        brandIds.append(brand.getId()+",");
                    }
                    c++;
                }
                returnIntent.putExtra("brands",brandIds.toString());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }


    private void getAllBrands(String subcat) {
        RequestParams params = new RequestParams();
        pd.show();
        String url = "brands";
        if(subcat != null && !subcat.isEmpty()) {
            url = url+"?subcategory="+subcat;
        }
        RestCall.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Brand>>() {}.getType();
                brands = gson.fromJson(new String(responseBody), type);
                updateRecyclerView(brands);
                pd.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toast(getResources().getString(R.string.try_later));
                pd.dismiss();
            }
        });
    }

    private void updateRecyclerView(List<Brand> brands) {
        if(brands != null && !brands.isEmpty()) {
            BrandAdapter brandAdapter = null;
            if(selectedBrandsFromPr != null)
                brandAdapter = new BrandAdapter(this, brands, selectedBrandsFromPr.split(","));
            else
                brandAdapter = new BrandAdapter(this, brands, null);
            RecyclerView recyclerView = findViewById(R.id.brand_rl);
            //recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setAdapter( brandAdapter );
        }
    }

    private Integer[] selectedBrands;
    private Integer[] selectedPrices;

    public void setSelectedBrands(Integer[] selectedBrands){
        this.selectedBrands = selectedBrands;
        if(selectedBrands != null){
            Log.d("selectedBrands", selectedBrands.length +"-------------------------------------");
            for(int i=0; i<selectedBrands.length; i++) {
                Log.d("selectedBrands", selectedBrands[i]+"");
            }
        }

    }

    public void setSelectedPrices(Integer[] selectedPrices){
        this.selectedPrices = selectedPrices;
    }

    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }
}
