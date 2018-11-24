package com.htmessage.cola_marketing.activity.common;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.widget.PhotoViewPager;

import uk.co.senab.photoview.PhotoView;

public class BigImageActivity extends BaseActivity {
    private PhotoViewPager photoViewPager;
    private TabLayout tabLayout;
    private TabLayout.Tab[] tabs;

    private boolean isNetUrl = true;
    private String[] images;
    private int page = 0;
    private String imageBaseUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        getData();
        initView();
        initData();
        setListener();
    }

    private void setListener() {

    }

    private void initData() {
        MyAdapter adapter = new MyAdapter(images);
        photoViewPager.setAdapter(adapter);
        photoViewPager.setCurrentItem(page);
        tabLayout.setupWithViewPager(photoViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabs = new TabLayout.Tab[images.length];
        for (int i = 0; i < images.length; i++) {
            tabs[i] = tabLayout.getTabAt(i);
            tabs[i].setCustomView(getBottomView(i,page, R.drawable.pointer_selector));
        }
    }

    private void initView() {
        photoViewPager = this.findViewById(R.id.viewpager_image);
        tabLayout = this.findViewById(R.id.tabLayout);
    }

    private void getData() {
        //图片路径
        images = getIntent().getStringArrayExtra("images");
        //当前索引8
        page = getIntent().getIntExtra("page", 0);
        //是否是网络图片
        isNetUrl = getIntent().getBooleanExtra("isNetUrl",true);
        //网络图片地址头部
        imageBaseUrl = getIntent().getStringExtra("imageBaseUrl");
    }


    private View getBottomView(final int index, final int page, int drawableRes) {
        View view = getLayoutInflater().inflate(R.layout.layout_big_photo_point, null);
        ImageView button = view.findViewById(R.id.iv_foot_point);
        if (index == page){
            tabs[index].select();
        }
        button.setBackground(getDrawable(drawableRes));
        if (isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
            button.setStateListAnimator(null);
        }
        return view;
    }




    private class MyAdapter extends PagerAdapter {
        private String[] images;

        public MyAdapter(String[] images) {
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public PhotoView instantiateItem(ViewGroup container, int position) {
            String image = images[position];
            PhotoView photoView = new PhotoView(BigImageActivity.this);
            if(!isNetUrl){
                RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image).error(R.drawable.default_image);
                Glide.with(BigImageActivity.this).load(image).apply(options).into(photoView);
            } else if(!image.contains("http")){
                if (imageBaseUrl == null || imageBaseUrl.isEmpty())
                    image = HTConstant.baseImgUrl+image;
                else
                    image = imageBaseUrl+image;
            }
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image).error(R.drawable.default_image);
            Glide.with(BigImageActivity.this).load(image).apply(options).into(photoView);
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((PhotoView) object);
        }
    }
}
