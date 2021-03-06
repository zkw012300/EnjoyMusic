package com.zspirytus.enjoymusic.impl.glide.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.zspirytus.enjoymusic.utils.BitmapUtil;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;


public class BlurTransformation extends BitmapTransformation {

    private static String ID;

    private Context mContext;
    private int mBlurRadius;
    private int mScale;

    /**
     * Default Blur Radius = 25 & Default Scale = 1
     *
     * @param context Context
     */
    public BlurTransformation(Context context) {
        this(context, 25);
    }

    /**
     * Default Scale = 1
     *
     * @param context    Context
     * @param blurRadius The Radius of Blur
     */
    public BlurTransformation(Context context, int blurRadius) {
        this(context, blurRadius, 1);
    }

    /**
     * @param context    Context
     * @param blurRadius The Radius of Blur
     * @param scale      The Scale of Cropping Bitmap, To reduce computation.
     */
    public BlurTransformation(Context context, int blurRadius, int scale) {
        mContext = context;
        mBlurRadius = blurRadius;
        mScale = scale;
        ID = "com.zspirytus.enjoymusic.impl.glide.transformation.BlurTransformation Context = " + context.toString() + " blurRadius = " + blurRadius + " scale = " + scale;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return BitmapUtil.bitmapBlur(mContext, toTransform, outWidth, outHeight, mBlurRadius, 1.0f / mScale);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        try {
            messageDigest.update(ID.getBytes(STRING_CHARSET_NAME));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlurTransformation) {
            return hashCode() == obj.hashCode();
        }
        return false;
    }
}
