package apps.codette.geobuy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import apps.codette.geobuy.adapters.MyCustomPagerAdapter;
import me.relex.circleindicator.CircleIndicator;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
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
        final String[] images =  getIntent().getStringArrayExtra("images");
        ViewPager product_viewPager = findViewById(R.id.product_viewPager);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        MyCustomPagerAdapter pagerAdapter = new MyCustomPagerAdapter( this, images) ;
        pagerAdapter.setDoOpen(false);
        product_viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(product_viewPager);
    }

}
