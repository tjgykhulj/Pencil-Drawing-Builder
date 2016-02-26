package com.dell.photodemo;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by DELL on 2015/9/3.
 */
public class TImage {
    byte[] data;
    int cols;

    //载入Mat并读出数据
    TImage(Mat srcMat) {
        if (srcMat.type() != CvType.CV_8UC1) {
            srcMat.convertTo(srcMat, CvType.CV_8UC1);
        }
        cols = srcMat.cols();
        data = new byte[srcMat.rows() * cols];
        srcMat.get(0, 0, data);
    }

    void set(int x, int y, int val)
    {
        data[x*cols+y] = (byte) val;
    }
    int get(int x, int y)
    {
        int ans = data[x * cols + y];
        if (ans<0) ans+=256;
        return ans;
    }
}
