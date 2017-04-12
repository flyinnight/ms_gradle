package com.dilapp.radar.ui.topic;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.textbuilder.BBSTextBuilder;
import com.dilapp.radar.textbuilder.utils.L;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.util.DensityUtils;
import com.dilapp.radar.util.DownloadUtils;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.MD5;
import com.dilapp.radar.util.PathUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by husj1 on 2015/7/6.
 */
public class TopicHelper {
    private static final String TAG = L.makeTag(TopicHelper.class);
    private static final boolean LOG = false;
    private static String[] IMAGE_DIRS = new String[]{
            "post/collection", "post/pic", "user/img/portrait",
            "topic/icon", "banner/pic", "facialAnalyze/pic",
            "solution/pic", "solution/cover", "solution/comment/pic"
    };

    /**
     * BBSDescribeItem.TYPE_PLAN_STEP
     * 护肤计划的步骤
     */
    public static final int TYPE_PLAN_STEP = 12;


    /**
     * 未知的路径
     */
    public static final int PATH_UNKNOWN = -1;
    /**
     * 本地SDCARD的路径
     */
    public static final int PATH_LOCAL_SDCARD = 0;
    /**
     * 服务器的相对路径
     */
    public static final int PATH_SERVER_RELATIVE = 1;
    /**
     * 服务器的绝对路径
     */
    public static final int PATH_SERVER_ABSOLUTE = 2;

    /**
     * 是否是特殊的Topic
     * @param topicId
     * @return
     */
    public static boolean isSpecialTopic(long topicId) {
        long aim = topicId;
        long[] data = Constants.TOPIC_AD_IDS;
        int start = 0;
        int end = data.length - 1;
        int mid = (start + end) / 2;//a
        while (data[mid] != aim && end > start) {//如果data[mid]等于aim则死循环，所以排除
            if (data[mid] > aim) {
                end = mid - 1;
            } else if (data[mid] < aim) {
                start = mid + 1;
            }
            mid = (start + end) / 2;//b，注意a，b
        }
        return (data[mid] != aim) ? false : true;//返回结果
    }

    /**
     * 路径判断
     *
     * @param url
     * @return
     */
    public static String wrappeImagePath(String url) {
        switch (isImagePath(url)) {
            case PATH_SERVER_RELATIVE:
                return HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP + url;
            case PATH_SERVER_ABSOLUTE:
            case PATH_LOCAL_SDCARD:
            default:
                return url;
        }
    }

    /**
     * 判断改地址到底属于哪种路径
     * {@link #PATH_UNKNOWN}
     * {@link #PATH_SERVER_ABSOLUTE}
     * {@link #PATH_SERVER_RELATIVE}
     * {@link #PATH_LOCAL_SDCARD}
     *
     * @param url
     * @return
     */
    public static int isImagePath(String url) {
        if (url == null || "".equals(url.trim())) {
            return PATH_UNKNOWN;
        }
        if (url.startsWith("http")) {// 代表这是网络的绝对路径
            return PATH_SERVER_ABSOLUTE;
        }
        for (int i = 0; i < IMAGE_DIRS.length; i++) {
            if (url.startsWith(IMAGE_DIRS[i])) {
                return PATH_SERVER_RELATIVE;
            }
        }
        // 代表这是本地路径
        return PATH_LOCAL_SDCARD;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int dividerHeight = listView.getDividerHeight();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            if (Build.VERSION.SDK_INT >= 19) {
                if (listView.areHeaderDividersEnabled() && i == 0) {
                    totalHeight += dividerHeight;
                }
                if (listView.areFooterDividersEnabled() && i == listAdapter.getCount() - 1) {
                    totalHeight += dividerHeight;
                }
            }
            if (i != listAdapter.getCount() - 1) {
                totalHeight += dividerHeight;
            }
        }

