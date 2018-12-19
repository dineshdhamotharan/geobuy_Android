package apps.codette.geobuy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import apps.codette.forms.Product;
import apps.codette.forms.User;
import apps.codette.forms.UserFragmentItem;
import apps.codette.geobuy.adapters.UserFragmentItemAdapter;
import apps.codette.geobuy.service.MyFirebaseInstanceIDService;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "UserFragment" ;
    private FirebaseAuth mAuth;
    String personId;
    private int RC_SIGN_IN = 9001;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String userName;
    private String userEmail;
    private String userImg;
    private String userPhoneNumber;
    private String userId;

    private GoogleApiClient mGoogleApiClient;
    SessionManager sessionManager;

    Button googleButton;

    ProgressDialog pd;

    LinearLayout layout;
    LinearLayout accounLatout;

    private boolean doClose;
    List<UserFragmentItem> userFragmentItems = null;

    //MainActivity mainActivity;

    public UserFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //mainActivity = (MainActivity) this.getActivity();
        //mainActivity.setModule("USERFRAGMENT");
        // Inflate the layout for this fragment
        userFragmentItems = new ArrayList<>();
        userFragmentItems.add(new UserFragmentItem(R.drawable.cart_black, "My Cart"));
        userFragmentItems.add(new UserFragmentItem(R.drawable.basketdarkbg, "My Orders"));
        userFragmentItems.add(new UserFragmentItem(R.drawable.ic_notifications_black_24dp, "Notifications"));
        userFragmentItems.add(new UserFragmentItem(R.drawable.favorite_black, "Wish list"));
        userFragmentItems.add(new UserFragmentItem(R.drawable.ic_subject_black_24dp, "My Address"));
        userFragmentItems.add(new UserFragmentItem(R.drawable.supervisor_black, "Support"));
        userFragmentItems.add(new UserFragmentItem(R.drawable.copyright_black, "Legal"));
        View view = inflater.inflate(R.layout.fragment_user, container, false);
       // mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this.getContext());
        layout = view.findViewById(R.id.signin_layout);
        accounLatout = view.findViewById(R.id.user_account_view);
        Map<String, ?> userDetails = sessionManager.getUserDetails();
        if(userDetails != null && !userDetails.isEmpty() && userDetails.get("useremail") != null)
        {
            hideView(layout);
            showView(accounLatout);

            TextView user_name = this.getActivity().findViewById(R.id.user_name);
            TextView user_email = this.getActivity().findViewById(R.id.user_email);
            ImageView userImage = this.getActivity().findViewById(R.id.user_image);
            user_name.setText((String)userDetails.get("username"));
            user_email.setText((String)userDetails.get("useremail"));
           // MyFirebaseInstanceIDService.subscribeTopic((String)userDetails.get("useremail"));
            if(userDetails.get("image") != null && !userDetails.get("image").toString().equalsIgnoreCase("null"))
                Glide.with(this.getContext()).load((String)userDetails.get("image")).into(userImage);

            RecyclerView recyclerView = view.findViewById(R.id.user_item);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            recyclerView.setAdapter(new UserFragmentItemAdapter(this.getContext(),  userFragmentItems));
        } else {
            hideView(accounLatout);
            showView(layout);

            initializeGooglePlusSettings();
            googleButton = view.findViewById(R.id.google_signin);
            googleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    googleSignIn();
                }
            });
        }

        return view;
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();

                userName = account.getDisplayName(); //this is the name gotten from the Google Account, you can choose to store this in a Shared pref and use in all activities or whatever
                userEmail = account.getEmail();
                userImg = account.getPhotoUrl().toString();
                userId = account.getId();
                syncwithGeobuyUser();

            } else {
                toast(this.getResources().getString(R.string.try_later));
            }
        }
    }

    private void syncwithGeobuyUser() {
        pd = new ProgressDialog(this.getContext());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        pd.show();

        RequestParams requestParams = new RequestParams();
        requestParams.put("userid",userId);
        requestParams.put("username",userName);
        requestParams.put("useremail",userEmail);
        requestParams.put("image",userImg);

        RestCall.post("syncUser", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               if(pd != null)
                 pd.dismiss();
               signedInwithGeobuy(new String(responseBody));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(pd != null)
                    pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    private void signedInwithGeobuy(String responseBody) {
        Type type = new TypeToken<User>() {}.getType();
        User user = new Gson().fromJson(new String(responseBody), type);

        TextView user_name = this.getActivity().findViewById(R.id.user_name);
        TextView user_email = this.getActivity().findViewById(R.id.user_email);
        ImageView userImage = this.getActivity().findViewById(R.id.user_image);
        SharedPreferences.Editor editor = sessionManager.getEditor();
        editor.putString("username", userName);
        editor.putString("useremail", userEmail);
        editor.putString("image", userImg);
        editor.putString("userid", userId);
        StringBuilder stringBuilder = new StringBuilder();
        if(user.getCart() != null && user.getCart().size() > 0) {
            List<Product> products = user.getCart();
            for(Product product : products)
                stringBuilder.append(","+product.getId());

            editor.putString("cart",stringBuilder.substring(1));
        }

        if(user.getWishlist() != null && !user.getWishlist().isEmpty()) {
            String wishlist = user.getWishlist();
            editor.putString("wishlist", wishlist);
        }
        sessionManager.put(editor);

        hideView(layout);
        showView(accounLatout);
        user_name.setText(userName);
        user_email.setText(userEmail);
        RecyclerView recyclerView = this.getActivity().findViewById(R.id.user_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(new UserFragmentItemAdapter(this.getContext(),  userFragmentItems));
        /*RelativeLayout user_cart = accounLatout.findViewById(R.id.user_cart);
        user_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCart();
            }
        });
        user_cart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                moveToCart();
               return false;
            }
        });*/

        if(userImg != null)
             Glide.with(this.getContext()).load(userImg).into(userImage);

        if(doClose)
            this.getActivity().finish();


    }

    private void moveToCart() {
        Intent intent = new Intent(this.getActivity(), CartActivity.class);
        startActivity(intent);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //here we take the account that was passed to this method when the authentication with Gmail was successful, and then use that to perform
        //a firebase authentication
        //initialize my Firebase Auth (get an instance of it)
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            //you can add an intent of the new activity where you want the user to go to next when the authentication is successful
                            //verifyWithCodette(user);
                            toast("Success:Success "+userName);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            toast("signInWithCredential:failure");
                        }

                        // ...
                    }
                });

    }

    private void initializeGooglePlusSettings() {

        //intialize the google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //get an instance of the google sign in
        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .enableAutoManage(this.getActivity() ,this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void toast(String s) {
       // if(mainActivity.getModule().equalsIgnoreCase("USERFRAGMENT"))
            Toast.makeText(this.getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toast("Connection failed");
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }

    }



    public void doCloseActivity(boolean doClose){
        this.doClose = doClose;
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
