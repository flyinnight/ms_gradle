package com.dilapp.radar.util;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.exceptions.EaseMobException;

public class EMChatUtils {

    private static final String COMM_PWD = "123456";

    /**
     * 必须在application中调用
     *
     * @param context
     */
    public static void init(Context context) {
        int pid = android.os.Process.myPid();
        EMChat.getInstance().setAutoLogin(false);
        // EMChat.getInstance().setInitSingleProcess(false);
        String processAppName = getAppName(context, pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process
        // name就立即返回

        if (processAppName == null
                || !processAppName.equalsIgnoreCase("com.dilapp.radar")) {
            Slog.e("enter the service process! : " + processAppName);
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        } else {
            EMChat.getInstance().init(context);
            // EMChat.getInstance().setDebugMode(true);
            // setDebugMode(true);
            // EMChatManager.getInstance().getChatOptions().setNotificationEnable(false);
        }
    }

    /**
     * 服务端会集成，仅供测试，极端建议不使用
     *
     * @param context
     * @param username
     * @param pwd
     */
    public static void startRegister(final Context context,
                                     final String username, final String pwd) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    EMChatManager.getInstance().createAccountOnServer(username,
                            pwd);
                } catch (final EaseMobException e) {
                    // 注册失败
                    int errorCode = e.getErrorCode();
                    if (errorCode == EMError.NONETWORK_ERROR) {
                        Toast.makeText(context.getApplicationContext(),
                                "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                    } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                        Toast.makeText(context.getApplicationContext(),
                                "用户已存在！", Toast.LENGTH_SHORT).show();
                    } else if (errorCode == EMError.UNAUTHORIZED) {
                        Toast.makeText(context.getApplicationContext(),
                                "注册失败，无权限！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context.getApplicationContext(),
                                "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        }).start();
    }

    /**
     * debugMode == true 时为打开，sdk 会在log里输入调试信息
     *
     * @param debugMode 在做代码混淆的时候需要设置成false
     */
    public static void setDebugMode(boolean mode) {
        // 在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
        EMChat.getInstance().setDebugMode(mode);
    }

    public static void startLogin(final String userID) {

        // if (EMChat.getInstance().isLoggedIn()) {
        // Slog.e("EMChat has Login");
        // return;
        // }

        EMChatManager.getInstance().login(userID, COMM_PWD, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    // EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    Slog.i("环信登陆成功-- : " + userID + " : " + COMM_PWD);
                } catch (Exception e) {
                    Slog.i("Login Failed ", e);
                }
            }

            @Override
            public void onProgress(int arg0, String arg1) {

            }

            @Override
            public void onError(int arg0, String arg1) {
                Slog.i("Login onError : " + arg0 + " : " + arg1);
                Slog.e("环信登陆失败-- : " + userID + " : " + COMM_PWD);
            }
        });
    }

    public static void startLogin(Context context) {

        // if (EMChat.getInstance().isLoggedIn()) {
        // Slog.e("EMChat has Login");
        // return;
        // }

        String userId = SharePreCacheHelper.getUserID(context);
        String passWord = SharePreCacheHelper.getPassword(context);
        String EMUserId = SharePreCacheHelper.getEMUserId(context);

        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(passWord)
                && !TextUtils.isEmpty(EMUserId)) {
            String EMPwd = encodeEMPwd(passWord, userId);
            Slog.i("passWord:" + passWord + "--userId:" + userId);
            EMChatManager.getInstance().login(EMUserId, EMPwd,
                    new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            try {
                                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                                // ** manually load all local groups and
                                // EMGroupManager.getInstance().loadAllGroups();
                                EMChatManager.getInstance()
                                        .loadAllConversations();
                                Slog.i("环信登陆成功");
                            } catch (Exception e) {
                                Slog.i("Login Failed ", e);
                            }
                        }

                        @Override
                        public void onProgress(int arg0, String arg1) {

                        }

                        @Override
                        public void onError(int arg0, String arg1) {
                            Slog.i("Login onError : " + arg0 + " : " + arg1);
                            Slog.e("环信登陆失败 : " + arg0 + " : " + arg1);
                        }
                    });
        }
    }

    private static String encodeEMPwd(String pwd, String userId) {
        MD5PasswordEncoder encoder = new MD5PasswordEncoder();
        String encoded = encoder.encodeEMPwd(pwd, userId);
        return encoded;
    }

    /**
     * 登出，在设置，登出时调用。
     */
    public static void startLogout() {
        if (EMChat.getInstance().isLoggedIn()) {
            EMChatManager.getInstance().logout();
        }
    }

    /**
     * 用来判定是否有新消息。
     *
     * @return
     */
    public static int getAllUnreadCount() {
        return EMChatManager.getInstance().getUnreadMsgsCount();
    }

    /**
     * 判断否个会话是否有新消息
     *
     * @param userid
     * @return
     */
    public static int getUnReadyCountById(String userid) {
        int result = 0;
        EMConversation mConversation = EMChatManager.getInstance()
                .getConversation(userid);
        if (mConversation != null) {
            result = mConversation.getUnreadMsgCount();
        }
        return result;
    }

    /*******************/
    public static String getAppName(Context context, int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = context.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
                    .next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm
                            .getApplicationInfo(info.processName,
                                    PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

}
