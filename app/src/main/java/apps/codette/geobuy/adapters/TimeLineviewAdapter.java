package apps.codette.geobuy.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

import apps.codette.forms.Orientation;
import apps.codette.forms.Shipping;
import apps.codette.geobuy.R;

public class TimeLineviewAdapter  extends RecyclerView.Adapter<TimeLineviewAdapter.TimeLineViewHolder> {

    private List<Shipping> mFeedList;
    private Context mContext;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private LayoutInflater mLayoutInflater;


    public TimeLineviewAdapter(List<Shipping> feedList, Orientation orientation, boolean withLinePadding) {
        mFeedList = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_timeline, null);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {
        holder.dateTextView.setText(mFeedList.get(position).getDate());
        holder.text_timeline_title.setText(mFeedList.get(position).getMessage());
    }


    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder {
        public TimelineView mTimelineView;
        AppCompatTextView dateTextView;
        AppCompatTextView text_timeline_title;
        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
            mTimelineView.initLine(viewType);
            dateTextView = itemView.findViewById(R.id.text_timeline_date);
            text_timeline_title = itemView.findViewById(R.id.text_timeline_title);

        }
    }
}
