package apps.codette.geobuy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import apps.codette.forms.Banner;
import apps.codette.geobuy.BusinessActivity;
import apps.codette.geobuy.R;
import apps.codette.geobuy.SearchResultActivity;

/**
 * Created by user on 06-04-2018.
 */

public class BannerPagerAdapter extends PagerAdapter{
    Context context;
    List<Banner> banners;
    LayoutInflater layoutInflater;


    public BannerPagerAdapter(Context context, List<Banner> banners) {
        this.context = context;
        this.banners = banners;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.business_image, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        //imageView.setImageResource(images[position]);
        final Banner banner = banners.get(position);

        Log.i("url", banner.getImage());
        Glide.with(context)
                .load(banner.getImage())
                .fitCenter()
                .into(imageView);

        container.addView(itemView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductsByGroup(banner);
            }
        });

        return itemView;
    }

    private void openProductsByGroup(Banner category) {
        String[] linkId = null;
        if(category.isOrg()) {
            linkId = category.getLinkId();
            Intent intent = new Intent(context, BusinessActivity.class);
            intent.putExtra("orgid", linkId[0]);
            context.startActivity(intent);
        } else if (category.isProducts()) {
            linkId = category.getLinkId();
            Intent intent = new Intent(context, SearchResultActivity.class);
            intent.putExtra("productIds", linkId);
            context.startActivity(intent);
        }

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
