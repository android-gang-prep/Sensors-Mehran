package com.example.mehranm1;

import android.graphics.Bitmap;

public class ImageModel {
    private Bitmap bitmap;
    private float left;
    private float top;
    private RecordItemModel recordItemModel;

    public ImageModel(Bitmap bitmap, float left, float top, RecordItemModel recordItemModel) {
        this.bitmap = bitmap;
        this.left = left;
        this.top = top;
        this.recordItemModel = recordItemModel;
    }

    public RecordItemModel getRecordItemModel() {
        return recordItemModel;
    }

    public void setRecordItemModel(RecordItemModel recordItemModel) {
        this.recordItemModel = recordItemModel;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }
}
