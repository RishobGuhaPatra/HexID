package com.example.joe.hexcamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.joe.example.hexcamera.CameraSurfaceView;

public class MainActivity extends Activity implements CameraInterface.CamOpenOverCallback,Camera.PreviewCallback {

    CameraSurfaceView surfaceView = null;
    ImageButton shutterBtn;
    TextView tvColorHEX;
    float previewRate = -1f;
    private int[] pixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread openThread = new Thread(){
            @Override
            public void run() {
                CameraInterface.getInstance().doOpenCamera(MainActivity.this);
            }
        };
        openThread.start();
        setContentView(R.layout.activity_main);
        initUI();
        initViewParams();

        shutterBtn.setOnClickListener(new BtnListeners());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void initUI(){
        surfaceView = findViewById(R.id.camera_surfaceview);
        shutterBtn = findViewById(R.id.btn_shutter);
        tvColorHEX = findViewById(R.id.tv_color_HEX);
    }

    private void initViewParams() {
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        params.width = dm.widthPixels;
        params.height = dm.heightPixels;
        previewRate = DisplayUtil.getScreenRate(this);
        surfaceView.setLayoutParams(params);
        pixels = new int[params.width * params.height];

        ViewGroup.LayoutParams p2 = shutterBtn.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);;
        shutterBtn.setLayoutParams(p2);
    }

    private class BtnListeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_shutter:
                    CameraInterface.getInstance().doTakePicture();
                    break;
                default:break;
            }
        }
    }

    @Override
    public void cameraHasOpened() {
        SurfaceHolder holder = surfaceView.getSurfaceHolder();
        CameraInterface.getInstance().setPreviewCallback(this);
        CameraInterface.getInstance().doStartPreview(holder, previewRate);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (pixels == null ) return;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        //decodeYUV420SP(pixels, data, dm.widthPixels, dm.heightPixels);
        String a = Integer.toHexString(pixels[pixels.length / 2 + dm.heightPixels/2]);
        tvColorHEX.setText(a);
    }

    public void decodeYUV420SP(int[] rgba, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int r, g, b, y1192, y, i, uvp, u, v;
        for (int j = 0, yp = 0; j < height; j++) {
            uvp = frameSize + (j >> 1) * width;
            u = 0;
            v = 0;
            for (i = 0; i < width; i++, yp++) {
                y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    u = (0xff & yuv420sp[uvp++]) - 128;
                    v = (0xff & yuv420sp[uvp++]) - 128;
                }

                y1192 = 1192 * y;
                r = (y1192 + 1634 * v);
                g = (y1192 - 833 * v - 400 * u);
                b = (y1192 + 2066 * u);

                r = Math.max(0, Math.min(r, 262143));
                g = Math.max(0, Math.min(g, 262143));
                b = Math.max(0, Math.min(b, 262143));

                rgba[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00)
                        | ((b >> 10) | 0xff);
            }
        }
    }
}
