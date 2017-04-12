package com.dilapp.radar.ui.mine;

import java.util.List;

import com.dilapp.radar.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class InfoEditAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> mNodeList;
	private int checkPosition = -1;
	private int checkGender;

	// HashMap<String,Boolean> states = new
	// HashMap<String,Boolean>();//用于记录每个RadioButton的状态

	public InfoEditAdapter(Context context, List<String> list) {
		this.mContext = context;
		this.mNodeList = list;
	}

	public void setData(int checkGender) {
		this.checkGender = checkGender;
	}

	@Override
	public int getCount() {
		return mNodeList.size();
	}

	@Override
	public Object getItem(int position) {
		return mNodeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.mine_edit_item, null);
			holder = new ViewHolder();
			holder.background = (LinearLayout) convertView
					.findViewById(R.id.edit_item_layout);
			holder.nodeName = (TextView) convertView
					.findViewById(R.id.edit_item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final RadioButton radio = (RadioButton) convertView
				.findViewById(R.id.check_node);
		holder.rdBtn = radio;
		holder.rdBtn.setClickable(false);
		holder.rdBtn.setFocusable(false);

		holder.nodeName.setText(mNodeList.get(position));

		return convertView;
	}

	static class ViewHolder {
		LinearLayout background;
		TextView nodeName;
		RadioButton rdBtn;
	}
}
