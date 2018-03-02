package com.iflytek.util;

import android.graphics.Rect;

import com.moinapp.wuliao.util.StringUtil;

/**
 * @author MatrixCV
 *         FaceRect是用于表示人脸检测的结果，其中包括了 人脸的角度、得分、检测框位置、关键点
 */
public class FaceRect {
	public float score;

	public Rect bound = new Rect();
	public MyPoint point[];

	public Rect raw_bound = new Rect();
	public MyPoint raw_point[];

	public MyPoint getPointByKey(String key) {
		if (StringUtil.isNullOrEmpty(key) || null == point || point.length == 0) {
			return null;
		}
		for (int i = 0; i < point.length; i++) {
			MyPoint myPoint = point[i];
			if (key.equalsIgnoreCase(myPoint.name)) {
				return myPoint;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return bound.toString();
	}
}
