package com.dilapp.radar.ui.topic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.SolutionCreateUpdate;
import com.dilapp.radar.domain.SolutionCreateUpdate.SolutionUpdateReq;
import com.dilapp.radar.domain.SolutionCreateUpdate.SolutionCreateReq;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.domain.impl.SolutionCreateUpdateAsyncImpl;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.textbuilder.utils.JsonUtils;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.DensityUtils;
import com.dilapp.radar.util.MD5;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.view.OverScrollView;
import com.dilapp.radar.widget.PromptDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * Created by husj1 on 2015/10/13.
 */
public class ActivityCarePlanEdit extends ActivityPostEditBase {

    private final static int REQ_CHOSE_COVER = 99;
    private final static int REQ_THUMB_COVER = 98;
    private final static int REQ_STEP_IMAGE = 97;
    private final static String COVER_TEMP_SAVE_PATH = MD5.getMD5("plan_cover") + ".png";
    private TitleView mTitle;

    private OverScrollView osv_scroll;
    private ImageView iv_cover;
    private TextView tv_cover;
    private EditText et_introduction;
    private ViewGroup gl_effects;
    private ViewGroup gl_parts;

    private Button btn_add_step;
    private PromptDialog prompt;

    private SolutionCreateUpdate scu;
    private int tempClickImageIndex = -1;
    private String coverPath;
    private boolean isModify;
    private MSolutionResp plan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_plan_edit);

        Context context = getApplicationContext();
        Intent data = getIntent();

        isModify = data.getBooleanExtra(Constants.EXTRA_EDIT_POST_IS_MODIFY, false);
        if (isModify) {
            plan = (MSolutionResp) data.getSerializableExtra(Constants.EXTRA_EDIT_POST_MODIFY_POST);
            d("III", "json-> " + JsonUtils.toJson(plan));
            if (plan == null) {
                Log.i("III_logic", "请选择 Post " + Constants.EXTRA_EDIT_POST_MODIFY_POST + "=" + GetPostList.MPostResp.class.getSimpleName());
                finish();
                return;
            }
        } else {
            plan = new MSolutionResp();
        }
        scu = ReqFactory.buildInterface(this, SolutionCreateUpdate.class);
        // new SolutionCMSyncWrapper(new SolutionCreateUpdateAsyncImpl(this));

        initView(savedInstanceState);
        setTouchAnyShowKeyboard(false);
        mViewBuilder.setDividerDrawable(null);
        mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
        mTitle.setLeftIcon(R.drawable.btn_back, this);
        mTitle.setCenterText(isModify ? R.string.plan_edit_modify_title :
                R.string.plan_edit_release_title, null);
        mTitle.setRightText(R.string.send_comment_release, this);

        osv_scroll = findViewById_(R.id.osv_scroll);
        iv_cover = findViewById_(R.id.iv_cover);
        tv_cover = findViewById_(R.id.tv_cover);
        et_introduction = findViewById_(R.id.et_introduction);
        gl_effects = findViewById_(R.id.gl_effects);
        gl_parts = findViewById_(R.id.gl_parts);
        btn_add_step = findViewById_(R.id.btn_add_step);
        prompt = new PromptDialog(this);
        prompt.setConfirmId(R.id.dialog_btn_ok);
        prompt.setCancelId(R.id.dialog_btn_cancel);
        prompt.setConfirmOnClickListener(this);
        prompt.setCancelOnClickListener(this);

        if (isModify && savedInstanceState == null) {
            et_title.setText(plan.getTitle());
            et_introduction.setText(plan.getIntroduction());
            setViewGroupCheckedByTags(gl_effects, plan.getEffect(), true);
            setViewGroupCheckedByTags(gl_parts, plan.getPart(), true);
            int pathType = TopicHelper.isImagePath(plan.getCoverImgUrl());
            if (pathType != TopicHelper.PATH_UNKNOWN) {
                coverPath = plan.getCoverImgUrl();
                if (pathType == TopicHelper.PATH_LOCAL_SDCARD) {
                    ImageLoader.getInstance().displayImage("file://" + plan.getCoverImgUrl(), iv_cover);
                } else {
                    ImageLoader.getInstance().displayImage(TopicHelper.wrappeImagePath(plan.getCoverImgUrl()), iv_cover);
                }
                tv_cover.setVisibility(View.GONE);
            }
        }

        if (savedInstanceState != null) {// 内存不足时，现场恢复数据
            coverPath = savedInstanceState.getString("PlanCoverPath");
            et_introduction.setText(savedInstanceState.getString("PlanIntroduction"));
            setViewGroupCheckedByTags(gl_effects, savedInstanceState.getStringArray("PlanEffects"), true);
            setViewGroupCheckedByTags(gl_parts, savedInstanceState.getStringArray("PlanParts"), true);

            int pathType = TopicHelper.isImagePath(coverPath);
            if (pathType != TopicHelper.PATH_UNKNOWN) {
                if (pathType == TopicHelper.PATH_LOCAL_SDCARD) {
                    ImageLoader.getInstance().displayImage("file://" + coverPath, iv_cover);
                } else {
                    ImageLoader.getInstance().displayImage(TopicHelper.wrappeImagePath(coverPath), iv_cover);
                }
                tv_cover.setVisibility(View.GONE);
            }
        }
    }

    private boolean thisFirst = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && thisFirst) {
            if (isModify && sis == null) {// 需要做个sis变量为null的判断
                // 否则修改模式下，按Home键，如果内存不足，一回来，你会发现你编辑的东西都没了
                mTextBuilder.setString(plan.getContent());
                // mTextBuilder.getBBSDescribe();
                mViewBuilder.notifyTextBuilderChanged();
                initBindView();
            } else {
                mViewBuilder.removeAll();
                BBSDescribeItem item = new BBSDescribeItem();
                item.setType(TopicHelper.TYPE_PLAN_STEP);
                item.putParam("first", true);
                item.putParam("last", true);
                mViewBuilder.append(item);
                bindView(mViewBuilder.get(0));
                et_title.requestFocus();
                osv_scroll.scrollTo(0, osv_scroll.getChildAt(0).getPaddingTop());
            }
            thisFirst = false;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case TitleView.ID_LEFT:
                finish();
                break;
            case TitleView.ID_RIGHT:
                String title = et_title.getText().toString();
                String introduction = et_introduction.getText().toString();
                String[] effects = getViewGroupCheckedTags(gl_effects);
                String[] parts = getViewGroupCheckedTags(gl_parts);
                if (iv_cover.getDrawable() == null || coverPath == null || "".equals(coverPath.trim())) {
                    Toast.makeText(this, R.string.plan_release_chose_cover, Toast.LENGTH_SHORT).show();
                    osv_scroll.smoothScrollTo(0, osv_scroll.getChildAt(0).getPaddingTop());
                    break;
                } else if (title == null || "".equals(title.trim())) {
                    et_title.requestFocus();
                    setSoftKeyboardVisiable(true);
                    break;
                } else if (introduction == null || "".equals(introduction.trim())) {
                    et_introduction.requestFocus();
                    setSoftKeyboardVisiable(true);
                    break;
                } else if (effects == null || effects.length == 0) {
                    setSoftKeyboardVisiable(false);
                    osv_scroll.smoothScrollTo(0, et_introduction.getBottom());
                    break;
                } else if (parts == null || parts.length == 0) {
                    setSoftKeyboardVisiable(false);
                    osv_scroll.smoothScrollTo(0, gl_effects.getBottom());
                    break;
                } else if (mTextBuilder.size() != 0) {
                    List<BBSDescribeItem> descs = mTextBuilder.getBBSDescribe();
                    boolean flag = false;
                    for (int i = 0; i < descs.size(); i++) {
                        BBSDescribeItem desc = descs.get(i);
                        String image = (String) desc.getParam("image_01");
                        if (image == null || "".equals(image.trim())) {
                            flag = true;
                            Toast.makeText(this, R.string.plan_release_chose_image, Toast.LENGTH_SHORT).show();
                        } else if (desc.getContent() == null ||
                                "".equals(desc.getContent().toString().trim())) {
                            flag = true;
                            Toast.makeText(this, R.string.plan_release_input_desc, Toast.LENGTH_SHORT).show();
                        }
                        if (flag) {
                            osv_scroll.smoothScrollTo(0, mViewBuilder.get(i).getTop() + post_container.getTop());
                            break;
                        }
                    }
                    if (flag) break;
                }
                setSoftKeyboardVisiable(false);
                d("III", "cover = " + coverPath);
                d("III", "title = " + title);
                d("III", "introduction = " + introduction);
                d("III", "effects = " + unionArrays(effects, ","));
                d("III", "parts = " + unionArrays(parts, ","));
                d("III", "result = " + mTextBuilder.getString());
                SolutionUpdateReq req = new SolutionUpdateReq();
                req.setCoverUrl(coverPath);
                req.setTitle(title);
                req.setIntroduction(introduction);
                req.setEffect(effects);
                req.setPart(parts);
                req.setContent(mTextBuilder.getString());
                req.setSolutionId(plan.getSolutionId());
                if (isModify) {
                    requestUpdatePlan(req);
                } else {
                    requestReleasePlan(req);
                }
                break;
            case R.id.vg_click_cover: {
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, REQ_CHOSE_COVER);
                break;
            }
            case R.id.ibtn_delete: {
                ViewGroup container = mViewBuilder.getContainer();
                int index = container.indexOfChild((View) v.getTag());
                BBSDescribeItem item = mTextBuilder.get(index);
                // 有内容向用户确认
                if (item.getContent() != null && !"".equals(item.getContent())
                        || item.getParam("image_01") != null &&
                        !"".equals(item.getParam("image_01"))) {
                    prompt.setMessage(R.string.plan_delete_step_confirm);
                    prompt.setTag(v.getTag());
                    prompt.setTag(R.id.tv_tag, R.id.ibtn_delete);
                    prompt.show();
                } else {
                    // 无内容直接删除
                    removeStep((View) v.getTag());
                }
                break;
            }
            case R.id.vg_click_image: {
                tempClickImageIndex = mViewBuilder.getContainer().indexOfChild((View) v.getParent());
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, REQ_STEP_IMAGE);
                break;
            }
            case R.id.btn_add_step:
                Map<String, Object> params = new HashMap<String, Object>(3);
                params.put("last", true);
                if (mTextBuilder.size() >= 1) {
                    int last = mViewBuilder.size() - 1;
                    mViewBuilder.get(last).findViewById(R.id.v_end)
                            .setVisibility(View.GONE);
                    mTextBuilder.get(last).removeParam("last");
                } else if (mTextBuilder.size() == 0) {
                    params.put("first", true);
                }
                View view = mViewBuilder.appendForType(TopicHelper.TYPE_PLAN_STEP, params);
                bindView(view);
