package com.iflytek;

import android.graphics.Point;

import java.io.Serializable;

/**
 * Created by liujiancheng on 16/8/11.
 */
public class Landmark implements Serializable {
    /**
     * 左眉毛左角点
     */
    private Point left_eyebrow_left_corner;
    /**
     * 左眉毛中间点
     */
    private Point left_eyebrow_middle;
    /**
     * 左眉毛右角点
     */
    private Point left_eyebrow_right_corner;

    /**
     * 右眉毛左角点
     */
    private Point right_eyebrow_left_corner;
    /**
     * 右眉毛中间点
     */
    private Point right_eyebrow_middle;
    /**
     * 右眉毛右角点
     */
    private Point right_eyebrow_right_corner;

    /**
     * 左眼睛左角点
     */
    private Point left_eye_left_corner;
    /**
     * 左眼睛中间点
     */
    private Point left_eye_center;
    /**
     * 左眼睛右角点
     */
    private Point left_eye_right_corner;

    /**
     * 右眼睛左角点
     */
    private Point right_eye_left_corner;
    /**
     * 右眼睛中间点
     */
    private Point right_eye_center;
    /**
     * 右眼睛右角点
     */
    private Point right_eye_right_corner;

    /**
     * 鼻子左
     */
    private Point nose_left;
    /**
     * 鼻子上
     */
    private Point nose_top;
    /**
     * 鼻子下
     */
    private Point nose_bottom;
    /**
     * 鼻子右
     */
    private Point nose_right;

    /**
     * 嘴的左边点位置
     */
    private Point mouth_left_corner;
    /**
     * 上嘴唇上的位置
     */
    private Point mouth_upper_lip_top;
    /**
     * 嘴部中心点
     */
    private Point mouth_middle;
    /**
     * 嘴右边点位置
     */
    private Point mouth_right_corner;
    /**
     * 下嘴唇下的位置
     */
    private Point mouth_lower_lip_bottom;

    public Point[] getPoints() {
        Point[] points = new Point[21];
        points[0] = getLeft_eyebrow_left_corner();
        points[1] = getLeft_eyebrow_middle();
        points[2] = getLeft_eyebrow_right_corner();

        points[3] = getLeft_eye_left_corner();
        points[4] = getLeft_eye_center();
        points[5] = getLeft_eye_right_corner();

        points[6] = getRight_eyebrow_left_corner();
        points[7] = getRight_eyebrow_middle();
        points[8] = getRight_eyebrow_right_corner();

        points[9] = getRight_eye_left_corner();
        points[10] = getRight_eye_center();
        points[11] = getRight_eye_right_corner();

        points[12] = getNose_left();
        points[13] = getNose_right();
        points[14] = getNose_top();
        points[15] = getNose_bottom();

        points[16] = getMouth_left_corner();
        points[17] = getMouth_right_corner();
        points[18] = getMouth_upper_lip_top();
        points[19] = getMouth_middle();
        points[20] = getMouth_lower_lip_bottom();

        return points;
    }

    public Point getLeft_eyebrow_left_corner() {
        return left_eyebrow_left_corner;
    }

    public void setLeft_eyebrow_left_corner(Point left_eyebrow_left_corner) {
        this.left_eyebrow_left_corner = left_eyebrow_left_corner;
    }

    public Point getLeft_eyebrow_middle() {
        return left_eyebrow_middle;
    }

    public void setLeft_eyebrow_middle(Point left_eyebrow_middle) {
        this.left_eyebrow_middle = left_eyebrow_middle;
    }

    public Point getLeft_eyebrow_right_corner() {
        return left_eyebrow_right_corner;
    }

    public void setLeft_eyebrow_right_corner(Point left_eyebrow_right_corner) {
        this.left_eyebrow_right_corner = left_eyebrow_right_corner;
    }

    public Point getRight_eyebrow_left_corner() {
        return right_eyebrow_left_corner;
    }

    public void setRight_eyebrow_left_corner(Point right_eyebrow_left_corner) {
        this.right_eyebrow_left_corner = right_eyebrow_left_corner;
    }

    public Point getRight_eyebrow_middle() {
        return right_eyebrow_middle;
    }

    public void setRight_eyebrow_middle(Point right_eyebrow_middle) {
        this.right_eyebrow_middle = right_eyebrow_middle;
    }

    public Point getRight_eyebrow_right_corner() {
        return right_eyebrow_right_corner;
    }

    public void setRight_eyebrow_right_corner(Point right_eyebrow_right_corner) {
        this.right_eyebrow_right_corner = right_eyebrow_right_corner;
    }

