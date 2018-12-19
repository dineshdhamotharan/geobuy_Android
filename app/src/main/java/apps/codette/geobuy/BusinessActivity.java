package apps.codette.geobuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import apps.codette.forms.Organization;
import apps.codette.geobuy.Constants.OrgService;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

public class BusinessActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private ProgressDialog pd;

    private Organization organization;
    Toolbar toolbar;


    OrgDetailsFragment orgDetailsFragment;

    OrgProductsFragment orgProductsFragment;

    OrgReviewsFragment orgReviewsFragment;

    String orgId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        orgId = intent.getStringExtra("orgid");
        getOrgDetails(orgId);
    }


    private void getOrgDetails(String orgId) {
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        pd.show();
        RequestParams requestParams = new RequestParams();
        requestParams.put("orgid",orgId);
        RestCall.get("org/"+orgId, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Gson gson = new Gson();
                Type type = new TypeToken<Organization>() {}.getType();
                organization = gson.fromJson(new String(responseBody), type);
                OrgService.organization = organization;
                toolbar.setTitle(organization.getOrgname());
                if(pd != null)
                    pd.dismiss();
                assignTabLayout();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(pd != null)
                    pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    private void assignTabLayout() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.products) +" ("+(organization.getProducts() != null ? organization.getProducts().size() : 0)+")");
        tabLayout.getTabAt(2).setText(getResources().getString(R.string.ratings) +" ("+(organization.getReviews() != null ? organization.getReviews().size() : 0)+")");
        //tabLayout.getTabAt(1).setText(getResources().getString(R.string.reviews) +" ("+(organization.getR != null ? organization.getProducts().size() : 0)+")");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_business, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {





    }






    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.i("sectionNumber",position + 1+"");
            position = position+1;
            if(position == 1) {
                if(orgDetailsFragment == null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("orgid", orgId);
                    orgDetailsFragment = new OrgDetailsFragment();
                    orgDetailsFragment.setArguments(bundle);
                   // orgDetailsFragment.setOrganization(organization, userDetails);
                }
                return orgDetailsFragment;
            } else if(position == 2) {
                if(orgProductsFragment == null) {
                    orgProductsFragment = new OrgProductsFragment();
                }
                return orgProductsFragment;
            } else if(position == 3) {
                Bundle bundle = new Bundle();
                bundle.putString("orgid", orgId);
                if(orgReviewsFragment == null) {
                    orgReviewsFragment = new OrgReviewsFragment();
                }
                orgReviewsFragment.setArguments(bundle);
                return orgReviewsFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }
}
