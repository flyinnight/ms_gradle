package com.dilapp.radar;

import java.util.ArrayList;

import com.dilapp.radar.cache.SharePrefUtil;
import com.dilapp.radar.skinproducts.database.QueryInterface;
import com.dilapp.radar.skinproducts.database.SkinProduct;
import com.dilapp.radar.skinproducts.database.SkinProductsUtils;
import com.dilapp.radar.util.Constants;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SkinProductsScan extends Activity implements OnClickListener {
	private EditText et_name = null;
	private Button btn_search = null;
	private Button btn_scanner = null;
	private SkinProductsUtils mSkinProductsUtils;
	private SQLiteDatabase mDb = null;
	private ArrayList<SkinProduct> mProducts = null;
	private final int SUCCESS = 0;
	private final int FAILURE = 1;
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private TextView tv_product_info = null;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				tv_product_info.setText(mProducts.toString());
				break;
			case FAILURE:
				tv_product_info.setText("没有查询到相关护肤品!");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchskinprod);
		mSkinProductsUtils = new SkinProductsUtils();
		mDb = mSkinProductsUtils.OpenDb(this, 10);
		initData();
		et_name = (EditText) findViewById(R.id.et_name);
		btn_scanner = (Button) findViewById(R.id.btn_scanner);
		btn_search = (Button) findViewById(R.id.btn_seacher);
		tv_product_info = (TextView) findViewById(R.id.tv_product_info);
		btn_scanner.setOnClickListener(this);
		btn_search.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_scanner:
			Intent intent = new Intent();
			intent.setClass(SkinProductsScan.this, MipcaActivityCapture.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			break;
		case R.id.btn_seacher:
			long mProductId = 0;
			String mStr = et_name.getText().toString().trim();
			try {
				long _v = Long.parseLong(mStr);
				mProductId = Long.valueOf(mStr);
			} catch (NumberFormatException e) {
				Toast.makeText(SkinProductsScan.this, "查询条件错误", 1).show();
				return;
			}
			SkinProductsUtils.findById(new QueryInterface() {

				@Override
				public void onSucess(ArrayList<SkinProduct> produts) {
					mProducts = produts;
					mHandler.sendEmptyMessage(SUCCESS);
				}

				@Override
				public void failure(Exception e) {
					Log.v("info", e.getLocalizedMessage());
					mHandler.sendEmptyMessage(FAILURE);
				}
			}, mProductId, mDb);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mDb != null) {
			SkinProductsUtils.CloseDb();
		}
		SharePrefUtil.remove(this,Constants.PRODUCT_NAME);
		super.onDestroy();
	}

	private void initData() {
		SkinProduct product = new SkinProduct();
		product.setId(1);
		product.setName("大宝");
		product.setPrice(30);
		product.setDescription("手足护理霜，采用极易被皮肤吸收的水解蛋白为原料，配以多种天然植物提取液精制而成");
		product.setHomepage("http://www.lenovo.com");
		product.setType(1);
		SkinProductsUtils.InserSkinProduct(product, mDb);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				Log.v("info", bundle.getString("result"));
				et_name.append(bundle.getString("result"));
				// mImageView.setImageBitmap((Bitmap) data
				// .getParcelableExtra("bitmap"));
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		String str = SharePrefUtil.getString(SkinProductsScan.this,
				Constants.PRODUCT_NAME, "");
		if ("".equals(str))
			return;
		et_name.append(str);
	}
}
