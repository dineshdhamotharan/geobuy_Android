package apps.codette.geobuy;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import apps.codette.forms.Organization;
import apps.codette.geobuy.Constants.OrgService;
import apps.codette.geobuy.adapters.MyCustomPagerAdapter;
import apps.codette.geobuy.service.MyFirebaseInstanceIDService;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;
import me.relex.circleindicator.CircleIndicator;

@SuppressLint("ValidFragment")
public class OrgDetailsFragment extends Fragment{

    String orgid;
    Organization organization;
    Context ctx;
    Map<String, ?> userDetails;
    ProgressDialog pd;

    TextView followersCount;

    SessionManager sessionManager;

    Toolbar toolbar;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    View rootView = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orgid = (String) getArguments().get("orgid");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        organization = OrgService.organization;
        rootView = inflater.inflate(R.layout.fragment_business_details, container, false);
        sessionManager = new SessionManager(this.getContext());
        userDetails = sessionManager.getUserDetails();
        toolbar = (Toolbar) this.getActivity().findViewById(R.id.toolbar);
        if(OrgService.organization == null)
            getOrgDetails(orgid);
        else {
            organization = OrgService.organization;
            toolbar.setTitle(organization.getOrgname());
            if(pd != null)
                pd.dismiss();
            formOrgDetails();
        }
        return rootView;
    }

    /**
     *
     * @param orgId
     */
    private void getOrgDetails(String orgId) {
        pd = new ProgressDialog(this.getContext());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        pd.show();
        RequestParams requestParams = new RequestParams();
        requestParams.put("orgid",orgId);
        RestCall.post("orgDetails", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Gson gson = new Gson();
                Type type = new TypeToken<Organization>() {}.getType();
                organization = gson.fromJson(new String(responseBody), type);
                OrgService.organization = organization;
                toolbar.setTitle(organization.getOrgname());
                if(pd != null)
                    pd.dismiss();
                formOrgDetails();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(pd != null)
                    pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });
    }


    private void formOrgDetails()  {
        ImageView logo = rootView.findViewById(R.id.org_logo);
        final Button btnfollow = rootView.findViewById(R.id.btnfollow);
        if(organization.getLogo() != null) {
            Glide.with(this)
                    .load(organization.getLogo())
                    .into(logo);
        }
        handleFollowButtonEvents(btnfollow);
        TextView business_phone = rootView.findViewById(R.id.business_phone);
        business_phone.setText(organization.getOrgphoneno());
        TextView business_email = rootView.findViewById(R.id.business_email);
        business_email.setText(organization.getOrgemail());
        TextView business_address = rootView.findViewById(R.id.business_address);
        business_address.setText(organization.getOrgaddress());
        ViewPager viewPager = rootView.findViewById(R.id.org_images);

        CircleIndicator indicator = (CircleIndicator) rootView.findViewById(R.id.indicator);
        String images [] = organization.getImages();
        if(images !=  null && images.length > 0) {
            MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(this.getActivity(), images);
            viewPager.setAdapter(myCustomPagerAdapter);
            indicator.setViewPager(viewPager);
        }
    }

    private void handleFollowButtonEvents(final Button btnfollow) {
        followersCount = rootView.findViewById(R.id.followers_count);

        if(organization.getFollowers() != null && organization.getFollowers().length > 0) {
            followersCount.setText(organization.getFollowers().length+" Followers");
            String email = (String) userDetails.get("useremail");
            List<String> followers = Arrays.asList(organization.getFollowers());
            if(email != null && followers.contains(email)){
                btnfollow.setText("Following");
                btnfollow.setTextColor(getResources().getColor(R.color.white));
                btnfollow.setBackground(getResources().getDrawable(R.drawable.selectedbutton));
                btnfollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followOrg(organization.getOrgid(), btnfollow,false, organization.getFollowers().length-1);
                    }
                });
            } else {
                btnfollow.setText("Follow");
                btnfollow.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnfollow.setBackground(getResources().getDrawable(R.drawable.selected));
                btnfollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followOrg(organization.getOrgid(), btnfollow,true, organization.getFollowers().length+1);
                    }
                });
            }
        } else {
            followersCount.setText("No Followers");
            btnfollow.setText("Follow");
            btnfollow.setTextColor(getResources().getColor(R.color.colorPrimary));
            btnfollow.setBackground(getResources().getDrawable(R.drawable.selected));
            btnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followOrg(organization.getOrgid(), btnfollow,true, 1);
                }
            });
        }
    }


    private void followOrg(final String orgid, final Button btnfollow, final boolean follow, final int count) {
        if(userDetails.get("useremail") != null) {
            final String email = (String) userDetails.get("useremail");
            RequestParams requestParams = new RequestParams();
            requestParams.put("orgid", orgid);
            requestParams.put("follower", email);
            requestParams.put("follow", follow);
            pd = new ProgressDialog(this.getContext());
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setIndeterminate(true);
            pd.setMessage("Loading");
            pd.show();
            RestCall.post("followOrg", requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    pd.dismiss();
                    getOrgDetails(orgid);
                    if(follow)
                        MyFirebaseInstanceIDService.subscribeTopic(orgid);
                    else
                        MyFirebaseInstanceIDService.unSubscribeTopic(orgid);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    pd.dismiss();
                    toast(getResources().getString(R.string.try_later));
                }
            });
        } else {
            Intent intent = new Intent(this.getContext(), SigninActivity.class);
            startActivity(intent);
        }


    }


    @Override
    public void onResume(){
        super.onResume();
        sessionManager = new SessionManager(this.getContext());
        userDetails = sessionManager.getUserDetails();
    }

    private void toast(String s) {
        Toast.makeText(this.getContext(), ""+s, Toast.LENGTH_SHORT).show();
    }

}
