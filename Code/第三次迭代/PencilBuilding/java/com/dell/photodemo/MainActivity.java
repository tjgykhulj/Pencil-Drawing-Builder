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
    //OpenCV���ز���ʼ���ɹ���Ļص�����
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
       // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar); //titlebarΪ�Լ��������Ĳ���
        ActionBar actionBar = getActionBar();
        Drawable myDrawable = getResources().getDrawable(R.drawable.bg_titlebar_main);
        actionBar.setBackgroundDrawable(myDrawable);
        actionBar.setDisplayShowHomeEnabled(false);

        // customize the SlidingMenu
        mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setMode(SlidingMenu.LEFT);       //�������Ҷ����Ի���SlidingMenu�˵�
        mSlidingMenu.setShadowDrawable(R.drawable.drawer_shadow);           //������ӰͼƬ
        mSlidingMenu.setShadowWidth(R.dimen.shadow_width);             //������ӰͼƬ�Ŀ��
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset); //SlidingMenu����ʱ��ҳ����ʾ��ʣ����
        mSlidingMenu.setFadeDegree(0.35f);
        //����SlidingMenu ������ģʽ
        //TOUCHMODE_FULLSCREEN ȫ��ģʽ��������contentҳ���У����������Դ�SlidingMenu
        //TOUCHMODE_MARGIN ��Եģʽ����contentҳ���У�������SlidingMenu,����Ҫ����Ļ��Ե�����ſ��Դ�SlidingMenu
        //TOUCHMODE_NONE ����ͨ�����ƴ�SlidingMenu
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        //���� SlidingMenu ����
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.left_menu, new MenuFragment());/*
        fragmentTransaction.replace(R.id.content, new ChooseFromFragment());
        setTitle("Choose");*/

        fragmentTransaction.commit();

        //ʹ�����Ϸ�icon�ɵ㣬������onOptionsItemSelected����ſ��Լ�����R.id.home
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
                toggle(); //��̬�ж��Զ��رջ���SlidingMenu
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