package com.dilapp.radar.ble;

import java.lang.ref.SoftReference;

import com.dilapp.radar.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class BleDeviceSearchAnimationFrameLayout extends FrameLayout {

	private SoftReference<Bitmap> m_bitmapRipple;// 软引用
    private ImageView[] m_imageVRadars;
    private Context mContext;

    public BleDeviceSearchAnimationFrameLayout(Context paramContext) {
        super(paramContext);
        init(paramContext);
    }

    public BleDeviceSearchAnimationFrameLayout(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext);
    }

    public BleDeviceSearchAnimationFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext);
    }

    private void init(Context paramContext) {
        mContext = paramContext;
        loadRadarBitmap();
        m_imageVRadars = new ImageView[3];
        LayoutInflater.from(mContext).inflate(R.layout.bledevice_search_device_anima, this);
        m_imageVRadars[0] = ((ImageView) findViewById(R.id.radar_ray_1));
        m_imageVRadars[1] = ((ImageView) findViewById(R.id.radar_ray_2));
        m_imageVRadars[2] = ((ImageView) findViewById(R.id.radar_ray_3));
       // m_imageVRadars[3] = ((ImageView) findViewById(R.id.radar_ray_4));
        //m_imageVRadars[4] = ((ImageView) findViewById(R.id.radar_ray_5));*/
    }

  

    private void loadRadarBitmap() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(),R.drawable.bledevice_body_ripple, options);
            m_bitmapRipple = new SoftReference<Bitmap>(
                    ImageUtils.decodeSampledBitmapFromResource(getResources(),
                            R.drawable.bledevice_body_ripple, 150, 150));
        } catch (OutOfMemoryError localOutOfMemoryError) {
            Log.e("SZU WifiApSearchAnim",
                    Log.getStackTraceString(localOutOfMemoryError));
        }
    }

    // 重置，停止动画
    public final void stopAnimation() {
        int mLength = m_imageVRadars.length;
        for (int i = 0; i < mLength; ++i) {
            if (m_bitmapRipple != null) {
                Bitmap localBitmap = (Bitmap) m_bitmapRipple.get();
                if ((localBitmap != null) && (!localBitmap.isRecycled()))
                    localBitmap.recycle();
            }
            m_bitmapRipple = null;
            ImageView localImageView = m_imageVRadars[i];
            localImageView.setImageBitmap(null);
            localImageView.setVisibility(View.GONE);
            localImageView.clearAnimation();
        }
    }

    // 开始动画
    public final void startAnimation() {
        if (m_bitmapRipple == null)
            loadRadarBitmap();
        int mLength = m_imageVRadars.length;
        int count = 1;
        for (int i = 0; i < mLength; ++i) {
            ImageView localImageView;
            long l;
            while (true) {
                localImageView = m_imageVRadars[i];
                localImageView.setImageBitmap((Bitmap) m_bitmapRipple.get());
                localImageView.setVisibility(View.VISIBLE);
//                l = 400L * i;
                l = 400L ;
                if (localImageView.getAnimation() == null)
                    break;
                localImageView.getAnimation().start();
            }
            
          
            ScaleAnimation localScaleAnimation =  new ScaleAnimation(1.0f, (float)(2+i*1), 1.0f,(float)(2+i*1),   
            		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            localScaleAnimation.setRepeatCount(-1);
            AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.80F, 0.1F);
            localAlphaAnimation.setRepeatCount(-1);
            AnimationSet localAnimationSet = new AnimationSet(true);
            localAnimationSet.addAnimation(localScaleAnimation);
            localAnimationSet.addAnimation(localAlphaAnimation);
            localAnimationSet.setDuration(1500+i*100);
            localAnimationSet.setRepeatCount(-1);
            localAnimationSet.setFillEnabled(false);
            localAnimationSet.setFillBefore(false);
            //设置启动时间
            localAnimationSet.setStartOffset(l);
            localAnimationSet.setInterpolator(new AccelerateDecelerateInterpolator());
            localAnimationSet.setAnimationListener(new WTSearchAnimationHandler(this, localImageView));
            localImageView.setAnimation(localAnimationSet);
            localImageView.startAnimation(localAnimationSet);
        }
    }

    final class WTSearchAnimationHandler implements Animation.AnimationListener {
        private ImageView m_imageVRadar;

        public WTSearchAnimationHandler(BleDeviceSearchAnimationFrameLayout paramImageView, ImageView imageView) {
            m_imageVRadar = imageView;
        }

        public final void onAnimationEnd(Animation paramAnimation) {
            this.m_imageVRadar.setVisibility(View.GONE);
        }

        public final void onAnimationRepeat(Animation paramAnimation) {
            paramAnimation.setStartOffset(200L);
        }

        public final void onAnimationStart(Animation paramAnimation) {
        }
    }
	
}
