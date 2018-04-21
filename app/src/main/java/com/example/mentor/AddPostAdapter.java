package com.example.mentor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;


public class AddPostAdapter extends RecyclerView.Adapter<AddPostAdapter.ViewHolder> {

    ArrayList<AddPostModel> mValues;
    Context mContext;
    protected ItemListener mListener;

    public AddPostAdapter(Context context, ArrayList<AddPostModel> values, ItemListener itemListener) {
        mValues = values;
        mContext = context;
        mListener = itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView postTitleTv;
        public TextView postDescrtiptionTv;
        public TextView postMentorNameTv;
        public TextView postDate;
        public TextView postIndustryTv;
        public ImageView postImg;
        AddPostModel item;

        public ViewHolder(View v) {

            super(v);

            v.setOnClickListener(this);
            postTitleTv = (TextView) v.findViewById(R.id.row_item_posts_homescreen_title_tv);
            postDescrtiptionTv = (TextView) v.findViewById(R.id.row_item_posts_homescreen_description);
            postMentorNameTv = (TextView) v.findViewById(R.id.row_item_posts_homescreen_mentor_name_tv);
            postDate = (TextView) v.findViewById(R.id.row_item_posts_homescreen_date);
            postIndustryTv = (TextView) v.findViewById(R.id.row_item_posts_homescreen_industry_tv);
            postImg = (ImageView) v.findViewById(R.id.row_item_posts_homescreen_image_img);
//            relativeLayout = (RelativeLayout) v.findViewById(R.id.row_layout_home_relativeLayout);

        }

        public void setData(AddPostModel item) {
            this.item = item;
            Log.e("TAG_NAME", item.getTitle());

            postTitleTv.setText(item.getTitle());
            postDescrtiptionTv.setText(item.getDescription());
            postMentorNameTv.setText("@" + item.getMentorname());
            postDate.setText(item.getPostdate());
            postIndustryTv.setText(item.getIndustry());


            Log.e("URL", item.getDescription() + "sdfsdfdfdfdfdf");
            Glide.with(mContext).load(item.getImageurl())
                    .placeholder(R.drawable.no_img)
                    .crossFade()
                    .error(android.R.color.holo_red_light)
                    .fallback(android.R.color.holo_orange_light)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(postImg);
//            relativeLayout.setBackgroundColor(Color.parseColor(item.color));

        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item_posts_homescreen, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder Vholder, int position) {
        Vholder.setData(mValues.get(position));

    }

    @Override
    public int getItemCount() {

        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(AddPostModel item);
    }
}