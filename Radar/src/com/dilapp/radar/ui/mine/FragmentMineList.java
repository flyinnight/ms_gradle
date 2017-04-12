package com.dilapp.radar.ui.mine;

import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseFragment;

import static com.dilapp.radar.textbuilder.utils.L.d;

public class FragmentMineList extends BaseFragment {

	private List<MineGroup> mGroups;
	private LinearLayout mContainer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mContainer == null) {
			mContainer = new LinearLayout(mContext);
			mContainer.setOrientation(LinearLayout.VERTICAL);
			buildView(mGroups, mContainer);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setContentView(mContainer);
		// setCacheView(true);
		// if (getContentView() == null || !isCacheView()) {
		// long st = System.currentTimeMillis();
		// long et = System.currentTimeMillis();
		// Log.i(getClass().getSimpleName(), "gen time " + (et - st));
		// }
		return getContentView();
	}

	@Override
	public void onDestroyView() {
		if (mContainer != null && !isCacheView()) {
			((ViewGroup) mContainer.getParent()).removeView(mContainer);
		}
		super.onDestroyView();
	}

	public List<MineGroup> getGroups() {
		return mGroups;
	}

	/**
	 * 你是猪吗？这还用解释！
	 * @param groups
	 */
	public void setGroups(List<MineGroup> groups) {
		this.mGroups = groups;
		if (mContainer != null) {
			notifyDataSetChanged();
		}
	}

	/**
	 * 内容有变？好我马上更新
	 */
	public void notifyDataSetChanged() {
		mContainer.removeAllViews();
		buildView(mGroups, mContainer);
	}

	/**
	 * 生成多个组
	 * @param groups 组List
	 * @param container 容器
	 */
	private void buildView(List<MineGroup> groups, ViewGroup container) {
		if (groups == null) {
			return;
		}
		for (int i = 0; i < groups.size(); i++) {
			MineGroup group = groups.get(i);
			if (group == null) {
				continue;
			}
			ViewGroup groupView = buildGroup(group);
			container.addView(groupView);
		}
	}

	/**
	 * 生成多个组
	 * @param groups 组List
	 * @return 多组的容器
	 */
	private LinearLayout buildView(List<MineGroup> groups) {
		LinearLayout content = new LinearLayout(mContext);
		content.setOrientation(LinearLayout.VERTICAL);
		buildView(groups, content);
		return content;
	}

	/**
	 * 生成一组项
	 * @param group 组的内容
	 * @return 组View
	 */
	private ViewGroup buildGroup(MineGroup group) {
		if (group == null) {
			return null;
		}
		LinearLayout ll = new LinearLayout(mContext);
		// 获取几个布局需要的值
		// 分割线的高度、组与组之间的距离、内部分割线左边的距离
		Resources res = mContext.getResources();
		final int lineSize = res
				.getDimensionPixelSize(R.dimen.default_line_size);
		final int groupDistance = res
				.getDimensionPixelSize(R.dimen.topic_send_comment_toolbar_padding);
		final int contentDistance = res
				.getDimensionPixelSize(R.dimen.mine_mine_item_padding_l_r);
		// 子元素默认的布局
		final LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 一定要记得是垂直的，默认是水平的
		ll.setOrientation(LinearLayout.VERTICAL);
		// 设置组之间的距离
		ll.setPadding(0, groupDistance, 0, 0);
		List<MineItem> items = group.getItems();
		// 是否需要头部分割线
		if (group.isEnableHeadDivider()) {
			ll.addView(buildDivider(), LayoutParams.MATCH_PARENT, lineSize);
		}
		for (int i = 0; i < items.size(); i++) {
			MineItem item = items.get(i);
			if (item == null) {
				continue;
			}
			// 获取子项
			ViewGroup itemView = buildItem(item);
			ll.addView(itemView, params);
			// 是否需要内部分割线
			if (group.isEnableDivider() && i != items.size() - 1) {
				// 这里写ViewGroup.MarginLayoutParams没有用
				// 它只认识LinearLayout.LayoutParams
				LinearLayout.LayoutParams marginParams = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, lineSize);
				marginParams.leftMargin = contentDistance;
				View divider = buildDivider();
				ll.addView(divider, marginParams);
			}
		}
		// 是否需要底部分割线
		if (group.isEnableFootDivider()) {
			ll.addView(buildDivider(), LayoutParams.MATCH_PARENT, lineSize);
		}
		return ll;
	}

	/**
	 * 生成一项View
	 * @param item 内容
	 * @return View
	 */
	private ViewGroup buildItem(MineItem item) {
		if (item == null) {
			return null;
		}
		ViewGroup itemView = (ViewGroup) mInflater.inflate(R.layout.item_mine,
				null);
		if (item.getId() > 0) {
			itemView.setId(item.getId());
		}
		if (item.getIconRes() != 0) {
			ImageView icon = (ImageView) itemView
					.findViewById(android.R.id.icon);
			icon.setImageResource(item.getIconRes());
			icon.setVisibility(View.VISIBLE);
		}
		if (item.getTextRes() != 0) {
			TextView text = (TextView) itemView
					.findViewById(android.R.id.text1);
			text.setText(item.getTextRes());
			text.setVisibility(View.VISIBLE);
		}
		if (item.isDot()) {
			itemView.findViewById(android.R.id.toggle).setVisibility(
					View.VISIBLE);
		} else {
			itemView.findViewById(android.R.id.toggle).setVisibility(View.GONE);
		}
		if (item.isNext()) {
			itemView.findViewById(android.R.id.icon1).setVisibility(
					View.VISIBLE);
		} else {
			itemView.findViewById(android.R.id.icon1).setVisibility(View.INVISIBLE);
		}
		if (item.getCustom() != null) {
			View custom = itemView.findViewById(android.R.id.custom);
			((ViewGroup) custom).addView(item.getCustom());
			custom.setVisibility(View.VISIBLE);
		}
		if (item.getOnClickListener() != null) {
			itemView.setOnClickListener(item.getOnClickListener());
		}
		return itemView;
	}

	// 生成一条线
	private View buildDivider() {
		View divider = new View(mContext);
		divider.setBackgroundColor(mContext.getResources().getColor(
				R.color.default_line_color));
		return divider;
	}

	public static class MineGroup {
		private boolean enableDivider;
		private boolean enableHeadDivider;
		private boolean enableFootDivider;
		private List<MineItem> items;

		public MineGroup() {
		}

		/**
		 * 
		 * @param enableDivider 是否启用内部线
		 * @param enableHeadDivider 头部线
		 * @param enableFootDivider 底部线
		 * @param items 不解释
		 */
		public MineGroup(boolean enableDivider, boolean enableHeadDivider,
				boolean enableFootDivider, List<MineItem> items) {
			this.enableDivider = enableDivider;
			this.enableHeadDivider = enableHeadDivider;
			this.enableFootDivider = enableFootDivider;
			this.items = items;
		}

		public boolean isEnableDivider() {
			return enableDivider;
		}

		public void setEnableDivider(boolean enableDivider) {
			this.enableDivider = enableDivider;
		}

		public boolean isEnableHeadDivider() {
			return enableHeadDivider;
		}

		public void setEnableHeadDivider(boolean enableHeadDivider) {
			this.enableHeadDivider = enableHeadDivider;
		}

		public boolean isEnableFootDivider() {
			return enableFootDivider;
		}

		public void setEnableFootDivider(boolean enableFootDivider) {
			this.enableFootDivider = enableFootDivider;
		}

		public List<MineItem> getItems() {
			return items;
		}

		public void setItems(List<MineItem> items) {
			this.items = items;
		}

	}

	public static class MineItem {

		private int id;// Item的标识
		private int iconRes;// 图标
		private int textRes;// 文字
		private boolean dot;// 是否显示红点
		private boolean next;// 是否还有下一级
		private View custom;// 自定义的内容
		private OnClickListener onClickListener;// 点击事件

		public MineItem() {
		}

		/**
		 * 
		 * @param id 点击事件的标识
		 * @param iconRes 图表 0 代表无图标
		 * @param textRes 文字 0 代表无文字
		 * @param dot 红点
		 * @param next 是否有箭头
		 * @param onClickListener 事件
		 */
		public MineItem(int id, int iconRes, int textRes, boolean dot,
				boolean next, OnClickListener onClickListener) {
			this.id = id;
			this.iconRes = iconRes;
			this.textRes = textRes;
			this.dot = dot;
			this.next = next;
			this.onClickListener = onClickListener;
		}

		/**
		 * 
		 * @param id 点击事件的标识
		 * @param iconRes 图表 0 代表无图标
		 * @param textRes 文字 0 代表无文字
		 * @param dot 红点
		 * @param next 是否有箭头
		 * @param custom 自定义的内容区域, null就没有
		 * @param onClickListener 事件
		 */
		public MineItem(int id, int iconRes, int textRes, boolean dot,
				boolean next, View custom, OnClickListener onClickListener) {
			this(id, iconRes, textRes, dot, next, onClickListener);
			this.custom = custom;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getIconRes() {
			return iconRes;
		}

		public void setIconRes(int iconRes) {
			this.iconRes = iconRes;
		}

		public int getTextRes() {
			return textRes;
		}

		public void setTextRes(int textRes) {
			this.textRes = textRes;
		}

		public boolean isDot() {
			return dot;
		}

		public void setDot(boolean dot) {
			this.dot = dot;
		}

		public boolean isNext() {
			return next;
		}

		public void setNext(boolean next) {
			this.next = next;
		}

		public View getCustom() {
			return custom;
		}

		public void setCustom(View custom) {
			this.custom = custom;
		}

		public OnClickListener getOnClickListener() {
			return onClickListener;
		}

		public void setOnClickListener(OnClickListener onClickListener) {
			this.onClickListener = onClickListener;
		}

	}
}
