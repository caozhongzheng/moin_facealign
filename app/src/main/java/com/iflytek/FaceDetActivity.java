package com.iflytek;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.iflytek.cloud.Setting;
import com.moinapp.wuliao.R;

/**
 * 人脸识别示例
 */
public class FaceDetActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facedet);

		findViewById(R.id.btn_online_demo).setOnClickListener(FaceDetActivity.this);
		findViewById(R.id.btn_offline_demo).setOnClickListener(FaceDetActivity.this);
		findViewById(R.id.btn_video_demo).setOnClickListener(FaceDetActivity.this);
		
		Setting.setShowLog(true);

		test();
	}

	private void test() {
/*
in Java
 sun.misc.Launcher$AppClassLoader@659e0bfd  //系统类加载器
 sun.misc.Launcher$ExtClassLoader@6d06d69c  //扩展类加载器. 最后之所以没有引导类加载器,是因为JDK返回的是null

in Android
 dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.moinapp.wuliao-2/base.apk"],nativeLibraryDirectories=[/data/app/com.moinapp.wuliao-2/lib/arm, /vendor/lib, /system/lib]]]
 java.lang.BootClassLoader@956a823
 */
		ClassLoader loader = FaceDetActivity.class.getClassLoader();
		while (loader != null) {
			System.out.println(loader.toString());
			loader = loader.getParent();
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_online_demo:
			intent = new Intent(FaceDetActivity.this, OnlineFaceDemo.class);
			startActivity(intent);
			break;
		case R.id.btn_offline_demo:
			intent = new Intent(FaceDetActivity.this, OfflineFaceDemo.class);
			startActivity(intent);
			break;
		case R.id.btn_video_demo:
			intent = new Intent(FaceDetActivity.this, VideoDemo.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
