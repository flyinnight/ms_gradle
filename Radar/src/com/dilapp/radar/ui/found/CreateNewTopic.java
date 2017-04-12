package com.dilapp.radar.ui.found;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.CreateTopic;
import com.dilapp.radar.domain.CreateTopic.TopicReleaseReq;
import com.dilapp.radar.domain.CreateTopic.TopicReleaseResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.impl.CreateTopicImpl;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.StringUtils;

public class CreateNewTopic extends BaseActivity implements OnClickListener {
	private TitleView mTitle;
	private ImageView iv_selected_icon = null;
	private PopupWindow popupWindow;
	private static final int REQUEST_CODE = 2;
	private static final int TAKE_PICTURE = 0;
	private static final int CROP_PICTURE = 3;
	private Context mContext = null;
	private EditText et_title = null;
	private EditText et_content = null;
	private String mImageURL = null;
	private final int SUCCESS = 100;
	private final int FAILURE = 110;
	private TopicReleaseResp mRespone = null;
	private String iconpath=Environment.getExternalStorageDirectory()+"/tIcon.jpg";

	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				// TODO:这里暂且跳转到话题介绍页面,这里会存在一个异步和异常的问题,
				// 这里会不会有问题 值得商榷
				Intent intent = new Intent(CreateNewTopic.this,
						TopicIntroduceActivity.class);
				// TODO:需要将当前的话题ID发送过去
				startActivity(intent);
				break;
			case FAILURE:
				Toast.makeText(mContext, "话题发布失败,请检查", 1).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_new_topic);
		mContext = this;
		View vg_title = findViewById(TitleView.ID_TITLE);
		mTitle = new TitleView(this, vg_title);
		mTitle.setCenterText(R.string.create_topic, null);
		mTitle.setRightText(R.string.speak_release, this);
		mTitle.setLeftText(R.string.cancel, this);
		et_title = (EditText) findViewById(R.id.et_topic_title);
		et_content = (EditText) findViewById(R.id.et_content);

		iv_selected_icon = (ImageView) findViewById(R.id.iv_icon);
		iv_selected_icon.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			finish();
			break;
		case TitleView.ID_RIGHT:
			if (!StringUtils.isEmpty(et_content.getText().toString())
					&& !StringUtils.isEmpty(et_title.getText().toString())) {
				upLoadImage();
			} else {
				// test
				Toast.makeText(mContext, "信息填写不完整", 1).show();
			}
			break;
		case R.id.iv_icon:
			showPopupWindow();
			break;
		case R.id.btn_from_camera:
			Uri imageUri = null;
			String fileName = null;
			Intent openCameraIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			// 删除上一次截图的临时文件
			SharedPreferences sharedPreferences = getSharedPreferences("temp",
					Context.MODE_WORLD_WRITEABLE);
			ImageTools.deletePhotoAtPathAndName(Environment
					.getExternalStorageDirectory().getAbsolutePath(),
					sharedPreferences.getString("tempName", ""));
			// 保存本次截图临时文件名字
			fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
			Editor editor = sharedPreferences.edit();
			editor.putString("tempName", fileName);
			editor.commit();
			imageUri = Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), fileName));
			// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(openCameraIntent, REQUEST_CODE);
			break;
		case R.id.btn_from_gallery:
			Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
			openAlbumIntent.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(openAlbumIntent, REQUEST_CODE);
			break;
		case R.id.btn_cancel:

			break;
		default:
			break;
		}
	}

	private void showPopupWindow() {
		View view = (LinearLayout) LayoutInflater.from(CreateNewTopic.this)
				.inflate(R.layout.image_select, null);
		Button bt_gallery = (Button) view.findViewById(R.id.btn_from_gallery);
		Button bt_camera = (Button) view.findViewById(R.id.btn_from_camera);
		Button bt_cancel = (Button) view.findViewById(R.id.btn_cancel);
		bt_gallery.setOnClickListener(this);
		bt_camera.setOnClickListener(this);
		bt_cancel.setOnClickListener(this);
		if (popupWindow == null) {
			popupWindow = new PopupWindow(CreateNewTopic.this);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
			popupWindow.setTouchable(true); // 设置PopupWindow可触摸
			popupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
			popupWindow.setContentView(view);
			popupWindow.setWidth(LayoutParams.MATCH_PARENT);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setAnimationStyle(R.style.popuStyle); // 设置 popupWindow
		}
		popupWindow.showAtLocation(iv_selected_icon, Gravity.BOTTOM, 0, 0);
		popupWindow.update();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE:
				Uri uri = null;
				String fileName = getSharedPreferences("temp",
						Context.MODE_WORLD_WRITEABLE).getString("tempName", "");
				if (data != null) {
					uri = data.getData();
					System.out.println("Data");
				} else {
					System.out.println("File");
					uri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), fileName));
				}
				cropImage(uri, 500, 500, CROP_PICTURE);
				break;
			case CROP_PICTURE:
				Bitmap photo = null;
				Uri photoUri = data.getData();
				if (photoUri != null) {
					photo = BitmapFactory.decodeFile(photoUri.getPath());
				}
				if (photo == null) {
					Bundle extra = data.getExtras();
					if (extra != null) {
						photo = (Bitmap) extra.get("data");
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					}
				}
				saveBitmap(photo);
				iv_selected_icon.setImageBitmap(photo);
				break;
			default:
				break;
			}
		}
	}

	// 截取图片
	public void cropImage(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, requestCode);
	}

	private void upLoadImage() {
		Object obj = ReqFactory.buildInterface(mContext, CreateTopic.class);
		CreateTopicImpl mDetail = (CreateTopicImpl) obj;
		// 图片路径
		ArrayList<String> mPathList = new ArrayList<String>();
		if (iconpath != null && !"".equals(iconpath)) {
			mPathList.add(iconpath);
			BaseCall<CreateTopic.TopicReleaseResp> node = new BaseCall<CreateTopic.TopicReleaseResp>() {
				@Override
				public void call(TopicReleaseResp resp) {
					if ("SUCCESS".equals(resp.getStatus())) {
						upLoadTopic(resp.getTopicImgUrl());
					} else {
						Toast.makeText(mContext, "图片上传失败", 1).show();
					}
				}
			};
			addCallback(node);
			mDetail.uploadTopicImgAsync(mPathList, node);
		} else {
			Toast.makeText(mContext, "请选择一张图片作为话题图标", 1).show();
		}
	}

	private void upLoadTopic(String imageUrl) {
		Object obj = ReqFactory.buildInterface(mContext, CreateTopic.class);
		CreateTopicImpl mDetail = (CreateTopicImpl) obj;
		TopicReleaseReq req = new TopicReleaseReq();
		String title = et_title.getText().toString();
		String content = et_content.getText().toString();
		if (!StringUtils.isEmpty(title)) {
			req.setTopicTitle(title);
		}
		if (StringUtils.isEmpty(content)) {
			req.setTopicDes(content);
		}
		BaseCall<CreateTopic.TopicReleaseResp> node = new BaseCall<CreateTopic.TopicReleaseResp>() {

			@Override
			public void call(TopicReleaseResp resp) {
				if ("SUCCESS".equals(resp.getStatus())) {
					mRespone = resp;
					mhandler.sendEmptyMessage(SUCCESS);
				} else {
					mhandler.sendEmptyMessage(FAILURE);
				}
			}
		};
		addCallback(node);
		mDetail.createTopicAsync(req, node);
	}

	public void saveBitmap(Bitmap map) {
		File f = new File(Environment.getExternalStorageDirectory(),
				"tIcon.jpg");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			map.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
