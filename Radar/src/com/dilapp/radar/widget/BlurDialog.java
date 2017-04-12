package com.dilapp.radar.widget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dilapp.radar.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;

@SuppressLint("NewApi")
@Deprecated// 不赞成使用
public class BlurDialog extends Dialog implements Callback {

	private static final String TAG = BlurDialog.class.getName();
	private static final boolean LOG = true;

	private Activity mActivity;
	private Context mContext;
	private LayoutInflater mInflater;
	private ViewGroup mContainer;
	private ViewGroup mContent;
	private Handler mHandler;

	private boolean isReady;
	private boolean isReadyShow;// 是否在准备好之前就调用了show

	public BlurDialog(Activity acitvity) {
		super(acitvity, R.style.BaseDialog);
		this.mActivity = acitvity;
		this.mContext = acitvity.getApplicationContext();
		this.mInflater = LayoutInflater.from(mContext);
		this.mHandler = new Handler(this);
		Window window = getWindow();
		android.view.WindowManager.LayoutParams attrs = window.getAttributes();
		// window.setFlags(, mask);
		initContainer();
	}

	private void initContainer() {
		LayoutParams defaultParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		FrameLayout container = new FrameLayout(mContext);
		container.setLayoutParams(defaultParams);
		View view = new View(mContext);
		view.setBackgroundColor(0x4d000000);
		container.addView(view, defaultParams);
		FrameLayout content = new FrameLayout(mContext);
		container.addView(content, defaultParams);
		mContent = content;
		mContainer = container;
		super.setContentView(container);
		flushBlurBackground();
	}

	@Override
	public void setContentView(int layoutResID) {
		this.setContentView(mInflater.inflate(layoutResID, null));
	}

	@Override
	public void setContentView(View view) {
		this.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		mContent.addView(view, params);
	}

	public void flushBlurBackground() {

		new Thread() {
			@Override
			public void run() {
				synchronized (BlurDialog.this) {
					isReady = true;
				}
				ViewGroup decorView = (ViewGroup) mActivity.getWindow()
						.getDecorView();

				// for (int i = 0; i < decorView.getChildCount(); i++) {
				// View child = decorView.getChildAt(i);
				// l(i + " -> " + child.getClass().getSimpleName());
				// if (child instanceof ViewGroup) {
				// ViewGroup childView = (ViewGroup) child;
				// for (int j = 0; j < childView.getChildCount(); j++) {
				// l("->"
				// + j
				// + " -> "
				// + childView.getChildAt(j).getClass()
				// .getSimpleName());
				//
				// }
				// }
				// }
				ViewGroup contentView = (ViewGroup) ((ViewGroup) (decorView)
						.getChildAt(0)).getChildAt(1);
				long st = System.currentTimeMillis();
				contentView.setDrawingCacheEnabled(true);
				contentView.buildDrawingCache();
				Bitmap background = Bitmap.createBitmap(contentView
						.getDrawingCache()); // 创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
				contentView.setDrawingCacheEnabled(false); // 禁用DrawingCahce否则会影响性能
				long et = System.currentTimeMillis();
				l("clip time " + (et - st));
				st = System.currentTimeMillis();
				Bitmap blurBackground = doBlur(background, 20, true);
				et = System.currentTimeMillis();
				l("blur time " + (et - st));
				saveMyBitmap("aaaaa", blurBackground);
				// if (!background.isRecycled()) {
				// background.recycle();
				// }
				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				msg.obj = blurBackground;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	private void setBlurBackground(Bitmap blur) {
		if (Build.VERSION.SDK_INT >= 16) {
			mContainer.setBackground(new BitmapDrawable(blur));
		} else {
			mContainer.setBackgroundDrawable(new BitmapDrawable(blur));
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case 1:
			setBlurBackground((Bitmap) msg.obj);

			if (isReadyShow && !isShowing()) {
				show();
			}
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public void show() {
		if (isReady) {
			super.show();
		} else {
			isReadyShow = true;
		}
	}

	@Override
	public void onDetachedFromWindow() {
		mHandler = null;
		mContainer = null;
		mInflater = null;
		mContext = null;
		mActivity = null;
		super.onDetachedFromWindow();
	}

	public void saveMyBitmap(String bitName, Bitmap mBitmap) {
		File f = new File(Environment.getExternalStorageDirectory(), bitName
				+ ".png");
		l("path " + f.getAbsolutePath());
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// DebugMessage.put("在保存图片时出错：" + e.toString());
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap doBlur(Bitmap sentBitmap, int radius,
			boolean canReuseInBitmap) {
		Bitmap bitmap;
		if (canReuseInBitmap) {
			bitmap = sentBitmap;
		} else {
			bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
		}

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
						| (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);

		return (bitmap);
	}

	private static void l(String msg) {
		if (LOG)
			Log.d(TAG, msg);
	}
}
