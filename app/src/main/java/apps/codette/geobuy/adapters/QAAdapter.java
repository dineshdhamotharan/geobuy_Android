package apps.codette.geobuy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import apps.codette.forms.Issues;
import apps.codette.forms.QA;
import apps.codette.geobuy.R;

/**
 * Created by user on 25-03-2018.
 */

public class QAAdapter extends RecyclerView.Adapter<QAAdapter.ViewHolder> {

    private Context mCtx;
    private List<QA> qas;

    public QAAdapter(Context ctxt, List<QA> qas) {
        this.mCtx = ctxt;
        this.qas = qas;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.qa_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QA qa = qas.get(position);
        holder.question.setText(qa.getQuestion());
        holder.answer.setText(qa.getAnswer());
    }




    @Override
    public int getItemCount() {
        return qas.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private void toast(String s) {
        Toast.makeText(mCtx, s, Toast.LENGTH_SHORT).show();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView question, answer;
        public ViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
        }
    }
}
