package com.dilapp.radar.ui.topic;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Selection;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.textbuilder.BBSTextBuilder;
import com.dilapp.radar.textbuilder.impl.BBSTextBuilderImpl;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.comm.image.ImageBucketActivity;
import com.dilapp.radar.util.AndroidBugsSolution;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.viewbuilder.BBSViewBuilder;
import com.dilapp.radar.viewbuilder.BBSViewGetter;
import com.dilapp.radar.viewbuilder.impl.BBSViewBuilderImpl;
import com.dilapp.radar.widget.BaseDialog;
import com.dilapp.radar.widget.ButtonsListDialog;
import com.dilapp.radar.widget.ButtonsListDialog.ButtonsListItem;
import com.lenovo.text.bbsbuild.BBSViewGetterImpl;
import com.rockerhieu.emojicon.EmojiconGridFragment.OnEmojiconClickedListener;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.EmojiconsFragment.OnEmojiconBackspaceClickedListener;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.ArrayList;
import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 *
 */
public abstract class ActivityPostEditBase extends BaseFragmentActivity implements View.OnClickListener, AndroidBugsSolution.OnKeyboardListener, OnEmojiconBackspaceClickedListener, OnEmojiconClickedListener, BBSViewGetterImpl.OnRequestChangeCallback, View.OnLongClickListener {

    private static int REQ_IMAGE = 20;
    private static int REQ_IMAGE_REPLACE = 100;
    private static int SOFT_KEYBOARD_DELAYED_TIME = 80;

    protected EditText et_title;
    protected ViewGroup post_container;
    protected ImageButton ibtn_switch;
    protected Fragment mFEmojicon;

    protected NetImagePathDialog mNetImage;
    protected ButtonsListDialog mOption;
    protected Button btn_delete;
    protected Button btn_replace;

    protected BBSViewGetter mViewGetter;
    protected BBSTextBuilder mTextBuilder;
    protected BBSViewBuilder mViewBuilder;

    protected InputMethodManager imm;
    protected Bundle sis;

    protected boolean first = true;
    private boolean mTouchAnyShowKeyboard = true;

