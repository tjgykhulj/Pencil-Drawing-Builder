package com.dell.slidedemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dell.slidedemo.fragment.CommunityFragment;
import com.dell.slidedemo.fragment.FindPeopleFragment;
import com.dell.slidedemo.fragment.HomeFragment;
import com.dell.slidedemo.fragment.MenuFragment;
import com.dell.slidedemo.fragment.MenuFragment.SLMenuListOnItemClickListener;
import com.dell.slidedemo.fragment.PagesFragment;
import com.dell.slidedemo.fragment.PhotosFragment;
import com.dell.slidedemo.fragment.RightMenuFragment;
import com.dell.slidedemo.fragment.WhatsHotFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity implements SLMenuListOnItemClickListener{

    private SlidingMenu mSlidingMenu;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Home");
//      setTitle(R.string.sliding_title);
        setContentView(R.layout.frame_content);

        //set the Behind View
        setBehindContentView(R.layout.frame_left_menu);

        // customize the SlidingMenu
        mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);//�������Ҷ����Ի���SlidingMenu�˵�
        mSlidingMenu.setSecondaryMenu(R.layout.frame_right_menu);   //�����Ҳ�˵��Ĳ����ļ�
        mSlidingMenu.setSecondaryShadowDrawable(R.drawable.drawer_shadow);

//      mSlidingMenu.setShadowWidth(5);
//      mSlidingMenu.setBehindOffset(100);
        mSlidingMenu.setShadowDrawable(R.drawable.drawer_shadow);//������ӰͼƬ
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width); //������ӰͼƬ�Ŀ��
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset); //SlidingMenu����ʱ��ҳ����ʾ��ʣ����
        mSlidingMenu.setFadeDegree(0.35f);
        //����SlidingMenu ������ģʽ
        //TOUCHMODE_FULLSCREEN ȫ��ģʽ��������contentҳ���У����������Դ�SlidingMenu
        //TOUCHMODE_MARGIN ��Եģʽ����contentҳ���У�������SlidingMenu,����Ҫ����Ļ��Ե�����ſ��Դ�SlidingMenu
        //TOUCHMODE_NONE ����ͨ�����ƴ�SlidingMenu
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        //���� SlidingMenu ����
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.left_menu, new MenuFragment());
        fragmentTransaction.replace(R.id.right_menu, new RightMenuFragment());
        fragmentTransaction.replace(R.id.content, new HomeFragment());

        fragmentTransaction.commit();

        //ʹ�����Ϸ�icon�ɵ㣬������onOptionsItemSelected����ſ��Լ�����R.id.home
        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setLogo(R.drawable.ic_logo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                toggle(); //��̬�ж��Զ��رջ���SlidingMenu
//          getSlidingMenu().showMenu();//��ʾSlidingMenu
//          getSlidingMenu().showContent();//��ʾ����
                return true;
            case R.id.action_refresh:

                Toast.makeText(getApplicationContext(), R.string.refresh, Toast.LENGTH_SHORT).show();

                return true;
            case R.id.action_person:

                if(mSlidingMenu.isSecondaryMenuShowing()){
                    mSlidingMenu.showContent();
                }else{
                    mSlidingMenu.showSecondaryMenu();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @SuppressLint("NewApi")
    @Override
    public void selectItem(int position, String title) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new FindPeopleFragment();
                break;
            case 2:
                fragment = new PhotosFragment();
                break;
            case 3:
                fragment = new CommunityFragment();
                break;
            case 4:
                fragment = new PagesFragment();
                break;
            case 5:
                fragment = new WhatsHotFragment();
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
}