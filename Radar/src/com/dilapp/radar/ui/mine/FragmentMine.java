package com.dilapp.radar.ui.mine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.Register.RegRadarReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.UpdateGetUser;
import com.dilapp.radar.domain.UpdateGetUser.GetUserReq;
import com.dilapp.radar.domain.UpdateGetUser.GetUserResp;
import com.dilapp.radar.domain.UpdateGetUser.UpdateUserResp;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.WelcomeLogin;
import com.dilapp.radar.ui.mine.FragmentMineList.MineGroup;
import com.dilapp.radar.ui.mine.FragmentMineList.MineItem;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.MineInfoUtils;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

public class FragmentMine extends BaseFragment implements OnClickListener {

	private static final int REQUEST_CODE_PICK_IMAGE = 111;
	private static final int REQUEST_CODE_CAPTURE_CAMEIA = 112;
	private static final int REQUEST_CODE_PICK_RESULT = 113;
	private final static int REQ_PERSONAL_INFO = 10;
	private final static int REQ_SETTING = 50;
	private final static int REQ_MESSAGE = 4;
	private FragmentMineList mFragmentMineList;
	private DialogHeadChange mHeadChangeDialog;
	private TitleView mTitle;
	private ImageView iv_head;

	private TextView mNickName;
	private TextView mGender;
	private TextView mLevel;
	private TextView mFollowCnt;
	private TextView mFollowedCnt;

	private UpdateGetUser mUpdateGetUser;
	private static final String IMAGE_FILE_LOCATION = PathUtils.RADAR_IMAGE_CACEH
			+ "radar_temp.jpg";
	private static final Uri imageUri = Uri.fromFile(new File(
			IMAGE_FILE_LOCATION));
	private static final String IMAGE_HEAD_CACHE = PathUtils.RADAR_IMAGE_CACEH
			+ "radar_head_cache.jpg";

