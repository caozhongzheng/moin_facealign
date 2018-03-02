package com.iflytek.util;

import android.graphics.Point;

/**
 * Created by moying on 16/8/12.
 */
public class MyPoint extends Point {

    /**
     * 左眉毛左角点
     */
    public static final String KEY_LEFT_EYEBROW_LEFT_CORNER = "left_eyebrow_left_corner";
    /**
     * 左眉毛中间点
     */
    public static final String KEY_LEFT_EYEBROW_MIDDLE = "left_eyebrow_middle";
    /**
     * 左眉毛右角点
     */
    public static final String KEY_LEFT_EYEBROW_RIGHT_CORNER = "left_eyebrow_right_corner";

    /**
     * 右眉毛左角点
     */
    public static final String KEY_RIGHT_EYEBROW_LEFT_CORNER = "right_eyebrow_left_corner";
    /**
     * 右眉毛中间点
     */
    public static final String KEY_RIGHT_EYEBROW_MIDDLE = "right_eyebrow_middle";
    /**
     * 右眉毛右角点
     */
    public static final String KEY_RIGHT_EYEBROW_RIGHT_CORNER = "right_eyebrow_right_corner";

    /**
     * 左眼睛左角点
     */
    public static final String KEY_LEFT_EYE_LEFT_CORNER = "left_eye_left_corner";
    /**
     * 左眼睛中间点
     */
    public static final String KEY_LEFT_EYE_CENTER = "left_eye_center";
    /**
     * 左眼睛右角点
     */
    public static final String KEY_LEFT_EYE_RIGHT_CORNER = "left_eye_right_corner";

    /**
     * 右眼睛左角点
     */
    public static final String KEY_RIGHT_EYE_LEFT_CORNER = "right_eye_left_corner";
    /**
     * 右眼睛中间点
     */
    public static final String KEY_RIGHT_EYE_CENTER = "right_eye_center";
    /**
     * 右眼睛右角点
     */
    public static final String KEY_RIGHT_EYE_RIGHT_CORNER = "right_eye_right_corner";

    /**
     * 鼻子左
     */
    public static final String KEY_NOSE_LEFT = "nose_left";
    /**
     * 鼻子上
     */
    public static final String KEY_NOSE_TOP = "nose_top";
    /**
     * 鼻子下
     */
    public static final String KEY_NOSE_BOTTOM = "nose_bottom";
    /**
     * 鼻子右
     */
    public static final String KEY_NOSE_RIGHT = "nose_right";

    /**
     * 嘴的左边点位置
     */
    public static final String KEY_MOUTH_LEFT_CORNER = "mouth_left_corner";
    /**
     * 上嘴唇上的位置
     */
    public static final String KEY_MOUTH_UPPER_LIP_TOP = "mouth_upper_lip_top";
    /**
     * 嘴部中心点
     */
    public static final String KEY_MOUTH_MIDDLE = "mouth_middle";
    /**
     * 嘴右边点位置
     */
    public static final String KEY_MOUTH_RIGHT_CORNER = "mouth_right_corner";
    /**
     * 下嘴唇下的位置
     */
    public static final String KEY_MOUTH_LOWER_LIP_BOTTOM = "mouth_lower_lip_bottom";

    public String name;

    public MyPoint(int x, int y, String name) {
        super(x, y);
        this.name = name;
    }

    public MyPoint(Point src, String name) {
        super(src);
        this.name = name;
    }

    public MyPoint(MyPoint src) {
        super(src);
        this.name = src.name;
    }

    @Override
    public String toString() {
        return "MyPoint{" +
                "name='" + name + '\'' +
                ", xy=" + x +
                "," + y +
                '}';
    }
}
