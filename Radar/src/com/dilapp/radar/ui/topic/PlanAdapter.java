package com.dilapp.radar.ui.topic;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.domain.SolutionCreateUpdate;
import com.dilapp.radar.domain.SolutionDetailData;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.util.MathUtils;
import com.dilapp.radar.util.ViewUtils;
import com.dilapp.radar.ui.topic.PostAdapter.PostAdapterListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;

public class PlanAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<MSolutionResp> list;
    private PostAdapterListener mListener;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            // 正在加载的图片
            .showImageOnLoading(R.drawable.img_bbs_default)
                    // URL请求失败
            .showImageForEmptyUri(R.drawable.img_bbs_default)
                    // 图片加载失败
            .showImageOnFail(R.drawable.img_bbs_default)
            .displayer(new FadeInBitmapDisplayer(200))
            .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    private View.OnClickListener mChildClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag(R.id.tv_tag) != null && mListener != null) {
                mListener.onChildViewClick(v, v.getTag(),
                        Integer.parseInt(v.getTag(R.id.tv_tag).toString()));
            }
        }
    };

    public PlanAdapter(Context context, LayoutInflater inflater) {
        this(context, inflater, new ArrayList<MSolutionResp>());
    }

    public PlanAdapter(Context context, LayoutInflater inflater, List<MSolutionResp> list) {
        this.context = context;
        this.inflater = inflater;
        this.list = list;
    }

    public List<MSolutionResp> getList() {
        return list;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public MSolutionResp getItem(int position) {
        return list != null && position < list.size() ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        SolutionDetailData.MSolutionResp item = getItem(position);
        return item != null ? item.getSolutionId() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlanViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_plan_list, parent, false);
            convertView.setTag(holder = new PlanViewHolder(convertView));
            // 发现一个问题，RelativeLayout下的View使用下面这个方法测量不出宽高
            ViewUtils.measureView(holder.iv_thumb);
        } else {
            holder = (PlanViewHolder) convertView.getTag();
        }
        MSolutionResp item = getItem(position);
        if (item == null) {
            return convertView;
        }

        if (item.getSolutionId() == 0 && item.getLocalSolutionId() != 0) {
            String coverUrl = item.getCoverImgUrl();
            Bitmap coverBmp = ImageLoader.getInstance().loadImageSync("file://" + coverUrl);
            d("III_adapter", "url " + coverUrl + ", " + holder.iv_thumb.getMeasuredWidth() +
                    "x" + holder.iv_thumb.getMeasuredHeight() + ", " + coverBmp);
            int w = holder.iv_thumb.getMeasuredWidth(), h = holder.iv_thumb.getMeasuredHeight();
            if (w == 0 || h == 0) {
                w = h = context.getResources().getDimensionPixelSize(R.dimen.topic_plan_item_thumb_round);
            }
            holder.iv_thumb.setImageBitmap(
                    ThumbnailUtils.extractThumbnail(coverBmp, w, h));
            if (coverBmp != null) {
                coverBmp.recycle();
            }

            switch (item.getSendState()) {
                case SolutionCreateUpdate.SOLUTION_RELEASE_FAILED: {
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
                            context.getResources().getDrawable(R.drawable.ico_error_red),
                            null, null, null);
                    // holder.tv_sending.invalidate();

                    holder.tv_sending.setVisibility(View.VISIBLE);
                    break;
                }
                case SolutionCreateUpdate.SOLUTION_RELEASE_SENDING: {

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
            setScoreVisibility(holder, View.GONE);
            // setSendingVisibility(holder, View.VISIBLE);
        } else {
            setSendingVisibility(holder, View.GONE);
            setScoreVisibility(holder, View.VISIBLE);
            ImageLoader.getInstance().displayImage(
                    TopicHelper.wrappeImagePath(item.getCoverThumbImgUrl()),
                    holder.iv_thumb, options);
        }
        String title = item.getTitle() != null ? item.getTitle() : "unknown";
        String nickname = item.getNickName() != null ? item.getNickName() : item.getUserId();
        String date = TopicHelper.getTopicDateString(context, System.currentTimeMillis(), item.getCreateTime());
        String score = MathUtils.round(item.getScore(), 1) + "";
        String used = context.getString(R.string.plan_used, item.getUsedCount() + "");
        String coll = context.getString(R.string.plan_coll, item.getStoreUpCount() + "");

        holder.tv_title.setText(title);
        holder.tv_nickname.setText(nickname);
        holder.tv_date.setText(date);
        holder.tv_score.setText(score);
        holder.tv_used.setText(used);
        holder.tv_coll.setText(coll);
        return convertView;
    }

    public void setPostAdapterListener(PostAdapterListener listener) {
        this.mListener = listener;
    }

    private void setScoreVisibility(PlanViewHolder vh, int visibility) {
        if (vh.tv_label_01.getVisibility() != visibility)
            vh.tv_label_01.setVisibility(visibility);
        if (vh.tv_score.getVisibility() != visibility)
            vh.tv_score.setVisibility(visibility);
        if (vh.tv_label_02.getVisibility() != visibility)
            vh.tv_label_02.setVisibility(visibility);
    }

    private void setSendingVisibility(PlanViewHolder vh, int visibility) {
        if (vh.tv_sending.getVisibility() != visibility)
            vh.tv_sending.setVisibility(visibility);
        if (vh.tv_delete.getVisibility() != visibility)
            vh.tv_delete.setVisibility(visibility);
    }


    class PlanViewHolder {

        private ImageView iv_thumb;
        private TextView tv_title;
        private TextView tv_nickname;
        private TextView tv_date;
        private TextView tv_score;
        private TextView tv_used;
        private TextView tv_coll;
        private TextView tv_label_01;
        private TextView tv_label_02;

        private TextView tv_sending;
        private TextView tv_delete;

        public PlanViewHolder(View itemView) {
            iv_thumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_nickname = (TextView) itemView.findViewById(R.id.tv_nickname);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_score = (TextView) itemView.findViewById(R.id.tv_score);
            tv_used = (TextView) itemView.findViewById(R.id.tv_used);
            tv_coll = (TextView) itemView.findViewById(R.id.tv_coll);
            tv_label_01 = (TextView) itemView.findViewById(R.id.tv_label_01);
            tv_label_02 = (TextView) itemView.findViewById(R.id.tv_label_02);

            tv_sending = (TextView) itemView.findViewById(R.id.tv_sending);
            tv_delete = (TextView) itemView.findViewById(R.id.tv_delete);
        }
    }
}