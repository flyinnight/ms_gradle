package com.dilapp.radar.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewTreeObserver;

public class RSBlurUtils {
	private static final int DEF_SCALE_FACTOR = 15;
	private static final int DEF_RADIUS = 2;
	
	public static long blurDef(Context context, Bitmap bkg, View targetView){
		 
		 int scaleFactor = DEF_SCALE_FACTOR;
		 int radius = DEF_RADIUS;
		 return blur(context, bkg, targetView, scaleFactor, radius);
		 
		 
	}
	
	public static long blur(Context context, Bitmap bkg, View view, int scaleFactor, int radius){
		long startMs = System.currentTimeMillis();
		
		Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        RenderScript rs = RenderScript.create(context);
        Allocation overlayAlloc = Allocation.createFromBitmap(
                rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
        view.setBackground(new BitmapDrawable(context.getResources(), overlay));
        rs.destroy();
        return (System.currentTimeMillis() - startMs);
	}
	
	public static Bitmap blur(Context context, Bitmap bkg, float scaleFactor, int radius){
//		long startMs = System.currentTimeMillis();
		
		Bitmap overlay = Bitmap.createBitmap((int) (bkg.getWidth() / scaleFactor),
                (int) (bkg.getHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
//        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        RenderScript rs = RenderScript.create(context);
        Allocation overlayAlloc = Allocation.createFromBitmap(
                rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
//        view.setBackground(new BitmapDrawable(context.getResources(), overlay));
        rs.destroy();
        return overlay;
	}
	
//	private void applyBlur(final View src, final View target) {
//        src.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                src.getViewTreeObserver().removeOnPreDrawListener(this);
//                src.buildDrawingCache();
//
//                Bitmap bmp = src.getDrawingCache();
////                blur(bmp, target);
//                return true;
//            }
//        });
//    }

}
