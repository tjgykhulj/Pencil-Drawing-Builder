package com.dell.photodemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dell.photodemo.PencilDrawing;
import com.dell.photodemo.R;

import org.opencv.android.OpenCVLoader;

/**
 * Created by DELL on 2015/9/11.
 */
public class SettingActivity extends Activity {

    SeekBar dirNumSeek;
    SeekBar sizeSeek;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //设定按钮
        TextView nTextView = (TextView) this.findViewById(R.id.title_bar_text);
        ImageView nBack = (ImageView) this.findViewById(R.id.title_bar_back);
        ImageView nSetting = (ImageView) this.findViewById(R.id.title_bar_setting);
        //Setting界面不必再显示设置
        nSetting.setVisibility(View.INVISIBLE);
        nTextView.setText("Result");
        nBack.setOnClickListener(new View.OnClickListener() { //返回
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //根据ID找到RadioGroup实例
        RadioGroup group = (RadioGroup) findViewById(R.id.operatorGroup);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                if (radioButtonId == R.id.sobel_operator)
                    PencilDrawing.useSobelOperator();
                else
                    PencilDrawing.useGradsOperator();
            }
        });

        final TextView dirNumText = (TextView) findViewById(R.id.textview_dirnum);
        dirNumSeek = (SeekBar) findViewById(R.id.seekbar_dirnum);
        dirNumSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = seekBar.getProgress() + 2;
                PencilDrawing.setDirNumber(value);
                dirNumText.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView sizeText = (TextView) findViewById(R.id.textview_size);
        sizeSeek = (SeekBar) findViewById(R.id.seekbar_size);
        sizeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = seekBar.getProgress();
                PencilDrawing.setSize(value);
                sizeText.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void onResume() {
        super.onResume();
        if (PencilDrawing.getOperator() == PencilDrawing.SOBEL_OPERATOR) {
            RadioButton sobel = (RadioButton) findViewById(R.id.sobel_operator);
            sobel.setChecked(true);
        } else {
            RadioButton grads = (RadioButton) findViewById(R.id.grads_operator);
            grads.setChecked(true);
        }
        dirNumSeek.setProgress(PencilDrawing.getDirNum() - 2);
        sizeSeek.setProgress(PencilDrawing.getSize());
    }
}
