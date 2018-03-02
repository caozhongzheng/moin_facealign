package com.iflytek;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.util.Accelerometer;
import com.iflytek.util.FaceRect;
import com.iflytek.util.FaceUtil;
import com.iflytek.util.MyPoint;
import com.iflytek.util.ParseResult;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.Point2D;
import com.moinapp.wuliao.util.BitmapUtil;

import java.io.IOException;

/**
 * 离线视频流检测示例
 * 该业务仅支持离线人脸检测SDK，请开发者前往<a href="http://www.xfyun.cn/">讯飞语音云</a>SDK下载界面，下载对应离线SDK
 */
public class VideoDemo extends Activity {
    public final static String TAG = VideoDemo.class.getSimpleName();
    private SurfaceView mPreviewSurface;
    private SurfaceView mFaceSurface;
    private RadioGroup alignGruop;
    private Button takePicture;
    private Camera mCamera;
    private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;
    // Camera nv21格式预览帧的尺寸，默认设置640*480
    public static final int PREVIEW_WIDTH = 640;
    public static final int PREVIEW_HEIGHT = 480;
    // 预览帧数据存储数组和缓存数组
    private byte[] nv21;
    private byte[] buffer;
    // 缩放矩阵
    private Matrix mScaleMatrix = new Matrix();
    // 加速度感应器，用于获取手机的朝向
    private Accelerometer mAcc;
    // FaceDetector对象，集成了离线人脸识别：人脸检测、视频流检测功能
    private FaceDetector mFaceDetector;
    private boolean mStopTrack;
    private Toast mToast;
    private long mLastClickTime;
    private int isAlign = 0;
    private Paint paint;

    //Camera类
    private android.graphics.Camera mCamera2;

    private Bitmap face;
    private Paint mPaint = new Paint();
    private boolean canPrint = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_demo);

        initUI();

        nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        buffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        mAcc = new Accelerometer(VideoDemo.this);
        mFaceDetector = FaceDetector.createDetector(VideoDemo.this, null);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);

        mCamera2 = new android.graphics.Camera();
        mPaint.setAntiAlias(true);
    }


    private Callback mPreviewCallback = new Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            closeCamera();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            openCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            mScaleMatrix.setScale(width / (float) PREVIEW_HEIGHT, height / (float) PREVIEW_WIDTH);
            Log.w(TAG, "mScaleMatrix=" + mScaleMatrix.toString());
        }
    };

    private void setSurfaceSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = (int) (width * PREVIEW_WIDTH / (float)PREVIEW_HEIGHT);
        RelativeLayout.LayoutParams params = new LayoutParams(width, height);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        mPreviewSurface.setLayoutParams(params);
        mFaceSurface.setLayoutParams(params);

        ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) findViewById(R.id.ly_bottom_area).getLayoutParams();
        params1.topMargin = width;
