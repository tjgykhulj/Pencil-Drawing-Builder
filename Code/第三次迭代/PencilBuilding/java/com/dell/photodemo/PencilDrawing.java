package com.dell.photodemo;

import android.graphics.Bitmap;
import android.graphics.Matrix;


import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.add;

/**
 * Created by DELL on 2015/8/30.
 */
public class PencilDrawing
{
    //不知道为什么Imgproc.java里这两个项是private的，只好自己定义了。
    private static int
            CV_INTER_LINEAR = 1,
            CV_WARP_FILL_OUTLIERS = 8;
    public static int
            SOBEL_OPERATOR = 0,
            GRADS_OPERATOR = 1;

    //边缘检测方法
    private static int edgeDetectingOperator = GRADS_OPERATOR;
    public static void useSobelOperator() { edgeDetectingOperator = SOBEL_OPERATOR; }
    public static void useGradsOperator() { edgeDetectingOperator = GRADS_OPERATOR; }
    public static int getOperator() { return edgeDetectingOperator; }
    //向dirNum个方向判断
    private static int dirNum = 4;
    public static void setDirNumber(int value) { dirNum = value; }
    public static int getDirNum() { return dirNum; }
    //卷积核的半径sz
    private static int sz = 4;
    public static void setSize(int value) { sz = value; }
    public static int getSize() { return sz; }
    //用于旋转矩阵，Java与C++的openCV接口差别很大，不确定是否正确使用
    private static Mat rotateImage(Mat img, double degree) {
        Point center = new Point((img.cols()-1) / 2.0, (img.rows()-1) / 2.0);
        Mat M = Imgproc.getRotationMatrix2D(center, degree, 1);
        Mat result = new Mat();

        Imgproc.warpAffine(img, result, M, img.size(), CV_INTER_LINEAR + CV_WARP_FILL_OUTLIERS);
        return result;
    }
    //还原类似Matlab的conv2函数
    private static void conv2(Mat img, Mat kernel, Mat dest) {
        Point anchor = new Point(kernel.cols() - kernel.cols() / 2 - 1, kernel.rows() - kernel.rows() / 2 - 1);
        Imgproc.filter2D(img, dest, img.depth(), kernel, anchor, 0);
    }
    //Mat *= value
    private static void mul(Mat srcMat, double value) {
        for (int i = 0; i < srcMat.rows(); i++)
            for (int j = 0; j < srcMat.cols(); j++)
                srcMat.put(i, j, srcMat.get(i, j)[0] * value);
    }
    //从原图获取灰度Mat
    private static Mat getGrayMat(Bitmap bitmap) {
        Mat srcMat = new Mat();
        Mat grayMat = new Mat();
        Utils.bitmapToMat(bitmap, srcMat);

        Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_RGB2GRAY);
        srcMat.release(); //释放多余空间
        return grayMat;
    }
    //平滑矩阵
    private static void smooth(double a[], int n) {
        double[] s = new double[n];
        s[0] = a[0];
        for (int i = 1; i < n; i++) s[i] = s[i - 1] + a[i];
        a[0] = s[1]/2;
        a[1] = s[2]/3;
        for (int i = 2; i < n-1; i++) a[i] = (s[i + 1] - s[i - 2]) / 3;
    }
    //求和、归一化
    private static double[] sumAndNormalize(double[] src, int len) {

        double []dest = new double[len];
        dest[0] = src[0];
        for (int i = 1; i < len; i++) dest[i] = dest[i - 1] + src[i];
        for (int i = 0; i < len; i++) dest[i] /= dest[len - 1];
        return dest;
    }

    /**************************以下供外部调用**************************************/
    //获取边缘检测
    static Mat getEdgeDetection(Bitmap srcBitmap) {

        Mat grayMat = getGrayMat(srcBitmap);
        TImage gray = new TImage(grayMat);
        int rows = grayMat.rows();
        int cols = grayMat.cols();
        if (edgeDetectingOperator == SOBEL_OPERATOR) {
            //使用Sobel算子得出边缘检测图
            for (int i = 0; i < rows - 2; i++)
                for (int j = 0; j < cols - 2; j++) {
                    int cx = (2 * gray.get(i + 2, j + 1) + gray.get(i + 2, j) + gray.get(i + 2, j + 2))
                            - (2 * gray.get(i, j + 1) + gray.get(i, j) + gray.get(i, j + 2));
                    int cy = (2 * gray.get(i + 1, j + 2) + gray.get(i, j + 2) + gray.get(i + 2, j + 2))
                            - (2 * gray.get(i + 1, j) + gray.get(i, j) + gray.get(i + 2, j));
                    gray.set(i, j, (byte) Math.sqrt(cx * cx + cy * cy));
                }
            //将倒二行与倒二行处理一下
            for (int i=0; i<cols-1; i++) gray.set(rows-2, i, gray.get(rows-3, i));
            for (int i=0; i<rows-1; i++) gray.set(i, cols-2, gray.get(i, cols-3));
        } else {
            //使用梯度算子得出边缘检测图
            for (int i = 0; i < grayMat.rows() - 1; i++)
                for (int j = 0; j < grayMat.cols() - 1; j++) {
                    int cx = gray.get(i, j + 1) - gray.get(i, j);
                    int cy = gray.get(i + 1, j) - gray.get(i, j);
                    gray.set(i, j, (byte) Math.sqrt(cx * cx + cy * cy));
                }
        }
        //将末行末列处理一下
        for (int i=0; i<cols; i++) gray.set(rows-1, i, gray.get(rows-2, i));
        for (int i=0; i<rows; i++) gray.set(i, cols-1, gray.get(i, cols-2));
        grayMat.put(0, 0, gray.data);
        return grayMat;
    }
    //通过边缘检测获取Stroke图
    static Mat getStrokeFromEdge(Mat srcMat) {
        //将边缘图映射到0..1
        Mat edgeMat = new Mat();
        srcMat.convertTo(edgeMat, CvType.CV_32FC1);
        mul(edgeMat, 1.0 / 256);

        System.out.println("Step 1 : mul");
        //计算L0
        int len = sz * 2 + 1;
        Mat L = new Mat(len, len, CvType.CV_32FC1, Scalar.all(0));
        for (int i = 0; i < len; i++) L.put(sz, i, 1);

        //计算Li与Gi
        Mat[] l = new Mat[dirNum];
        Mat[] G = new Mat[dirNum];
        for (int i = 0; i < dirNum; i++)
        {
            G[i] = new Mat();
            l[i] = rotateImage(L, i * 180.0 / dirNum);
            mul(l[i], 1.0 / len);
            conv2(edgeMat, l[i], G[i]);
        }
        System.out.println("Step 2 : Calc L,G");

        //初始化并计算Ci
        Mat[] C = new Mat[dirNum];
        for (int i = 0; i < dirNum; i++) {
            C[i] = new Mat(edgeMat.rows(), edgeMat.cols(), CvType.CV_32FC1, Scalar.all(0));
        }

        for (int i = 0; i < edgeMat.rows(); i++)
            for (int j = 0; j < edgeMat.cols(); j++) {
                int key = 0;
                for (int k = 1; k < dirNum; k++)
                    if (G[key].get(i, j)[0] < G[k].get(i, j)[0]) key = k;
                ;
                C[key].put(i, j, edgeMat.get(i, j));
            }
        System.out.println("Step 3 : Calc C");

        //计算S
        Mat S = new Mat(edgeMat.rows(), edgeMat.cols(), CvType.CV_32FC1, Scalar.all(0));
        for (int i = 0; i < dirNum; i++) {
            Mat temp = new Mat();
            conv2(C[i], l[i], temp);
            Mat sum = new Mat();
            add(S, temp, sum);

            S.release();
            temp.release();
            S = sum;
        }
        System.out.println("Step 4 : Calc S");

        for (int i = 0; i < dirNum; i++) {
            G[i].release();
            C[i].release();
            l[i].release();
        }

        //将S映射到0..1
        float Max = 0;
        for (int i = 0; i < S.rows(); i++)
            for (int j = 0; j < S.cols(); j++) Max = Math.max(Max, (float) S.get(i,j)[0]);

        for (int i = 0; i < S.rows(); i++)
            for (int j = 0; j < S.cols(); j++) S.put(i,j, (1-S.get(i,j)[0]/Max)*255);

        Mat ans = new Mat();
        S.convertTo(ans, CvType.CV_8UC1);
        S.release();

        System.out.println("Step 5 : end");
        return ans;
    }
    //获取Tone图
    static Mat getTone(Bitmap srcBitmap, Bitmap tonalBitmap) {
        //计算p函数并归一化为G
        double p1, p2, p3;
        double[] p = new double[256];
        double temp = Math.sqrt(2 * Math.PI * 10);
        for (int i = 0; i < 256; i++) {
            p1 = 1 / 9.0 * Math.exp(-(256 - i) / 9.0);
            p2 = (i >= 105 && i <= 255) ? 1.0/(255-105) : 0;
            p3 = Math.exp(-(i - 80) * (i - 80) / (2.0 * 10 * 10)) / temp;
            p[i] = 0.52 * p1 + 0.37 * p2 + 0.11 * p3;
        }
        smooth(p, 256);
        smooth(p, 256);
        double[] G = sumAndNormalize(p, 256);
        //读入原图并归一化为S
        Mat grayMat = getGrayMat(srcBitmap);
        TImage gray = new TImage(grayMat);
        double[] pHis = new double[256];
        for (int i = 0; i < 256; i++) pHis[i] = 0;
        for (int i = 0; i < grayMat.rows(); i++)
            for (int j = 0; j < grayMat.cols(); j++) {
                pHis[gray.get(i,j)] += 1;
            }
        double[] S = sumAndNormalize(pHis, 256);

        int[] index = new int[256];
        for (int i = 0; i < 256; i++) {
            int k = 0;
            for (int j = 1; j < 256; j++)
                if (Math.abs(G[k] - S[i]) > Math.abs(G[j] - S[i])) k = j;
            index[i] = k;
        }
        for (int i = 0; i < grayMat.rows(); i++)
            for (int j = 0; j < grayMat.cols(); j++) gray.set(i, j, index[gray.get(i, j)]);

        Mat pRender = getGrayMat(tonalBitmap);
        TImage pR = new TImage(pRender);

        double [][]b = new double[grayMat.rows()][grayMat.cols()];
        for (int i = 0; i < grayMat.rows(); i++)
            for (int j = 0; j < grayMat.cols(); j++)
            {
                double H = pR.get(i % pRender.rows(), j % pRender.cols()) / 256.0;
                double J = gray.get(i,j) / 256.0;
                double bx = (i>0) ? b[i - 1][j] : 0;
                double by = (j>0) ? b[i][j - 1] : 0;
                double A = 0.2 * 2 + Math.log(H) * Math.log(H);
                double B = -2 * (0.2*(bx + by) + Math.log(H) * Math.log(J));
                b[i][j] = -B / (2 * A);
                gray.set(i, j, (int) (Math.pow(H, b[i][j]) * 256));
            }
        pRender.release();
        grayMat.put(0, 0, gray.data);
        return grayMat;
    }

    static Mat getPencilDrawing(Bitmap srcBitmap, Bitmap tonalBitmap) {
        Mat edgeMat = PencilDrawing.getEdgeDetection(srcBitmap);
        Mat strokeMat = PencilDrawing.getStrokeFromEdge(edgeMat);
        edgeMat.release();
        Mat toneMat = PencilDrawing.getTone(srcBitmap, tonalBitmap);
        TImage stroke = new TImage(strokeMat);
        TImage tone = new TImage(toneMat);
        int rows = toneMat.rows();
        int cols = toneMat.cols();
        Mat ansMat = new Mat(rows, cols, CvType.CV_8UC1, Scalar.all(0));
        TImage ans = new TImage(ansMat);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                ans.set(i, j, (int) Math.sqrt(stroke.get(i,j) * tone.get(i,j)));
            }
        ansMat.put(0,0,ans.data);
        strokeMat.release();
        toneMat.release();
        return ansMat;
    }
}
