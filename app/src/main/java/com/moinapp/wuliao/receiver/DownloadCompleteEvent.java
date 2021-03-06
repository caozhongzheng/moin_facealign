package com.moinapp.wuliao.receiver;

public class DownloadCompleteEvent {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    public long mRefer;

    // ===========================================================
    // Constructors
    // ===========================================================

    public DownloadCompleteEvent(long refer){
        mRefer = refer;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public long getRefer() {
        return mRefer;
    }

    public void setRefer(long refer) {
        this.mRefer = refer;
    }


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
