package com.dilapp.radar.ui.mine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewConfigurationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
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
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.mine.FragmentMineList.MineGroup;
import com.dilapp.radar.ui.mine.FragmentMineList.MineItem;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.MineInfoUtils;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.view.CircularImage;

public class ActivityPersonalInfo extends BaseFragmentActivity implements
        OnClickListener {

    private FragmentMineList mFragmentMineList;
    private DialogHeadChange mHeadChangeDialog;
    private TitleView mTitle;

    private ImageView mHeader;
    private TextView mNickName;
    private TextView mPhone;
    private TextView mPassword;
    private TextView mEmail;
    private TextView mAgeArea;
    private TextView mLocation;
    private TextView mSkin;
    private TextView mGender;
    private TextView mUserId;

    private int iparam;
    private UpdateGetUser mUpdateGetUser;

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 2;
    private static final int REQUEST_CODE_PICK_RESULT = 3;
    private static final int REQUEST_CODE_EDIT_NICKNAME = 4;
    private static final int REQUEST_CODE_EDIT_GENDER = 5;
    private static final int REQUEST_CODE_EDIT_SKIN_TYPE = 6;
    private static final int REQUEST_CODE_EDIT_AGE = 7;
    private static final int REQUEST_CODE_EDIT_LOCATION = 8;
    private static final int REQUEST_CODE_EDIT_PHONE = 9;

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
                SharePreCacheHelper.setUserIconUrl(getApplicationContext(),
                        portrainUrl);
                RegRadarReq mRadarReq = new RegRadarReq();
                mRadarReq.setPortrait(portrainUrl);
                MineInfoUtils.reloadDefalutUserInfo(getApplicationContext(),
                        mRadarReq);
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
                mUserReq.setUserId(SharePreCacheHelper
                        .getUserID(getApplicationContext()));
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
            if (resp == null) {
                Slog.e("Error GetUserResp == NULL!!");
            } else {
                MineInfoUtils.saveUserInfo(getApplicationContext(), resp);
                handleInfoChanged();
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        final ViewConfiguration configuration = ViewConfiguration.get(this);
        int slop = ViewConfigurationCompat
                .getScaledPagingTouchSlop(configuration);
        int minimumFlingVelocity = configuration
                .getScaledMinimumFlingVelocity();
        int maximumFlingVelocity = configuration
                .getScaledMaximumFlingVelocity();
        Log.i("III", "slop " + slop);
        Log.i("III", "minimumFlingVelocity " + minimumFlingVelocity);
        Log.i("III", "maximumFlingVelocity " + maximumFlingVelocity);
        mFragmentMineList = new FragmentMineList();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mFragmentMineList, "mineList")
                .commit();

        mHeadChangeDialog = new DialogHeadChange(this);
        mHeadChangeDialog.setButtonsOnClickListener(this, this, this);

        View title = findViewById(R.id.vg_title);
        mTitle = new TitleView(getApplicationContext(), title);
        mTitle.setLeftIcon(R.drawable.btn_back, this);
        mTitle.setCenterText(R.string.info_title, null);

        Info info = (Info) getIntent().getExtras().getSerializable("info");
        initView(info);
        mFragmentMineList.setGroups(genGroups());

        mUpdateGetUser = ReqFactory.buildInterface(this, UpdateGetUser.class);

        addCallback(mPortraitCallback);
        addCallback(mUserInfoResp);
        addCallback(mUserUpdateCallback);

        // GetUserReq mUserReq = new GetUserReq();
        // mUserReq.setUserId(SharePreCacheHelper.getUserID(getApplicationContext()));
        // mUpdateGetUser.getUserAsync(mUserReq, mUserInfoResp);
    }

    private void setHeadImage() {
        String iconUrl = SharePreCacheHelper.getUserIconUrl(this);
        if (TextUtils.isEmpty(iconUrl)) {
            mHeader.setImageResource(R.drawable.img_default_head);
        } else {
            if (iconUrl.startsWith("http")) {
                MineInfoUtils.setImageFromUrl(iconUrl, mHeader);
            } else {
                MineInfoUtils.setImageFromUrl(
                        HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP + iconUrl,
                        mHeader);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case TitleView.ID_LEFT:
                finish();
                break;
            case R.string.info_header:
                mHeadChangeDialog.show();
                // vToast.makeText(this, v.getId(), Toast.LENGTH_SHORT).show();
                break;
            case R.string.info_nick_name:
                intent = new Intent(ActivityPersonalInfo.this,
                        ActivityEditNickName.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_NICKNAME);
                // Toast.makeText(this, v.getId(), Toast.LENGTH_SHORT).show();
                break;
            case R.string.info_bind_phone:
                intent = new Intent(ActivityPersonalInfo.this,
                        ActivityBindingPhone.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_PHONE);
                break;
            case R.string.info_bind_email:
                intent = new Intent(ActivityPersonalInfo.this,
                        ActivityBindingEmail.class);
                startActivity(intent);
                break;
            case R.string.info_amend_password:
                intent = new Intent(ActivityPersonalInfo.this,
                        ActivityAmendPassword.class);
                startActivity(intent);
                break;
            case R.string.info_age_area:
                intent = new Intent(ActivityPersonalInfo.this,
                        ActivityEditAgeArea.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_AGE);
                // Toast.makeText(this, v.getId(), Toast.LENGTH_SHORT).show();
                break;
            case R.string.info_location:
                intent = new Intent(ActivityPersonalInfo.this,
                        ActivityEditLocation.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_LOCATION);
                // Toast.makeText(this, v.getId(), Toast.LENGTH_SHORT).show();
                break;
            case R.string.info_skin:
                intent = new Intent(ActivityPersonalInfo.this,
                        ActivityEditSkinType.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_SKIN_TYPE);
                // Toast.makeText(this, v.getId(), Toast.LENGTH_SHORT).show();
                break;
            case R.string.info_gender:
                intent = new Intent(ActivityPersonalInfo.this,
                        ActivityEditGender.class);
                Log.i("ActivityPersonalInfo", "sex:---->>>" + iparam);
                intent.putExtra("gender", iparam);
                startActivityForResult(intent, REQUEST_CODE_EDIT_GENDER);
                // Toast.makeText(this, v.getId(), Toast.LENGTH_SHORT).show();
                break;
            case R.string.info_userid:
                // Toast.makeText(this, v.getId(), Toast.LENGTH_SHORT).show();
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

    private void handleInfoChanged() {
        Context context = getApplicationContext();
        String param = SharePreCacheHelper.getNickName(context);
        if (!TextUtils.isEmpty(param)) {
            mNickName.setText(param);
        } else {
            mNickName.setText(getResources().getString(R.string.unknown));
        }
        param = SharePreCacheHelper.getBindedPhone(context);
        if (!TextUtils.isEmpty(param)) {
            mPhone.setText(param);
        } else {
            mPhone.setText(getResources().getString(R.string.unknown));
        }
        param = SharePreCacheHelper.getBindedEmail(context);
        if (!TextUtils.isEmpty(param)) {
            mEmail.setText(param);
        } else {
            mEmail.setText(getResources().getString(R.string.unknown));
        }
        mPassword.setText(R.string.password);
        Date birth = SharePreCacheHelper.getBirthDay(context);
        if (birth != null) {
            Calendar a = Calendar.getInstance();
            int cyear = a.get(Calendar.YEAR);
            int byear = birth.getYear() + 1900;
            Slog.e("bird year : " + byear + "  cyear : " + cyear);
            int ygap = cyear - byear;
            if (ygap < 15) {
                mAgeArea.setText(getResources().getString(R.string.less_15));
            } else if (ygap <= 20) {
                mAgeArea.setText(getResources().getString(R.string.less_20));
            } else if (ygap <= 25) {
                mAgeArea.setText(getResources().getString(R.string.less_25));
            } else if (ygap <= 30) {
                mAgeArea.setText(getResources().getString(R.string.less_30));
            } else if (ygap <= 40) {
                mAgeArea.setText(getResources().getString(R.string.less_40));
            } else if (ygap <= 50) {
                mAgeArea.setText(getResources().getString(R.string.less_50));
            } else {
                mAgeArea.setText(getResources().getString(R.string.more_50));
            }
        } else {
            mAgeArea.setText(getResources().getString(R.string.unknown));
        }

        param = SharePreCacheHelper.getArea(context);
        mLocation.setText(Constants.getAddressString(this, param));

        int skintype = SharePreCacheHelper.getSkinType(context);
        mSkin.setText(Constants.getSkinTypeString(this, skintype));

        iparam = SharePreCacheHelper.getGender(context);
        mGender.setText(Constants.getGenderString(this, iparam));
        mUserId.setText(SharePreCacheHelper.getUserID(context));

        setHeadImage();
    }

    private void initView(Info info) {
        Context context = getApplicationContext();
        mHeader = new CircularImage(context);
        mNickName = new TextView(context);
        mPhone = new TextView(context);
        mEmail = new TextView(context);
        mPassword = new TextView(context);
        mAgeArea = new TextView(context);
        mLocation = new TextView(context);
        mSkin = new TextView(context);
        mGender = new TextView(context);
        mUserId = new TextView(context);

        final int headRound = getResources().getDimensionPixelSize(
                R.dimen.mine_focus_item_head_round);
        LayoutParams params = new LayoutParams(headRound, headRound);
        LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        mHeader.setLayoutParams(params);
        mNickName.setLayoutParams(textParams);
        mPhone.setLayoutParams(textParams);
        mAgeArea.setLayoutParams(textParams);
        mLocation.setLayoutParams(textParams);
        mSkin.setLayoutParams(textParams);
        mGender.setLayoutParams(textParams);
        mUserId.setLayoutParams(textParams);
        mEmail.setLayoutParams(textParams);
        mPassword.setLayoutParams(textParams);

        setTextStyle(mNickName);
        setTextStyle(mPhone);
        setTextStyle(mAgeArea);
        setTextStyle(mLocation);
        setTextStyle(mSkin);
        setTextStyle(mGender);
        setTextStyle(mUserId);
        setTextStyle(mEmail);
        setTextStyle(mPassword);

        handleInfoChanged();
    }

    private void setTextStyle(TextView tv) {
        if (tv == null) {
            return;
        }
        // R.dimen.mine_mine_head_info_text_size
        final float scale = getResources().getDisplayMetrics().density;
        float size = (getResources().getDimensionPixelSize(
                R.dimen.mine_mine_head_info_text_size)
                / scale + 0.5f);
        tv.setTextSize(size);
        tv.setTextColor(getResources().getColor(
                R.color.mine_personal_info_text_color));
    }

    private List<MineGroup> genGroups() {
        OnClickListener l = this;
        List<MineGroup> groups = new ArrayList<FragmentMineList.MineGroup>(1);
        List<MineItem> i1 = new ArrayList<MineItem>(1);
        i1.add(new MineItem(R.string.info_header, 0, R.string.info_header,
                false, true, mHeader, l));
        groups.add(new MineGroup(true, true, true, i1));

        List<MineItem> i2 = new ArrayList<MineItem>(5);
        i2.add(new MineItem(R.string.info_nick_name, 0,
                R.string.info_nick_name, false, true, mNickName, l));
        i2.add(new MineItem(R.string.info_userid, 0, R.string.info_userid,
                false, false, mUserId, l));
        i2.add(new MineItem(R.string.info_bind_phone, 0,
                R.string.info_bind_phone, false, true, mPhone, l));

        i2.add(new MineItem(R.string.info_bind_email, 0,
                R.string.info_bind_email, false, true, mEmail, l));
        i2.add(new MineItem(R.string.info_amend_password, 0,
                R.string.info_amend_password, false, true, mPassword, l));

        groups.add(new MineGroup(true, true, true, i2));

        List<MineItem> i3 = new ArrayList<MineItem>(4);
        i3.add(new MineItem(R.string.info_gender, 0, R.string.info_gender,
                false, true, mGender, l));
        i3.add(new MineItem(R.string.info_skin, 0, R.string.info_skin, false,
                true, mSkin, l));
        i3.add(new MineItem(R.string.info_age_area, 0, R.string.info_age_area,
                false, true, mAgeArea, l));
        i3.add(new MineItem(R.string.info_location, 0, R.string.info_location,
                false, true, mLocation, l));
        groups.add(new MineGroup(true, true, true, i3));
        return groups;
    }

    private void getImageFromAlbum() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

        // Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        // intent.setType("image/*");
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        // intent.putExtra("crop", "true");
        // intent.putExtra("aspectX", 1);
        // intent.putExtra("aspectY", 1);
        // intent.putExtra("outputX", 200);
        // intent.putExtra("outputY", 200);
        // intent.putExtra("scale", true);
        // intent.putExtra("return-data", false);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        // intent.putExtra("outputFormat",
        // Bitmap.CompressFormat.JPEG.toString());
        // intent.putExtra("noFaceDetection", true); // no face detection
        // startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
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
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Slog.w("cropImageUri Uri: " + uri.toString());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String furl = PathUtils.getPath(this, uri);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            cropImageUri(imageUri, 200, 200, REQUEST_CODE_PICK_RESULT);
        } else if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (data == null)
                return;
            cropImageUri(data.getData(), 200, 200, REQUEST_CODE_PICK_RESULT);
        } else if (REQUEST_CODE_PICK_RESULT == requestCode) {
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
                        mHeader.setImageBitmap(photo);
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
                mHeader.setImageURI(uri);
            }
        } else if (REQUEST_CODE_EDIT_NICKNAME == requestCode) {
            Slog.d("REQUEST_CODE_EDIT_NICKNAME!!!!!");
            RegRadarReq mRadarReq = new RegRadarReq();
            mRadarReq.setName(SharePreCacheHelper.getNickName(this));
            MineInfoUtils.reloadDefalutUserInfo(getApplicationContext(),
                    mRadarReq);
            mUpdateGetUser.updateUserAsync(mRadarReq, mUserUpdateCallback);
        } else if (REQUEST_CODE_EDIT_GENDER == requestCode) {
            Slog.d("REQUEST_CODE_EDIT_GENDER!!!!!");
            RegRadarReq mRadarReq = new RegRadarReq();
            mRadarReq.setGender(SharePreCacheHelper.getGender(this));
            MineInfoUtils.reloadDefalutUserInfo(getApplicationContext(),
                    mRadarReq);
            mUpdateGetUser.updateUserAsync(mRadarReq, mUserUpdateCallback);
        } else if (REQUEST_CODE_EDIT_SKIN_TYPE == requestCode) {
            Slog.d("REQUEST_CODE_EDIT_SKIN_TYPE!!!!");
            RegRadarReq mRadarReq = new RegRadarReq();
            mRadarReq.setSkinQuality(SharePreCacheHelper.getSkinType(this));
            SharePreCacheHelper.setPreferChooseSkinType(
                    getApplicationContext(), true);
            mRadarReq.setPreferChoseSkin(true);
            MineInfoUtils.reloadDefalutUserInfo(getApplicationContext(),
                    mRadarReq);
            mUpdateGetUser.updateUserAsync(mRadarReq, mUserUpdateCallback);
        } else if (REQUEST_CODE_EDIT_AGE == requestCode) {
            Slog.d("REQUEST_CODE_EDIT_AGE!!!!");
            Date mdate = SharePreCacheHelper.getBirthDay(this);
            if (mdate != null) {
                RegRadarReq mRadarReq = new RegRadarReq();
                mRadarReq.setBirthday(mdate.getTime());
                MineInfoUtils.reloadDefalutUserInfo(getApplicationContext(),
                        mRadarReq);
                mUpdateGetUser.updateUserAsync(mRadarReq, mUserUpdateCallback);
            } else {
                Slog.e("mbirthday is NULL!!!!!");
            }

        } else if (REQUEST_CODE_EDIT_LOCATION == requestCode) {
            Slog.d("REQUEST_CODE_EDIT_LOCATION!!!!");
            RegRadarReq mRadarReq = new RegRadarReq();
            mRadarReq.setLocation(SharePreCacheHelper.getArea(this));
            MineInfoUtils.reloadDefalutUserInfo(getApplicationContext(),
                    mRadarReq);
            mUpdateGetUser.updateUserAsync(mRadarReq, mUserUpdateCallback);
        } else if (requestCode == REQUEST_CODE_EDIT_PHONE) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String phone = bundle.getString("phone");
                mPhone.setText(phone);
                SharePreCacheHelper.setBindedPhone(getApplicationContext(),
                        phone);
            }
        }
    }

    public static class Info implements Serializable {
        String nickname;
        String phone;
        String ageArea;
        String location;
        String skin;

        public Info() {
        }

        public Info(String nickname, String phone, String ageArea,
                    String location, String skin) {
            this.nickname = nickname;
            this.phone = phone;
            this.ageArea = ageArea;
            this.location = location;
            this.skin = skin;
        }

    }
}
