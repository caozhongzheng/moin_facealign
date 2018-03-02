package com.iflytek.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.iflytek.FaceInfo;
import com.iflytek.Landmark;
import com.iflytek.VideoDemo;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.Point2D;
import com.moinapp.wuliao.util.BitmapUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class FaceUtil {
	public final static int REQUEST_PICTURE_CHOOSE = 1;
	public final static int  REQUEST_CAMERA_IMAGE = 2;
	public final static int REQUEST_CROP_IMAGE = 3;
	
	/***
	 * 裁剪图片
	 * @param activity Activity
	 * @param uri 图片的Uri
	 */
	public static void cropPicture(Activity activity, Uri uri) {
		Intent innerIntent = new Intent("com.android.camera.action.CROP");
		innerIntent.setDataAndType(uri, "image/*");
		innerIntent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
		innerIntent.putExtra("aspectX", 1); // 放大缩小比例的X
		innerIntent.putExtra("aspectY", 1);// 放大缩小比例的X   这里的比例为：   1:1
		innerIntent.putExtra("outputX", 320);  //这个是限制输出图片大小
		innerIntent.putExtra("outputY", 320); 
		innerIntent.putExtra("return-data", true);
		// 切图大小不足输出，无黑框
		innerIntent.putExtra("scale", true);
		innerIntent.putExtra("scaleUpIfNeeded", true);
		File imageFile = new File(getImagePath(activity.getApplicationContext()));
		innerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
		innerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		activity.startActivityForResult(innerIntent, REQUEST_CROP_IMAGE);
	}
	
	/**
	 * 保存裁剪的图片的路径
	 * @return
	 */
	public static String getImagePath(Context context){
		String path;
		
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			path = context.getFilesDir().getAbsolutePath();
		} else {
			path =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/";
		}
		
		if(!path.endsWith("/")) {
			path += "/";
		}
		
		File folder = new File(path);
		if (folder != null && !folder.exists()) {
			folder.mkdirs();
		}
		path += "ifd.jpg";
		return path;
	}
	
	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path 图片绝对路径
	 * @return degree 旋转角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	
	/**
	 * 旋转图片
	 * 
	 * @param angle	旋转角度
	 * @param bitmap 原图
	 * @return bitmap 旋转后的图片
	 */
	public static Bitmap rotateImage(int angle, Bitmap bitmap) {
		// 图片旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 得到旋转后的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 在指定画布上将人脸框出来
	 * 
	 * @param canvas 给定的画布
	 * @param face 需要绘制的人脸信息
	 * @param width 原图宽
	 * @param height 原图高
	 * @param frontCamera 是否为前置摄像头，如为前置摄像头需左右对称
	 * @param DrawOriRect 可绘制原始框，也可以只画四个角
	 */
	static public void drawFaceRect(Canvas canvas, FaceRect face, int width, int height, boolean frontCamera, boolean DrawOriRect) {
		if(canvas == null) {
			return;
		}

		Paint paint = new Paint(); 
		paint.setColor(Color.rgb(255, 203, 15));
		int len = (face.bound.bottom - face.bound.top) / 8;
		if (len / 8 >= 2) paint.setStrokeWidth(len / 8);
		else paint.setStrokeWidth(2);
		
		Rect rect = face.bound;

		if(frontCamera) {
			int top = rect.top;
			rect.top = width - rect.bottom;
			rect.bottom = width - top;
		}
//		Log.d(VideoDemo.TAG, "drawFaceRect new rect = " + rect);

		if (DrawOriRect) {
			paint.setStyle(Style.STROKE);
			canvas.drawRect(rect, paint);
		} else {
			int drawl = rect.left	- len;
			int drawr = rect.right	+ len;
			int drawu = rect.top 	- len;
			int drawd = rect.bottom	+ len;
			
			canvas.drawLine(drawl,drawd,drawl,drawd-len, paint);
			canvas.drawLine(drawl,drawd,drawl+len,drawd, paint);
			canvas.drawLine(drawr,drawd,drawr,drawd-len, paint);
			canvas.drawLine(drawr,drawd,drawr-len,drawd, paint);
			canvas.drawLine(drawl,drawu,drawl,drawu+len, paint);
			canvas.drawLine(drawl,drawu,drawl+len,drawu, paint);
			canvas.drawLine(drawr,drawu,drawr,drawu+len, paint);
			canvas.drawLine(drawr,drawu,drawr-len,drawu, paint);
		}

		if (face.point != null) {
			for (MyPoint p : face.point) {
				if(frontCamera) {
					p.y = width - p.y;
				}

//				canvas.drawPoint(p.x, p.y, paint);
			}

		}

	}

	/**
	 * 在指定画布上将人脸框出来   new
	 *
	 * @param canvas 给定的画布
	 * @param face 需要绘制的人脸信息
	 * @param width 原图宽
	 * @param height 原图高
	 * @param frontCamera 是否为前置摄像头，如为前置摄像头需左右对称
	 * @param DrawOriRect 可绘制原始框，也可以只画四个角
	 */
	static public void drawFaceRect(Canvas canvas, FaceInfo face, int width, int height, boolean frontCamera, boolean DrawOriRect) {
		if(canvas == null) {
			return;
		}

		Paint paint = new Paint();
		paint.setColor(Color.rgb(255, 0, 0));
		int len = (face.getPosition().bottom - face.getPosition().top) / 8;
		if (len / 8 >= 2) paint.setStrokeWidth(len / 8);
		else paint.setStrokeWidth(2);

		Rect rect = face.getPosition();

		if(frontCamera) {
			int top = rect.top;
			rect.top = width - rect.bottom;
			rect.bottom = width - top;
		}
		Log.d(VideoDemo.TAG, "drawFaceRect new rect = " + rect);

		if (DrawOriRect) {
			paint.setStyle(Style.STROKE);
//			canvas.drawLine(rect.left, rect.bottom, rect.left,   rect.bottom-len, paint);
//			canvas.drawLine(rect.left, rect.bottom, rect.left + len, rect.bottom, paint);

			canvas.drawRect(rect, paint);
		} else {
			int drawl = rect.left	- len;
			int drawr = rect.right	+ len;
			int drawu = rect.top 	- len;
			int drawd = rect.bottom	+ len;

			canvas.drawLine(drawl,drawd,drawl,drawd-len, paint);
			canvas.drawLine(drawl,drawd,drawl+len,drawd, paint);
			canvas.drawLine(drawr,drawd,drawr,drawd-len, paint);
			canvas.drawLine(drawr,drawd,drawr-len,drawd, paint);
			canvas.drawLine(drawl,drawu,drawl,drawu+len, paint);
			canvas.drawLine(drawl,drawu,drawl+len,drawu, paint);
			canvas.drawLine(drawr,drawu,drawr,drawu+len, paint);
			canvas.drawLine(drawr,drawu,drawr-len,drawu, paint);
		}

		if (face.getLandmark() != null) {
			if (null != face.getLandmark() && null != face.getLandmark().getPoints()) {
				for (Point p : face.getLandmark().getPoints()) {
					if (frontCamera) {
						p.y = width - p.y;
					}
					canvas.drawPoint(p.x, p.y, paint);
				}
				paint.setColor(Color.GREEN);
				Point points = face.getLandmark().getLeft_eyebrow_left_corner();
				if (frontCamera) {
					canvas.drawPoint(points.x, width - points.y, paint);
					canvas.drawPoint(points.x, width - points.y, paint);
					canvas.drawPoint(points.x, width - points.y, paint);
				} else {
					canvas.drawPoint(points.x, points.y, paint);
					canvas.drawPoint(points.x, points.y, paint);
					canvas.drawPoint(points.x, points.y, paint);
				}
			}
		}
	}

	/**
	 * 根据左右两点的位置缩放并旋转原图
	 * @param original 原图
	 * @param p1 左边点的位置
	 * @param p2 右边点的位置
	 * @return
	 */
	public static Bitmap scaleBitmap(Bitmap original, Point p1, Point p2, boolean frontCamera) {
		return scaleBitmap(original, p1, p2, null, null, frontCamera);
	}

	/**
	 * 根据左右两点的位置缩放并旋转原图
	 * @param original 原图
	 * @param p1 左边点的位置
	 * @param p2 右边点的位置
	 * @param p3 上边点的位置
	 * @param p4 下边点的位置
	 * @return
	 */
	public static Bitmap scaleBitmap(Bitmap original, Point p1, Point p2, Point p3, Point p4, boolean frontCamera) {
		if (p1 == null) {
			return original;
		}
		Bitmap bitmap = null;

		PointF pf1 = new PointF(p1);
		PointF pf2 = new PointF(p2);
		//两点间的距离就是目标bitmap的宽度大小
		float distance = (float) Point2D.distance(pf1, pf2);
		float ratio = distance/original.getWidth();
		if (null != p3 && null != p4) {
			PointF pf3 = new PointF(p3);
			PointF pf4 = new PointF(p4);

			distance = (float) Point2D.distance(pf3, pf4);
			ratio = Math.max(ratio, distance/original.getHeight());
		}

		//计算角度
		double radians = Math.atan2(p2.y - p1.y, p2.x - p1.x);
		double degree = Math.toDegrees(radians);//Point2D.angleBetweenPoints(pf1, pf2);
		if (frontCamera) {
			degree = (degree + 180D) % 360D;
		}

		Log.d(VideoDemo.TAG, "scaleBitmap degree = " + degree);
//    bitmap = BitmapUtil.zoomBitmap(original, ratio);
		bitmap = BitmapUtil.zoomAndRotateBitmap(original, ratio, (float) degree);
		return bitmap;
	}

	/**
	 * 将矩形随原图顺时针旋转90度
	 * 
	 * @param r
	 * 待旋转的矩形
	 * 
	 * @param width
	 * 输入矩形对应的原图宽
	 * 
	 * @param height
	 * 输入矩形对应的原图高
	 * 
	 * @return
	 * 旋转后的矩形
	 */
	static public Rect RotateDeg90(Rect r, int width, int height) {
		int left = r.left;
		r.left	= height- r.bottom;
		r.bottom= r.right;
		r.right	= height- r.top;
		r.top	= left;
		return r;
	}
	
	/**
	 * 将点随原图顺时针旋转90度
	 * @param p
	 * 待旋转的点
	 * 
	 * @param width
	 * 输入点对应的原图宽
	 * 
	 * @param height
	 * 输入点对应的原图宽
	 * 
	 * @return
	 * 旋转后的点 
	 */
	static public Point RotateDeg90(Point p, int width, int height) {
		int x = p.x;
		p.x = height - p.y;
		p.y = x;
		return p;
	}
	static public MyPoint RotateDeg90(MyPoint p, int width, int height) {
		int x = p.x;
		p.x = height - p.y;
		p.y = x;
		return p;
	}
	static public MyPoint RotateDeg90(MyPoint p, int width, int height, boolean frontCamera) {
		int x = p.x;
		p.x = height - p.y;
		if (frontCamera) {
			p.y = width - x;
		} else {
			p.y = x;
		}
		return p;
	}

	static public void RotateDeg90(Landmark landmark, int width, int height) {
		if (landmark == null) return;

		// 左眉毛
		if (landmark.getLeft_eyebrow_left_corner() != null) {
			landmark.setLeft_eyebrow_left_corner(RotateDeg90(landmark.getLeft_eyebrow_left_corner(), width, height));
		}
		if (landmark.getLeft_eyebrow_middle() != null) {
			landmark.setLeft_eyebrow_middle(RotateDeg90(landmark.getLeft_eyebrow_middle(), width, height));
		}
		if (landmark.getLeft_eyebrow_right_corner() != null) {
			landmark.setLeft_eyebrow_right_corner(RotateDeg90(landmark.getLeft_eyebrow_right_corner(), width, height));
		}

		// 右眉毛
		if (landmark.getRight_eyebrow_left_corner() != null) {
			landmark.setRight_eyebrow_left_corner(RotateDeg90(landmark.getRight_eyebrow_left_corner(), width, height));
		}
		if (landmark.getRight_eyebrow_middle() != null) {
			landmark.setRight_eyebrow_middle(RotateDeg90(landmark.getRight_eyebrow_middle(), width, height));
		}
		if (landmark.getRight_eyebrow_right_corner() != null) {
			landmark.setRight_eyebrow_right_corner(RotateDeg90(landmark.getRight_eyebrow_right_corner(), width, height));
		}

		// 左眼
		if (landmark.getLeft_eye_left_corner() != null) {
			landmark.setLeft_eye_left_corner(RotateDeg90(landmark.getLeft_eye_left_corner(), width, height));
		}
		if (landmark.getLeft_eye_center() != null) {
			landmark.setLeft_eye_center(RotateDeg90(landmark.getLeft_eye_center(), width, height));
		}
		if (landmark.getLeft_eye_right_corner() != null) {
			landmark.setLeft_eye_right_corner(RotateDeg90(landmark.getLeft_eye_right_corner(), width, height));
		}

		// 右眼
		if (landmark.getRight_eye_left_corner() != null) {
			landmark.setRight_eye_left_corner(RotateDeg90(landmark.getRight_eye_left_corner(), width, height));
		}
		if (landmark.getRight_eye_center() != null) {
			landmark.setRight_eye_center(RotateDeg90(landmark.getRight_eye_center(), width, height));
		}
		if (landmark.getRight_eye_right_corner() != null) {
			landmark.setRight_eye_right_corner(RotateDeg90(landmark.getRight_eye_right_corner(), width, height));
		}

		// 鼻子
		if (landmark.getNose_left() != null) {
			landmark.setNose_left(RotateDeg90(landmark.getNose_left(), width, height));
		}
		if (landmark.getNose_top() != null) {
			landmark.setNose_top(RotateDeg90(landmark.getNose_top(), width, height));
		}
		if (landmark.getNose_right() != null) {
			landmark.setNose_right(RotateDeg90(landmark.getNose_right(), width, height));
		}
		if (landmark.getNose_bottom() != null) {
			landmark.setNose_bottom(RotateDeg90(landmark.getNose_bottom(), width, height));
		}

		// 嘴
		if (landmark.getMouth_left_corner() != null) {
			landmark.setMouth_left_corner(RotateDeg90(landmark.getMouth_left_corner(), width, height));
		}
		if (landmark.getMouth_upper_lip_top() != null) {
			landmark.setMouth_upper_lip_top(RotateDeg90(landmark.getMouth_upper_lip_top(), width, height));
		}
		if (landmark.getMouth_middle() != null) {
			landmark.setMouth_middle(RotateDeg90(landmark.getMouth_middle(), width, height));
		}
		if (landmark.getMouth_lower_lip_bottom() != null) {
			landmark.setMouth_lower_lip_bottom(RotateDeg90(landmark.getMouth_lower_lip_bottom(), width, height));
		}
		if (landmark.getMouth_right_corner() != null) {
			landmark.setMouth_right_corner(RotateDeg90(landmark.getMouth_right_corner(), width, height));
		}
	}


	public static int getNumCores() {
	    class CpuFilter implements FileFilter {
	        @Override
	        public boolean accept(File pathname) {
	            if(Pattern.matches("cpu[0-9]", pathname.getName())) {
	                return true;
	            }
	            return false;
	        }      
	    }
	    try {
	        File dir = new File("/sys/devices/system/cpu/");
	        File[] files = dir.listFiles(new CpuFilter());
	        return files.length;
	    } catch(Exception e) {
	        e.printStackTrace();
	        return 1;
	    }
	}
	
	/**
	 * 保存Bitmap至本地
	 * @param bmp
	 */
	public static void saveBitmapToFile(Context context,Bitmap bmp){
		String file_path = getImagePath(context);
		File file = new File(file_path);
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
