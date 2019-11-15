package com.example.joe.hexcamera;

import android.hardware.Camera;
import android.hardware.Camera.Size;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CamParaUtil {
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CamParaUtil myCamPara = null;
    private CamParaUtil(){

    }
    public static CamParaUtil getInstance(){
        if(myCamPara == null){
            myCamPara = new CamParaUtil();
            return myCamPara;
        }
        else{
            return myCamPara;
        }
    }

    public Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Size s:list){
            if((s.width >= minWidth) && equalRate(s, th)){
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;
        }
        return list.get(i);
    }
    public Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Size s:list){
            if((s.width >= minWidth) && equalRate(s, th)){
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;
        }
        return list.get(i);
    }

    public boolean equalRate(Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.03)
        {
            return true;
        }
        else{
            return false;
        }
    }

    public  class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Size lhs, Size rhs) {
            if(lhs.width == rhs.width){
                return 0;
            }
            else if(lhs.width > rhs.width){
                return 1;
            }
            else{
                return -1;
            }
        }

    }

    public  void printSupportPreviewSize(Camera.Parameters params){
        List<Size> previewSizes = params.getSupportedPreviewSizes();
        for(int i=0; i< previewSizes.size(); i++){
            Size size = previewSizes.get(i);
        }

    }

    public  void printSupportPictureSize(Camera.Parameters params){
        List<Size> pictureSizes = params.getSupportedPictureSizes();
        for(int i=0; i< pictureSizes.size(); i++){
            Size size = pictureSizes.get(i);
        }
    }

    public void printSupportFocusMode(Camera.Parameters params){
        List<String> focusModes = params.getSupportedFocusModes();
    }
}
