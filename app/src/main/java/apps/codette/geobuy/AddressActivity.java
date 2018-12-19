package apps.codette.geobuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import apps.codette.forms.Address;
import apps.codette.forms.Product;
import apps.codette.forms.User;
import apps.codette.geobuy.Constants.OrgService;
import apps.codette.utils.RestCall;
import apps.codette.utils.SessionManager;
import cz.msebera.android.httpclient.Header;

public class AddressActivity extends AppCompatActivity {

    private Address address;
    Button houseButton;
    Button office_type;
    Button other_type;
    SessionManager sessionManager;
    ProgressDialog pd;
    Map<String, ?> userDetails;
    TextView city,state,doorno,street, person, contact, alternate_contact,pincode;
    Switch default_address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
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
        manageButtons();
        sessionManager = new SessionManager(this);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER );
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(true);
        pd.setMessage("Loading");
        intializeFields();
        String addressJson =  getIntent().getStringExtra("address");
        if(addressJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Address>() {}.getType();
            address = gson.fromJson(addressJson, type);
            assignValuesForUI(address);
        } else {
            address = new Address();
        }
        userDetails = sessionManager.getUserDetails();
    }



    private void manageButtons() {
        Button saveButton = findViewById(R.id.save_address);
        Button cancelButton = findViewById(R.id.cancel_address);
        houseButton = findViewById(R.id.house_type);
        office_type = findViewById(R.id.office_type);
        other_type = findViewById(R.id.other_type);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAddress();
            }
        });
        houseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAddressTypeSelected("H");
            }
        });
        office_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAddressTypeSelected("O");
            }
        });
        other_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAddressTypeSelected("OT");
            }
        });
    }

    private void saveAddress() {
        if(isvalidAddress()) {
            pd.show();
            if(address.getId() == null)
                address.setId(UUID.randomUUID().toString());
            RequestParams requestParams = new RequestParams();
            requestParams.put("address", new Gson().toJson(address));
            requestParams.put("useremail", userDetails.get("useremail"));
            RestCall.post("saveAddress", requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    pd.dismiss();
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK,returnIntent);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    pd.dismiss();
                    toast(getResources().getString(R.string.try_later));

                }
            });
        }
    }

    private void intializeFields() {
        pincode = findViewById(R.id.pincode);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        doorno = findViewById(R.id.doorno);
        street = findViewById(R.id.street);
        person = findViewById(R.id.person);
        contact = findViewById(R.id.contact);
        alternate_contact = findViewById(R.id.alternate_contact);
        default_address = findViewById(R.id.default_address);
    }

    private void assignValuesForUI(Address address) {
        pincode.setText(address.getPincode());
        city.setText(address.getCity());
        state.setText(address.getState());
        doorno.setText(address.getDoorno());
        street.setText(address.getStreet());
        person.setText(address.getName());
        contact.setText(address.getMobileno());
        if(address.getMobileno() != null)
            alternate_contact.setText(address.getMobileno());
        if(address.getAddresstype() != null)
            setAddressTypeSelected(address.getAddresstype());
        if(address.isIsdefault())
            default_address.setChecked(true);
    }

    private boolean isvalidAddress() {
        if(pincode.getText().toString() != null && !pincode.getText().toString().isEmpty()) {
            address.setPincode(pincode.getText().toString());
        } else {
            pincode.setError("Pincode is mandatory");
            return false;
        }

        if(city.getText().toString() != null && !city.getText().toString().isEmpty()) {
            address.setCity(city.getText().toString());
        } else {
            city.setError("City is mandatory");
            return false;
        }

        if(state.getText().toString() != null && !state.getText().toString().isEmpty()) {
            address.setState(state.getText().toString());
        } else {
            state.setError("State is mandatory");
            return false;
        }

        if(doorno.getText().toString() != null && !doorno.getText().toString().isEmpty()) {
            address.setDoorno(doorno.getText().toString());
        } else {
            doorno.setError("Pincode is mandatory");
            return false;
        }

        if(street.getText().toString() != null && !street.getText().toString().isEmpty()) {
            address.setStreet(street.getText().toString());
        } else {
            street.setError("Street is mandatory");
            return false;
        }

        if(person.getText().toString() != null && !person.getText().toString().isEmpty()) {
            address.setName(person.getText().toString());
        } else {
            person.setError("Name is mandatory");
            return false;
        }

        if(contact.getText().toString() != null && !contact.getText().toString().isEmpty()) {
            if(contact.getText().toString().length() == 10) {
                address.setMobileno(contact.getText().toString());
            } else {
                contact.setError("Enter a valid Phone No");
                return false;
            }
        } else {
            contact.setError("Phone No is mandatory");
            return false;
        }

        if(alternate_contact.getText().toString() != null && !alternate_contact.getText().toString().isEmpty()) {
            if(alternate_contact.getText().toString().length() == 10) {
                address.setAlternatemobileno(alternate_contact.getText().toString());
            } else {
                alternate_contact.setError("Enter a valid Phone No");
                return false;
            }
        }
        //if(default_address.isChecked()) {
        address.setIsdefault(default_address.isChecked());
        //}
        return true;
    }

    private void setAddressTypeSelected(String val) {
        houseButton.setBackground(getResources().getDrawable(R.drawable.unselected));
        houseButton.setTextColor(getResources().getColor(R.color.black_overlay));
        office_type.setBackground(getResources().getDrawable(R.drawable.unselected));
        office_type.setTextColor(getResources().getColor(R.color.black_overlay));
        other_type.setBackground(getResources().getDrawable(R.drawable.unselected));
        other_type.setTextColor(getResources().getColor(R.color.black_overlay));
        address.setAddresstype(val);
        switch(val) {
            case "H" : {
                houseButton.setBackground(getResources().getDrawable(R.drawable.selected));
                houseButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            }
            case "O" : {
                office_type.setBackground(getResources().getDrawable(R.drawable.selected));
                office_type.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            }
            case "OT" : {
                other_type.setBackground(getResources().getDrawable(R.drawable.selected));
                other_type.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            }
        }
    }


    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }

}
