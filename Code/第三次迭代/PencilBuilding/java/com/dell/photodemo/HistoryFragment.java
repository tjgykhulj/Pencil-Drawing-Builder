package com.dell.photodemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.ViewSwitcher;

import com.dell.photodemo.R;
import com.dell.photodemo.adapter.HistoryImageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 2015/9/10.
 */
public class HistoryFragment extends Fragment implements ViewSwitcher.ViewFactory {

    private ImageSwitcher imageSwitcher;
    private Gallery gallery;
    private List<String> photoList;
    private int downX,upX;
    private String newFilePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        photoList = readFileList();
        imageSwitcher = (ImageSwitcher) rootView.findViewById(R.id.switcher);
        imageSwitcher.setFactory(this);
        /*
         * ���뵭��Ч��
         */
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.fade_out));
        imageSwitcher.setOnTouchListener(touchListener);
        gallery = (Gallery)rootView.findViewById(R.id.gallery);
        gallery.setAdapter(new HistoryImageAdapter(photoList, getActivity()));
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long when) {
                newFilePath = photoList.get(position);
                Bitmap bm = BitmapFactory.decodeFile(newFilePath);
                BitmapDrawable bd = new BitmapDrawable(bm);
                imageSwitcher.setImageDrawable(bd);
            }
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
        return rootView;
    }

    /**
     * ע��һ�������¼�
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                downX=(int) event.getX();//ȡ�ð���ʱ������
                return true;
            }
            else if(event.getAction()==MotionEvent.ACTION_UP)
            {
                upX=(int) event.getX();//ȡ���ɿ�ʱ������
                int index=0;
                if(upX-downX>100)//�����ϵ��ң�����ǰһ��
                {
                    //����ǵ�һ����ȥ��β��
                    if(gallery.getSelectedItemPosition()==0)
                        index=gallery.getCount()-1;
                    else
                        index=gallery.getSelectedItemPosition()-1;
                }
                else if(downX-upX>100)//�����ϵ��󣬼�����һ��
                {
                    //����������ȥ����һ
                    if(gallery.getSelectedItemPosition()==(gallery.getCount()-1))
                        index=0;
                    else
                        index=gallery.getSelectedItemPosition()+1;
                }
                //�ı�galleryͼƬ��ѡ���Զ�����ImageSwitcher��setOnItemSelectedListener
                gallery.setSelection(index, true);
                return true;
            }
            return false;
        }
    };

    /**
     * ��ȡSD���е�����ͼƬ·��
     * @return
     */
    private List<String> readFileList(){
        List<String> fileList = new ArrayList<String>();
        File fileDir = new File(Environment.getExternalStorageDirectory(), "PencilBuildingResult");
        File[] files = fileDir.listFiles();
        if(files!=null){
            for(File file:files) {
                fileList.add(file.getPath());
            }
        }
        return fileList;
    }

    @Override
    public View makeView() {
        ImageView imageView = new ImageView(getActivity());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(//����ӦͼƬ��С
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return imageView;
    }
}
