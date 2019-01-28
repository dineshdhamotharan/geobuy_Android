package apps.codette.geobuy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import apps.codette.forms.Address;
import apps.codette.forms.Order;
import apps.codette.forms.OrderStatus;
import apps.codette.forms.Orientation;
import apps.codette.forms.Product;
import apps.codette.forms.Shipping;
import apps.codette.geobuy.adapters.OrderProductsAdapter;
import apps.codette.geobuy.adapters.TimeLineviewAdapter;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

public class OrderDetailActivity extends AppCompatActivity implements PaymentResultListener {

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
    ProgressDialog pd;

    Intent bundle;

    Order order;

    SessionManager sessionManager;

    Map<String, ?> userDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_activity_order_detail));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        bundle = getIntent();
        getOrderDetails(bundle.getStringExtra("id"));
        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUserDetails();
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


    public void getOrderDetails(String id) {
        pd.show();
        RequestParams requestParams = new RequestParams();
        RestCall.get("order/"+id, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new Gson();
                Type type = new TypeToken<Order>() {}.getType();
                order = gson.fromJson(new String(responseBody), type);
                updateUI(order);
                if(pd != null)
                    pd.dismiss();
                LinearLayout pay_ll = findViewById(R.id.pay_ll);

                if(!order.getPaymentstatus().equalsIgnoreCase("PAID") && !order.getStatus().equalsIgnoreCase("C"))
                   showView(pay_ll);

                Button pay = findViewById(R.id.pay);
                pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startPayment();
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(pd != null)
                    pd.dismiss();
                toast(getResources().getString(R.string.try_later));
            }
        });
    }

    private void updateUI(Order order) {
        assignTabs(order);
    }

    private void assignTabs(Order order) {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), order);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
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

    public void closeActivity(){
        this.finish();
    }

    @Override
    public void onPaymentSuccess(String paymentId) {
        updatePayment(paymentId);
    }

    private void updatePayment(String paymentId) {
        pd.show();
        String useremail = (String) userDetails.get("useremail");
        RequestParams requestParams = new RequestParams();
        requestParams.put("paymentstatus", "PAID");
        requestParams.put("paymentmode", "RAZORPAY");
        requestParams.put("paymentId", paymentId);
        requestParams.put("useremail", useremail);
        requestParams.put("message", "Order amount of Rs."+order.getTotalamount()+" has been paid, Txn Id : "+paymentId);
        String url ="payment/"+order.getOrderNo()+"/"+order.getId()+"/update";
        RestCall.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               // getOrderDetails(order.getId());
                pd.dismiss();
                closeActivity();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toast("Payment Success ");
                pd.dismiss();
            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        toast("Payment Error ");
    }


    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: Rentomojo || HasGeek etc.
             */
            options.put("name", "Geobuy");

            /**
             * Description can be anything
             * eg: Order #123123
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Order #"+order.getOrderNo());

            options.put("currency", "INR");

            /**
             * Amount is always passed in PAISE
             * Eg: "500" = Rs 5.00
             */
            options.put("amount", order.getSubtotalamount()*100);

            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e("Error", "Error in starting Razorpay Checkout", e);
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        View rootView;
        TextView order_no,payment_status;
        TextView product_name;
        Order order;
       // String productId;
        ImageView product_image;
        TextView quanity;
        TextView selling_price;
        TextView total_amount;
        TextView shipping_fee;
        Button return_order, cancel_order;


        private static final String ARG_SECTION_NUMBER = "section_number";
        ProgressDialog pd;
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String orderJson) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("orderJson", orderJson);
          //  args.putString("productId", productId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);
            Gson gson = new Gson();
            Type type = new TypeToken<Order>() {}.getType();
            order = gson.fromJson(new String(getArguments().get("orderJson").toString()), type);
            //         //   productId = getArguments().get("productId").toString();
            pd = new ProgressDialog(this.getContext());
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setIndeterminate(true);
            pd.setMessage("Loading");
            initializeAllViews(rootView);
            return rootView;
        }

        private void initializeAllViews(View rootView) {
            RecyclerView order_products_recycler_view = rootView.findViewById(R.id.order_products_recycler_view);
            OrderProductsAdapter orderProductsAdapter = new OrderProductsAdapter(this.getActivity(), order.getProducts());
            LinearLayoutManager ll = new LinearLayoutManager(this.getContext());
            order_products_recycler_view.setLayoutManager(ll);
            order_products_recycler_view.setAdapter(orderProductsAdapter);
            order_no = rootView.findViewById(R.id.order_no);
            order_no.setText(order.getOrderNo());
            payment_status = rootView.findViewById(R.id.payment_status);
            setAddressSelected(order.getAddress());
            //product_name.setText();

            if(order.getPaymentstatus().equalsIgnoreCase("PAID")) {
                payment_status.setText("PAID");
                payment_status.setTextColor(getResources().getColor(R.color.green));
            } else {
                payment_status.setTextColor(getResources().getColor(R.color.darkRed));
                payment_status.setText("NOT PAID");
            }
            if(order.getPaymentmode().equalsIgnoreCase("TA")) {
                showView(rootView.findViewById(R.id.qrll));
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = null;
                try {
                    bitmap = barcodeEncoder.encodeBitmap(order.getId(), BarcodeFormat.QR_CODE, 400, 400);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                ImageView imageViewQrCode = (ImageView) rootView.findViewById(R.id.qrCode);
                imageViewQrCode.setImageBitmap(bitmap);
            }

            if(order.getStatus().equalsIgnoreCase("D")) {
                LinearLayout return_order_ll = rootView.findViewById(R.id.return_order_ll);
                showView(return_order_ll);
            } else if(!order.getStatus().equalsIgnoreCase("C")){
                LinearLayout cancel_order_ll = rootView.findViewById(R.id.cancel_order_ll);
                showView(cancel_order_ll);
            }
            return_order = rootView.findViewById(R.id.return_order);
            cancel_order = rootView.findViewById(R.id.cancel_order);
            return_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateOrder("RR");
                }
            });
            cancel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateOrder("C");
                }
            });
        }

        private void updateOrder(String status) {
            pd.show();
            RequestParams requestParams =  new RequestParams();
            requestParams.put("status", status);
            RestCall.post("order/"+order.getId()+"/update", requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    updateSuccess();
                    pd.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    updateFailure();
                    pd.dismiss();
                }
            });
        }

        private void updateFailure() {
            toast(getString(R.string.try_later));
        }

        private void updateSuccess() {
            toast("Order updated sucessfully");
            this.getActivity().finish();
        }

        public void toast(String s) {
            Toast.makeText(this.getContext(), ""+s, Toast.LENGTH_SHORT).show();
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

        private void setAddressSelected(Address address) {
            TextView name = rootView.findViewById(R.id.name);
            name.setText(address.getName());
            TextView doorno = rootView.findViewById(R.id.doorno);
            doorno.setText(address.getDoorno());
            TextView street = rootView.findViewById(R.id.street);
            street.setText(address.getStreet());
            TextView city = rootView.findViewById(R.id.district);
            city.setText(address.getCity());
            TextView state = rootView.findViewById(R.id.state);
            state.setText(address.getState());
            TextView pincode = rootView.findViewById(R.id.pincode);
            pincode.setText(address.getPincode());
            order.setAddress(address);
        }

    }

    public static class ShippingFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private TimeLineviewAdapter mTimeLineAdapter;
        private List <Shipping> mDataList = null;
        private Orientation mOrientation;
        private boolean mWithLinePadding;
        View rootView;
        Order order;
        //private String productId;

        public static ShippingFragment newInstance(int sectionNumber, String orderJson) {
            ShippingFragment fragment = new ShippingFragment();
            Bundle args = new Bundle();
            args.putString("orderJson", orderJson);
           // args.putString("productId", productId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.shipping_details, container, false);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(getLinearLayoutManager());
            mRecyclerView.setHasFixedSize(true);
            Gson gson = new Gson();
            Type type = new TypeToken<Order>() {}.getType();
            order = gson.fromJson(new String(getArguments().get("orderJson").toString()), type);
       //     productId = getArguments().get("productId").toString();
            initView();
            return rootView;
        }

        private LinearLayoutManager getLinearLayoutManager() {
            if (mOrientation == Orientation.HORIZONTAL) {
                return new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
            } else {
                return new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
            }
        }

        private void initView() {
            //setDataListItems();
            mDataList = order.getShippings();
            mTimeLineAdapter = new TimeLineviewAdapter(mDataList, mOrientation, mWithLinePadding);
            mRecyclerView.setAdapter(mTimeLineAdapter);
        }

        private void setDataListItems(){
            mDataList.add(new Shipping("Item successfully delivered", "2017-02-12 08:00", OrderStatus.INACTIVE));
            mDataList.add(new Shipping("Courier is out to delivery your order", "2017-02-12 08:00", OrderStatus.ACTIVE));
            mDataList.add(new Shipping("Item has reached courier facility at New Delhi", "2017-02-11 21:00", OrderStatus.COMPLETED));
            mDataList.add(new Shipping("Item has been given to the courier", "2017-02-11 18:00", OrderStatus.COMPLETED));
            mDataList.add(new Shipping("Item is packed and will dispatch soon", "2017-02-11 09:30", OrderStatus.COMPLETED));
            mDataList.add(new Shipping("Order is being readied for dispatch", "2017-02-11 08:00", OrderStatus.COMPLETED));
            mDataList.add(new Shipping("Order processing initiated", "2017-02-10 15:00", OrderStatus.COMPLETED));
            mDataList.add(new Shipping("Order confirmed by seller", "2017-02-10 14:30", OrderStatus.COMPLETED));
            mDataList.add(new Shipping("Order placed successfully", "2017-02-10 14:00", OrderStatus.COMPLETED));
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Order order;

        public SectionsPagerAdapter(FragmentManager fm, Order order) {
            super(fm);
            this.order = order;
        }

        @Override
        public Fragment getItem(int position) {
            ++position;
            String orderJson = new Gson().toJson(order);

            if(position == 1 ) {
                return PlaceholderFragment.newInstance(position, orderJson);
            }else
                return ShippingFragment.newInstance(position, orderJson);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }


    public void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }



}