	private BaseCall<UpdateUserResp> mPortraitCallback = new BaseCall<UpdateUserResp>() {

		@Override
		public void call(UpdateUserResp resp) {
			// TODO Auto-generated method stub
			if (resp.isRequestSuccess()) {
				String portrainUrl = resp.getPortraitURL();
				Slog.d("update Portraint success and update userinfo : "
						+ portrainUrl);
				SharePreCacheHelper.setUserIconUrl(getActivity(), portrainUrl);
				RegRadarReq mRadarReq = new RegRadarReq();
				mRadarReq.setPortrait(portrainUrl);
				MineInfoUtils.reloadDefalutUserInfo(getActivity(), mRadarReq);
				mUpdateGetUser.updateUserAsync(mRadarReq, mUserUpdateCallback);
			} else {
				Slog.e("Failed update Portrait!!!!!!!  " + resp.getMessage());
			}
			setHeadImage();
		}

	};
	private BaseCall<UpdateUserResp> mUserUpdateCallback = new BaseCall<UpdateUserResp>() {

		@Override
		public void call(UpdateUserResp resp) {
			// TODO Auto-generated method stub
			if (resp.isRequestSuccess()) {
				Slog.d("update userinfo success");
				GetUserReq mUserReq = new GetUserReq();
				mUserReq.setUserId(SharePreCacheHelper.getUserID(getActivity()));
				mUpdateGetUser.getUserAsync(mUserReq, mUserInfoResp);
				// RegRadarReq mRadarReq = new RegRadarReq();
			} else {
				Slog.e("Failed update userinfo!!!!!!! " + resp.getMessage());
			}
		}

	};
	private BaseCall<GetUserResp> mUserInfoResp = new BaseCall<GetUserResp>() {

		@Override
		public void call(GetUserResp resp) {
			// TODO Auto-generated method stub
			if (resp == null) {
				Slog.e("Error GetUserResp == NULL!!");
			} else {
				MineInfoUtils.saveUserInfo(getActivity(), resp);
				handleInfoChanged();
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFragmentMineList = new FragmentMineList();
		/*
		 * Toast.makeText(mContext,
		 * "Part of the function has not been developed",
		 * Toast.LENGTH_LONG).show();
		 */
		mUpdateGetUser = ReqFactory.buildInterface(getActivity(),
				UpdateGetUser.class);

		addCallback(mPortraitCallback);
		addCallback(mUserInfoResp);
		addCallback(mUserUpdateCallback);

		// GetUserReq mUserReq = new GetUserReq();
		// mUserReq.setUserId(SharePreCacheHelper.getUserID(getApplicationContext()));
		// mUpdateGetUser.getUserAsync(mUserReq, mUserInfoResp);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setContentView(R.layout.fragment_mine);
		setCacheView(true);
		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(mContext, title);
		mTitle.setCenterText(R.string.me, null);
		iv_head = findViewById(R.id.iv_head);
		mNickName = findViewById(R.id.tv_nickname);
		mGender = findViewById(R.id.tv_gender);
		mLevel = findViewById(R.id.tv_level);
		mFollowCnt = findViewById(R.id.tv_focus_num);
		mFollowedCnt = findViewById(R.id.tv_fans_num);
		// findViewById(R.id.vg_focus).setOnClickListener(this);
		findViewById(R.id.layout_focus).setOnClickListener(this);
		findViewById(R.id.layout_fans).setOnClickListener(this);
		iv_head.setOnClickListener(this);

		FragmentManager cfm = getChildFragmentManager();
		if (null == cfm.findFragmentByTag("mineList")
				|| !mFragmentMineList.isInLayout()) {
			mFragmentMineList.setGroups(genGroups());
			getChildFragmentManager()
					.beginTransaction()
					.replace(R.id.fragment_container, mFragmentMineList,
							"mineList").commit();
		}
		mHeadChangeDialog = new DialogHeadChange(getActivity());
		mHeadChangeDialog.setButtonsOnClickListener(this, this, this);
		setMessageImage();
		return getContentView();
	}

	private void setMessageImage() {
		int allUnreadMessage = 0;
		int unreadMessage = 0;
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		for (EMConversation conversation : conversations.values()) {
			unreadMessage = conversation.getUnreadMsgCount();
			allUnreadMessage = allUnreadMessage + unreadMessage;
		}
		if (allUnreadMessage > 0)
			mTitle.setLeftIcon(R.drawable.mine_new_message, this);
		else
			mTitle.setLeftIcon(R.drawable.mine_message, this);

	}

	private void handleInfoChanged() {
		String iconUrl = SharePreCacheHelper.getUserIconUrl(getActivity());
		if (TextUtils.isEmpty(iconUrl)) {
			iv_head.setImageResource(R.drawable.img_default_head);
		} else {
			if (iconUrl.startsWith("http")) {
				MineInfoUtils.setImageFromUrl(iconUrl, iv_head);
			} else {
				MineInfoUtils.setImageFromUrl(
						HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP + iconUrl,
						iv_head);
			}
		}

		String sparam = SharePreCacheHelper.getNickName(getActivity());
		if (!TextUtils.isEmpty(sparam)) {
			mNickName.setText(sparam);
		} else {
			mNickName.setText(getResources().getString(R.string.unknown));
		}

		int iparam = SharePreCacheHelper.getGender(getActivity());
		switch (iparam) {
		case 1:
			mGender.setText(R.string.man);
			break;
		case 2:
			mGender.setText(R.string.woman);
			break;
		case 3:
			mGender.setText(R.string.keep_secret);
			break;
		default:
			mGender.setText(R.string.keep_secret);
		}

		mLevel.setText("LV" + SharePreCacheHelper.getLevel(getActivity()));
		mFollowCnt.setText("" + SharePreCacheHelper.getFollow(getActivity()));
		mFollowedCnt.setText(""
				+ SharePreCacheHelper.getFollowed(getActivity()));
	}

	@Override
	public void onResume() {
		super.onResume();
		handleInfoChanged();
		GetUserReq mUserReq = new GetUserReq();
		mUserReq.setUserId(SharePreCacheHelper.getUserID(getActivity()));
		mUpdateGetUser.getUserAsync(mUserReq, mUserInfoResp);
		boolean haspaired = SharePreCacheHelper.getPairStatus(getActivity());
        if(haspaired && !ReleaseUtils.CAUSE_END_AFTER_SKINTEST){
        		AllKfirManager.getInstance(getActivity()).endSkinTest();
        }
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void setHeadImage() {
		String iconUrl = SharePreCacheHelper.getUserIconUrl(getActivity());
		if (TextUtils.isEmpty(iconUrl)) {
			iv_head.setImageResource(R.drawable.img_default_head);
		} else {
			if (iconUrl.startsWith("http")) {
				MineInfoUtils.setImageFromUrl(iconUrl, iv_head);
			} else {
				MineInfoUtils.setImageFromUrl(
						HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP + iconUrl,
						iv_head);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			Intent intent1 = new Intent(mContext, ActivityMyMessage.class);
			startActivityForResult(intent1, REQ_MESSAGE);
			break;
		case R.id.iv_head:
			mHeadChangeDialog.show();
			break;
		case R.id.layout_focus:
			startActivity(new Intent(mContext, ActivityFocusonFans.class));
			break;
		case R.id.layout_fans: {
			Intent intent = new Intent(mContext, ActivityFocusonFans.class);
			intent.putExtra(Constants.EXTRA_FOCUS_FANS_PAGE, 1);
			startActivity(intent);
			break;
		}
		case R.string.mine_personal_data:
			ActivityPersonalInfo.Info info = new ActivityPersonalInfo.Info(
					"雅诗兰黛", "12345678", "18-22岁", "上海", "干性皮肤");
			Intent intent = new Intent(mContext, ActivityPersonalInfo.class);
			intent.putExtra("info", info);
			startActivityForResult(intent, REQ_PERSONAL_INFO);
			// Toast.makeText(mContext, v.getId(), Toast.LENGTH_SHORT).show();
			break;
		case R.string.mine_my_speak:
			startActivity(new Intent(mContext, MineSpeakActivity.class));
			// Toast.makeText(mContext, v.getId(), Toast.LENGTH_SHORT).show();
			break;
		case R.string.mine_my_start_topic:
			startActivity(new Intent(mContext, MineTopicActivity.class));
			// Toast.makeText(mContext, v.getId(), Toast.LENGTH_SHORT).show();
			break;
		case R.string.mine_my_care_plan: {
			startActivity(new Intent(mContext, ActivityMyCarePlan.class));
			break;
		}
		case R.string.mine_setting:
			startActivityForResult(new Intent(mContext, ActivitySetting.class),
					REQ_SETTING);
			// Toast.makeText(mContext, v.getId(), Toast.LENGTH_SHORT).show();
			break;
		case DialogHeadChange.ID_PHOTO:
			mHeadChangeDialog.dismiss();
			getImageFromAlbum();
			break;
		case DialogHeadChange.ID_TAKIN:
			mHeadChangeDialog.dismiss();
			getImageFromCamera();
			break;
		case DialogHeadChange.ID_CANCEL:
			mHeadChangeDialog.dismiss();
			break;
		default:
			break;
		}

	}

	private void getImageFromAlbum() {

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
	}

	private void getImageFromCamera() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			Intent getImageByCamera = new Intent(
					"android.media.action.IMAGE_CAPTURE");
			// Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);
			getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(getImageByCamera,
					REQUEST_CODE_CAPTURE_CAMEIA);
		} else {
			Toast.makeText(getActivity(), "请确认已经插入SD卡", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		Slog.w("cropImageUri Uri: " + uri.toString());
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			String furl = PathUtils.getPath(getActivity(), uri);
			intent.setDataAndType(Uri.fromFile(new File(furl)), "image/*");
		} else {
			intent.setDataAndType(uri, "image/*");
		}
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", outputX);
		intent.putExtra("aspectY", outputX);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		// intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);

	}

	private boolean saveImage(Bitmap photo, String spath) {
		try {
			File file = new File(spath);
			File parant = file.getParentFile();
			if (!parant.exists()) {
				parant.mkdirs();
			}
			if (file.exists()) {
				file.delete();
			}
			// file.createNewFile();
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(spath, false));
			photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			Slog.e("", e);
			return false;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_PERSONAL_INFO) {

		}
		switch (requestCode) {
		case REQUEST_CODE_CAPTURE_CAMEIA:
			cropImageUri(imageUri, 200, 200, REQUEST_CODE_PICK_RESULT);
			break;
		case REQUEST_CODE_PICK_IMAGE:
			if (data == null)
				return;
			cropImageUri(data.getData(), 200, 200, REQUEST_CODE_PICK_RESULT);
			break;
		case REQUEST_CODE_PICK_RESULT:
			if (data == null)
				return;
			Uri uri = data.getData();
			if (uri == null) {
				// use bundle to get data
				Slog.d("use bundle to get Bitmap !!!!");
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					Bitmap photo = (Bitmap) bundle.get("data"); // get bitmap
					// spath :生成图片取个名字和路径包含类型
					if (photo != null) {
						iv_head.setImageBitmap(photo);
						Slog.w("update portrait : " + IMAGE_HEAD_CACHE);
						saveImage(photo, IMAGE_HEAD_CACHE);
						List<String> mimgs = new ArrayList<String>();
						mimgs.add(IMAGE_HEAD_CACHE);
						mUpdateGetUser.uploadPortraitAsync(mimgs,
								mPortraitCallback);
					} else {
						Slog.e("Can not get Photo Bitmap3 !!!!!");
					}
				} else {
					Slog.e("Can not get Photo Bitmap4 !!!!!");
				}
			} else {
				Slog.d("get Photo Url : " + uri.toString());
				iv_head.setImageURI(uri);
			}
			break;
		case REQ_PERSONAL_INFO: {
			break;
		}
		case REQ_SETTING: {
			if (resultCode == Activity.RESULT_OK) {
				// Logout
				Intent intent = new Intent(mContext, WelcomeLogin.class);
				// intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				startActivity(intent);
				getActivity().finish();
			}
			break;
		}
		case REQ_MESSAGE:
			if (resultCode == Activity.RESULT_OK) {
				setMessageImage();
			}
			break;
		}
	}

	private List<MineGroup> genGroups() {
		OnClickListener l = this;
		List<MineGroup> groups = new ArrayList<MineGroup>(3);

		// 第一组，只有一个个人资料
		List<MineItem> is1 = new ArrayList<FragmentMineList.MineItem>(1);
		is1.add(new MineItem(R.string.mine_personal_data,
				R.drawable.ico_mine_mine2, R.string.mine_personal_data, false,
				true, l));
		groups.add(new MineGroup(true, true, true, is1));

		// 第二组，有我的发言、我发起的话题和我的消息
		List<MineItem> is2 = new ArrayList<FragmentMineList.MineItem>(2);
		is2.add(new MineItem(R.string.mine_my_speak, R.drawable.mine_my_speak2,
				R.string.mine_my_speak, false, true, l));
		is2.add(new MineItem(R.string.mine_my_start_topic,
				R.drawable.ico_mine_topic2, R.string.mine_my_start_topic,
				false, true, l));
		is2.add(new MineItem(R.string.mine_my_care_plan,
				R.drawable.ico_my_care_plan, R.string.mine_my_care_plan,
				false, true, l));
		// is2.add(new MineItem(R.string.mine_my_message,
		// R.drawable.ico_mine_message, R.string.mine_my_message, false,
		// true, l));
		groups.add(new MineGroup(true, true, true, is2));

		// 第三组，有 设置
		List<MineItem> is3 = new ArrayList<FragmentMineList.MineItem>(1);
		is3.add(new MineItem(R.string.mine_setting,
				R.drawable.ico_mine_setting2, R.string.mine_setting, false,
				true, l));
		groups.add(new MineGroup(true, true, true, is3));
		return groups;
	}
}