    protected void initView(Bundle savedInstanceState) {
        AndroidBugsSolution.assistActivity(this, this);
        Context context = getApplicationContext();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        sis = savedInstanceState;

        et_title = findViewById_(R.id.et_title);
        post_container = findViewById_(R.id.post_container);
        ibtn_switch = findViewById_(R.id.ibtn_switch);
        post_container.setOnClickListener(this);

        mNetImage = new NetImagePathDialog(this);
        mNetImage.setCancelOnClickListener(this);
        mNetImage.setConfirmOnClickListener(this);

        mOption = new ButtonsListDialog(this, genButtonsList());
        btn_delete = mOption.findViewById_(R.id.btn_delete);
        btn_replace = mOption.findViewById_(R.id.btn_replace);

        FragmentManager fm = getSupportFragmentManager();
        mFEmojicon = fm.findFragmentById(R.id.emojicons);
        fm.beginTransaction().hide(mFEmojicon).commit();

        mViewGetter = new BBSViewGetterImpl(context, getLayoutInflater());
        ((BBSViewGetterImpl) mViewGetter).setOnRequestChangeCallback(this);
        mTextBuilder = new BBSTextBuilderImpl("[]");
        mViewBuilder = new BBSViewBuilderImpl(context, BBSViewBuilder.MODE_EDITER, mTextBuilder, mViewGetter);
        mViewBuilder.setDividerDrawableRes(R.drawable.divider_transparent);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && first) {
            post_container.addView(mViewBuilder.getContainer());
            // Log.i("III", "container width " + post_container.getMeasuredWidth());
            ((BBSViewGetterImpl) mViewGetter).setParentWidth(
                    post_container.getMeasuredWidth() -
                            (post_container.getPaddingLeft() + post_container.getPaddingRight()));
            PresetPostModel model = (PresetPostModel) getIntent().getSerializableExtra(Constants.EXTRA_EDIT_POST_PRESET_CONTENT);
            presetPost(model);

            if (sis != null) {
                et_title.setText(sis.getString("PostTitle", ""));
                mTextBuilder.setString(sis.getString("PostContent", "[]"));
                mViewBuilder.notifyTextBuilderChanged();
                d("III", "Read Post " + sis.getString("PostContent"));
                sis = null;
                // mTextBuilder.getBBSDescribe();
            }
            first = false;
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.post_container: {
                if (mTouchAnyShowKeyboard) {
                    setSoftKeyboardVisiable(true);
                }
                break;
            }
            case R.id.ibtn_switch: {
                int tag = ibtn_switch.getDrawable().getLevel();
                if (tag == 1) {
                    setEmojicoVisiable(false);
                    setSoftKeyboardVisiable(true);
                    ibtn_switch.getDrawable().setLevel(0);
                } else {
                    //Toast.makeText(this, R.string.topic_emoji_no_open, Toast.LENGTH_SHORT).show();
                    //if (1 != 1) {
                        boolean isOpen = imm.isActive();
                        setSoftKeyboardVisiable(false);
                        new android.os.Handler() {// 做个短暂的延时，否则会有点不好看
                            public void handleMessage(Message msg) {
                                setEmojicoVisiable(true);
                                ibtn_switch.getDrawable().setLevel(1);
                            }
                        }.sendEmptyMessageDelayed(0, isOpen ? SOFT_KEYBOARD_DELAYED_TIME : 0);
                    //}
                }
                break;
            }
            case R.id.btn_add: {
//                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                Intent getImage = new Intent(this, ImageBucketActivity.class);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                getImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(getImage, REQ_IMAGE);
                break;
            }
            case R.id.btn_replace: {
                mOption.dismiss();
                int index;
                if (btn_replace.getTag() instanceof Integer &&
                        (index = Integer.parseInt(btn_replace.getTag().toString())) >= 0) {
                    Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                    getImage.addCategory(Intent.CATEGORY_OPENABLE);
                    getImage.setType("image/*");
                    startActivityForResult(getImage, REQ_IMAGE_REPLACE + index);
                } else {
                    d("III", "没有索引");
                }
                break;
            }
            case R.id.btn_delete: {
                mOption.dismiss();
                int index = 0;
                if (btn_delete.getTag() instanceof Integer &&
                        (index = Integer.parseInt(btn_delete.getTag().toString())) >= 0) {
                    mViewBuilder.removeAt(index);// 删除图片
                    ViewGroup container = mViewBuilder.getContainer();
                    if (index < container.getChildCount() &&
                            // 类型是文本
                            mTextBuilder.getBBSDescribe().get(index).getType() == BBSDescribeItem.TYPE_TEXT_EMOJI_LINK &&
                            // 文本框没有任何内容
                            "".equals(((EditText) container.getChildAt(index).findViewById(R.id.et_edittext)).getText().toString().trim()) &&
                            // 前面一个控件也是文本框
                            (index > 0 && mTextBuilder.getBBSDescribe().get(index - 1).getType() == BBSDescribeItem.TYPE_TEXT_EMOJI_LINK)) {
                        mViewBuilder.removeAt(index);// 买一送一，删除文本框
                    }
                } else {
                    d("III", "没有索引");
                }

                break;
            }
            case R.id.btn_cancel: {
                mOption.dismiss();
                break;
            }
            case R.id.btn_cancel_path: {
                mNetImage.setUrl("");
                mNetImage.dismiss();
                break;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_IMAGE && resultCode == RESULT_OK) {
            String path = null;
            ClipData mClipData = data.getClipData();
            if (mClipData != null) {
                Slog.e("kfir :: mClipData size : " + mClipData.getItemCount());
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    Item mItem = mClipData.getItemAt(i);
//        				Slog.e("kfir :: Item "+mItem.toString());
                    path = PathUtils.getPath(getApplication(), mItem.getUri());
                    Log.i("III_logic", "path=" + path);
                    View view = mViewBuilder.appendImage(path);
                    EditText v = (EditText) mViewBuilder.appendForType(BBSDescribeItem.TYPE_TEXT_EMOJI_LINK).findViewById(R.id.et_edittext);
                    v.requestFocus();
                    bindView(view);
                }
            } else {
                path = PathUtils.getPath(getApplication(), data.getData());
                Log.i("III_logic", "path=" + path);
                View view = mViewBuilder.appendImage(path);
                EditText v = (EditText) mViewBuilder.appendForType(BBSDescribeItem.TYPE_TEXT_EMOJI_LINK).findViewById(R.id.et_edittext);
                v.requestFocus();
                bindView(view);
            }


            // bindView(view);
        } else if (requestCode >= REQ_IMAGE_REPLACE &&
                requestCode < REQ_IMAGE_REPLACE + mTextBuilder.getBBSDescribe().size() &&
                resultCode == RESULT_OK) {
            // 替换图片
            String path = PathUtils.getPath(getApplication(), data.getData());
            int index = requestCode - REQ_IMAGE_REPLACE;
            int type = mTextBuilder.get(index).getType();
            d("III", "index " + index + ", path " + path);
            if (type == BBSDescribeItem.TYPE_IMAGE || type == BBSDescribeItem.TYPE_IMAGE_LINK) {
                mViewBuilder.replaceContent(index, path);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("PostTitle", et_title.getText().toString());
        outState.putString("PostContent", mTextBuilder.getString());
        d("III", "Saved Post " + mTextBuilder.getString());
    }

    @Override
    public void onKeyboardChanged(int state) {
        if (state == AndroidBugsSolution.OnKeyboardListener.SHOW) {
            setEmojicoVisiable(false);
            ibtn_switch.getDrawable().setLevel(0);
        } else {
            ibtn_switch.getDrawable().setLevel(0);
        }
    }

    protected void bindView(View v) {

        int index = mViewBuilder.getContainer().indexOfChild(v);
        int type = mTextBuilder.get(index).getType();
        if (type == BBSDescribeItem.TYPE_IMAGE_LINK ||
                type == BBSDescribeItem.TYPE_IMAGE) {
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int index = mViewBuilder.getContainer().indexOfChild(v);
                    int type = mTextBuilder.get(index).getType();
                    d("III", "index " + index + ", type " + type);
                    btn_delete.setTag(index);
                    btn_replace.setTag(index);
                    mOption.show();
                    return true;
                }
            });
        }
    }

    protected void initBindView() {

        ViewGroup container = mViewBuilder.getContainer();
        int size = container.getChildCount();
        for (int i = 0; i < size; i++) {
            bindView(container.getChildAt(i));
        }
    }

    protected void initImageInsertText(boolean first, boolean last) {

        List<BBSDescribeItem> des = mTextBuilder.getBBSDescribe();
        if (des == null || des.size() == 0) return;

        if (first && des.get(0).getType() != BBSDescribeItem.TYPE_TEXT_EMOJI_LINK) {
            // mViewBuilder.insertLink();
        }

    }

    protected void setEmojicoVisiable(boolean visiable) {
        boolean isOpen = mFEmojicon.isVisible();
        if (visiable) {
            // if(!isOpen) {
            getSupportFragmentManager().beginTransaction().show(mFEmojicon).commit();
            // }
        } else {
            // if(isOpen) {
            getSupportFragmentManager().beginTransaction().hide(mFEmojicon).commit();
            // }
        }
    }

    protected void setSoftKeyboardVisiable(boolean visiable) {
        boolean isOpen = imm.isActive();
//        if(isOpen != visiable) {
//            return;
//        }

        try {
            if (visiable) {
//            if(!isOpen) {
                imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);
//            }
            } else {
//            if(isOpen) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void presetPost(PresetPostModel model) {

        if (model == null) {
            return;
        }
        if (model.getTitle() != null) {
            et_title.setText(model.getTitle());
        }
        if (model.getList() != null) {
            final int size = model.getList().size();
            for (int i = 0; i < size; i++) {
                BBSDescribeItem item = model.getList().get(i);
                if (item == null ||
                        item.getContent() == null ||
                        "".equals(item.getContent().toString().trim()))
                    continue;
                mViewBuilder.append(model.getList().get(i));
            }
        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        View view = getCurrentFocus();
        if (!(view instanceof EditText)) {
            return;
        }
        EmojiconsFragment.backspace((EditText) view);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        View view = getCurrentFocus();
        if (!(view instanceof EditText)) {
            return;
        }
        EmojiconsFragment.input((EditText) view, emojicon);
    }

    private List<ButtonsListItem> genButtonsList() {
        List<ButtonsListItem> list = new ArrayList<ButtonsListItem>(3);
        list.add(new ButtonsListItem(R.id.btn_replace, getString(R.string.topic_edit_replace), this, this));
        list.add(new ButtonsListItem(R.id.btn_delete, getString(R.string.topic_edit_delete), this, this));
        list.add(new ButtonsListItem(R.id.btn_cancel, getString(R.string.cancel), this, this));
        return list;
    }

    @Override
    public boolean onRequestRemove(View v) {
        ViewGroup contaienr = mViewBuilder.getContainer();
        int index = contaienr.indexOfChild(v);
        if (index > 0) {
            // 删除文本框,这种情况会在文本框没有文字时用户还按了删除，会回调这个方法
            if (mTextBuilder.getBBSDescribe().get(index - 1).getType() ==
                    BBSDescribeItem.TYPE_TEXT_EMOJI_LINK) {
                // 判断要删除的这个View里面是否还有文字，
                // 有文字的话，把它添加到上一个文本框控件里去
                String appendText = "";
                if (mTextBuilder.getBBSDescribe().get(index).getType() ==
                        BBSDescribeItem.TYPE_TEXT_EMOJI_LINK) {
                    EditText ett = (EditText) contaienr.getChildAt(index).findViewById(R.id.et_edittext);
                    appendText = ett.getText().toString();
                }
                mViewBuilder.removeAt(index);
                EditText et = (EditText) contaienr.getChildAt(index - 1).findViewById(R.id.et_edittext);
                et.requestFocus();
                int len = et.getText().length();
                et.getText().append(appendText);
                Selection.setSelection(et.getText(), len);
                return true;
            }
        }
        return false;
    }

    public boolean isTouchAnyShowKeyboard() {
        return mTouchAnyShowKeyboard;
    }

    public void setTouchAnyShowKeyboard(boolean touchAnyShowKeyboard) {
        this.mTouchAnyShowKeyboard = touchAnyShowKeyboard;
    }

    class NetImagePathDialog extends BaseDialog {

        private EditText et_path;
        private Button btn_cancel;
        private Button btn_confirm;
        public NetImagePathDialog(Activity acitvity) {
            super(acitvity, R.style.ShadowDialog);
            setContentView(R.layout.dialog_input_url);

            Window window = getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            WindowManager wm = mActivity.getWindowManager();
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                lp.width = (int) (wm.getDefaultDisplay().getWidth() * .9f);
            } else {
                lp.width = (int) (wm.getDefaultDisplay().getHeight() * .9f);
            }
            window.setAttributes(lp);
            setCanceledOnTouchOutside(false);

            et_path = findViewById_(R.id.et_path);
            btn_cancel = findViewById_(R.id.btn_cancel_path);
            btn_confirm = findViewById_(R.id.btn_confirm_path);
        }

        public String getUrl() {
            return et_path.getText().toString();
        }

        public void setUrl(String url) {
            et_path.setText(url);
        }

        public void setCancelOnClickListener( View.OnClickListener l) {
            btn_cancel.setOnClickListener(l);
        }

        public void setConfirmOnClickListener(View.OnClickListener l) {
            btn_confirm.setOnClickListener(l);
        }
    }
}
