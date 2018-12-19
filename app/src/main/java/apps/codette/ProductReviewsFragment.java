package apps.codette;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import apps.codette.forms.Review;
import apps.codette.geobuy.R;
import apps.codette.geobuy.adapters.ReviewsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductReviewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductReviewsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String reviewsJson;
    private String mParam2;
    View view;

    private OnFragmentInteractionListener mListener;

    public ProductReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductReviewsFragment newInstance(String param1, String param2) {
        ProductReviewsFragment fragment = new ProductReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reviewsJson = getArguments().getString("reviews");
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_reviews, container, false);
        Gson gson = new Gson();
        Type type = new TypeToken< List<Review>>() {}.getType();
        List<Review> reviews = gson.fromJson(reviewsJson, type);
        formReviews(view, reviews);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public int getHieght(){
        return view.getHeight();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void formReviews(View view, List<Review> reviews) {
        TabLayout tabLayout = (TabLayout) this.getActivity().findViewById(R.id.tabs);
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.reviews) +" ("+(reviews != null ? reviews.size() : 0)+")");
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.product_reviews_recyclerview);
        if(reviews != null) {
            ReviewsAdapter adapter = new ReviewsAdapter(this.getContext(), reviews);
            LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this.getContext());
            rv.setLayoutManager(linearLayoutManager);
            rv.setAdapter(adapter);
          //  rv.setNestedScrollingEnabled(false);
        } else
            hideView(rv);

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
