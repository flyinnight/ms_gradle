package com.dilapp.radar.ui.topic;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.textbuilder.utils.JsonUtils;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.util.VerticalImageSpan;
import com.dilapp.radar.util.ViewUtils;
import com.dilapp.radar.view.LinearLayoutForListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;


public class PostAdapter extends LinearLayoutForListView.LinearLayoutForListViewAdapter
        implements AdapterView.OnItemClickListener {

    public static final String PLAN_SOURCES = "CarePlan";

    static void d(String msg) {
        if (false) {
            com.dilapp.radar.textbuilder.utils.L.d("III_PostAdapter", msg);
        }
    }

    List<MPostResp> list;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_image_loading)
                    // 正在加载的图片
            .showImageForEmptyUri(R.drawable.img_image_loading)
                    // URL请求失败
            .showImageOnFail(R.drawable.img_image_loading)
                    // 图片加载失败
            .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(200))
            .imageScaleType(ImageScaleType.EXACTLY).build();
    private Context mContext;
    private LayoutInflater mInflater;
    private boolean mItemViewClickable = true;
    private PostAdapterListener mListener;
    private View.OnClickListener mChildClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag(R.id.tv_tag) != null && mListener != null) {
                mListener.onChildViewClick(v, v.getTag(),
                        Integer.parseInt(v.getTag(R.id.tv_tag).toString()));
            }
        }
    };

    public PostAdapter(Context context, LayoutInflater inflater) {
        this.mContext = context;
        this.mInflater = inflater;
        this.list = new ArrayList<GetPostList.MPostResp>(0);
    }

    public PostAdapter(Context context, LayoutInflater inflater, int size) {
        this(context, inflater, new ArrayList<GetPostList.MPostResp>(size));
        for (int i = 0; i < size; i++) {
            list.add(null);
        }
    }

    public PostAdapter(Context context, LayoutInflater inflater, List<GetPostList.MPostResp> list) {
        this(context, inflater);
        this.list = list;
    }

    public List<MPostResp> getList() {
        return this.list;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public GetPostList.MPostResp getItem(int position) {
        return list != null && position < list.size() ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        GetPostList.MPostResp item = getItem(position);
        return item != null ? item.getId() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_post_list, parent, false);
            convertView.setTag(holder = new ViewHolder(convertView));
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GetPostList.MPostResp item = getItem(position);
        if (item == null) {
            return convertView;
        }
        String nickname = item.getUserName() == null || "".equals(item.getUserName().trim()) ? item.getUserId() : item.getUserName();
        String gender = Constants.getGenderString(mContext, item.isGender());
        String level = "lv " + item.getLevel();
        String topic = (item.getTopicTitle() != null && !"".equals(item.getTopicTitle().trim()) ?
                mContext.getResources().getString(R.string.topic_prefix, item.getTopicTitle()) : "");
        String title = (item.isSelectedToSolution() ? PLAN_SOURCES + " " : "") +
                topic + ("".equals(topic) ? "" : "| ") +
                (item.getPostTitle() != null ? item.getPostTitle().trim() : "unknown title");
        String datetime = TopicHelper.getTopicDateString(mContext, System.currentTimeMillis(), item.getUpdateTime());
        String onlookers = item.getPostViewCount() + "";
        String praise = item.getLike() + "";
        String reply = item.getTotalFollows() + "";
        String[] thumbs = item.getThumbURL() != null && item.getThumbURL().size() > 0 ? item.getThumbURL().toArray(new String[0]) : null;
        d("size " + (item.getThumbURL() != null ? item.getThumbURL().size() : "null") + " " + item.isSelectedToSolution());

        convertView.setClickable(mItemViewClickable);
        holder.tv_nickname.setText(nickname);
        holder.tv_gender.setText(gender);
        holder.tv_level.setText(level);
        Spannable s = new SpannableStringBuilder(title);
        int topicStart = 0;
        if (item.isSelectedToSolution()) {
            topicStart = PLAN_SOURCES.length();
            s.setSpan(new VerticalImageSpan(mContext, R.drawable.img_skin_plan_selected, ImageSpan.ALIGN_BOTTOM), 0, PLAN_SOURCES.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        s.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.test_primary)), topicStart, topicStart + topic.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tv_title.setText(s);
        holder.tv_datetime.setText(datetime);
        holder.tv_onlookers.setText(onlookers);
        holder.tv_like.setText(praise);
        holder.tv_reply.setText(reply);
        ImageLoader.getInstance()
                .displayImage(TopicHelper.wrappeImagePath(item.getUserHeadIcon()),
                        holder.iv_header, options);
        if (item.getLocalPostId() != 0 &&
                item.getSendState() != PostReleaseCallBack.POST_RELEASE_SENDSUCCESS) {
            holder.tv_datetime.setVisibility(View.GONE);
            switch (item.getSendState()) {
                case PostReleaseCallBack.POST_RELEASE_SENDFAILED: {
                    // 发送失败的状态
                    holder.tv_delete.setVisibility(View.VISIBLE);
                    holder.tv_delete.setTag(item);
                    holder.tv_delete.setTag(R.id.tv_tag, position);
                    holder.tv_delete.setOnClickListener(mChildClickListener);

                    holder.tv_sending.setTag(item);
                    holder.tv_sending.setTag(R.id.tv_tag, position);
                    holder.tv_sending.setOnClickListener(mChildClickListener);
                    holder.tv_sending.setText(R.string.topic_resend);
                    // holder.tv_sending.getCompoundDrawables()[0] =  mContext.getResources().getDrawable(R.drawable.ico_error_red);
                    holder.tv_sending.setCompoundDrawablesWithIntrinsicBounds(
                            mContext.getResources().getDrawable(R.drawable.ico_error_red),
                            null, null, null);
                    // holder.tv_sending.invalidate();

                    holder.tv_sending.setVisibility(View.VISIBLE);
                    break;
                }
                case PostReleaseCallBack.POST_RELEASE_SENDING: {

                    holder.tv_delete.setClickable(false);
                    holder.tv_delete.setVisibility(View.GONE);
                    holder.tv_sending.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    holder.tv_sending.setText(R.string.topic_sending);
                    holder.tv_sending.setClickable(false);
                    // 发送中
                    holder.tv_sending.setVisibility(View.VISIBLE);
                    break;
                }
            }
        } else {
            holder.tv_delete.setVisibility(View.GONE);
            holder.tv_sending.setVisibility(View.GONE);
            holder.tv_datetime.setVisibility(View.VISIBLE);
        }
        Log.i("III_data", position + " = " + item.getUserHeadIcon());
        if (thumbs != null && thumbs.length != 0) {
            // 这个不是本地帖子
            boolean isThumbsZero = true;
            for (int i = 0; i < holder.iv_thumbs.length; i++) {
                if (i < thumbs.length && thumbs[i] != null && !"".equals(thumbs[i].trim())) {
                    isThumbsZero = false;
                    // ThumbnailUtils.
                    holder.iv_thumbs[i].setVisibility(View.VISIBLE);
                    if (TopicHelper.isImagePath(thumbs[i]) != TopicHelper.PATH_LOCAL_SDCARD) {
                        // 网络贴的图片设置方式
                        ImageLoader.getInstance().displayImage(TopicHelper.wrappeImagePath(thumbs[i]), holder.iv_thumbs[i], options);
                    } else {
                        Bitmap b = ImageLoader.getInstance().loadImageSync("file://" + thumbs[i]);
                        if (b != null) {
                            ViewUtils.measureView(holder.iv_thumbs[i]);
                            holder.iv_thumbs[i].setImageBitmap(
                                    ThumbnailUtils.extractThumbnail(b,
                                            holder.iv_thumbs[i].getMeasuredWidth(),
                                            holder.iv_thumbs[i].getMeasuredHeight()));
                        }
                    }
                    Log.i("III_data", position + ", " + i + " = " + thumbs[i]);
                } else {
                    holder.iv_thumbs[i].setVisibility(View.INVISIBLE);
                }
            }
            holder.vg_thumbs.setVisibility(isThumbsZero ? View.GONE : View.VISIBLE);
        } else {
            holder.vg_thumbs.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public void addItem(Object item) {
        list.add((GetPostList.MPostResp) item);
        super.addItem(item);
    }

    public boolean isItemViewClickable() {
        return mItemViewClickable;
    }

    public void setItemViewClickable(boolean mItemViewClickable) {
        this.mItemViewClickable = mItemViewClickable;
    }

    public PostAdapterListener getPostAdapterListener() {
        return mListener;
    }

    public void setPostAdapterListener(PostAdapterListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position--;
        MPostResp item = getItem(position);
        d("position " + position + ", " + JsonUtils.toJson(item));
        Intent intent = new Intent(mContext, ActivityPostDetail.class);
        intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
        mContext.startActivity(intent);
    }


    public static class ViewHolder {
        ImageView iv_header;// 头像
        TextView tv_nickname;// 昵称
        TextView tv_gender;// 性别
        TextView tv_level;// 等级
        TextView tv_title;// 标题
        ViewGroup vg_thumbs;
        ImageView iv_thumb_00;// 略缩图
        ImageView iv_thumb_01;
        ImageView iv_thumb_02;
        ImageView iv_thumb_03;
        TextView tv_datetime;// 时间
        TextView tv_sending;// 发送中
        TextView tv_delete;// 删除
        TextView tv_onlookers;// 围观
        TextView tv_like;// 喜欢
        TextView tv_reply;// 回复
        ImageView[] iv_thumbs;

        public ViewHolder(View parent) {
            // (\w+) (\w+);
            // $2 = \($1\) parent\.findViewById\(R\.id\.$2\);
            iv_header = (ImageView) parent.findViewById(R.id.iv_header);// 头像
            tv_nickname = (TextView) parent.findViewById(R.id.tv_nickname);// 昵称
            tv_gender = (TextView) parent.findViewById(R.id.tv_gender);// 性别
            tv_level = (TextView) parent.findViewById(R.id.tv_level);// 等级
            tv_title = (TextView) parent.findViewById(R.id.tv_title);// 标题
            vg_thumbs = (ViewGroup) parent.findViewById(R.id.vg_thumbs);
            iv_thumb_00 = (ImageView) parent.findViewById(R.id.iv_thumb_00);// 略缩图
            iv_thumb_01 = (ImageView) parent.findViewById(R.id.iv_thumb_01);
            iv_thumb_02 = (ImageView) parent.findViewById(R.id.iv_thumb_02);
            iv_thumb_03 = (ImageView) parent.findViewById(R.id.iv_thumb_03);
            tv_datetime = (TextView) parent.findViewById(R.id.tv_datetime);// 时间
            tv_sending = (TextView) parent.findViewById(R.id.tv_sending);//
            tv_delete = (TextView) parent.findViewById(R.id.tv_delete);//
            tv_onlookers = (TextView) parent.findViewById(R.id.tv_onlookers);// 围观
            tv_like = (TextView) parent.findViewById(R.id.tv_like);// 喜欢
            tv_reply = (TextView) parent.findViewById(R.id.tv_reply);// 回复
            iv_thumbs = new ImageView[]{iv_thumb_00, iv_thumb_01, iv_thumb_02, iv_thumb_03};
        }
    }

    public interface PostAdapterListener {

        void onChildViewClick(View v, Object item, int positon);
    }
}