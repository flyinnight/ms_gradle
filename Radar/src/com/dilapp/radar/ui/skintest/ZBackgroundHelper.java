package com.dilapp.radar.ui.skintest;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dilapp.radar.R;
import com.dilapp.radar.util.FastBlur;
import com.dilapp.radar.util.MD5;
import com.dilapp.radar.util.RSBlurUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * Created by husj1 on 2015/8/20.
 */
public final class ZBackgroundHelper {

    public static final String TAG = "III_ZBackgroundHelper";
    public static final boolean ALWAYS_BLUR = false;// 这个值在发布时千万要设置成false

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_BLACK_BLUR = 1;

    private static final DisplayImageOptions OPTIONS;

    private static String BACKGROUND_NAME;
    private static String BACKGROUND_BLUR_NAME;

    static {
        BACKGROUND_NAME = MD5.getMD5("bg_test_default");
        BACKGROUND_BLUR_NAME = MD5.getMD5("bg_test_default_blur");
        OPTIONS = new DisplayImageOptions.Builder()
                .displayer(new FadeInBitmapDisplayer(200))
                .imageScaleType(ImageScaleType.EXACTLY).build();
        d(TAG, "bg " + BACKGROUND_NAME + ", blur " + BACKGROUND_BLUR_NAME);
    }

    /**
     * 应用启动时，初始化
     *
     * @param context
     * @param arg     比如用户ID optional
     */
    public final static void initBackground(Context context, String arg) {
        if (arg != null) {
            BACKGROUND_NAME = MD5.getMD5("bg_test_default" + arg);
            BACKGROUND_BLUR_NAME = MD5.getMD5("bg_test_default_blur" + arg);
        }
        try {
            setBackground(context, context.getAssets().open("bg_test_default.jpg"), ALWAYS_BLUR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置背景
     *
     * @param context
     */
    public final static void resetBackground(Context context) {
        try {
            setBackground(context, context.getAssets().open("bg_test_default.jpg"), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置全局的背景
     *
     * @param context
     * @param is       流，读取完后，会自动关闭
     * @param override 是否覆盖
     */
    public final static void setBackground(Context context, InputStream is, boolean override) {

        File fileBg = new File(context.getFilesDir(), BACKGROUND_NAME);
        File fileBlur = new File(context.getFilesDir(), BACKGROUND_BLUR_NAME);

        if (!fileBg.isFile() || override) {// 背景文件不存在
            d(TAG, "bg_test_default writeing!");
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(is);
                bos = new BufferedOutputStream(new FileOutputStream(fileBg));

                int len = 0;
                byte[] buff = new byte[1024];
                while ((len = bis.read(buff)) != -1) {
                    bos.write(buff, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                    }
                }
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                    }
                }
            }
        } else {
            d(TAG, "bg_test_default exists!");
        }

        if (!fileBlur.isFile() || override) {
            d(TAG, "blur writeing!");
            Bitmap bitmapBg = BitmapFactory.decodeFile(fileBg.getAbsolutePath());
            //bitmapBg.setDensity(3);

            long s = System.currentTimeMillis();
            Bitmap bitmapBlur;
            final String flag;
            if (Build.VERSION.SDK_INT >= 11) {
                flag = "RenderScript";
                bitmapBlur = RSBlurUtils.blur(context, bitmapBg, 15, 2);
            } else {
                flag = "FastBlur";
                bitmapBlur = FastBlur.doBlur(bitmapBg, 25, false);
            }
            d(TAG, flag + " time " + (System.currentTimeMillis() - s) + "ms");
            //bitmapBlur.setDensity(3);

            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(fileBlur));
                bitmapBlur.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                    }
                }
            }
            bitmapBg.recycle();
            bitmapBlur.recycle();
        } else {
            d(TAG, "blur exists!");
        }
    }

    /**
     * 设置背景
     *
     * @param context
     * @param path    图片路径
     * @throws FileNotFoundException
     */
    public final static void setBackground(Context context, String path) throws FileNotFoundException {

        File file = new File(path);
        if (!file.isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        setBackground(context, new FileInputStream(path), true);
    }

    public final static View getBackgroundView(Context context, LayoutInflater inflater, @LayoutRes int layoutId, int type) {
        View v = inflater.inflate(layoutId, null);
        v.setBackground(getDrawable(context, type));
        return v;
    }

    public final static void setBackgroundForActivity(Activity activity, int type) {
        ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0)
                .setBackground(getDrawable(activity, type));
    }

    /**
     * 获取背景图片
     * @param context
     * @param type
     * @return
     */
    public final static Drawable getDrawable(Context context, int type) {

        BitmapDrawable bd;
        Drawable[] drawables;
        switch (type) {
            case TYPE_BLACK_BLUR: {
                //ImageLoader.getInstance().displayImage("file://" + context.getFilesDir() + "/" + BACKGROUND_BLUR_NAME, iv, OPTIONS);
                //frame.setVisibility(View.VISIBLE);

                bd = new BitmapDrawable(ImageLoader.getInstance().loadImageSync("file://" + context.getFilesDir() + "/" + BACKGROUND_BLUR_NAME));
//                bd = new BitmapDrawable(context.getResources(), context.getFilesDir() + "/" + BACKGROUND_BLUR_NAME);
                drawables = new Drawable[]{bd, new ColorDrawable(context.getResources().getColor(R.color.test_bg_cover))};
                break;
            }
            case TYPE_NORMAL:
            default: {
                //ImageLoader.getInstance().displayImage("file://" + context.getFilesDir() + "/" + BACKGROUND_NAME, iv, OPTIONS);
                //frame.setVisibility(View.GONE);
                bd = new BitmapDrawable(ImageLoader.getInstance().loadImageSync("file://" + context.getFilesDir() + "/" + BACKGROUND_NAME));
//                bd = new BitmapDrawable(context.getResources(), context.getFilesDir() + "/" + BACKGROUND_NAME);
                drawables = new Drawable[]{bd};

                break;
            }
        }
        return new LayerDrawable(drawables);
    }
}
