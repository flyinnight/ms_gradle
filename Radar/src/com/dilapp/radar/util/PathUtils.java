package com.dilapp.radar.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.lidroid.xutils.cache.MD5FileNameGenerator;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

public class PathUtils {

    public static final String SD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String RADAR_CACEH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.radarCache/";
    public static final String UPDATE_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";
    public static final String RADAR_IMAGE_CACEH = "/sdcard/.radarCache/radarImageCache/";
    public static final String SHARE_IMAGE_PATH = RADAR_CACEH + "share_image.jpg";
    public static final String TEST_IMAGE_BASE = RADAR_CACEH + "test_cache/" + "test_image_";
    public static final String SD_LOG = RADAR_CACEH + "sd_log/";
    public static final String PICTURE_CACHE = RADAR_CACEH + "picture_cache/";
    public static final String IMAGE_LOAD_CACHE = RADAR_CACEH + "image_load";

    //以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider  
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider  
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider  
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider  
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)  
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address  
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File  
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    //add by kfir
    public static String compressDefPicFile(String srcpath) {
        return compressPicFile(srcpath, 800);
    }

    /**
     * @param srcpath
     * @param maxwidth
     * @param maxheight
     * @return the path of the new compressed picture
     */
    public static String compressPicFile(String srcpath, int maxlength) {
        String result = srcpath;
        File destFile = null;
        if (TextUtils.isEmpty(srcpath)) {
            return result;
        }
        if (!srcpath.startsWith("/sdcard") && !srcpath.startsWith(SD_ROOT)) {
            return result;
        }
        MD5FileNameGenerator mGenerator = new MD5FileNameGenerator();
        String cacheKey = mGenerator.generate(srcpath);
        destFile = new File(PICTURE_CACHE + cacheKey + ".jpg");
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
//    		destFile = ABFileUtil.getFileAutoCreated();
        if (destFile.exists()) {
            return destFile.getAbsolutePath();
        }

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(srcpath, options);
            int srcWidth = options.outWidth;
            int srcHeight = options.outHeight;
//    			int destWidth = 0;
//    			int destHeight = 0;
            float ratio = 0.0F;
            if (srcWidth > srcHeight) {
                if (srcWidth < maxlength) {
                    options.outWidth = srcWidth;
                    options.outHeight = srcHeight;
                    options.inSampleSize = 1;
                } else {
                    ratio = (float) srcWidth / (float) maxlength;
                    options.outWidth = maxlength;
                    options.outHeight = (int) ((float) srcHeight / ratio);
                    options.inSampleSize = (int) ratio + 1;
                }

            } else {
                if (srcHeight < maxlength) {
                    options.outWidth = srcWidth;
                    options.outHeight = srcHeight;
                    options.inSampleSize = 1;
                } else {
                    ratio = (float) srcHeight / (float) maxlength;
                    options.outHeight = maxlength;
                    options.outWidth = (int) ((float) srcWidth / ratio);
                    options.inSampleSize = (int) ratio + 1;
                }
            }

//    			options.inSampleSize = (int)ratio + 1;
//    			options.outWidth = destWidth;
//    			options.outHeight = destHeight;
            options.inJustDecodeBounds = false;
            Bitmap destBm = BitmapFactory.decodeFile(srcpath, options);
            OutputStream os = new FileOutputStream(destFile);
            destBm.compress(CompressFormat.JPEG, 100, os);
            os.close();
            if (destBm != null && destBm.isRecycled()) {
                destBm.recycle();
                destBm = null;
            }
            result = destFile.getAbsolutePath();
        } catch (Exception e) {
            Slog.e("compressBitmapFile Error !", e);
            result = srcpath;
            if (destFile != null && destFile.exists()) {
                destFile.delete();
            }
        }
        return result;
    }

}
