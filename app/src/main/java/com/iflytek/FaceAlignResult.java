package com.iflytek;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liujiancheng on 16/8/11.
 */
public class FaceAlignResult implements Serializable {
    private List<FaceInfo> face;
    private int ret;

    public List<FaceInfo> getFace() {
        return face;
    }

    public void setFace(List<FaceInfo> face) {
        this.face = face;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }
}
