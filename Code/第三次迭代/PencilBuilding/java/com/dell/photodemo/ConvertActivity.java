package com.dell.photodemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by DELL on 2015/9/7.
 */
public class ConvertActivity extends Activity {

    private Bitmap srcBitmap;
    private Bitmap tonalBitmap;
    private ImageView mImageView;
    private ImageButton mButtonStandard;
    private ImageButton mButtonStroke;
    private TextView nTextView;
    private ImageView nBack;
    private ImageView nSetting;
    boolean flags = false;

    private void openIntent(Bitmap dstBitmap) {
        //新建一个显式意图，第一个参数为当前Activity类对象，第二个参数为你要打开的Activity类
        Intent intent =new Intent(ConvertActivity.this, ResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //帮助返回首页
        intent.putExtra("bitmap", dstBitmap);
        startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);
        mImageView = (ImageView) this.findViewById(R.id.imageView);
        mButtonStandard = (ImageButton) this.findViewById(R.id.button_standard);
        mButtonStroke = (ImageButton) this.findViewById(R.id.button_stroke);

        nTextView = (TextView) this.findViewById(R.id.title_bar_text);
        nBack = (ImageView) this.findViewById(R.id.title_bar_back);
        nSetting = (ImageView) this.findViewById(R.id.title_bar_setting);
        nTextView.setText("Convert");
        nBack.setOnClickListener(new View.OnClickListener() { //打开Camera
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nSetting.setOnClickListener(new View.OnClickListener() { //打开Camera
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConvertActivity.this, SettingActivity.class));
            }
        });
        //设定按钮
        mButtonStandard.setOnClickListener(new View.OnClickListener() { //Standard
            @Override
            public void onClick(View v) {
                Mat ansMat = PencilDrawing.getPencilDrawing(srcBitmap, tonalBitmap);
                //设置图像并释放多余空间
                Bitmap dstBitmap = Bitmap.createBitmap(srcBitmap);
                Utils.matToBitmap(ansMat, dstBitmap); //mat to bitmap
                ansMat.release();
                openIntent(dstBitmap);
            }
        });
        mButtonStroke.setOnClickListener(new View.OnClickListener() {  //Stroke
            @Override
            public void onClick(View v) {
                Mat edgeMat = PencilDrawing.getEdgeDetection(srcBitmap);
                Mat ansMat = PencilDrawing.getStrokeFromEdge(edgeMat);
                //设置图像并释放多余空间
                Bitmap dstBitmap = Bitmap.createBitmap(srcBitmap);
                Utils.matToBitmap(ansMat, dstBitmap); //mat to bitmap
                edgeMat.release();
                ansMat.release();
                openIntent(dstBitmap);
            }
        });

        //新页面接收数据
        Intent srcIntent = this.getIntent();
        //接收method值并获取照片
        String method = srcIntent.getStringExtra("method");
        if (method.equals("Camera")) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), "camera.jpg")));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(intent, 10);
        } else
        if (method.equals("Photo")) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 11);
        } else
        if (method.equals("Sample")) {
            //接受时将srcBitmap也要处理，方便计算
            int resId = srcIntent.getIntExtra("resId", R.drawable.sample_other_1);
            srcBitmap = BitmapFactory.decodeResource(getResources(), resId);
            makeItSmaller();
            mImageView.setImageBitmap(srcBitmap);
        }
    }

    private void makeItSmaller() {
        float len = 400;
        if (srcBitmap.getWidth() * srcBitmap.getHeight() > len*len) {
            Matrix matrix = new Matrix();
            float z = len / Math.max(srcBitmap.getWidth(), srcBitmap.getHeight());
            matrix.postScale(z, z); //长和宽放大缩小的比例
            Bitmap temp = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
            srcBitmap.recycle();
            srcBitmap = temp;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK) return;
        Uri uri;
        if (requestCode == 10)
            uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "camera.jpg"));
        else
            uri = data.getData();
        ContentResolver cr = this.getContentResolver();
        try {
            if(srcBitmap != null)
                srcBitmap.recycle();
            srcBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
            makeItSmaller();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.mImageView.setImageBitmap(srcBitmap);
    }

    @Override
    public void onResume() {
        super.onResume();
        Resources res = getResources();
        tonalBitmap = BitmapFactory.decodeResource(res, R.drawable.tonal_texture);
        if (srcBitmap != null)
            this.mImageView.setImageBitmap(srcBitmap);
        else if (flags)
            finish();
        else
            flags = true;
    }
}
