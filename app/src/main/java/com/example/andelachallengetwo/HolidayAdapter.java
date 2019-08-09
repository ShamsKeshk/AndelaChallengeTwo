package com.example.andelachallengetwo;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder> {

    public HolidayAdapter(){
        if (mHolidayDeals == null){
            mHolidayDeals = new ArrayList<>();
        }
    }

    private List<HolidayDeal> mHolidayDeals;

    public List<HolidayDeal> getHolidayDeals() {
        return mHolidayDeals;
    }

    private Context mContext;
    public void setHolidayDeals(List<HolidayDeal> holidayDeals) {
        mHolidayDeals = holidayDeals;
        notifyDataSetChanged();
    }

    public void addHolidayChilc(HolidayDeal holidayDeal){
        if (holidayDeal != null){
            mHolidayDeals.add(holidayDeal);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutId = R.layout.holiday_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutId,parent,false);
        HolidayViewHolder holidayViewHolder = new HolidayViewHolder(view);
        return holidayViewHolder;
    }

    private HolidayDeal getItem(int position){
        return mHolidayDeals.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int position) {
        HolidayDeal holidayDeal = getItem(position);

        holder.mTextViewTitle.setText(holidayDeal.getTitle());
        holder.mTextViewDescription.setText(holidayDeal.getDescription());
        Glide
                .with(mContext)
                .load(holidayDeal.getImageUrl())
                .placeholder(R.drawable.tourism_placeholder)
                .into(holder.mImageViewHolidayImage);
        holder.mTextViewPrice.setText(" $ " + String.valueOf(holidayDeal.getPrice()));
    }

    @Override
    public int getItemCount() {
        if (mHolidayDeals != null) return mHolidayDeals.size();
        return 0;
    }

    public void clear(){
        if (mHolidayDeals != null){
            mHolidayDeals.clear();
            notifyDataSetChanged();
        }
    }

    public class HolidayViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_holiday_image_home_activity)
        ImageView mImageViewHolidayImage;
        @BindView(R.id.tv_holiday_title_home_activity)
        TextView mTextViewTitle;
        @BindView(R.id.tv_holiday_description_home_activity)
        TextView mTextViewDescription;
        @BindView(R.id.tv_holiday_offer_price_home_activity)
        TextView mTextViewPrice;

        public HolidayViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
