package com.dilapp.radar.ui.found;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.Register.RegReq;
import com.dilapp.radar.util.HttpConstant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TopicForPostAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<MPostResp> mData;
	private LayoutInflater mInflater;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private DisplayImageOptions options;
	static final List<String> displayedImages = Collections
			.synchronizedList(new LinkedList<String>());

	public TopicForPostAdapter(Context mContext, ArrayList<MPostResp> data) {
		this.mContext = mContext;
		this.mData = data;
		mInflater = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				// 正在加载的图片
				.showImageForEmptyUri(R.drawable.ic_launcher)
				// URL请求失败
				.showImageOnFail(R.drawable.ic_launcher)
				// 图片加载失败
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY).build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			/*convertView = mInflater.inflate(R.layout.topic_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_gender = (TextView) convertView
					.findViewById(R.id.tv_gander);
			holder.tv_level = (TextView) convertView
					.findViewById(R.id.tv_level);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_topic_title);
			holder.tv_time = (TextView) convertView
					.findViewById(R.id.tv_release_time);
			holder.tv_weiguang = (TextView) convertView
					.findViewById(R.id.tv_wg);
			holder.tv_reply = (TextView) convertView
					.findViewById(R.id.tv_reply);
			holder.iv_header = (ImageView) convertView
					.findViewById(R.id.iv_user_header);
			holder.iv_one = (ImageView) convertView.findViewById(R.id.iv_one);
			holder.iv_two = (ImageView) convertView.findViewById(R.id.iv_two);
			holder.iv_three = (ImageView) convertView
					.findViewById(R.id.iv_three);
			holder.iv_four = (ImageView) convertView.findViewById(R.id.iv_four);*/
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_name.setText(mData.get(position).getUserName());
		if (mData.get(position).isGender() == RegReq.FEMALE) {
			holder.tv_gender.setText(R.string.woman);
		} else if (mData.get(position).isGender() == RegReq.MALE) {
			holder.tv_gender.setText(R.string.man);
		}
		
		if (mData.get(position).getLevel() == 0) {
			holder.tv_level.setText(" lv0");
		} else {
			holder.tv_level.setText(" lv"+mData.get(position).getLevel());
    	}
		holder.tv_title.setText(mData.get(position).getPostTitle());

		Date date = new Date(mData.get(position).getUpdateTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		holder.tv_time.setText(sdf.format(date));

		holder.tv_weiguang.setText(mData.get(position).getPostViewCount());
		holder.tv_reply.setText(mData.get(position).getTotalFollows());
		//TODO:貌似没有这个字段,暂时先使用帖子缩略图,实际应该显示话题图标
		if (mData.get(position).getThumbURL().size() > 0) {
			ImageLoader.getInstance().loadImage(
					HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP+mData.get(position).getThumbURL().get(0), options,	
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view,
								Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							holder.iv_header.setImageBitmap(loadedImage);
							if (loadedImage != null) {
								ImageView imageView = (ImageView) view;
								boolean firstDisplay = !displayedImages
										.contains(imageUri);
								if (firstDisplay) {
									FadeInBitmapDisplayer.animate(imageView, 500);
									displayedImages.add(imageUri);
								}
							}

						}
					});	
		}

		if (mData.get(position).getThumbURL().size() > 0) {
			ImageLoader.getInstance().loadImage(
					HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP+mData.get(position).getThumbURL().get(0), options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view,
								Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							holder.iv_one.setImageBitmap(loadedImage);
							if (loadedImage != null) {
								ImageView imageView = (ImageView) view;
								boolean firstDisplay = !displayedImages
										.contains(imageUri);
								if (firstDisplay) {
									FadeInBitmapDisplayer.animate(imageView, 500);
									displayedImages.add(imageUri);
								}
							}

						}
					});
		}
		
		if (mData.get(position).getThumbURL().size() > 1) {
			ImageLoader.getInstance().loadImage(
					HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP+mData.get(position).getThumbURL().get(1), options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view,
								Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							holder.iv_two.setImageBitmap(loadedImage);
							if (loadedImage != null) {
								ImageView imageView = (ImageView) view;
								boolean firstDisplay = !displayedImages
										.contains(imageUri);
								if (firstDisplay) {
									FadeInBitmapDisplayer.animate(imageView, 500);
									displayedImages.add(imageUri);
								}
							}

						}
					});
		}
		
		if (mData.get(position).getThumbURL().size() > 2) {
			ImageLoader.getInstance().loadImage(
					HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP+mData.get(position).getThumbURL().get(2), options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view,
								Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							holder.iv_three.setImageBitmap(loadedImage);
							if (loadedImage != null) {
								ImageView imageView = (ImageView) view;
								boolean firstDisplay = !displayedImages
										.contains(imageUri);
								if (firstDisplay) {
									FadeInBitmapDisplayer.animate(imageView, 500);
									displayedImages.add(imageUri);
								}
							}

						}
					});
		}
		
		if (mData.get(position).getThumbURL().size() > 3) {
			ImageLoader.getInstance().loadImage(
					HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP+mData.get(position).getThumbURL().get(3), options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view,
								Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							holder.iv_four.setImageBitmap(loadedImage);
							if (loadedImage != null) {
								ImageView imageView = (ImageView) view;
								boolean firstDisplay = !displayedImages
										.contains(imageUri);
								if (firstDisplay) {
									FadeInBitmapDisplayer.animate(imageView, 500);
									displayedImages.add(imageUri);
								}
							}

						}
					});
		}
		
		return convertView;
	}

	static class ViewHolder {
		private TextView tv_name;
		private TextView tv_gender;
		private TextView tv_level;
		private TextView tv_title;
		private TextView tv_time;
		// 围观
		private TextView tv_weiguang;
		// 回复
		private TextView tv_reply;
		private ImageView iv_header;
		private ImageView iv_one;
		private ImageView iv_two;
		private ImageView iv_three;
		private ImageView iv_four;
	}

	protected static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
