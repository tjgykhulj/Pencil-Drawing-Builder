package com.dell.photodemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by DELL on 2015/9/7.
 */
public class ResultActivity extends Activity {
    private Bitmap dstBitmap;
    private ImageView mImageView;
    private ImageButton mButtonSave;
    private ImageButton mButtonShare;
    private TextView mHintView;

    private TextView nTextView;
    private ImageView nBack;
    private ImageView nHome;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mImageView = (ImageView) this.findViewById(R.id.imageView);
        mButtonSave = (ImageButton) this.findViewById(R.id.button_save);
        mButtonShare = (ImageButton) this.findViewById(R.id.button_share);
        mHintView = (TextView) this.findViewById(R.id.hint);
        //设定按钮
        nTextView = (TextView) this.findViewById(R.id.title_bar_text);
        nBack = (ImageView) this.findViewById(R.id.title_bar_back);
        nHome = (ImageView) this.findViewById(R.id.title_bar_setting);
        nTextView.setText("Result");
        nBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Result界面可直接返回首页
        nHome.setImageResource(R.drawable.ic_home);
        nHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //返回首页
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //帮助返回首页
                startActivity(intent);
            }
        });

        mButtonSave.setOnClickListener(new View.OnClickListener() { //保存结果
            @Override
            public void onClick(View v) {
                saveMyBitmap(dstBitmap);
            }
        });
        mButtonShare.setOnClickListener(new View.OnClickListener() {  //分享结果
            @Override
            public void onClick(View v) {
                File f = new File(Environment.getExternalStorageDirectory(), "share.jpg");
                try {
                    FileOutputStream fos = new FileOutputStream(f);
                    dstBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpg");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                intent.putExtra(Intent.EXTRA_TEXT, "Content");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "Share to"));
            }
        });

        //新页面接收数据
        Intent intent = this.getIntent();
        //接收method值并获取照片
        dstBitmap = intent.getParcelableExtra("bitmap");
        mImageView.setImageBitmap(dstBitmap);
    }
    @Override

    public void onResume() {
        super.onResume();
        if (dstBitmap != null) {
            this.mImageView.setImageBitmap(dstBitmap);
        }
    }

    private void saveMyBitmap(Bitmap bmp){
        File dir = new File(Environment.getExternalStorageDirectory(), "PencilBuildingResult");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File f = new File(dir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    f.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + f.getAbsolutePath())));

        AnimationSet animationSet = new AnimationSet(true);
        Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(1000);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mHintView.setAlpha(1.0f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHintView.setAlpha(0.0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mHintView.startAnimation(animationSet);
    }
}
