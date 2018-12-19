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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

import apps.codette.forms.Address;
import apps.codette.forms.Issues;
import apps.codette.forms.QA;
import apps.codette.geobuy.AddressActivity;
import apps.codette.geobuy.AddressListAcitivty;
import apps.codette.geobuy.QAActivity;
import apps.codette.geobuy.R;
import apps.codette.utils.RestCall;
import cz.msebera.android.httpclient.Header;

/**
 * Created by user on 25-03-2018.
 */

public class IssuesAdapter extends RecyclerView.Adapter<IssuesAdapter.ViewHolder> {

    private Context mCtx;
    private List<Issues> addresses;

    public IssuesAdapter(Context ctxt, List<Issues> addresses) {
        this.mCtx = ctxt;
        this.addresses = addresses;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Issues issue = addresses.get(position);
        holder.list_item_text.setText(issue.getName());
        holder.list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToQAActivity(issue.getQa());
            }
        });
    }

    private void goToQAActivity(List<QA> qa) {
        Intent intent = new Intent(mCtx, QAActivity.class);
        intent.putExtra("qas", new Gson().toJson(qa));
        mCtx.startActivity(intent);
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


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView list_item_text;
        LinearLayout list_item;
        public ViewHolder(View itemView) {
            super(itemView);
            list_item_text = itemView.findViewById(R.id.list_item_text);
            list_item = itemView.findViewById(R.id.list_item);
        }
    }
}
