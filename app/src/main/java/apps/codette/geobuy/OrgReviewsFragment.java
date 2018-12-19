package apps.codette.geobuy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import apps.codette.forms.Organization;
import apps.codette.forms.Review;
import apps.codette.geobuy.Constants.GeobuyConstants;
import apps.codette.geobuy.Constants.OrgService;
import apps.codette.geobuy.adapters.ReviewsAdapter;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

public class OrgReviewsFragment  extends Fragment {

    View rootView;
    Dialog rate_Review_dialog;
    SessionManager sessionManager;
    Map<String, ?> userDetails;
    String orgid;
    ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orgid = (String) getArguments().get("orgid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sessionManager = new SessionManager(this.getContext());
        userDetails = sessionManager.getUserDetails();
        rootView = inflater.inflate(R.layout.fragment_business_reviews, container, false);
        Button rateAndReviewButton = rootView.findViewById(R.id.rate_and_review_button);

        if(OrgService.organization != null && OrgService.organization.getReviews() != null && !OrgService.organization.getReviews().isEmpty()) {
            updateReviewsInView(OrgService.organization.getReviews());
        }
        if(OrgService.organization.getRating() != 0) {
            TextView product_rating_text = rootView.findViewById(R.id.product_rating_text);
            product_rating_text.setText(OrgService.organization.getRating()+"");
        }

        if(userDetails.get("useremail") != null) {
            rateAndReviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    manageRateAndReviewPopup(OrgService.organization.getOrgid());
                }
            });
        } else {
            rateAndReviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToSignIn();
                }
            });
        }
        return rootView;
    }

    private void moveToSignIn() {
        Intent intent = new Intent(this.getContext(), SigninActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        sessionManager = new SessionManager(this.getContext());
        userDetails = sessionManager.getUserDetails();
    }


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
                Organization organization = gson.fromJson(new String(responseBody), type);
                OrgService.organization = organization;
                if(pd != null)
                    pd.dismiss();
                updateReviewsInView(OrgService.organization.getReviews());
                String avgRating = (organization.getRating() >0 ? organization.getRating()+""  : null );
                if(avgRating != null) {
                    avgRating = (avgRating.length() >3 ? avgRating.substring(0,3) : avgRating );
                    TextView product_rating_text = rootView.findViewById(R.id.product_rating_text);
                    product_rating_text.setText(avgRating);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(pd != null)
                    pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    private void manageRateAndReviewPopup(final String orgid) {
        rate_Review_dialog = new Dialog(this.getContext());
        rate_Review_dialog.setContentView(R.layout.rate_review_popup);
        final RatingBar ratingBar =(RatingBar) rate_Review_dialog.findViewById(R.id.product_rating_bar);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float rating = ratingBar.getRating();
                TextView tv = rate_Review_dialog.findViewById(R.id.product_rating_bar_text);
                tv.setText(rating+"");
                return false;
            }
        });

        Button submitButton = rate_Review_dialog.findViewById(R.id.review_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRateAndReview(orgid);
                rate_Review_dialog.dismiss();
            }
        });
        Button cancelButton = rate_Review_dialog.findViewById(R.id.review_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate_Review_dialog.dismiss();
            }
        });
        rate_Review_dialog.show();
    }

    private void submitRateAndReview(String orgId) {
        RatingBar product_rating_bar = rate_Review_dialog.findViewById(R.id.product_rating_bar);
        float rating = product_rating_bar.getRating();
        RatingBar ratingBar =(RatingBar) rate_Review_dialog.findViewById(R.id.product_rating_bar);
        EditText shrtText = (EditText) rate_Review_dialog.findViewById(R.id.short_review);
        EditText reviewText = (EditText) rate_Review_dialog.findViewById(R.id.detailed_review);
        SimpleDateFormat ft = new SimpleDateFormat ("dd, MMM YY");
        String time = ft.format(new Date());

        RequestParams requestParams = new RequestParams();
        requestParams.put("id",orgId);
        requestParams.put("table", GeobuyConstants.ORG_TABLE);
        requestParams.put("rating",ratingBar.getRating());
        requestParams.put("ratings",ratingBar.getRating());
        requestParams.put("heading",shrtText.getText());
        requestParams.put("review",reviewText.getText());
        requestParams.put("time",time);
        requestParams.put("user",(String) userDetails.get("username"));
        requestParams.put("useremail",(String) userDetails.get("useremail"));
        RestCall.post("rateAndReview", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                toast(getResources().getString(R.string.review_done));
                getOrgDetails(orgid);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    private void updateReviewsInView(List<Review> reviews) {
        List<Review> finalReviews = new ArrayList<>();
        for(Review review : reviews) {
            if(review.getHeading() != null && !review.getHeading().isEmpty() && review.getReview() != null && !review.getReview().isEmpty()) {
                finalReviews.add(review);
            }
        }

        TextView product_rating_reviews = rootView.findViewById(R.id.product_rating_reviews);
        if(finalReviews != null && !finalReviews.isEmpty())
            product_rating_reviews.setText(finalReviews.size() +" " + this.getContext().getResources().getString(R.string.reviews));
        else
            product_rating_reviews.setText("0 " + this.getContext().getResources().getString(R.string.reviews));
        RecyclerView recyclerView = rootView.findViewById(R.id.business_reviews);
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(this.getContext(), finalReviews);
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(reviewsAdapter);
        //recyclerView.setNestedScrollingEnabled(false);
    }

    private void toast(String s) {
        Toast.makeText(this.getContext(), ""+s, Toast.LENGTH_LONG).show();
    }
}