//        params1.width = width;
//        params1.height = (int) (metrics.heightPixels - width - TDevice.dpToPixel(24f));
    }

    @SuppressLint("ShowToast")
    @SuppressWarnings("deprecation")
    private void initUI() {
        mPreviewSurface = (SurfaceView) findViewById(R.id.sfv_preview);
        mFaceSurface = (SurfaceView) findViewById(R.id.sfv_face);

        mPreviewSurface.getHolder().addCallback(mPreviewCallback);
        mPreviewSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mFaceSurface.setZOrderOnTop(true);
        mFaceSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        // 点击SurfaceView，切换摄相头
        mFaceSurface.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 只有一个摄相头，不支持切换
                if (Camera.getNumberOfCameras() == 1) {
                    showTip("只有后置摄像头，不能切换");
                    return;
                }
                closeCamera();
                if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
                    mCameraId = CameraInfo.CAMERA_FACING_BACK;
                } else {
                    mCameraId = CameraInfo.CAMERA_FACING_FRONT;
                }
                canPrint = true;
                openCamera();
            }
        });

        // 长按SurfaceView 500ms后松开，摄相头聚集
        mFaceSurface.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastClickTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - mLastClickTime > 500) {
                            mCamera.autoFocus(null);

                            return true;
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

        alignGruop = (RadioGroup) findViewById(R.id.align_mode);
        alignGruop.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                switch (arg1) {
                    case R.id.detect:
                        isAlign = 0;
                        break;
                    case R.id.align:
                        isAlign = 1;
                        break;
                    default:
                        break;
                }
            }
        });

        takePicture = (Button) findViewById(R.id.take_picture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mCamera.takePicture(null, null, mPictureCallback);
                } catch (Throwable t) {
                    t.printStackTrace();
                    showTip("拍照失败，请重试！");
                    try {
                        mCamera.startPreview();
                    } catch (Throwable e) {

                    }
                }
            }
        });
        setSurfaceSize();
        mToast = Toast.makeText(VideoDemo.this, "", Toast.LENGTH_SHORT);
    }

    //点击拍照
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (null != mCamera) {
                try {
                    String path = BitmapUtil.saveToSDCard(data, CameraInfo.CAMERA_FACING_FRONT == mCameraId, BitmapUtil.BITMAP_CACHE + "1.jpg");
                    showTip("拍照成功 " + path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void openCamera() {
        if (null != mCamera) {
            return;
        }

        if (!checkCameraPermission()) {
            showTip("摄像头权限未打开，请打开后再试");
            mStopTrack = true;
            return;
        }

        // 只有一个摄相头，打开后置
        if (Camera.getNumberOfCameras() == 1) {
            mCameraId = CameraInfo.CAMERA_FACING_BACK;
        }

        try {
            mCamera = Camera.open(mCameraId);
            if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
                showTip("前置摄像头已开启，点击可切换");
            } else if (Camera.getNumberOfCameras() > 1) {
                showTip("后置摄像头已开启，点击可切换");
            } else {
                showTip("后置摄像头已开启");
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeCamera();
            return;
        }

        Parameters params = mCamera.getParameters();
        params.setPreviewFormat(ImageFormat.NV21);
        params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        params.setPictureSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        mCamera.setParameters(params);

        // 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewCallback(new PreviewCallback() {

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                System.arraycopy(data, 0, nv21, 0, data.length);
            }
        });

        try {
            mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private boolean checkCameraPermission() {
        int status = checkPermission(permission.CAMERA, Process.myPid(), Process.myUid());
        if (PackageManager.PERMISSION_GRANTED == status) {
            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null != mAcc) {
            // 启动监听重力加速计,以获取手机朝向
            mAcc.start();
        }

        mStopTrack = false;
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!mStopTrack) {
                    if (null == nv21) {
                        continue;
                    }

                    synchronized (nv21) {
                        System.arraycopy(nv21, 0, buffer, 0, nv21.length);
                    }

                    // 获取手机朝向，返回值0,1,2,3分别表示0,90,180和270度
                    int direction = Accelerometer.getDirection();
                    boolean frontCamera = (CameraInfo.CAMERA_FACING_FRONT == mCameraId);
                    // 前置摄像头预览显示的是镜像，需要将手机朝向换算成摄相头视角下的朝向。
                    // 转换公式：a' = (360 - a)%360，a为人眼视角下的朝向（单位：角度）
                    if (frontCamera) {
                        // SDK中使用0,1,2,3,4分别表示0,90,180,270和360度
                        direction = (4 - direction)%4;
                    }

                    if(mFaceDetector == null) {
                        /**
                         * 离线视频流检测功能需要单独下载支持离线人脸的SDK
                         * 请开发者前往语音云官网下载对应SDK
                         */
                        showTip("本SDK不支持离线视频流检测");
                        break;
                    }

                    String result = mFaceDetector.trackNV21(buffer, PREVIEW_WIDTH, PREVIEW_HEIGHT, isAlign, direction);
//                    Log.d(TAG, "result:"+result);

                    FaceRect[] faces = ParseResult.parseResult(result);

                    // new parse result 把识别结果转为对象
                    FaceAlignResult faceAlignResult = ParseResult.parseGsonResult(result);
                    if (faceAlignResult == null || faceAlignResult.getFace() == null || faceAlignResult.getFace().isEmpty()) {
                        // 没聚焦出脸的时候,什么也不做
                        continue;
                    }

                    Canvas canvas = mFaceSurface.getHolder().lockCanvas();
                    if (null == canvas) {
                        continue;
                    }

                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    // 在目标设备上缩放
                    canvas.setMatrix(mScaleMatrix);
/* new parse result
                    // 准备画21点和脸部rect
                    if (frontCamera == (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId)) {

                        for (FaceInfo face: faceAlignResult.getFace()) {
                            if (face != null) {
                                face.setPosition(FaceUtil.RotateDeg90(face.getPosition(), PREVIEW_WIDTH, PREVIEW_HEIGHT));
                                FaceUtil.RotateDeg90(face.getLandmark(), PREVIEW_WIDTH, PREVIEW_HEIGHT);
                            }

                            FaceUtil.drawFaceRect(canvas, face, PREVIEW_WIDTH, PREVIEW_HEIGHT,
                                    frontCamera, false);
                        }
                    } else {
                        Log.d(TAG, "faces:0");
                    }
*/

/* old parse result*/

                    if( faces.length <=0 ) {
                        mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
                        continue;
                    }
                    if (null != faces && frontCamera == (CameraInfo.CAMERA_FACING_FRONT == mCameraId)) {
                        for (FaceRect face: faces) {
                            face.bound = FaceUtil.RotateDeg90(face.bound, PREVIEW_WIDTH, PREVIEW_HEIGHT);

                            if (face.point != null) {
                                for (int i = 0; i < face.point.length; i++) {
                                    face.point[i] = FaceUtil.RotateDeg90(face.point[i], PREVIEW_WIDTH, PREVIEW_HEIGHT);
                                }

                            }
                            FaceUtil.drawFaceRect(canvas, face, PREVIEW_WIDTH, PREVIEW_HEIGHT,
                                    frontCamera, true);
                            if (face.point != null) {
                                //TODO 在此处计算旋转角度, [但是还没有考虑将手机沿着Z轴旋转的情况,需要考虑divide by zero.以及不用y,而是用distance]

                                MyPoint lem, rem, nt;
                                // eye middle point
                                MyPoint p1 = lem = face.getPointByKey(MyPoint.KEY_LEFT_EYE_CENTER);
                                MyPoint p2 = rem = face.getPointByKey(MyPoint.KEY_RIGHT_EYE_CENTER);
                                MyPoint eyeHMP = new MyPoint(Point2D.getMidpointCoordinate(p1.x, p1.y, p2.x, p2.y), "eye_middle");
                                log(canPrint, p1.toString());
                                log(canPrint, p2.toString());
                                log(canPrint, eyeHMP.toString());


                                // nose horizontal middle point
                                p1 = face.getPointByKey(MyPoint.KEY_NOSE_LEFT);
                                p2 = face.getPointByKey(MyPoint.KEY_NOSE_RIGHT);
                                MyPoint noseHMP = new MyPoint(Point2D.getMidpointCoordinate(p1.x, p1.y, p2.x, p2.y), "nose_lr_middle");
                                log(canPrint, p1.toString());
                                log(canPrint, p2.toString());
                                log(canPrint, noseHMP.toString());

                                // nose vertical middle point
                                p1 = nt = face.getPointByKey(MyPoint.KEY_NOSE_TOP);
                                p2 = face.getPointByKey(MyPoint.KEY_NOSE_BOTTOM);
                                MyPoint noseVMP = new MyPoint(Point2D.getMidpointCoordinate(p1.x, p1.y, p2.x, p2.y), "nose_tb_middle");
                                log(canPrint, p1.toString());
                                log(canPrint, p2.toString());
                                log(canPrint, noseVMP.toString());


                                MyPoint mouthMP = face.getPointByKey(MyPoint.KEY_MOUTH_MIDDLE);
                                log(canPrint, mouthMP.toString());

                                int y_eye_nose_lr = eyeHMP.y - noseHMP.y;
                                int y_eye_nose_tb = eyeHMP.y - noseVMP.y;
                                int y_nose_mouth_lr = noseHMP.y - mouthMP.y;
                                int y_nose_mouth_tb = noseVMP.y - mouthMP.y;
                                if (y_nose_mouth_lr == 0 || y_nose_mouth_tb == 0) {
                                    continue;
                                }

                                int ratio_eye_nose_lr = y_eye_nose_lr * 10000 / y_nose_mouth_lr;
                                int ratio_eye_nose_tb = y_eye_nose_tb * 10000 / y_nose_mouth_tb;
                                int ratio_eye_nose_mouth = (ratio_eye_nose_lr + ratio_eye_nose_tb) / 2;
                                log(canPrint, "y_eye_nose_lr= " + y_eye_nose_lr);
                                log(canPrint, "y_eye_nose_tb= " + y_eye_nose_tb);
                                log(canPrint, "y_nose_mouth_lr= " + y_nose_mouth_lr);
                                log(canPrint, "y_nose_mouth_tb= " + y_nose_mouth_tb);
                                log(canPrint, "ratio_eye_nose_lr= " + ratio_eye_nose_lr);
                                log(canPrint, "ratio_eye_nose_tb= " + ratio_eye_nose_tb);

                                double distance_le_n = Point2D.distance(lem.x, lem.y, nt.x, nt.y);
                                double distance_re_n = Point2D.distance(rem.x, rem.y, nt.x, nt.y);
                                if (distance_le_n == 0 || distance_re_n == 0) {
                                    continue;
                                }
                                double ratio_le_n_re = Math.max(distance_le_n, distance_re_n) * 10000 / Math.min(distance_le_n, distance_re_n);
                                log(canPrint, "ratio_le_n_re= " + ratio_le_n_re + ", distance_le_n= " + distance_le_n + ", distance_re_n= " + distance_re_n);


                                int deltaNoseMidY = (int) Point2D.distance(noseHMP.x, noseHMP.y, noseVMP.x, noseVMP.y);
                                log(true, "your nose H,V middle deltaY = " + deltaNoseMidY);
                                StringBuffer sb = new StringBuffer();

                                boolean tured = ratio_le_n_re >= 11000D;
                                if (tured) {
                                    // 转头了
                                    boolean left = distance_le_n > distance_re_n;

//                                    showTip("转头-> " + (left ? "左" : "右") + " :" + ratio_le_n_re);
                                    sb.append("转头-> " + (left ? "左" : "右") + " :" + ratio_le_n_re);
                                    log(true, "your face turn " + (left ? "left" : "right") + " :" + ratio_le_n_re);
                                }

                                if (Math.abs(deltaNoseMidY) > 5) {
                                    if (tured) {
                                        sb.append(" + ");
                                    }
                                    if (ratio_eye_nose_mouth <= 10000) {
                                        // 抬头
//                                        showTip("抬头");
                                        sb.append("抬头");
                                        log(true, "you look up:" + ratio_eye_nose_mouth);
                                    } else if (ratio_eye_nose_mouth >= 14000) {
                                        // 低头
                                        sb.append("低头");
//                                        showTip("低头");
                                        log(true, "you bow your head:" + ratio_eye_nose_mouth);
                                    }
                                } else if (!tured){
                                    sb.append("拍照");
//                                    showTip("拍照");
                                }
                                showTip(sb.toString());

                                Paint paint = new Paint();
                                paint.setColor(Color.RED);
                                paint.setStrokeWidth(10);
                                // 画左眼(左右转的话,可以沿着Y轴)
                                String key = MyPoint.KEY_LEFT_EYE_CENTER;
                                MyPoint lemPoint = face.getPointByKey(key);
                                if (lemPoint != null) {
                                    Bitmap leftEyeBmp = BitmapFactory.decodeResource(getResources(), frontCamera ? R.drawable.eye_left : R.drawable.eye_right);

                                    Bitmap l = FaceUtil.scaleBitmap(leftEyeBmp, face.getPointByKey(MyPoint.KEY_LEFT_EYE_LEFT_CORNER), face.getPointByKey(MyPoint.KEY_LEFT_EYE_RIGHT_CORNER), frontCamera);
                                    Bitmap leb = BitmapUtil.doMatrix(l, rotate(l, (int) (distance_re_n - distance_le_n), noseHMP.x - noseVMP.x));
                                    canvas.drawBitmap(leb, lemPoint.x - leb.getWidth() / 2, lemPoint.y - leb.getHeight() / 2, null);
                                }

                                // 画右眼
                                key = MyPoint.KEY_RIGHT_EYE_CENTER;
                                MyPoint remPoint = face.getPointByKey(key);
                                if (remPoint != null) {
                                    Bitmap rightEyeBmp = BitmapFactory.decodeResource(getResources(), frontCamera ? R.drawable.eye_right : R.drawable.eye_left);

                                    Bitmap r = FaceUtil.scaleBitmap(rightEyeBmp, face.getPointByKey(MyPoint.KEY_RIGHT_EYE_LEFT_CORNER), face.getPointByKey(MyPoint.KEY_RIGHT_EYE_RIGHT_CORNER), frontCamera);
                                    Bitmap reb = BitmapUtil.doMatrix(r, rotate(r, (int) (distance_re_n - distance_le_n), noseHMP.x - noseVMP.x));
                                    canvas.drawBitmap(reb, remPoint.x - reb.getWidth() / 2, remPoint.y - reb.getHeight() / 2, null);
                                }

                                // 画嘴唇
                                key = MyPoint.KEY_MOUTH_MIDDLE;
                                MyPoint mouthPoint = face.getPointByKey(key);
                                if (mouthPoint != null) {
                                    Bitmap mouthBmp = BitmapFactory.decodeResource(getResources(), R.drawable.mouth);

                                    Bitmap m = FaceUtil.scaleBitmap(mouthBmp,
                                            face.getPointByKey(MyPoint.KEY_MOUTH_LEFT_CORNER), face.getPointByKey(MyPoint.KEY_MOUTH_RIGHT_CORNER),
                                            face.getPointByKey(MyPoint.KEY_MOUTH_UPPER_LIP_TOP), face.getPointByKey(MyPoint.KEY_MOUTH_LOWER_LIP_BOTTOM),
                                            frontCamera);
                                    Bitmap mb = BitmapUtil.doMatrix(m, rotate(m, (int) (distance_re_n - distance_le_n), noseHMP.x - noseVMP.x));
                                    canvas.drawBitmap(mb, mouthPoint.x - mb.getWidth() / 2, mouthPoint.y - mb.getHeight() / 2, null);
                                }

                                // 画头发
                                key = MyPoint.KEY_NOSE_BOTTOM;
                                MyPoint noseBottomPoint = face.getPointByKey(key);
                                key = MyPoint.KEY_LEFT_EYEBROW_MIDDLE;
                                MyPoint leftEyeBrowPoint = face.getPointByKey(key);
                                key = MyPoint.KEY_RIGHT_EYEBROW_MIDDLE;
                                MyPoint rightEyeBrowPoint = face.getPointByKey(key);
                                MyPoint hairPoint = new MyPoint(0,0,"hair");

                                hairPoint.x = (leftEyeBrowPoint.x + rightEyeBrowPoint.x) - noseBottomPoint.x;
                                hairPoint.y = (leftEyeBrowPoint.y + rightEyeBrowPoint.y) - noseBottomPoint.y;

                                if (hairPoint != null) {
                                    Bitmap hairBmp = BitmapFactory.decodeResource(getResources(), R.drawable.hair1);

                                    MyPoint l = face.getPointByKey(MyPoint.KEY_LEFT_EYEBROW_LEFT_CORNER);
                                    MyPoint r = face.getPointByKey(MyPoint.KEY_RIGHT_EYEBROW_RIGHT_CORNER);
                                    MyPoint moreLeft = Point2D.getMoreLeftPointCoordinate(l.x, l.y, r.x, r.y, 30, true);
                                    MyPoint moreRight = Point2D.getMoreLeftPointCoordinate(l.x, l.y, r.x, r.y, 30,false);
                                    Bitmap h = FaceUtil.scaleBitmap(hairBmp, moreLeft, moreRight,
                                            frontCamera);
                                    Bitmap hb = BitmapUtil.doMatrix(h, rotate(h, (int) (distance_re_n - distance_le_n), noseHMP.x - noseVMP.x));
                                    canvas.drawBitmap(hb, hairPoint.x - hb.getWidth() / 2, hairPoint.y - hb.getHeight() / 2, null);
                                }

                                // 画胡子
                                MyPoint beardPoint = face.getPointByKey(MyPoint.KEY_MOUTH_LOWER_LIP_BOTTOM);
//                                MyPoint beardPoint = new MyPoint(0,0,"beard");
//                                beardPoint.x = (noseBottomPoint.x + mouthPoint.x) / 2;
//                                beardPoint.y = (noseBottomPoint.y + mouthPoint.y) / 2;

                                if (beardPoint != null) {
                                    Bitmap beardBmp = BitmapFactory.decodeResource(getResources(), R.drawable.beard2);

                                    Bitmap b = FaceUtil.scaleBitmap(beardBmp,
                                            face.getPointByKey(MyPoint.KEY_MOUTH_LEFT_CORNER), face.getPointByKey(MyPoint.KEY_MOUTH_RIGHT_CORNER),
                                            frontCamera);
                                    Bitmap bb = BitmapUtil.doMatrix(b, rotate(b, (int) (distance_re_n - distance_le_n), noseHMP.x - noseVMP.x));
                                    canvas.drawBitmap(bb, noseBottomPoint.x - bb.getWidth() / 2, noseBottomPoint.y - bb.getHeight() / 2, null);
                                }

                                // 画鼻子
                                key = MyPoint.KEY_NOSE_TOP;
                                MyPoint nPoint = face.getPointByKey(key);
                                if (nPoint != null) {
                                    Bitmap noseBmp = BitmapFactory.decodeResource(getResources(), R.drawable.nose);

                                    Bitmap n = FaceUtil.scaleBitmap(noseBmp, face.getPointByKey(MyPoint.KEY_NOSE_LEFT), face.getPointByKey(MyPoint.KEY_NOSE_RIGHT), frontCamera);
                                    Bitmap nb = BitmapUtil.doMatrix(n, rotate(n, (int) (distance_re_n - distance_le_n), noseHMP.x - noseVMP.x));
                                    canvas.drawBitmap(nb, nPoint.x - nb.getWidth() / 2, nPoint.y - nb.getHeight() / 2, null);
                                }

                            }

                        }
                        canPrint = false;
                    } else {
                        Log.d(TAG, "faces:0");
                    }


//                    paint.setColor(Color.RED);
//                    canvas.drawPoint(10, 10, paint);
//
//                    paint.setColor(Color.BLUE);
//                    canvas.drawPoint(PREVIEW_HEIGHT - 10, PREVIEW_HEIGHT - 10, paint);

                    mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }).start();
    }

    private void log(boolean canPrint, String string) {
        if (canPrint) {
            Log.i(TAG, string);
        }
    }

    Matrix rotate(Bitmap bmp, int deltaX, int deltaY) {
        int centerX = bmp.getWidth() >> 1;
        int centerY = bmp.getHeight() >> 1;

        Matrix mMatrix = new Matrix();
        mCamera2.save();
        mCamera2.rotateY(deltaX);
        mCamera2.rotateX(-deltaY);
        mCamera2.translate(0, 0, -centerX);
        mCamera2.getMatrix(mMatrix);
        mCamera2.restore();

        //以图片的中心点为旋转中心,如果不加这两句，就是以（0,0）点为旋转中心
        mMatrix.preTranslate(-centerX, -centerY);
        mMatrix.postTranslate(centerX, centerY);
        mCamera2.save();

        return mMatrix;
//        postInvalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
        if (null != mAcc) {
            mAcc.stop();
        }
        mStopTrack = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁对象
        mFaceDetector.destroy();
    }

    Handler handler = new Handler();
    private void showTip(final String str) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                takePicture.setText(str);

            }
        });
//        mToast.setText(str);
//        mToast.show();
    }

}
