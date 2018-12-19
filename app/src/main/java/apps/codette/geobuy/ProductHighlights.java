package apps.codette.geobuy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import apps.codette.forms.Product;
import apps.codette.geobuy.adapters.ProductHighlightsAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductHighlights.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductHighlights#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductHighlights extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String highlightsJson;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProductHighlights() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment product_highlights.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductHighlights newInstance(String param1, String param2) {
        ProductHighlights fragment = new ProductHighlights();
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
            highlightsJson = getArguments().getString("highlights");
           // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_highlights, container, false);
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> highlights = gson.fromJson(highlightsJson, type);
        formProductHighLights(view, highlights);
        return view;
    }

    public int getHieght(){
        return view.getHeight();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void formProductHighLights(View view, List<String> highLights) {
        LinearLayout ll = view.findViewById(R.id.products_highlight_layout);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.product_highlights_recyclerview);
        if(highLights != null) {
            showView(ll);
            ProductHighlightsAdapter adapter = new ProductHighlightsAdapter(this.getContext(), highLights);
            LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this.getContext());
            rv.setLayoutManager(linearLayoutManager);
            rv.setAdapter(adapter);
        } else {
            hideView(ll);
        }
       /* ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_UP);*/
        //rv.setNestedScrollingEnabled(false);
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
}
