package com.dell.photodemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.dell.photodemo.ConvertActivity;
import com.dell.photodemo.R;

/**
 * Created by DELL on 2015/9/7.
 */
public class ChooseFromFragment extends Fragment {

    private ImageButton mButtonCamera;
    private ImageButton mButtonPhoto;

    private void openIntent(String method) {
        //�½�һ����ʽ��ͼ����һ������Ϊ��ǰActivity����󣬵ڶ�������Ϊ��Ҫ�򿪵�Activity��
        Intent intent = new Intent(getActivity(), ConvertActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //����������ҳ
        intent.putExtra("method", method);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_from, container, false);
        mButtonCamera = (ImageButton) rootView.findViewById(R.id.button_camera);
        mButtonPhoto = (ImageButton) rootView.findViewById(R.id.button_photo);
        mButtonCamera.setOnClickListener(new View.OnClickListener() { //��Camera
            @Override
            public void onClick(View v) {
                openIntent("Camera");
            }
        });
        mButtonPhoto.setOnClickListener(new View.OnClickListener() {  //��ȡ���
            @Override
            public void onClick(View v) {
                openIntent("Photo");
            }
        });

        return rootView;
    }
}