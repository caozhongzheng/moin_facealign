package com.iflytek;

import android.graphics.Rect;

import java.io.Serializable;

/**
 * Created by liujiancheng on 16/8/11.
 */
public class FaceInfo implements Serializable {
    private Rect position;
    private Landmark landmark;

    public Rect getPosition() {
        return position;
    }

    public void setPosition(Rect position) {
        this.position = position;
    }

    public Landmark getLandmark() {
        return landmark;
    }

    public void setLandmark(Landmark landmark) {
        this.landmark = landmark;
    }

    @Override
    public String toString() {
        return "FaceInfo{" +
                "position=" + position +
                ", landmark=" + landmark.toString() +
                '}';
    }
}