//                plan_step_max
                if (mTextBuilder.size() >= Constants.PLAN_STEPS_MAX) {
                    // 限制最大的步骤数
                    btn_add_step.setEnabled(false);
                    btn_add_step.setText(R.string.plan_step_max);
                }
                break;
            case R.id.dialog_btn_ok: {
                prompt.dismiss();
                if (((Integer) prompt.getTag(R.id.tv_tag)) == R.id.ibtn_delete) {
                    removeStep((View) prompt.getTag());
                }
                break;

            }
            case R.id.dialog_btn_cancel:
                prompt.dismiss();
                break;
        }
    }

    @Override
    protected void bindView(View parent) {

        parent.setId(View.generateViewId());
        ImageButton ibtn_delete = (ImageButton) parent.findViewById(R.id.ibtn_delete);
        View vg_click_image = parent.findViewById(R.id.vg_click_image);
        View v_line = parent.findViewById(R.id.v_line);
        TextView tv_number = (TextView) parent.findViewById(R.id.tv_number);


        int index = mViewBuilder.getContainer().indexOfChild(parent);
        if (index == 0) {// if index is first的话，把圆上的线头去掉
            ((ViewGroup.MarginLayoutParams) v_line.getLayoutParams()).topMargin = DensityUtils.dip2px(this, 15);
            v_line.getParent().requestLayout();
        }
        if (index == mViewBuilder.getContainer().getChildCount() - 1) {
            // 最后一个,显示正方形
            parent.findViewById(R.id.v_end).setVisibility(View.VISIBLE);

            if (index != 0 && mTextBuilder.get(index - 1).getType() ==
                    TopicHelper.TYPE_PLAN_STEP) {
                mViewBuilder.get(index - 1).findViewById(R.id.v_end).setVisibility(View.GONE);
            }
        }
        tv_number.setText(getString(R.string.plan_step_num, (index + 1) + ""));
        ibtn_delete.setTag(parent);
        ibtn_delete.setOnClickListener(this);
        vg_click_image.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_STEP_IMAGE: {
                if (resultCode != RESULT_OK) break;
                d("III", "add image index " + tempClickImageIndex);
                if (tempClickImageIndex == -1) break;
                String url = PathUtils.getPath(this, data.getData());

                // 获取对应的View
                View stepView = mViewBuilder.get(tempClickImageIndex);
                ImageView iv = (ImageView) stepView.findViewById(R.id.iv_step_image);
                TextView tv = (TextView) stepView.findViewById(R.id.tv_step_image);
                try {
                    Bitmap bm = ImageLoader.getInstance().loadImageSync("file://" + url);
                    bm = ThumbnailUtils.extractThumbnail(bm, tv.getMeasuredWidth(), tv.getMeasuredHeight());
                    iv.setImageBitmap(bm);
                    tv.setVisibility(View.GONE);
                    mTextBuilder.get(tempClickImageIndex).getParams().put("image_01", url);
                } catch (Exception e) {
                }
                tempClickImageIndex = -1;
                break;
            }
            case REQ_CHOSE_COVER:
                if (resultCode != RESULT_OK) break;
                Intent intent = new Intent("com.android.camera.action.CROP");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    String furl = PathUtils.getPath(this, data.getData());
                    intent.setDataAndType(Uri.fromFile(new File(furl)), "image/*");
                } else {
                    intent.setDataAndType(data.getData(), "image/*");
                }
                int width = tv_cover.getMeasuredWidth() > 540 ? 540 : tv_cover.getMeasuredWidth();
                int height = tv_cover.getMeasuredHeight() > 225 ? 225 : tv_cover.getMeasuredHeight();
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", width);
                intent.putExtra("aspectY", height);
                intent.putExtra("outputX", width);
                intent.putExtra("outputY", height);
                d("III", "Width x Height " + width + "x" + height);
                intent.putExtra("scale", true);
                intent.putExtra("scaleUpIfNeeded", true);
                // intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.putExtra("return-data", true);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                // intent.putExtra("noFaceDetection", true); // no face detection
                startActivityForResult(intent, REQ_THUMB_COVER);
                break;
            case REQ_THUMB_COVER:
                if (resultCode != RESULT_OK) break;
                if (data == null) {
                    d("III", "THUMB_COVER break");
                    break;
                }
                Uri uri = data.getData();
                Bitmap photo;
                if (uri == null && data.getExtras() != null && data.getExtras().get("data") != null) {
                    photo = (Bitmap) data.getExtras().get("data");
                } else {
                    String url = PathUtils.getPath(this, uri);
                    d("III", "url " + url + " uri " + uri);
                    photo = ImageLoader.getInstance().loadImageSync(url);
                }
                // String path = PathUtils.getPath(getApplication(), data.getData());
                tv_cover.setVisibility(View.GONE);
                d("III", "" + (iv_cover.getDrawable() == null));
                iv_cover.setImageBitmap(photo);
                Drawable draw = iv_cover.getDrawable();
                coverPath = getCacheDir() + "/" + COVER_TEMP_SAVE_PATH;
                saveBitmap(coverPath, photo);
                    /*d("III", "" + draw.getClass().getName() + ", " + draw.getIntrinsicWidth() + "x" + draw.getIntrinsicHeight());
                    if (draw instanceof BitmapDrawable) {
                        BitmapDrawable bit = ((BitmapDrawable) draw);
                        d("III", "Bitmap");
                    }*/
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("PlanCoverPath", coverPath);
        outState.putString("PlanIntroduction", et_introduction.getText().toString());
        outState.putStringArray("PlanEffects", getViewGroupCheckedTags(gl_effects));
        outState.putStringArray("PlanParts", getViewGroupCheckedTags(gl_parts));

    }

    private void removeStep(View v) {

        ViewGroup container = mViewBuilder.getContainer();
        int count = container.getChildCount();
        int index = container.indexOfChild(v);
        if (index != -1) {
            if (index != 0 && index == count - 1) {// 删除的是最后一个
                mTextBuilder.get(index - 1).putParam("last", true);
                mViewBuilder.notifyTextItemChanged(index - 1);
                // container.getChildAt(index - 1).findViewById(R.id.v_end).setVisibility(View.VISIBLE);
            }
            if (index == 0 && count != 1) {// 删除的是第一个
                mTextBuilder.get(1).putParam("first", true);
                mViewBuilder.notifyTextItemChanged(1);
            }
            for (int i = index + 1; i < count; i++) {
                TextView tv_number = (TextView) container.getChildAt(i).findViewById(R.id.tv_number);
                tv_number.setText(getString(R.string.plan_step_num, (i) + ""));
            }
            mViewBuilder.removeAt(index);
            if (count == Constants.PLAN_STEPS_MAX) {
                btn_add_step.setEnabled(true);
                btn_add_step.setText(R.string.plan_step_add);
            }
        }
    }

    private String[] getViewGroupCheckedTags(ViewGroup vg) {
        List<String> tags = new ArrayList<String>();
        int childCount = vg.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = vg.getChildAt(i);
            if (view instanceof Checkable) {
                Checkable c = (Checkable) view;
                if (c.isChecked()) {
                    tags.add(view.getTag().toString());
                }
            }
        }
        return tags.toArray(new String[0]);
    }

    private void setViewGroupCheckedByTags(ViewGroup vg, String[] tags, boolean checked) {
        if (tags == null) {
            return;
        }
        List<String> list = new ArrayList<String>(Arrays.asList(tags));
        int childCount = vg.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = vg.getChildAt(i);
            if (!(view instanceof Checkable) || view.getTag() == null) continue;
            for (int j = 0; j < list.size(); j++) {
                String tag = list.get(j);
                if (tag == null) {
                    list.remove(j--);
                    continue;
                }
                if (tag.equals(view.getTag())) {
                    ((Checkable) view).setChecked(checked);
                    list.remove(j);
                    break;
                }
            }
        }
    }

    private String unionArrays(String[] arr, String unionStr) {
        if (arr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i != arr.length - 1) {
                sb.append(unionStr);
            }
        }
        return sb.toString();
    }

    private void saveBitmap(String path, Bitmap bitmap) {
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestReleasePlan(SolutionCreateReq bean) {
        BaseCall<MSolutionResp> call = new BaseCall<MSolutionResp>() {
            @Override
            public void call(MSolutionResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    d("III_request", "success msg: " + (resp.getMessage()));
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_SHORT).show();
                    d("III_request", "failed msg: " + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        addCallback(call);
        showWaitingDialog((AsyncTask) null);
        scu.solutionCreateAsync(bean, call);
    }

    private void requestUpdatePlan(SolutionUpdateReq bean) {
        BaseCall<MSolutionResp> call = new BaseCall<MSolutionResp>() {
            @Override
            public void call(MSolutionResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    d("III_request", "success msg: " + (resp.getMessage()));
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
                    d("III_request", "failed msg: " + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        addCallback(call);
        showWaitingDialog((AsyncTask) null);
        scu.solutionUpdateAsync(bean, call);
    }
}
