package com.dilapp.radar.ui.comm.image;

import java.io.Serializable;
import java.util.List;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ImageBucketActivity extends BaseActivity implements OnClickListener{
	// ArrayList<Entity> dataList;//用来装载数据源的列表
	List<ImageBucket> dataList;
	GridView gridView;
	ImageBucketAdapter adapter;// 自定义的适配器
	AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	private static final int REQ_CHOOSE_IMAGE = 1;
//	public static Bitmap bimap;
	
	private TitleView mTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_bucket);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		
		View title = findViewById(TitleView.ID_TITLE);
		mTitle = new TitleView(this, title);
        mTitle.setCenterText(R.string.gallery, null);
        mTitle.setLeftIcon(R.drawable.btn_back_white, this);

		initData();
		initView();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		dataList = helper.getImagesBucketList(true);	
	}

	/**
	 * 初始化view视图
	 */
	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new ImageBucketAdapter(ImageBucketActivity.this, dataList);
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ImageBucketActivity.this,
						ImageGridActivity.class);
				intent.putExtra(EXTRA_IMAGE_LIST,
						(Serializable) dataList.get(position).imageList);
				startActivityForResult(intent, REQ_CHOOSE_IMAGE);
			}

		});
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == REQ_CHOOSE_IMAGE && resultCode == RESULT_OK){
			ClipData mClip = data != null ? data.getClipData() : null;
			if(mClip != null){
				Intent mIntent = new Intent();
				mIntent.setClipData(mClip);
				setResult(RESULT_OK, mIntent);
				finish();
			}else{
				
			}
		}else{
//			finish();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.vg_left:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
	}
}
