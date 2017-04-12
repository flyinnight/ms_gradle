package com.dilapp.radar.util;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.Register.RegRadarReq;
import com.dilapp.radar.domain.UpdateGetUser.GetUserResp;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MineInfoUtils {

	// location ID
	public static final int LOCATION_HUABEI = 0x100;
	public static final int LOCATION_HUADONG = 0x200;
	public static final int LOCATION_HUANAN = 0x300;
	public static final int LOCATION_HUAZHONG = 0x400;
	public static final int LOCATION_DONGBEI = 0x500;
	public static final int LOCATION_XIBEI = 0x600;
	public static final int LOCATION_XINAN = 0x700;
	public static final int LOCATION_QINGZANG = 0x800;
	public static final int LOCATION_GANGAOTAI = 0x900;

	public static final int[] LOCATION_SID_LIST = new int[] {
			R.string.location_huabei, R.string.location_huadong,
			R.string.location_huanan, R.string.location_huazhong,
			R.string.location_dongbei, R.string.location_xibei,
			R.string.location_xinan, R.string.location_qingzang,
			R.string.location_gangaotai };

	public static String getStringByLocationID(Context context, String id) {
		String result = null;
		try {
			int ID = Integer.parseInt(id);
			switch (ID) {
			case LOCATION_HUABEI:
				result = context.getString(R.string.location_huabei);
				break;
			case LOCATION_HUADONG:
				result = context.getString(R.string.location_huadong);
				break;
			case LOCATION_HUANAN:
				result = context.getString(R.string.location_huanan);
				break;
			case LOCATION_HUAZHONG:
				result = context.getString(R.string.location_huazhong);
				break;
			case LOCATION_DONGBEI:
				result = context.getString(R.string.location_dongbei);
				break;
			case LOCATION_XIBEI:
				result = context.getString(R.string.location_xibei);
				break;
			case LOCATION_XINAN:
				result = context.getString(R.string.location_xinan);
				break;
			case LOCATION_QINGZANG:
				result = context.getString(R.string.location_qingzang);
				break;
			case LOCATION_GANGAOTAI:
				result = context.getString(R.string.location_gangaotai);
				break;
			default:
				result = context.getString(R.string.unknown);
			}
		} catch (Exception e) {
			Slog.e("Error Location ID and ignore trans : " + id);
			result = id;
		}
		return result;
	}

	public static String getLocationIDByStringID(int id) {
		String result = "";
		switch (id) {
		case R.string.location_huabei:
			result += LOCATION_HUABEI;
			break;
		case R.string.location_huadong:
			result += LOCATION_HUADONG;
			break;
		case R.string.location_huanan:
			result += LOCATION_HUANAN;
			break;
		case R.string.location_huazhong:
			result += LOCATION_HUAZHONG;
			break;
		case R.string.location_dongbei:
			result += LOCATION_DONGBEI;
			break;
		case R.string.location_xibei:
			result += LOCATION_XIBEI;
			break;
		case R.string.location_xinan:
			result += LOCATION_XINAN;
			break;
		case R.string.location_qingzang:
			result += LOCATION_QINGZANG;
			break;
		case R.string.location_gangaotai:
			result += LOCATION_GANGAOTAI;
			break;
		}
		return result;
	}

	public static void clearUserInfo(Context context) {
		SharePreCacheHelper.setNickName(context, "");
		SharePreCacheHelper.setBindedPhone(context, "");
		SharePreCacheHelper.setBindedEmail(context, "");
		SharePreCacheHelper.setUserIconUrl(context, "");
		SharePreCacheHelper.setGender(context, -1);
		SharePreCacheHelper.setSkinType(context, -1);
		SharePreCacheHelper.setLevel(context, 0);
		SharePreCacheHelper.setFollow(context, 0);
		SharePreCacheHelper.setFollowed(context, 0);
		SharePreCacheHelper.setPreferChooseSkinType(context, false);
		SharePreCacheHelper.setPublicPrivacy(context, false);
	}

	public static void saveUserInfo(Context context, GetUserResp resp) {
		if (resp.isRequestSuccess()) {
			Slog.d("get user info resp!!");
			// 昵称
			String nickname = resp.getName();
			if (!TextUtils.isEmpty(nickname)) {
				SharePreCacheHelper.setNickName(context, nickname);
			} else {
				SharePreCacheHelper.setNickName(context, "");
			}

			// 号码
			String phone = resp.getPhone();
			if (!TextUtils.isEmpty(phone)) {
				SharePreCacheHelper.setBindedPhone(context, phone);
			} else {
				SharePreCacheHelper.setBindedPhone(context, "");
			}

			// 邮箱
			String email = resp.getEmail();
			if (!TextUtils.isEmpty(email)) {
				SharePreCacheHelper.setBindedEmail(context, email);
			} else {
				SharePreCacheHelper.setBindedEmail(context, "");
			}
			// 头像
			String portrait = resp.getPortrait();
			if (!TextUtils.isEmpty(portrait)) {
				SharePreCacheHelper.setUserIconUrl(context, portrait);
			} else {
				SharePreCacheHelper.setUserIconUrl(context, "");
			}

			// 性别
			SharePreCacheHelper.setGender(context, resp.getGender());

			// 肤质
			int skintype = 0;
			if (resp.getPreferChoseSkin()) {
				skintype = resp.getSkinQuality();
			} else {
				skintype = resp.getSkinQualityCalculated();
			}
			SharePreCacheHelper.setSkinType(context, skintype);

			// 生日
			Date mbirthday = (Date) resp.getBirthday();
			if (mbirthday != null) {
				SharePreCacheHelper.setBirthDay(context, mbirthday);
			}

			// 地区
			String area = resp.getLocation();
			if (!TextUtils.isEmpty(area)) {
				SharePreCacheHelper.setArea(context, area);
			} else {
				SharePreCacheHelper.setArea(context, "");
			}

			// 等级
			SharePreCacheHelper.setLevel(context, resp.getLevel());
			// 关注
			SharePreCacheHelper.setFollow(context, resp.getFollowCount());
			// 粉丝
			SharePreCacheHelper.setFollowed(context, resp.getFollowedCount());

			SharePreCacheHelper.setPreferChooseSkinType(context,
					resp.getPreferChoseSkin());
			SharePreCacheHelper.setPublicPrivacy(context,
					resp.getPublicPrivacy());

		} else {
			Slog.e("Failed get UserInfo Resp!!!!!! " + resp.getMessage());
		}
	}

	public static void reloadDefalutUserInfo(Context context, RegRadarReq req) {
		if (req == null)
			return;
		req.setBirthday(SharePreCacheHelper.getBirthDay(context).getTime());
		req.setGender(SharePreCacheHelper.getGender(context));
		req.setSkinQuality(SharePreCacheHelper.getSkinType(context));
		req.setPreferChoseSkin(SharePreCacheHelper
				.isPreferChooseSkinType(context));
		req.setPublicPrivacy(SharePreCacheHelper.isPublicPrivacy(context));
	}

	public static void setImageFromUrl(final String url, final ImageView iv) {
		ImageLoader.getInstance().loadImage(url, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						super.onLoadingComplete(imageUri, view, loadedImage);
						if (loadedImage != null) {
							if (url != null && url.equals(imageUri)) {
								iv.setImageBitmap(loadedImage);
							}
						}

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

	private static List<String> displayedImages = Collections
			.synchronizedList(new LinkedList<String>());

	private static DisplayImageOptions options = new DisplayImageOptions.Builder()
			// 正在加载的图片
			.showImageOnLoading(R.drawable.img_default_head)
			// URL请求失败
			.showImageForEmptyUri(R.drawable.img_default_head)
			// 图片加载失败
			.showImageOnFail(R.drawable.img_default_head).cacheInMemory(true)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY)
			.build();

	public static void setImage(String url, ImageView imageView) {
		String imageViewUrl = null;
		if (TextUtils.isEmpty(url)) {
			imageView.setImageResource(R.drawable.img_default_head);
			return;
		} else {
			imageViewUrl = HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP + url;
		}
		ImageLoader.getInstance().displayImage(imageViewUrl,
				new ImageViewAware(imageView), options);
	}
}
