package com.dilapp.radar.chat;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.ble.ImageUtils;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.mine.DialogHeadChange;
import com.dilapp.radar.util.AndroidBugsSolution;
import com.dilapp.radar.util.CommonUtils;
import com.dilapp.radar.util.DialogUtils;
import com.dilapp.radar.util.ImageCache;
import com.dilapp.radar.util.MineInfoUtils;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.PathUtil;

public class ChatActivity2 extends BaseFragmentActivity implements
		OnClickListener {
	private static final String TAG = "ChatActivity2";
	private static final int HANDLER_MESSAGE_REFRESH_LIST = 1;
	private static final int HANDLER_MESSAGE_SEEK_TO = 2;
	private static final int DELETE_CHAT_LOG = 1021;

	private static final int MESSAGE_TYPE_RECV_TXT = 3;
	private static final int MESSAGE_TYPE_SENT_TXT = 4;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 5;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 6;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int HANDLER_MESSAGE_SELECT_LAST = 20;
	public static final String IMAGE_DIR = "chat/image/";
	private LayoutInflater inflater;
	private File cameraFile;
	private Context mContext;
	private TitleView mTitle;
	private EditText et_sendmessage;
	private ListView listView;
	private Button btn_send, btn_image;
	private EMConversation conversation;
	private String toChatUaseName, userId;
	private ChatAdapter adapter = null;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private String othersPortrait, userName;
	private String minePortrait;
	private Activity activity;
	private DialogHeadChange mHeadChangeDialog;
	private NewMessageBroadcastReceiver msgReceiver;
	private SwipeRefreshLayout swipeRefreshLayout;
	EMMessage[] messages = null;
	private boolean isClearConversation = false;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DELETE_CHAT_LOG:
				// 清空和某个user的聊天记录(包括本地)，不删除conversation这个会话对象
				// EMChatManager.getInstance().clearConversation(toChatUaseName);
				// 删除和某个user的整个的聊天记录(包括本地)
				EMChatManager.getInstance().deleteConversation(toChatUaseName);
				if (adapter != null) {
					adapter = null;
				}
				adapter = new ChatAdapter(mContext);
				listView.setAdapter(adapter);
				isClearConversation = true;
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat2);

		// 只有注册了广播才能接收到新消息，目前离线消息，在线消息都是走接收消息的广播（离线消息目前无法监听，在登录以后，接收消息广播会执行一次拿到所有的离线消息）
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);
		EMChat.getInstance().setAppInited();
		init_view();
	}

	private void init_view() {
		mContext = this;
		AndroidBugsSolution.assistActivity(this, null);
		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(mContext, title);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		mTitle.setRightIcon(R.drawable.mm_title_remove, this);
		et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
		listView = (ListView) findViewById(R.id.listView);
		btn_send = (Button) findViewById(R.id.btn_send);
		btn_image = (Button) findViewById(R.id.btn_image);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_layout);
		minePortrait = SharePreCacheHelper.getUserIconUrl(mContext);
		toChatUaseName = getIntent().getStringExtra("EMuserID");
		userId = getIntent().getStringExtra("userId");
		othersPortrait = getIntent().getStringExtra("othersPortrait");
		userName = getIntent().getStringExtra("userName");
		btn_send.setOnClickListener(this);
		btn_image.setOnClickListener(this);
		if (TextUtils.isEmpty(userName))
			mTitle.setCenterText(userId, null);
		else
			mTitle.setCenterText(userName, null);
		Log.i(TAG, "toChatUaseName:" + toChatUaseName + "--userName:"
				+ userName + "--othersPortrait:" + othersPortrait);
		if (!TextUtils.isEmpty(toChatUaseName)) {
			resetUnreadMsgCount(toChatUaseName);
		}
		adapter = new ChatAdapter(mContext);
		listView.setAdapter(adapter);
		// adapter.refreshSelectLast();
		listView.setSelection(listView.getCount() - 1);
		adapter.notifyDataSetChanged();

		mHeadChangeDialog = new DialogHeadChange(this);
		mHeadChangeDialog.setButtonsOnClickListener(this, this, this);

		et_sendmessage.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					btn_send.setVisibility(View.VISIBLE);
					btn_image.setVisibility(View.GONE);
				} else {
					btn_send.setVisibility(View.GONE);
					btn_image.setVisibility(View.VISIBLE);
				}
			}
		});

		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (listView.getFirstVisiblePosition() == 0
								&& !isloading && haveMoreData) {
							List<EMMessage> messages;
							try {
								messages = conversation
										.loadMoreMsgFromDB(adapter.getItem(0)
												.getMsgId(), pagesize);
							} catch (Exception e1) {
								swipeRefreshLayout.setRefreshing(false);
								return;
							}

							if (messages.size() > 0) {
								adapter.notifyDataSetChanged();
								adapter.refreshSeekTo(messages.size() - 1);
								if (messages.size() != pagesize) {
									haveMoreData = false;
								}
							} else {
								haveMoreData = false;
							}
							isloading = false;

						} else {
							Toast.makeText(mContext,
									getString(R.string.no_more_messages),
									Toast.LENGTH_SHORT).show();
						}
						swipeRefreshLayout.setRefreshing(false);
					}
				}, 1000);
			}
		});
	}

	/**
	 * 清空未读消息数
	 * 
	 * @param toChatUaseName
	 */
	private void resetUnreadMsgCount(String toChatUaseName) {
		conversation = EMChatManager.getInstance().getConversation(
				toChatUaseName);
		conversation.resetUnreadMsgCount();
	}

	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 注销广播
			// abortBroadcast();
			// 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
			String msgId = intent.getStringExtra("msgid");
			// 发送方
			String username = intent.getStringExtra("from");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(username);
			// 如果是群聊消息，获取到group id
			// if (message.getChatType() == ChatType.GroupChat) {
			// username = message.getTo();
			// }
			if (!username.equals(username)) {
				// 消息不是发给当前会话，return
				return;
			}
			Log.i(TAG, "收到的message:" + message);
			conversation.addMessage(message);
			listView.setAdapter(adapter);
			listView.setSelection(listView.getCount() - 1);
			adapter.notifyDataSetChanged();
		}
	}

	private class ViewHolder {
		private TextView tv_chatText;
		private ImageView iv_chatImage;
		private ImageView iv_icon;
	}

	private class ChatAdapter extends BaseAdapter {

		public ChatAdapter(Context context) {
			mContext = context;
			activity = (Activity) context;
			inflater = LayoutInflater.from(context);
			conversation = EMChatManager.getInstance().getConversation(
					toChatUaseName);
		}

		Handler handler = new Handler() {
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

			@Override
			public void handleMessage(android.os.Message message) {
				switch (message.what) {
				case HANDLER_MESSAGE_REFRESH_LIST:
					refreshList();
					break;
				case HANDLER_MESSAGE_SELECT_LAST:
					if (activity instanceof ChatActivity2) {
						ListView listView = ((ChatActivity2) activity)
								.getListView();
						if (messages.length > 0) {
							listView.setSelection(messages.length - 1);
						}
					}
					break;
				case HANDLER_MESSAGE_SEEK_TO:
					int position = message.arg1;
					if (activity instanceof ChatActivity2) {
						ListView listView = ((ChatActivity2) activity)
								.getListView();
						listView.setSelection(position);
					}
					break;
				default:
					break;
				}
			}
		};

		@Override
		public int getCount() {
			return conversation.getAllMessages() == null ? 0 : conversation
					.getAllMessages().size();
		}

		// @Override
		// public Object getItem(int position) {
		// return conversation.getAllMessages().get(position);
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
			handler.sendMessage(handler
					.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
			android.os.Message msg = handler
					.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
			msg.arg1 = position;
			handler.sendMessage(msg);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			EMMessage message = getItem(position);
			if (message == null) {
				return -1;
			}
			if (message.getType() == EMMessage.Type.TXT) {
				return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT
						: MESSAGE_TYPE_SENT_TXT;
			}
			if (message.getType() == EMMessage.Type.IMAGE) {
				return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE
						: MESSAGE_TYPE_SENT_IMAGE;
			}
			return -1;
		}

		private View createViewByMessage(EMMessage message, int position) {
			switch (message.getType()) {
			case IMAGE:
				return message.direct == EMMessage.Direct.RECEIVE ? inflater
						.inflate(R.layout.listview_receive_image, null)
						: inflater.inflate(R.layout.listview_send_image, null);
			case TXT:
				return message.direct == EMMessage.Direct.RECEIVE ? inflater
						.inflate(R.layout.listview_item1, null) : inflater
						.inflate(R.layout.listview_item2, null);
			default:
				return message.direct == EMMessage.Direct.RECEIVE ? inflater
						.inflate(R.layout.listview_item1, null) : inflater
						.inflate(R.layout.listview_item2, null);
			}

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final EMMessage message = conversation.getAllMessages().get(
					position);
			final ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = createViewByMessage(message, position);
				if (message.getType() == EMMessage.Type.TXT) {
					viewHolder.tv_chatText = (TextView) convertView
							.findViewById(R.id.tv_chatText);
					viewHolder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
				} else if (message.getType() == EMMessage.Type.IMAGE) {
					viewHolder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					viewHolder.iv_chatImage = (ImageView) convertView
							.findViewById(R.id.iv_chatImage);
				}
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			/**
			 * 设置头像
			 */
			setUserAvatar(message, viewHolder.iv_icon);
			switch (message.getType()) {
			// 根据消息type显示item
			case IMAGE: // 图片
				handleImageMessage(message, viewHolder, position, convertView);
				break;
			case TXT: // 文本
				handleTextMessage(message, viewHolder, position);
				break;
			}
			return convertView;
		}
	}

	@Override
	protected void onDestroy() {
		mContext.unregisterReceiver(msgReceiver);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			if (!TextUtils.isEmpty(toChatUaseName))
				resetUnreadMsgCount(toChatUaseName);
			Intent intent = new Intent();
			intent.putExtra("isClearConversation", isClearConversation);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case TitleView.ID_RIGHT:
			DialogUtils.deleteChatLogDialog(mContext, "是否清空所有聊天记录", mHandler);
			break;
		case R.id.btn_send:
			String messageContent = et_sendmessage.getText().toString();
			if (!TextUtils.isEmpty(toChatUaseName)
					&& !TextUtils.isEmpty(messageContent))
				sendMessage(toChatUaseName, messageContent);
			Log.i(TAG, "toChatUaseName-" + toChatUaseName
					+ "--messageContent--" + messageContent);
			break;
		case R.id.btn_image:
			mHeadChangeDialog.show();
			break;
		case DialogHeadChange.ID_PHOTO:
			mHeadChangeDialog.dismiss();
			selectPicFromLocal();
			break;
		case DialogHeadChange.ID_TAKIN:
			mHeadChangeDialog.dismiss();
			selectPicFromCamera();
			break;
		case DialogHeadChange.ID_CANCEL:
			mHeadChangeDialog.dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (!TextUtils.isEmpty(toChatUaseName))
			resetUnreadMsgCount(toChatUaseName);
		Intent intent = new Intent();
		intent.putExtra("isClearConversation", isClearConversation);
		setResult(RESULT_OK, intent);
		finish();
		super.onBackPressed();
	}

	private void sendMessage(String toChatUaseName, String messageContent) {
		// 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
		conversation = EMChatManager.getInstance().getConversation(
				toChatUaseName);
		// 创建一条文本消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		// 如果是群聊，设置chattype,默认是单聊
		// message.setChatType(ChatType.Chat);
		// 设置消息body
		TextMessageBody txtBody = new TextMessageBody(messageContent);
		message.addBody(txtBody);
		// 设置接收人
		message.setReceipt(toChatUaseName);
		// 把消息加入到此会话对象中
		conversation.addMessage(message);
		// 发送消息
		listView.setAdapter(adapter);
		listView.setSelection(listView.getCount() - 1);
		adapter.notifyDataSetChanged();
		et_sendmessage.setText("");
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onError(int arg0, String arg1) {
				Log.i(TAG, "send message failed," + arg1);
			}

			@Override
			public void onProgress(int arg0, String arg1) {

			}

			@Override
			public void onSuccess() {
				Log.i(TAG, "send message success");
			}
		});
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			String st = getResources().getString(
					R.string.sd_card_does_not_exist);
			Toast.makeText(getApplicationContext(), st, 0).show();
			return;
		}

		cameraFile = new File(PathUtil.getInstance().getImagePath(),
				SharePreCacheHelper.getUserID(mContext)
						+ System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_CAMERA:
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
				break;
			case REQUEST_CODE_LOCAL:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	public ListView getListView() {
		return listView;
	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		String to = toChatUaseName;
		// 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
		conversation = EMChatManager.getInstance().getConversation(
				toChatUaseName);
		// create and add image message in view
		final EMMessage message = EMMessage
				.createSendMessage(EMMessage.Type.IMAGE);
		// 如果是群聊，设置chattype,默认是单聊
		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true);
		message.addBody(body);
		message.setReceipt(toChatUaseName);
		conversation.addMessage(message);
		listView.setAdapter(adapter);
		// adapter.refreshSelectLast();
		listView.setSelection(listView.getCount() - 1);
		adapter.notifyDataSetChanged();
		setResult(RESULT_OK);
	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		String str = getResources().getString(R.string.cant_find_pictures);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			sendPicture(file.getAbsolutePath());
		}

	}

	/**
	 * 图片消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleImageMessage(final EMMessage message,
			final ViewHolder holder, final int position, View convertView) {
		// 接收方向的消息
		if (message.direct == EMMessage.Direct.RECEIVE) {
			// "it is receive msg";
			if (message.status == EMMessage.Status.INPROGRESS) {
				// "!!!! back receive";
				holder.iv_chatImage.setImageResource(R.drawable.default_image);
				showDownloadImageProgress(message, holder);
				// downloadImage(message, holder);
			} else {
				// "!!!! not back receive, show image directly");
				holder.iv_chatImage.setImageResource(R.drawable.default_image);
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (imgBody.getLocalUrl() != null) {
					// String filePath = imgBody.getLocalUrl();
					String remotePath = imgBody.getRemoteUrl();
					String filePath = ImageUtils.getImagePath(remotePath);
					String thumbRemoteUrl = imgBody.getThumbnailUrl();
					if (TextUtils.isEmpty(thumbRemoteUrl)
							&& !TextUtils.isEmpty(remotePath)) {
						thumbRemoteUrl = remotePath;
					}
					String thumbnailPath = ImageUtils
							.getThumbnailImagePath(thumbRemoteUrl);
					showImageView(thumbnailPath, holder.iv_chatImage, filePath,
							imgBody.getRemoteUrl(), message);
				}
			}
			return;
		}

		// 发送的消息
		// process send message
		// send pic, show the pic directly
		ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
		String filePath = imgBody.getLocalUrl();
		if (filePath != null && new File(filePath).exists()) {
			showImageView(ImageUtils.getThumbnailImagePath(filePath),
					holder.iv_chatImage, filePath, null, message);
		} else {
			showImageView(ImageUtils.getThumbnailImagePath(filePath),
					holder.iv_chatImage, filePath, IMAGE_DIR, message);
		}

		switch (message.status) {
		case SUCCESS:
			// Log.i(TAG, "图片发送成功");
			break;
		case FAIL:
			// Log.i(TAG, "图片发送失败");
			break;
		default:
			sendPictureMessage(message, holder);
		}
	}

	private boolean showImageView(final String thumbernailPath,
			final ImageView iv, final String localFullSizePath,
			String remoteDir, final EMMessage message) {
		// String imagename =
		// localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
		// localFullSizePath.length());
		// final String remote = remoteDir != null ? remoteDir+imagename :
		// imagename;
		final String remote = remoteDir;
		Log.d(TAG, "local = " + localFullSizePath + " remote: " + remote);
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "image view on click");
					Intent intent = new Intent(mContext,
							ShowBigImageActivity.class);
					File file = new File(localFullSizePath);
					if (file.exists()) {
						Uri uri = Uri.fromFile(file);
						intent.putExtra("uri", uri);
						Log.d(TAG, "here need to check why download everytime");
					} else {
						// The local full size pic does not exist yet.
						// ShowBigImage needs to download it from the server
						// first
						// intent.putExtra("", message.get);
						ImageMessageBody body = (ImageMessageBody) message
								.getBody();
						intent.putExtra("secret", body.getSecret());
						intent.putExtra("remotepath", remote);
					}
					if (message != null
							&& message.direct == EMMessage.Direct.RECEIVE) {
						try {
							EMChatManager.getInstance().ackMessageRead(
									message.getFrom(), message.getMsgId());
							message.isAcked = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					mContext.startActivity(intent);
				}
			});
			return true;
		} else {
			new LoadImageTask().execute(thumbernailPath, localFullSizePath,
					remote, message.getChatType(), iv, mContext, message);
			return true;
		}
	}

	/*
	 * send message with new sdk
	 */
	private void sendPictureMessage(final EMMessage message,
			final ViewHolder holder) {
		try {
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onSuccess() {
					Log.i(TAG, "send image message successfully");
				}

				@Override
				public void onError(int code, String error) {
				}

				@Override
				public void onProgress(final int progress, String status) {
					Log.i(TAG, "正在发送..." + progress);
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文本消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleTextMessage(EMMessage message, ViewHolder holder,
			final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		holder.tv_chatText.setText(txtBody.getMessage());
		// 设置长按事件监听
	}

	/**
	 * 显示用户头像
	 * 
	 * @param message
	 * @param imageView
	 */
	private void setUserAvatar(final EMMessage message, ImageView imageView) {
		if (message.direct == Direct.SEND) {
			// // 显示自己头像
			MineInfoUtils.setImage(minePortrait, imageView);
		} else {
			MineInfoUtils.setImage(othersPortrait, imageView);
		}
	}

	/*
	 * chat sdk will automatic download thumbnail image for the image message we
	 * need to register callback show the download progress
	 */
	private void showDownloadImageProgress(final EMMessage message,
			final ViewHolder holder) {
		Log.d(TAG, "!!! show download image progress");
		final ImageMessageBody msgbody = (ImageMessageBody) message.getBody();
		msgbody.setDownloadCallback(new EMCallBack() {

			@Override
			public void onSuccess() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (message.getType() == EMMessage.Type.IMAGE) {
							adapter.notifyDataSetChanged();
						}
					}
				});
			}

			@Override
			public void onError(int code, String message) {

			}

			@Override
			public void onProgress(final int progress, String status) {
				if (message.getType() == EMMessage.Type.IMAGE) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Log.i(TAG, "接收图片------" + progress + "%");
						}
					});
				}

			}

		});
	}

}
