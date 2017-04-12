package com.dilapp.radar.ui.mine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.view.DividerItemDecoration;
import com.dilapp.radar.view.EmptyView;
import com.dilapp.radar.view.RadarRefreshLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public final class MineViewHelper {

	public static RecyclerView getDefaultRecyclerView(Context context) {
		// final int gdist = context.getResources().getDimensionPixelSize(
		// R.dimen.mine_mine_item_group_distance);
		RecyclerView view = new RecyclerView(context);
		view.setLayoutManager(new LinearLayoutManager(context));
		view.setItemAnimator(new DefaultItemAnimator());
		DividerItemDecoration decor = new DividerItemDecoration(context,
				DividerItemDecoration.VERTICAL_LIST);
		decor.setDivider(getMineDefaultDivider(context));
		view.addItemDecoration(decor);
		view.setHasFixedSize(true);
		return view;
	}

	public static PullToRefreshListView getDefaultListView(Context context) {
		PullToRefreshListView ptrlv =
				new PullToRefreshListView(context,
						PullToRefreshBase.Mode.PULL_FROM_START,
						PullToRefreshBase.AnimationStyle.TWEEN);
		ListView view = ptrlv.getRefreshableView();
		view.setDivider(context.getResources().getDrawable(R.drawable.divider_transparent));
		view.setSelector(new ColorDrawable(Color.TRANSPARENT));
		return ptrlv;
	}

	public static View wrapStateView(Context context, View view) {

		FrameLayout wrap = new FrameLayout(context);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER
		);

		int index = 0;
		ViewGroup vg = (ViewGroup) view.getParent();
		if (vg != null) {
			index = vg.indexOfChild(view);
			vg.removeViewAt(index);
			vg.addView(wrap, index, view.getLayoutParams());
		}

		EmptyView empty = new EmptyView(context);
		empty.setId(R.id.ev_empty);
		empty.setVisibility(View.GONE);

		wrap.addView(empty, params);
		wrap.addView(view);

		return wrap;
	}

	public static SwipeRefreshLayout swipeRefreshWrapView(Context context,
			View view) {
		SwipeRefreshLayout srl = new SwipeRefreshLayout(context);

		// srl.setOnRefreshListener(this);
		srl.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		insertWrapView(srl, view);
		return srl;
	}

	public static RadarRefreshLayout radarRefreshWrapView(Context context,
			View view) {
		RadarRefreshLayout rrl = new RadarRefreshLayout(context);
		rrl.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		insertWrapView(rrl, view);
		return rrl;
	}

	// public static ViewGroup testRefreshWrapView(Context context,
	// View view) {
	// AbPullToRefreshView rrl = new AbPullToRefreshView(context);
	// rrl.setOrientation(LinearLayout.VERTICAL);
	// insertWrapView(rrl, view);
	// return rrl;
	// }

	private static void insertWrapView(ViewGroup wrapView, View child) {
		if (child.getParent() != null) {
			ViewGroup parent = (ViewGroup) child.getParent();
			int index = parent.indexOfChild(child);
			parent.removeViewAt(index);
			wrapView.setLayoutParams(child.getLayoutParams());
			parent.addView(wrapView, index);
		}
		wrapView.addView(child);
	}

	public static List<SpeakTopicMessageBean> genBeans(int size) {
		List<SpeakTopicMessageBean> list = new ArrayList<MineViewHelper.SpeakTopicMessageBean>(
				size);
		for (int i = 0; i < size; i++) {
			list.add(null);
		}
		return list;
	}

	private static Drawable getMineDefaultDivider(Context context) {
		final int dist = context.getResources().getDimensionPixelSize(
				R.dimen.mine_focus_item_distance);
		final Paint p = new Paint();
		p.setColor(Color.TRANSPARENT);
		p.setStyle(Style.FILL);
		Shape s = new Shape() {
			@Override
			public void draw(Canvas canvas, Paint paint) {
				canvas.drawRect(0, 0, getWidth(), getHeight(), p);
			}
		};
		ShapeDrawable sd = new ShapeDrawable(s);
		sd.setIntrinsicHeight(dist);// 设置分割线的高度
		return sd;
	}

	public static class SpeakTopicMessageAdapter extends
			RecyclerView.Adapter<ViewHolder> {

		private Context context;
		private LayoutInflater inflater;
		private List<SpeakTopicMessageBean> list;

		public SpeakTopicMessageAdapter(Context context,
				List<SpeakTopicMessageBean> list) {
			this.context = context;
			this.list = list;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View itemView = inflater.inflate(
					R.layout.item_mine_speak_topic_msg, parent, false);
			return new ViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			SpeakTopicMessageBean bean = list.get(position);
			View itemView = (View) holder.iv_head.getParent();
			if (position == 0) {
				MarginLayoutParams params = (MarginLayoutParams) itemView
						.getLayoutParams();
				params.topMargin = context.getResources()
						.getDimensionPixelSize(
								R.dimen.mine_mine_item_group_distance);
			} else {
				MarginLayoutParams params = (MarginLayoutParams) itemView
						.getLayoutParams();
				params.topMargin = 0;
			}
			if (bean == null) {
				return;
			}
			if (bean.head != null) {
				holder.iv_head.setImageBitmap(bean.head);
			}
			if (bean.text != null) {
				holder.tv_text.setText(bean.text);
			}
			if (bean.time != null) {
				holder.tv_time.setText(bean.time);
			}
			holder.tv_locks.setText(context.getString(
					R.string.speak_what_onlookers, bean.locks));
			holder.tv_reply.setText(context.getString(
					R.string.speak_what_reply, bean.reply));

		}

		@Override
		public int getItemCount() {
			return list != null ? list.size() : 0;
		}

	}

	public static class SpeakTopicMessageBean {
		public Bitmap head;
		public String text;
		public String time;
		public int locks;
		public int reply;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView iv_head;
		TextView tv_text;
		TextView tv_time;
		TextView tv_locks;
		TextView tv_reply;

		public ViewHolder(View itemView) {
			super(itemView);
			iv_head = (ImageView) itemView.findViewById(R.id.iv_head);
			tv_text = (TextView) itemView.findViewById(R.id.tv_text);
			tv_time = (TextView) itemView.findViewById(R.id.tv_time);
			tv_locks = (TextView) itemView.findViewById(R.id.tv_locks);
			tv_reply = (TextView) itemView.findViewById(R.id.tv_reply);
		}

	}
}
