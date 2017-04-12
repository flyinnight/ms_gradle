package com.dilapp.radar.chat;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

public class ChatAdapter extends BaseAdapter {
	private static final int HANDLER_MESSAGE_REFRESH_LIST = 1;
	private static final int HANDLER_MESSAGE_SEEK_TO = 2;
	private Context context;
	private EMConversation conversation;
	private EMMessage[] messages = null;
	private Handler handler = new Handler() {
		private void refreshList() {
			// UI线程不能直接使用conversation.getAllMessages()
			// 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
			messages = (EMMessage[]) conversation.getAllMessages().toArray(
					new EMMessage[conversation.getAllMessages().size()]);
			for (int i = 0; i < messages.length; i++) {
				// getMessage will set message as read status
				conversation.getMessage(i);
			}
			notifyDataSetChanged();
		}
	};

	public ChatAdapter(Context context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		return conversation.getAllMessages().size();
	}

	// @Override
	// public Object getItem(int position) {
	// return null;
	// }

	public EMMessage getItem(int position) {
		if (messages != null && position < messages.length) {
			return messages[position];
		}
		return null;
	}

	/**
	 * 刷新页面, 选择Position
	 */
	public void refreshSeekTo(int position) {
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
		msg.arg1 = position;
		handler.sendMessage(msg);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EMMessage message = conversation.getAllMessages().get(position);
		TextMessageBody body = (TextMessageBody) message.getBody();
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (message.direct == EMMessage.Direct.RECEIVE) {
				if (message.getType() == EMMessage.Type.TXT) {
					convertView = LayoutInflater.from(context).inflate(
							R.layout.listview_item1, null);
					viewHolder.tv_text = (TextView) convertView
							.findViewById(R.id.textView1);
				}
			} else {
				if (message.getType() == EMMessage.Type.TXT) {
					convertView = LayoutInflater.from(context).inflate(
							R.layout.listview_item2, null);
					viewHolder.tv_bodyText = (TextView) convertView
							.findViewById(R.id.textView1);
				}
			}
		} else
			viewHolder = (ViewHolder) convertView.getTag();

		if (conversation.getAllMessages().size() == 0)
			return convertView;
		if (message.direct == EMMessage.Direct.RECEIVE) {
			viewHolder.tv_text.setText(body.getMessage());
		} else
			viewHolder.tv_bodyText.setText(body.getMessage());
		return convertView;
	}

	private class ViewHolder {
		private TextView tv_text;
		private TextView tv_bodyText;
	}
}
