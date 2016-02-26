package com.dell.photodemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.dell.photodemo.ConvertActivity;
import com.dell.photodemo.R;
import com.dell.photodemo.adapter.SampleImageAdapter;

/**
 * Created by DELL on 2015/9/11.
 */
public class SampleFragment extends Fragment implements ViewSwitcher.ViewFactory  {
    private int[] buildingList = {
            R.drawable.sample_building_1, R.drawable.sample_building_2, R.drawable.sample_building_3,
            R.drawable.sample_building_4, R.drawable.sample_building_5, R.drawable.sample_building_6,
            R.drawable.sample_building_7, R.drawable.sample_building_8, R.drawable.sample_building_9};
    private int[] otherList = {
            R.drawable.sample_other_1, R.drawable.sample_other_2, R.drawable.sample_other_3,
            R.drawable.sample_other_4, R.drawable.sample_other_5};

    private Gallery other;
    private Gallery building;
    private ImageSwitcher imageSwitcher;
    private int resId = otherList[0];

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sample, container, false);

        imageSwitcher = (ImageSwitcher) rootView.findViewById(R.id.sample_switcher);
        imageSwitcher.setFactory(this);
        /*
         * 淡入淡出效果
         */
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.fade_out));
        imageSwitcher.setOnTouchListener(touchListener);

        other = (Gallery) rootView.findViewById(R.id.galleryOther);
        other.setAdapter(new SampleImageAdapter(otherList, getActivity()));
        other.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long when) {
                resId = otherList[position];
                imageSwitcher.setImageResource(resId);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        building = (Gallery)rootView.findViewById(R.id.galleryBuilding);
        building.setAdapter(new SampleImageAdapter(buildingList, getActivity()));
        building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long when) {
                resId = buildingList[position];
                imageSwitcher.setImageResource(resId);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        return rootView;
    }

    /**
     * 注册一个触摸事件
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        private int downX, upX;
        private int downY, upY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                downX = (int) event.getX();//取得按下时的坐标
                downY = (int) event.getY();
                return true;
            }
            else if(event.getAction()==MotionEvent.ACTION_UP)
            {
                upX=(int) event.getX();//取得松开时的坐标
                upY=(int) event.getY();
                int index=0;
                if(Math.abs(upX-downX) + Math.abs(upY-downY) < 20) {
                    Intent intent = new Intent(getActivity(), ConvertActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //帮助返回首页
                    intent.putExtra("method", "Sample");
                    intent.putExtra("resId", resId);
                    startActivity(intent);
                }
            }
            return true;
        }
    };

    @Override
    public View makeView() {
        ImageView imageView = new ImageView(getActivity());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(//自适应图片大小
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return imageView;
    }
}
