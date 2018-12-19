package apps.codette.geobuy.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

import apps.codette.forms.Address;
import apps.codette.forms.Banner;
import apps.codette.geobuy.AddressActivity;
import apps.codette.geobuy.AddressListAcitivty;
import apps.codette.geobuy.BusinessActivity;
import apps.codette.geobuy.R;
import apps.codette.geobuy.SearchResultActivity;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;

/**
 * Created by user on 25-03-2018.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private AddressListAcitivty mCtx;
    private List<Address> addresses;

    public AddressAdapter(AddressListAcitivty ctxt, List<Address> addresses) {

        this.mCtx = ctxt;
        this.addresses = addresses;
    }


    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.address_list_item, null);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        final Address address = addresses.get(position);
        if(address != null) {
            holder.name.setText(address.getName());
            holder.doorno.setText(address.getDoorno());
            holder.street.setText(address.getStreet());
            holder.city.setText(address.getCity());
            holder.state.setText(address.getState());
            holder.pincode.setText(address.getPincode());
            holder.editbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToEditAddress(address);
                }
            });
            holder.deletebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteAddress(address);
                }
            });
        }

    }

    private void deleteAddress(final Address address) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
        alertDialog.setTitle("Delete Address ?");
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAddressFromDB(address);
                dialog.dismiss();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void deleteAddressFromDB(final Address address) {
        mCtx.pd.show();
        RequestParams requestParams = new RequestParams();
        requestParams.put("address", new Gson().toJson(address));
        requestParams.put("delete", true);
        requestParams.put("useremail", mCtx.userDetails.get("useremail"));
        RestCall.post("saveAddress", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //getUserAddress();
                mCtx.pd.dismiss();
                for(Address addr : addresses) {
                    if(addr.getId().equals(address.getId()))
                        addresses.remove(addr);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mCtx.pd.dismiss();
                toast(mCtx.getResources().getString(R.string.try_later));

            }
        });
    }

    private void goToEditAddress(Address address) {
        Intent intent = new Intent(mCtx, AddressActivity.class);
        intent.putExtra("address", new Gson().toJson(address));
        mCtx.startActivityForResult(intent, mCtx.getUPDATE_REQ());
    }


    @Override
    public int getItemCount() {
        return addresses.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private void toast(String s) {
        Toast.makeText(mCtx, s, Toast.LENGTH_SHORT).show();
    }


    class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView doorno;
        TextView street;
        TextView state;
        TextView city;
        TextView pincode;
        ImageView editbutton;
        ImageView deletebutton;

        public AddressViewHolder(View itemView) {
            super(itemView);
            editbutton =itemView.findViewById(R.id.edit_button);
            deletebutton = itemView.findViewById(R.id.delete_button);
            doorno = itemView.findViewById(R.id.doorno);
            street = itemView.findViewById(R.id.street);
            city = itemView.findViewById(R.id.city);
            state = itemView.findViewById(R.id.state);
            pincode = itemView.findViewById(R.id.pincode);
            name = itemView.findViewById(R.id.name);
        }
    }
}
