package com.clevertap.android.sdk;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;

import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Viewholder class for Carousels
 */
class CTCarouselMessageViewHolder extends CTInboxBaseMessageViewHolder {

    private CTCarouselViewPager imageViewPager;
    private LinearLayout sliderDots;
    private TextView title,message,timestamp, carouselTimestamp;
    private ImageView readDot,carouselReadDot;
    private RelativeLayout clickLayout;

    CTCarouselMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewPager = itemView.findViewById(R.id.image_carousel_viewpager);
        sliderDots = itemView.findViewById(R.id.sliderDots);
        title = itemView.findViewById(R.id.messageTitle);
        message = itemView.findViewById(R.id.messageText);
        timestamp = itemView.findViewById(R.id.timestamp);
        carouselTimestamp = itemView.findViewById(R.id.carousel_timestamp);
        readDot = itemView.findViewById(R.id.read_circle);
        carouselReadDot = itemView.findViewById(R.id.carousel_read_circle);
        clickLayout = itemView.findViewById(R.id.body_relative_layout);
    }

    @Override
    void configureWithMessage(final CTInboxMessage inboxMessage, final CTInboxListViewFragment parent, final int position) {
        super.configureWithMessage(inboxMessage, parent, position);
        CTInboxMessageContent content = inboxMessage.getInboxMessageContents().get(0);
        if (inboxMessage.getType() == CTInboxMessageType.CarouselImageMessage) {
            this.title.setVisibility(View.GONE);
            this.message.setVisibility(View.GONE);
            this.carouselTimestamp.setVisibility(View.VISIBLE);
            if(inboxMessage.isRead()){
                this.carouselReadDot.setVisibility(View.GONE);
            }else{
                this.carouselReadDot.setVisibility(View.VISIBLE);
            }
            String carouselImageDisplayTimestamp  = calculateDisplayTimestamp(inboxMessage.getDate());
            this.carouselTimestamp.setText(carouselImageDisplayTimestamp);
            this.carouselTimestamp.setTextColor(Color.parseColor(inboxMessage.getInboxMessageContents().get(0).getTitleColor()));
            this.timestamp.setVisibility(View.GONE);
            this.readDot.setVisibility(View.GONE);
        } else {
            this.title.setVisibility(View.VISIBLE);
            this.message.setVisibility(View.VISIBLE);
            this.title.setText(content.getTitle());
            this.title.setTextColor(Color.parseColor(content.getTitleColor()));
            this.message.setText(content.getMessage());
            this.message.setTextColor(Color.parseColor(content.getMessageColor()));
            this.carouselTimestamp.setVisibility(View.GONE);
            this.carouselReadDot.setVisibility(View.GONE);
            if(inboxMessage.isRead()){
                this.readDot.setVisibility(View.GONE);
            }else{
                this.readDot.setVisibility(View.VISIBLE);
            }
            this.timestamp.setVisibility(View.VISIBLE);
            String carouselDisplayTimestamp  = calculateDisplayTimestamp(inboxMessage.getDate());
            this.timestamp.setText(carouselDisplayTimestamp);
            this.timestamp.setTextColor(Color.parseColor(inboxMessage.getInboxMessageContents().get(0).getTitleColor()));
        }

        this.clickLayout.setBackgroundColor(Color.parseColor(inboxMessage.getBgColor()));

        //Loads the viewpager
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.imageViewPager.getLayoutParams();
        CTCarouselViewPagerAdapter carouselViewPagerAdapter = new CTCarouselViewPagerAdapter(parent.getActivity(), parent, inboxMessage,layoutParams,position);
        this.imageViewPager.setAdapter(carouselViewPagerAdapter);
        //Adds the dots for the carousel
        int dotsCount = carouselViewPagerAdapter.getCount();
        ImageView[] dots = new ImageView[dotsCount];
        for(int k=0;k<dotsCount;k++){
            dots[k] = new ImageView(parent.getActivity());
            dots[k].setVisibility(View.VISIBLE);
            // noinspection ConstantConditions
            dots[k].setImageDrawable(parent.getActivity().getApplicationContext().getResources().getDrawable(R.drawable.unselected_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 6, 4, 6);
            params.gravity = Gravity.CENTER;
            if(this.sliderDots.getChildCount() < dotsCount)
                this.sliderDots.addView(dots[k],params);
        }
        // noinspection ConstantConditions
        dots[0].setImageDrawable(parent.getActivity().getApplicationContext().getResources().getDrawable(R.drawable.selected_dot));
        CTCarouselMessageViewHolder.CarouselPageChangeListener carouselPageChangeListener = new CTCarouselMessageViewHolder.CarouselPageChangeListener(parent.getActivity(), this, dots, inboxMessage);
        this.imageViewPager.addOnPageChangeListener(carouselPageChangeListener);

        this.clickLayout.setOnClickListener(new CTInboxButtonClickListener(position, inboxMessage,null, parent, this.imageViewPager));

        Runnable carouselRunnable = new Runnable() {
            @Override
            public void run() {
                parent.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(readDot.getVisibility() == View.VISIBLE) {
                            parent.didShow(null, position);
                        }
                        readDot.setVisibility(View.GONE);
                    }
                });
            }
        };
        Handler carouselHandler = new Handler();
        carouselHandler.postDelayed(carouselRunnable,2000);
    }

    /**
     * Custom PageChangeListener for Carousel
     */
    class CarouselPageChangeListener implements ViewPager.OnPageChangeListener {
        private CTCarouselMessageViewHolder viewHolder;
        private ImageView[] dots;
        private CTInboxMessage inboxMessage;
        private Context context;

        CarouselPageChangeListener(Context context, CTCarouselMessageViewHolder viewHolder, ImageView[] dots, CTInboxMessage inboxMessage) {
            this.context = context;
            this.viewHolder = viewHolder;
            this.dots = dots;
            this.inboxMessage = inboxMessage;
        }

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int position) {
            for (ImageView dot : this.dots) {
                dot.setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.unselected_dot));
            }
            dots[position].setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.selected_dot));
            viewHolder.title.setText(inboxMessage.getInboxMessageContents().get(position).getTitle());
            viewHolder.title.setTextColor(Color.parseColor(inboxMessage.getInboxMessageContents().get(position).getTitleColor()));
            viewHolder.message.setText(inboxMessage.getInboxMessageContents().get(position).getMessage());
            viewHolder.message.setTextColor(Color.parseColor(inboxMessage.getInboxMessageContents().get(position).getMessageColor()));
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    }
}