        totalHeight = DensityUtils.dip2px(listView.getContext(), 423.3f);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight/* + (listView.getDividerHeight() * (listAdapter.getCount() - 1))*/;
        listView.setLayoutParams(params);
    }

    public static void setStrings2BBSDescribeItemContent(List<String> strs, List<BBSDescribeItem> list, String prefix) {
        if (strs == null || list == null) {
            return;
        }
        int len = Math.max(strs.size(), list.size());
        for (int i = 0; i < len; i++) {
            if (i >= strs.size() || i >= list.size()) {
                return;
            }
            String str = strs.get(i);
            BBSDescribeItem item = list.get(i);
            item.setContent(prefix + str);
        }
    }

    public static List<String> describeItemContent2Strings(List<BBSDescribeItem> list) {
        if (list == null) {
            return null;
        }
        List<String> strs = new ArrayList<String>(list.size());
        for (int i = 0; i < list.size(); i++) {
            strs.add(list.get(i).getContent().toString());
        }
        return strs;
    }

    /**
     * 压缩一下图片，用于服务器上传
     * @param pics
     * @return
     */
    public static List<String> compress(List<String> pics) {
        if (pics == null) return null;
        List<String> coms = new ArrayList<String>(pics.size());
        final int size = pics.size();
        for (int i = 0; i < size; i++) {
            coms.add(PathUtils.compressDefPicFile(pics.get(i)));
        }
        return coms;
    }

    public static List<BBSDescribeItem> findImages(BBSTextBuilder btb) {
        List<BBSDescribeItem> bbsDescribe = btb.getBBSDescribe();
        if (bbsDescribe == null || bbsDescribe.size() == 0) {
            return null;
        }
        List<BBSDescribeItem> images = new ArrayList<BBSDescribeItem>(0);
        for (int i = 0; i < bbsDescribe.size(); i++) {
            BBSDescribeItem item = bbsDescribe.get(i);
            if (item == null) continue;
            if (item.getType() == BBSDescribeItem.TYPE_IMAGE ||
                    item.getType() == BBSDescribeItem.TYPE_IMAGE_LINK) {
                images.add(item);
            }
        }
        return images;
    }

    public static void trimBBSTextBuilder(BBSTextBuilder btb) {
        List<BBSDescribeItem> bbsDescribe = btb.getBBSDescribe();
        if (bbsDescribe == null || bbsDescribe.size() == 0) {
            return;
        }
        for (int i = 0; i < bbsDescribe.size(); i++) {
            BBSDescribeItem item = bbsDescribe.get(i);
            if (item == null) {
                bbsDescribe.remove(i--);
                continue;
            }

            if (item.getContent() == null || "".equals(item.getContent().toString().trim())) {
                bbsDescribe.remove(i--);
            } else if (item.getContent() instanceof String) {
                ((String) item.getContent()).trim();
            }
        }
        btb.getString();
    }

    public static boolean isNotEmpty(BBSTextBuilder btb) {
        List<BBSDescribeItem> bbsDescribe = btb.getBBSDescribe();
        if (bbsDescribe == null || bbsDescribe.size() == 0) {
            return false;
        }
        for (int i = 0; i < bbsDescribe.size(); i++) {
            BBSDescribeItem item = bbsDescribe.get(i);
            if (item.getContent() != null && !"".equals(item.getContent().toString().trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取时间字符串
     *
     * @param c
     * @param currTime
     * @param topicTime
     * @return
     */
    public static String getTopicDateString(Context c, long currTime, long topicTime) {
//        if (topicTime == 0) {
//            return "unknown date";
//        }
        String result = null;
        long diffSeconds = (currTime - topicTime) / 1000;
        if (diffSeconds < 60) {
            result = c.getString(R.string.datefmt_within_a_minute/*, "" + diffSeconds*/);
            // sdf.applyPattern(c.getString(R.string.datefmt_within_a_minute));
        } else if (diffSeconds < 60 * 60) {
            result = c.getString(R.string.datefmt_within_an_hour, "" + (diffSeconds / 60));
            // sdf.applyPattern(c.getString(R.string.datefmt_within_an_hour));
        } else if (diffSeconds < 60 * 60 * 24) {
            result = c.getString(R.string.datefmt_within_a_day, "" + (diffSeconds / 60 / 60));
            // sdf.applyPattern(c.getString(R.string.datefmt_within_a_day));
        } else if (diffSeconds < 60 * 60 * 24 * 7) {
            result = c.getString(R.string.datefmt_within_a_week, "" + (diffSeconds / 60 / 60 / 24));
            // sdf.applyPattern(c.getString(R.string.datefmt_within_a_week));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern(c.getString(R.string.datefmt_gt_a_week));
            // diffSeconds = topicTime * 1000;
            result = sdf.format(topicTime);
            result = result.startsWith("0") ? result.substring(1) : result;
        }
        //android.util.Log.i("III", "datetime " + result);
        return result;
    }

    /**
     * 或取假数据，测试用
     *
     * @param context
     * @param filename
     * @return
     */
    public static String getDemo(Context context, String filename) {
        AssetManager assets = context.getAssets();
        StringBuilder sb = new StringBuilder();
        try {
            int len = 0;
            char[] buff = new char[1024];
            InputStreamReader is = new InputStreamReader(assets.open(filename));
            while ((len = is.read(buff)) != -1) {
                sb.append(buff, 0, len);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    // 使用Bitmap加Matrix来缩放
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        if (bitmap == null) {
            return null;
        }
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    public static int[] getCurrWidthAndHeight(int imageWidth, int imageHeight, int withWidth) {

//        WindowManager wm = (WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE);
        final int screenWidth = withWidth;//wm.getDefaultDisplay().getWidth();

        int viewWidth = imageWidth;
        int viewHeight = imageHeight;

        // L.d(TAG, "ImageWidth: " + imageWidth + ", ImageHeight: " +
        // imageHeight
        // + ", ViewWidth: " + viewWidth);

        final int ctrolWidth = screenWidth;// - (inside != null ? (inside.left + inside.right) : 0);
        if (imageWidth > ctrolWidth) {
            viewWidth = ctrolWidth;
            viewHeight = Math.round(imageHeight
                    * (viewWidth / (float) imageWidth));
            // L.d(TAG, "viewWidth / imageWidth: "
            // + (viewWidth / (float) imageWidth));
            if (LOG)
                L.d(TAG, "图片需要缩小" + (viewWidth / (float) imageWidth * 100)
                        + "%" + ", 实际缩小" + (viewHeight - imageHeight) + "像素");
        }

        // #TODO 图片的缩放模式，这种是伸拉的
        else if (imageWidth < ctrolWidth) {
            viewWidth = ctrolWidth;
            float f = (ctrolWidth - imageWidth) / ((float) imageWidth);
            viewHeight = Math.round(imageHeight + imageHeight * f);
            if (LOG)
                L.d(TAG, "图片需要放大" + (f * 100) + "%" + ", 实际放大"
                        + (imageHeight * f) + "像素");
        }

        if (LOG)
            L.d(TAG, "ViewW: " + viewWidth + ", ViewH: " + viewHeight
                    + ", imageW: " + imageWidth + ", imageH: " + imageHeight
                    + ", ctrolW: " + ctrolWidth);
        return new int[]{viewWidth, viewHeight};
    }

    /**
     * 获取本地图片的宽高
     *
     * @param path
     * @return
     */
    public static int[] getLocalImageSize(String path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int[] wh = new int[]{options.outWidth, options.outHeight};
        return wh;
    }

    public static Bitmap getResizeBitmapForFile(Context context, String path, int withWidth) {
        // if(1==1) {
        // return BitmapFactory.decodeFile(path);
        // }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int[] wh = getCurrWidthAndHeight(options.outWidth, options.outHeight, withWidth);
        int viewWidth = wh[0];
        int viewHeight = wh[1];
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        bitmap = resizeImage(bitmap, viewWidth, viewHeight);
        // bitmap = ThumbnailUtils.extractThumbnail(bitmap, viewWidth,
        // viewHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static Bitmap getBitmapForFile(String path) {
        // if(1==1) {
        // return BitmapFactory.decodeFile(path);
        // }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        /*int[] wh = getCurrWidthAndHeight(context, options.outWidth, options.outHeight, inside);
        int viewWidth = wh[0];
        int viewHeight = wh[1];
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        bitmap = resizeImage(bitmap, viewWidth, viewHeight);*/
        // bitmap = ThumbnailUtils.extractThumbnail(bitmap, viewWidth,
        // viewHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 根据URL地址，获取图片
     *
     * @param path 图片的网络地址
     * @return
     */
    public static Bitmap getResizeBitmapForNet(Context context, String path, int withWidth) {
        try {
            URL url = new URL(path);
            // 首先获取图片的缓存目录
            String cacheDir = getImageCacheDir(context);
            // 图片在本地的文件名为 MD5编码后的url地址
            File file = new File(cacheDir, MD5.getMD5(url.toString()));
            // 查看缓存目录是否存在该URL的图片
            if (file.exists() && file.isFile()) {
                // 如果缓存目录已存在，直接获取该图片
                return getResizeBitmapForFile(context, file.getAbsolutePath(), withWidth);
            } else {
                // 没有的话，下载，并获取本地的文件路径
                path = DownloadUtils.downloadForUrl(url, cacheDir);
                return getResizeBitmapForFile(context, path, withWidth);
            }
        } catch (IOException e) {
            L.w(TAG, "", e);
        }

        return null;
    }

    public static Bitmap getBitmapForNet(Context context, String path) {
        try {
            URL url = new URL(path);
            // 首先获取图片的缓存目录
            String cacheDir = getImageCacheDir(context);
            // 图片在本地的文件名为 MD5编码后的url地址
            File file = new File(cacheDir, MD5.getMD5(url.toString()));
            // 查看缓存目录是否存在该URL的图片
            if (file.exists() && file.isFile()) {
                // 如果缓存目录已存在，直接获取该图片
                return getBitmapForFile(file.getAbsolutePath());
            } else {
                // 没有的话，下载，并获取本地的文件路径
                path = DownloadUtils.downloadForUrl(url, cacheDir);
                return getBitmapForFile(path);
            }
        } catch (IOException e) {
            L.w(TAG, "", e);
        }

        return null;
    }

    private static String getImageCacheDir(Context context) {
        if (existSDCard()) {
            return context.getExternalCacheDir().getAbsolutePath();
        }
        return context.getCacheDir().getAbsolutePath();
    }

    private static boolean existSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }


    private static List<String> displayedImages = Collections
            .synchronizedList(new LinkedList<String>());

    private static DisplayImageOptions options = new DisplayImageOptions.Builder()
            // 正在加载的图片
            .showImageOnLoading(R.drawable.img_bbs_default)
                    // URL请求失败
            .showImageForEmptyUri(R.drawable.img_bbs_default)
                    // 图片加载失败
            .showImageOnFail(R.drawable.img_bbs_default)
            .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    public static void setImageFromUrl(String url, final ImageView iv) {
        setImageFromUrl(url, iv, options);
    }

    public static void setImageFromUrl(String url, final ImageView iv, DisplayImageOptions options) {
        // Log.i("III", "url " + url);
        ImageLoader.getInstance().loadImage(
                url, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri,
                                                  View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        iv.setImageBitmap(loadedImage);
                        if (loadedImage != null) {
                            ImageView imageView = (ImageView) view;
                            boolean firstDisplay = !displayedImages
                                    .contains(imageUri);
                            if (firstDisplay) {
                                FadeInBitmapDisplayer.animate(imageView, 500);
                                displayedImages.add(imageUri);
                            }
                        }

                    }
                });
    }
}