    public Point getLeft_eye_left_corner() {
        return left_eye_left_corner;
    }

    public void setLeft_eye_left_corner(Point left_eye_left_corner) {
        this.left_eye_left_corner = left_eye_left_corner;
    }

    public Point getLeft_eye_center() {
        return left_eye_center;
    }

    public void setLeft_eye_center(Point left_eye_center) {
        this.left_eye_center = left_eye_center;
    }

    public Point getLeft_eye_right_corner() {
        return left_eye_right_corner;
    }

    public void setLeft_eye_right_corner(Point left_eye_right_corner) {
        this.left_eye_right_corner = left_eye_right_corner;
    }

    public Point getRight_eye_left_corner() {
        return right_eye_left_corner;
    }

    public void setRight_eye_left_corner(Point right_eye_left_corner) {
        this.right_eye_left_corner = right_eye_left_corner;
    }

    public Point getRight_eye_center() {
        return right_eye_center;
    }

    public void setRight_eye_center(Point right_eye_center) {
        this.right_eye_center = right_eye_center;
    }

    public Point getRight_eye_right_corner() {
        return right_eye_right_corner;
    }

    public void setRight_eye_right_corner(Point right_eye_right_corner) {
        this.right_eye_right_corner = right_eye_right_corner;
    }

    public Point getNose_left() {
        return nose_left;
    }

    public void setNose_left(Point nose_left) {
        this.nose_left = nose_left;
    }

    public Point getNose_top() {
        return nose_top;
    }

    public void setNose_top(Point nose_top) {
        this.nose_top = nose_top;
    }

    public Point getNose_bottom() {
        return nose_bottom;
    }

    public void setNose_bottom(Point nose_bottom) {
        this.nose_bottom = nose_bottom;
    }

    public Point getNose_right() {
        return nose_right;
    }

    public void setNose_right(Point nose_right) {
        this.nose_right = nose_right;
    }

    public Point getMouth_upper_lip_top() {
        return mouth_upper_lip_top;
    }

    public void setMouth_upper_lip_top(Point mouth_upper_lip_top) {
        this.mouth_upper_lip_top = mouth_upper_lip_top;
    }

    public Point getMouth_middle() {
        return mouth_middle;
    }

    public void setMouth_middle(Point mouth_middle) {
        this.mouth_middle = mouth_middle;
    }

    public Point getMouth_lower_lip_bottom() {
        return mouth_lower_lip_bottom;
    }

    public void setMouth_lower_lip_bottom(Point mouth_lower_lip_bottom) {
        this.mouth_lower_lip_bottom = mouth_lower_lip_bottom;
    }

    public Point getMouth_left_corner() {
        return mouth_left_corner;
    }

    public void setMouth_left_corner(Point mouth_left_corner) {
        this.mouth_left_corner = mouth_left_corner;
    }

    public Point getMouth_right_corner() {
        return mouth_right_corner;
    }

    public void setMouth_right_corner(Point mouth_right_corner) {
        this.mouth_right_corner = mouth_right_corner;
    }

    @Override
    public String toString() {
        return "Landmark{" +
                "left_eyebrow_left_corner=" + left_eyebrow_left_corner +
                ", left_eyebrow_middle=" + left_eyebrow_middle +
                ", left_eyebrow_right_corner=" + left_eyebrow_right_corner +
                ", right_eyebrow_left_corner=" + right_eyebrow_left_corner +
                ", right_eyebrow_middle=" + right_eyebrow_middle +
                ", right_eyebrow_right_corner=" + right_eyebrow_right_corner +
                ", left_eye_left_corner=" + left_eye_left_corner +
                ", left_eye_center=" + left_eye_center +
                ", left_eye_right_corner=" + left_eye_right_corner +
                ", right_eye_left_corner=" + right_eye_left_corner +
                ", right_eye_center=" + right_eye_center +
                ", right_eye_right_corner=" + right_eye_right_corner +
                ", nose_left=" + nose_left +
                ", nose_top=" + nose_top +
                ", nose_bottom=" + nose_bottom +
                ", nose_right=" + nose_right +
                ", mouth_upper_lip_top=" + mouth_upper_lip_top +
                ", mouth_middle=" + mouth_middle +
                ", mouth_lower_lip_bottom=" + mouth_lower_lip_bottom +
                ", mouth_left_corner=" + mouth_left_corner +
                ", mouth_right_corner=" + mouth_right_corner +
                '}';
    }
}
