package com.dell.photodemo;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dell.photodemo.MenuFragment.SLMenuListOnItemClickListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends SlidingFragmentActivity implements SLMenuListOnItemClickListener {
    private SlidingMenu mSlidingMenu;
    private static int fragmentPosition = 0;
    private static String fragmentTitle = "Choose";
    //OpenCV加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i("MainActivity", "success");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i("MainActivity", "fail");
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_content);
        //set the Behind View
        setBehindContentView(R.layout.frame_left_menu);
       // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar); //titlebar为自己标题栏的布局
        ActionBar actionBar = getActionBar();
        Drawable myDrawable = getResources().getDrawable(R.drawable.bg_titlebar_main);
        actionBar.setBackgroundDrawable(myDrawable);
        actionBar.setDisplayShowHomeEnabled(false);

        // customize the SlidingMenu
        mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setMode(SlidingMenu.LEFT);       //设置左右都可以划出SlidingMenu菜单
        mSlidingMenu.setShadowDrawable(R.drawable.drawer_shadow);           //设置阴影图片
        mSlidingMenu.setShadowWidth(R.dimen.shadow_width);             //设置阴影图片的宽度
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset); //SlidingMenu划出时主页面显示的剩余宽度
        mSlidingMenu.setFadeDegree(0.35f);
        //设置SlidingMenu 的手势模式
        //TOUCHMODE_FULLSCREEN 全屏模式，在整个content页面中，滑动，可以打开SlidingMenu
        //TOUCHMODE_MARGIN 边缘模式，在content页面中，如果想打开SlidingMenu,你需要在屏幕边缘滑动才可以打开SlidingMenu
        //TOUCHMODE_NONE 不能通过手势打开SlidingMenu
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        //设置 SlidingMenu 内容
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.left_menu, new MenuFragment());/*
        fragmentTransaction.replace(R.id.content, new ChooseFromFragment());
        setTitle("Choose");*/

        fragmentTransaction.commit();

        //使用左上方icon可点，这样在onOptionsItemSelected里面才可以监听到R.id.home
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle(); //动态判断自动关闭或开启SlidingMenu
                return true;
            case R.id.action_setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void selectItem(int position, String title) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        fragmentPosition = position;
        fragmentTitle = title;
        switch (position) {
            case 0:
                fragment = new ChooseFromFragment();
                break;
            case 1:
                fragment = new SampleFragment();
                break;
            case 2:
                fragment = new HistoryFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content, fragment).commit();
            // update selected item and title, then close the drawer
            setTitle(title);
            mSlidingMenu.showContent();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    public void onResume() {
        super.onResume();
        selectItem(fragmentPosition, fragmentTitle);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    }
}