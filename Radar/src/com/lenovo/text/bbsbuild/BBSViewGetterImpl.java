package com.lenovo.text.bbsbuild;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.textbuilder.impl.BBSUtils;
import com.dilapp.radar.textbuilder.utils.L;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.util.DensityUtils;
import com.dilapp.radar.viewbuilder.BBSViewGetter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconTextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BBSViewGetterImpl implements BBSViewGetter {

    private static final String TAG = L.makeTag(BBSViewGetterImpl.class);
    private static final boolean LOG = true;

    private Context mContext;
    private LayoutInflater mInflater;

    private int[] mMargins;
    private int mParentWidth;
    private OnRequestChangeCallback mCallback;

    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .displayer(new FadeInBitmapDisplayer(200))
            .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    public BBSViewGetterImpl(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public BBSViewGetterImpl(Context context, LayoutInflater inflater) {
        this.mContext = context;
        this.mInflater = inflater;
    }

    public BBSViewGetterImpl(Context context, LayoutInflater inflater, int parentWidth) {
        this(context, inflater);
        this.mParentWidth = parentWidth;
        android.util.Log.i("III", "parentWidth " + parentWidth);
    }

    @SuppressLint("NewApi")
    @Override
    public View getViewOnEditer(int type, Context context) {

        View view = null;
        switch (type) {
            case BBSDescribeItem.TYPE_TEXT_EMOJI_LINK:
                View v = mInflater.inflate(R.layout.item_post_edit_text, null);
                // EditText edit = (EditText) v.findViewById(R.id.et_edittext);
                view = v;
                break;
            case BBSDescribeItem.TYPE_IMAGE:
            case BBSDescribeItem.TYPE_IMAGE_LINK:
                // ImageView iview = new ImageView(context);
//			 iview.setScaleType(ScaleType.FIT_XY);
//			iview.setImageMatrix();
                view = mInflater.inflate(R.layout.item_post_edit_image, null);
                break;
            case TopicHelper.TYPE_PLAN_STEP:
                view = mInflater.inflate(R.layout.item_plan_edit_steps, null);
                break;
            default:
                break;
        }

        if (LOG)
            L.d(TAG, "build view for " + view.getClass().getName());
        return view;
    }

    @Override
    public View getViewOnNormal(int type, Context context) {
        View view = null;
        switch (type) {
            case BBSDescribeItem.TYPE_TEXT_EMOJI_LINK:/*
                EmojiconTextView tview = new EmojiconTextView(context);
                // tview.setBackgroundResource(R.drawable.edittext_2_textview);
                // tview.setEnabled(false);
                // tview.setGravity(Gravity.CENTER);
                tview.setClickable(true);
                tview.setDuplicateParentStateEnabled(true);
                tview.setHighlightColor(Color.TRANSPARENT);
                fullViewAttribute(tview, R.style.ViewBuilder_TextView);*/
                TextView tview = (TextView) mInflater.inflate(R.layout.item_post_normal_text, null);
                tview.setMovementMethod(LinkMovementMethod.getInstance());
                view = tview;
                break;
            case BBSDescribeItem.TYPE_IMAGE:
            case BBSDescribeItem.TYPE_IMAGE_LINK:
                ImageView iview = new ImageView(context);
                // iview.setScaleType(ScaleType.FIT_XY);
                iview.setDuplicateParentStateEnabled(true);
                view = iview;
                break;
            case TopicHelper.TYPE_PLAN_STEP:
                view = mInflater.inflate(R.layout.item_plan_nral_steps, null);
                break;

            default:
                break;
        }
        if (LOG)
            L.d(TAG, "build view for " + view.getClass().getName());
        return view;
    }

    @Override
    public void setViewContentOnEditer(final View view, final BBSDescribeItem item,
                                       Context context) {

        final int type = item.getType();
        switch (type) {
            case BBSDescribeItem.TYPE_TEXT_EMOJI_LINK: {
                final EditText et = (EditText) view.findViewById(R.id.et_edittext);
                String str = convert((String) item.getContent());
                // item.setContent(str);
                et.setText(BBSUtils.convertSpanned(mContext,
            /* convert((String) */str, R.drawable.class));
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if (item.getContent() == null) {
                            item.setContent("");
                        }
                        String str = (String) item.getContent();
                        StringBuilder sb = new StringBuilder(str);
                        if (count != 0) {
                            // 添加字符
                            CharSequence chars = s
                                    .subSequence(start, start + count);
                            sb.insert(start, chars);
                        } else if (before != 0) {
                            // 删除字符
                            sb.delete(start, start + before);
                            et.setTag(R.id.tv_tag, false);
                            // et.setTag(R.id.tv_tag, false);
                        }
                        item.setContent(sb.toString());

                        if (LOG) {
                            L.i("III_str", "onTextChanged: " + s.toString()
                                    + " --> " + "" + ", start: " + start
                                    + ", count: " + count + ", before: " + before
                                    + ", Text.length: " + s.length()
                                    + ", Content.length: " + sb.length());
                            L.d("III_str", sb.toString());
                            // L.e("III_str", "" + et.gette);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                et.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        L.d("III", "keyCode " + keyCode);
                        EditText e = (EditText) v;
                        if (keyCode == KeyEvent.KEYCODE_DEL &&
                                "".equals(e.getText().toString().trim())) {
                            if (et.getTag(R.id.tv_tag) == null) et.setTag(R.id.tv_tag, true);
                            // 如果文本框中没有字符，还按了删除的话
                            if (mCallback != null && Boolean.parseBoolean(et.getTag(R.id.tv_tag).toString())) {
                                return mCallback.onRequestRemove(view);
                            }
                            et.setTag(R.id.tv_tag, true);
                        }
                        return false;
                    }
                });
                break;
            }
            case BBSDescribeItem.TYPE_IMAGE:
            case BBSDescribeItem.TYPE_IMAGE_LINK: {
                final ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
                final String content = (String) item.getContent();
                setImage(content, iv);
                break;
            }
            case TopicHelper.TYPE_PLAN_STEP:
                final EditText et = (EditText) view.findViewById(R.id.et_introduce);
                String str = convert((String) item.getContent());
                // item.setContent(str);
                et.setText(BBSUtils.convertSpanned(mContext, /* convert((String) */str, R.drawable.class));
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if (item.getContent() == null) {
                            item.setContent("");
                        }
                        String str = (String) item.getContent();
                        StringBuilder sb = new StringBuilder(str);
                        if (count != 0) {
                            // 添加字符
                            CharSequence chars = s
                                    .subSequence(start, start + count);
                            sb.insert(start, chars);
                        } else if (before != 0) {
                            // 删除字符
                            sb.delete(start, start + before);
                            et.setTag(R.id.tv_tag, false);
                            // et.setTag(R.id.tv_tag, false);
                        }
                        item.setContent(sb.toString());

                        if (LOG) {
                            L.i("III_str", "onTextChanged: " + s.toString()
                                    + " --> " + "" + ", start: " + start
                                    + ", count: " + count + ", before: " + before
                                    + ", Text.length: " + s.length()
                                    + ", Content.length: " + sb.length());
                            L.d("III_str", sb.toString());
                            // L.e("III_str", "" + et.gette);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                final ImageView iv = (ImageView) view.findViewById(R.id.iv_step_image);
                final TextView tv = (TextView) view.findViewById(R.id.tv_step_image);
                // measureView(iv);
                String url = (String) item.getParam("image_01");
                int pathType = TopicHelper.isImagePath(url);
                if (pathType != TopicHelper.PATH_UNKNOWN) {
                    if (pathType == TopicHelper.PATH_LOCAL_SDCARD) {
                        Bitmap b = ImageLoader.getInstance().loadImageSync("file://" + url);
                        if (b != null) {
                            iv.setImageBitmap(ThumbnailUtils.extractThumbnail(b, iv.getMinimumWidth(), iv.getMinimumHeight()));
                        }
                    } else {
                        ImageLoader.getInstance().loadImage(TopicHelper.wrappeImagePath(url), new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                if (bitmap != null) {
                                    iv.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, iv.getMinimumWidth(), iv.getMinimumHeight()));
                                }
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {

                            }
                        });
                        // ImageLoader.getInstance().displayImage(TopicHelper.wrappeImagePath(url), iv);
                    }
                    tv.setVisibility(View.GONE);
                }
                if (item.getParam("first") != null) {
                    View v_line = view.findViewById(R.id.v_line);
                    ((ViewGroup.MarginLayoutParams) v_line.getLayoutParams()).topMargin = DensityUtils.dip2px(context, 15);

                }
                if (item.getParam("last") != null) {
                    view.findViewById(R.id.v_end).setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
        measureView(view);
        if (LOG)
            L.d(TAG, "set view for " + view.getClass().getSimpleName()
                    + ", width is " + view.getMeasuredWidth() + ", height is "
                    + view.getMeasuredHeight());
    }

    @Override
    public void setViewContentOnNormal(View view, BBSDescribeItem item,
                                       Context context) {
        final int type = item.getType();
        switch (type) {
            case BBSDescribeItem.TYPE_TEXT_EMOJI_LINK: {
                TextView tv = (TextView) view;
                String str = convert((String) item.getContent());
                // item.setContent(str);
                Spanned spanned = BBSUtils.convertSpanned(mContext, (str),
                        R.drawable.class);
                tv.setText(spanned);
                // fullViewAttribute(tv, R.style.ViewBuilder_TextView);
                break;
            }
            case BBSDescribeItem.TYPE_IMAGE:
            case BBSDescribeItem.TYPE_IMAGE_LINK: {
                final ImageView iv = (ImageView) view;
                final String content = (String) item.getContent();
                setImage(content, iv);
                /*DisplayImageOptions dio = new DisplayImageOptions.Builder()
                        .displayer(new FadeInBitmapDisplayer(200))
                        .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                        .build();
                ImageLoader.getInstance().displayImage(content, iv, dio);*/
                break;
            }
            case TopicHelper.TYPE_PLAN_STEP: {

                final TextView tv = (TextView) view.findViewById(R.id.tv_introduce);
                String str = convert((String) item.getContent());
                tv.setText(BBSUtils.convertSpanned(mContext,
            /* convert((String) */str, R.drawable.class));
                ImageView iv = (ImageView) view.findViewById(R.id.iv_step_image);
                String url = (String) item.getParam("image_01");
                setImage(url, iv);

                if (item.getParam("first") != null) {
                    View v_line = view.findViewById(R.id.v_line);
                    ((ViewGroup.MarginLayoutParams) v_line.getLayoutParams()).topMargin = DensityUtils.dip2px(context, 15);

                }
                if (item.getParam("last") != null) {
                    view.findViewById(R.id.v_end).setVisibility(View.VISIBLE);
                }
                break;
            }
            default:
                break;
        }
        measureView(view);
        if (LOG)
            L.d(TAG, "set view for " + view.getClass().getSimpleName()
                    + ", width is " + view.getMeasuredWidth() + ", height is "
                    + view.getMeasuredHeight());
    }

    private void setImage(final String content, final ImageView iv) {
        if (LOG) L.d(TAG, "content " + content);

        if (content == null || "".equals(content.trim())) {
            return;
        }
        final String url = TopicHelper.wrappeImagePath(content);
        // if (LOG) L.d(TAG, "url " + url);
        int[] wh = null;
        if (url.startsWith("http")) {
            wh = getRound(content);
            if (wh != null) {
                wh = TopicHelper.getCurrWidthAndHeight(wh[0], wh[1], mParentWidth);
                iv.setMaxWidth(wh[0]);
                iv.setMinimumWidth(wh[0]);

                iv.setMaxHeight(wh[1]);
                iv.setMinimumHeight(wh[1]);
                ImageLoader.getInstance().displayImage(url, iv, options);
            } else {
                final Handler h = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        iv.setImageBitmap((Bitmap) msg.obj);
                    }
                };
                new Thread() {
                    public void run() {
                        Message msg = new Message();
                        // inside.set(mMargins[0], mMargins[1], mMargins[2], mMargins[3]);
                        msg.obj = TopicHelper.getResizeBitmapForNet(mContext, url, mParentWidth);
                        h.sendMessage(msg);
                    }
                }.start();
            }
        } else {
            wh = TopicHelper.getLocalImageSize(url);
            wh = TopicHelper.getCurrWidthAndHeight(wh[0], wh[1], mParentWidth);
            iv.setMaxWidth(wh[0]);
            iv.setMinimumWidth(wh[0]);

            iv.setMaxHeight(wh[1]);
            iv.setMinimumHeight(wh[1]);

            ImageLoader.getInstance().displayImage("file://" + url, iv, options);

        }

       /* if (content == null || "".equals(content.trim())) {
        } else if (content.startsWith("http")) {
            int[] wh = getRound(content);
            if(wh != null) {
                wh = TopicHelper.getCurrWidthAndHeight(wh[0], wh[1], mParentWidth);
                iv.setMinimumWidth(wh[0]);
                iv.setMaxWidth(wh[0]);

                iv.setMinimumHeight(wh[1]);
                iv.setMaxHeight(wh[1]);
            }
            if(wh != null) {
                ImageLoader.getInstance().displayImage(content, iv,options );
            } else {
                final Handler h = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        iv.setImageBitmap((Bitmap) msg.obj);
                    }
                };
                new Thread() {
                    public void run() {
                        Message msg = new Message();
                        // inside.set(mMargins[0], mMargins[1], mMargins[2], mMargins[3]);
                        msg.obj = TopicHelper.getResizeBitmapForNet(mContext, content, mParentWidth);
                        h.sendMessage(msg);
                    }
                }.start();
            }
        } else {
            // ImageLoader.getInstance().displayImage();
            // inside.set(mMargins[0], mMargins[1], mMargins[2], mMargins[3]);
            Bitmap bitmap = TopicHelper.getResizeBitmapForFile(mContext, content, mParentWidth);
            // bitmap = ThumbnailUtils.extractThumbnail(bitmap, viewWidth,
            // viewHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            iv.setImageBitmap(bitmap);
        }*/
    }

    @Override
    public void setMargins(int[] margins) {
        this.mMargins = margins;
    }

    public int getParentWidth() {
        return mParentWidth;
    }

    public void setParentWidth(int parentWidth) {
        this.mParentWidth = parentWidth;
    }

    public int[] getRound(String url) {
        int[] wh = null;
        Pattern p = Pattern.compile("_(\\d+)_(\\d+)\\.\\w+");
        Matcher m = p.matcher(url);
        if (m.find() && m.groupCount() == 2) {
            int width = Integer.parseInt(m.group(1));
            int height = Integer.parseInt(m.group(2));
            if (LOG) L.d(TAG, "w " + width + ", h " + height);
            wh = new int[]{width, height};
        }
        return wh;
    }

    private static String convert(String content) {
        String str = content;

        if (str != null) {
            str = str.replaceAll("\r\n", "<br />").replaceAll("\n", "<br/>")
                    .replaceAll("  ", "&nbsp;&nbsp;");
            if (str.startsWith("<") && !str.startsWith("<a/>")) {
                str = "<a/>" + str;
            }
            if (LOG)
                L.d(TAG, "new str: " + str);
        }
        return str;
    }

    private void fullViewAttribute(TextView tv, int resStyle) {
        int[] attrs = new int[]{android.R.attr.textSize,
                android.R.attr.textColor, android.R.attr.lineSpacingExtra, android.R.attr.lineSpacingMultiplier,
                com.rockerhieu.emojicon.R.attr.emojiconSize};
        TypedArray arr = mContext.obtainStyledAttributes(resStyle, attrs);
        int spacing = (DensityUtils.dip2px(mContext, 0));
        float mut = (1.0f);
        for (int i = 0; i < arr.getIndexCount(); i++) {
            int attr = arr.getIndex(i);
            switch (attr) {
                case 0: {
                    float size = DensityUtils.px2dip(mContext,
                            arr.getDimensionPixelSize(attr, 0));
                    tv.setTextSize(size);
                    break;
                }
                case 1:
                    int color = arr.getColor(attr, Color.BLACK);
                    tv.setTextColor(color);
                    break;
                case 2:
                    spacing = arr.getDimensionPixelSize(attr, spacing);
                    break;
                case 3:
                    mut = arr.getFloat(attr, mut);
                    break;
                case 4: {
                    if (tv instanceof EmojiconEditText) {
                        EmojiconEditText eet = (EmojiconEditText) tv;
                        int size = arr.getDimensionPixelSize(attr, 0);
                        eet.setEmojiconSize(size);
                    } else if (tv instanceof EmojiconTextView) {
                        EmojiconTextView etv = (EmojiconTextView) tv;
                        int size = arr.getDimensionPixelSize(attr, 0);
                        etv.setEmojiconSize(size);
                    }
                    break;
                }
                default:
                    break;
            }
        }
        tv.setLineSpacing(spacing, mut);
        arr.recycle();
    }

    private static void measureView(View v) {
        if (v == null) {
            return;
        }
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
    }

    public OnRequestChangeCallback getOnRequestChangeCallback() {
        return mCallback;
    }

    public void setOnRequestChangeCallback(OnRequestChangeCallback callback) {
        this.mCallback = callback;
    }

    public interface OnRequestChangeCallback {

        boolean onRequestRemove(View v);
    }
}
