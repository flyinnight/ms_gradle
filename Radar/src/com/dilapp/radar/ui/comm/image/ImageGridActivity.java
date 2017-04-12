package com.dilapp.radar.ui.comm.image;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.comm.image.ImageGridAdapter.TextCallback;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ImageGridActivity extends BaseActivity implements OnClickListener{
	public static final String EXTRA_IMAGE_LIST = "imagelist";

	List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapter adapter;
	AlbumHelper helper;
	Button bt;
	
	private TitleView mTitle;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ImageGridActivity.this, "最多选择9张图片", 400).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_grid);
		
		View title = findViewById(TitleView.ID_TITLE);
		mTitle = new TitleView(this, title);
        mTitle.setCenterText(R.string.gallery, null);
        mTitle.setLeftIcon(R.drawable.btn_back_white, this);
//        mTitle.setRightText(R.string.confirm, this);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		dataList = (List<ImageItem>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_LIST);

		initView();
		bt = (Button) findViewById(R.id.bt);
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleConfirm();
			}
		});
	}
	
	private void handleConfirm(){
		Collection<String> c = adapter.map.values();
		Iterator<String> it = c.iterator();
		ClipData mData = null;
		for (; it.hasNext();) {
			ClipData.Item item = new ClipData.Item(Uri.fromFile(new File(it.next())));
			if(mData == null){
				mData = new ClipData("muti_image",new String[]{"image/*"},item);
			}else{
				mData.addItem(item);
			}
		}
		if(mData != null){
			Intent mIntent = new Intent();
			mIntent.setClipData(mData);
			setResult(RESULT_OK, mIntent);
			finish();
		}else{
			Toast.makeText(ImageGridActivity.this, "请选择图片", Toast.LENGTH_SHORT).show();;
		}
	}

	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,
				mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				bt.setText(getString(R.string.confirm) + "(" + count + ")");
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.notifyDataSetChanged();
			}

		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.vg_left:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.vg_right:
			handleConfirm();
			break;
		}
	}
}
