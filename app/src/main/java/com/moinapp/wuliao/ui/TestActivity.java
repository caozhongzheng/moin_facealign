package com.moinapp.wuliao.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.login.LoginApi;
import com.moinapp.wuliao.modules.login.model.BaseLoginResult;
import com.moinapp.wuliao.modules.mine.MineApi;
import com.moinapp.wuliao.modules.mine.model.GetMyFansResult;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StyledText;
import com.moinapp.wuliao.util.MD5;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.XmlUtils;

import org.apache.http.Header;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.OnClick;

/**
 * Created by liujiancheng on 15/9/6.
 */
public class TestActivity extends ActionBarActivity  implements View.OnClickListener {
    private ILogger MyLog = LoggerFactory.getLogger("test");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake);

//        testIndex(" @26  @HAHA  @Java132456  @mei ");
//        testIndex("@26 @HAHA @Java132456 @mei ");

        testAngle(1, 0);
        testAngle(-1, 0);
        testAngle(0, 1);
        testAngle(0, -1);
        // 1
        testAngle(40, 10);
        testAngle(10, 40);
        // 2
        testAngle(-10, 40);
        testAngle(-40, 10);
        // 3
        testAngle(-40, -10);
        testAngle(-10, -40);
        // 4
        testAngle(10, -40);
        testAngle(40, -10);

        /*
        APK大的原因有:dpi多样化(lmtvhxnodpi);openLib;高质量UI;其他
        1:proguard
        对java代码混淆,优化和压缩.tree-shaking.[重度依赖反射,需要进行配置哪些代码不用处理]
        2:lint
        ./gradlew lint命令查找/res里的unusedResources,如果这些资源真的没有被反射所用到,就可以删除了
        3:defaultConfig{resConfigs("xhdpi", "en")}
        4:9-patches,ImageOptim
        5:armeabi,x86>armeabi-v7a,arm64-v8a
        6:android_tint,android_tintMode,老版本用colorFilter对asset文件重新着色
        7:RotateDrawable
        <?xml version="1.0" encoding="utf-8">
        <rotate xmlns:android="http://schemas.android.com/apk/res/android"
            android:drawable="@drawable/ic_arrow_expand"
            android:fromDegrees="-180"
            android:pivotX="50%"
            android:pivotY="50%"
            android:toDegrees="180"/>
        8:Adobe After Effect to VectorDrawable
        9:之所以不让服务器根据目标设备配置来打包和分发不同的APK,就是为了保证Android生态的一个重要
        特诊:配置热置换.  而且如果是这样,还可能引起app崩溃.

         */
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void testAngle(int x, int y) {
        double radians = Math.atan2(x, y);
        double angle = Math.toDegrees(radians);
        MyLog.i("angle=["+ angle +"] delX,delY = " + x + ", " + y);
    }

    /**
     * 获取和保存当前屏幕的截图
     */
    private void GetandSaveCurrentImage()
    {
        //1.构建Bitmap
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();

        Bitmap Bmp = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );

        //2.获取屏幕
        View decorview = this.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();
        Toast.makeText(TestActivity.this, "截屏BMP" + (Bmp == null ? "不存在" : "存在"), Toast.LENGTH_SHORT).show();

        String SavePath = getSDCardPath()+"/msc/ScreenImage";

        //3.保存Bitmap
        try {
            File path = new File(SavePath);
            //文件
            String filepath = SavePath + "/Screen_1.png";
            File file = new File(filepath);
            if(!path.exists()){
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();

                Toast.makeText(TestActivity.this, "截屏文件已保存至SDCard/AndyDemo/ScreenImage/下", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    /**
     * 获取SDCard的目录路径功能
     * @return
     */
    private String getSDCardPath(){
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(sdcardExist){
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }
    /** 获取一段文字的长度 */

    private int getLenByPaint(String textString, Paint paint) {
        int textLen = (int) paint.measureText(textString);
        MyLog.i("\n获取一段文字[" + textString + "]的长度 measureText = " + textLen);
        Rect bound = new Rect();
        paint.getTextBounds(textString, 0, textString.length(), bound);
        MyLog.i("获取一段文字[" + textString + "]的长度 getTextBounds = " + (bound.right - bound.left));
        textLen = textLen > (bound.right - bound.left) ? textLen : (bound.right - bound.left);
        MyLog.i("获取一段文字[" + textString + "]的长度 textLen = " + textLen);
        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
        int fontHeight = fmi.descent - fmi.ascent;
        MyLog.i("获取一段文字[" + textString + "]的高度  fontHeight = " + fontHeight);
        return textLen;
    }
    private void testTxtWH(int size) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(3);
        paint.setTextSize(size);
//        paint.setColor(Color.CYAN);
//        canvas.drawRect(targetRect, paint);
        paint.setColor(Color.BLACK);
        String txt1 = "我";
        String txt2 = "我们";
        String txt3 = "一路向";
        String txt4 = "一路向西";

        MyLog.i(size + ", getLenByPaint=[" + getLenByPaint(txt1, paint) + "]");
        MyLog.i(size + ", getLenByPaint=[" + getLenByPaint(txt2, paint) + "]");
        MyLog.i(size + ", getLenByPaint=[" + getLenByPaint(txt3, paint) + "]");
        MyLog.i(size + ", getLenByPaint=[" + getLenByPaint(txt4, paint) + "]");
    }

    private void testIndex(String text) {

            if(StringUtil.isNullOrEmpty(text)) {
                return;
            }
            List<StyledText> sTextsStartList = new ArrayList<>();

        TreeMap<String, String> atUsersMap = new TreeMap<>();

        int sTextLength = -1;
            String temp = text;
            int lengthFront = 0;//记录被找出后前面的字段的长度
            int start = -1;
            int end = -1;
            do
            {
                MyLog.i("temp=[" + temp+"]");
                start = temp.indexOf("@", end);
                end = temp.indexOf(" ", start);
                MyLog.i("start=" + start + ", end=" + end);

                if(start != -1)
                {
//                    start = start + lengthFront;
                    sTextLength = end - start;
                    MyLog.i("start=" + start + ", sTextLength=" + sTextLength);
                    sTextsStartList.add(new StyledText(start, sTextLength, text.substring(start + 1, end)));

                    atUsersMap.put("" + start, text.substring(start+1, end));
//                    lengthFront = start + sTextLength;
//                    MyLog.i("lengthFront=" + lengthFront);
//                temp = text.substring(lengthFront);
                }

            }while(start != -1);

            int i = 0;
            for(StyledText st : sTextsStartList)
            {
                MyLog.i(i+", StyledText=" + st.toString());
//                MyLog.i(i+", StyledText=[" + temp.substring(st.getStart(), st.getEnd()));
//                styledText.setSpan(
//                        new ForegroundColorSpan(Color.parseColor("#FF0000")),
//                        st.getStart(),
//                        st.getEnd(),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                i++;
            }

        String del = "mei";
        if(atUsersMap.containsValue(del)) {
            String key = null;
            Iterator iter = atUsersMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
//            Object key = entry.getKey();
//            Object val = entry.getValue();
                if(entry.getValue().equals(del)) {
                    key = (String) entry.getKey();
                }
            }
            if(!StringUtil.isNullOrEmpty(key)) {
                atUsersMap.remove(key);
            }
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        Iterator iter = atUsersMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
//            Object key = entry.getKey();
//            Object val = entry.getValue();
            if(first) {
                sb.append(entry.getValue());
                first = false;
            } else {
                sb.append(",").append(entry.getValue());
            }
        }
//        for(Map.Entry<String, String> entry : atUsersMap.entrySet()) {
//            if(first) {
//                sb.append(entry.getValue());
//                first = false;
//            } else {
//                sb.append(",").append(entry.getValue());
//            }
//        }
        MyLog.i("atUsersMap , StyledText=[" + sb.toString() + "]");

//            return styledText;
    }

    private void testSort() {
        Log.i("sort", "--------原始-----------\n");
        ArrayList<TypeItem> list = new ArrayList<>();
        list.add(new TypeItem(1));
        list.add(new TypeItem(2));
        list.add(new TypeItem(3));
        list.add(new TypeItem(4));
        list.add(new TypeItem(5));
        list.add(new TypeItem(6));

        int end = 0;
        List<TypeItem> tmp = list.subList(0,end);
        Log.i("sort", "---tmp.isnull=" + (null == tmp));
        Log.i("sort", "---tmp.size=" + tmp.size());
        for (int i = 0; i < tmp.size(); i++) {
            Log.i("sort", i+"---"+tmp.get(i).getId());
        }

        Log.i("sort", "---------------------------------");
        end = list.size();
        tmp = list.subList(end, list.size());
        Log.i("sort", "---tmp.isnull="+(null==tmp));
        Log.i("sort", "---tmp.size="+tmp.size());
        for (int i = 0; i < tmp.size(); i++) {
            Log.i("sort", i+"---"+tmp.get(i).getId());
        }
//
//        Collections.sort(list, new Comparator<TypeItem>() {
//            @Override
//            public int compare(TypeItem lhs, TypeItem rhs) {
//                // 排序方式是234561
//                return rhs.getId() - lhs.getId();
//            }
//        });
//        Log.i("sort", "--------排序1-----------\n");
//        for (int i = 0; i < list.size(); i++) {
//            Log.i("sort", i+"---"+list.get(i).getId());
//        }
//
//
//        int end = 0;
//        for (int i = 0; i < list.size(); i++) {
//            if(list.get(i).getId() == 1) {
//                end = i;
//                break;
//            }
//            Log.i("sort", i + "-排序2---" + list.get(i).getId());
//        }
//        List<TypeItem> tmp = list.subList(0,end);
//        Collections.sort(tmp, new Comparator<TypeItem>() {
//            @Override
//            public int compare(TypeItem lhs, TypeItem rhs) {
//                // 排序方式是234561
//                return lhs.getId() - rhs.getId();
//            }
//        });
//
//        ArrayList<TypeItem> list2 = new ArrayList<>();
//        list2.addAll(tmp);
//        list2.addAll(list.subList(end,list.size()));
//        Log.i("sort", "---------排序3-------------end=" + end + "\n");
//        for (int i = 0; i < list2.size(); i++) {
//            Log.i("sort", i+"---"+list2.get(i).getId());
//        }

    }



    class TypeItem {
        public int getId() {
            return id;
        }

        private int id;

        public TypeItem(int id) {
            this.id = id;
        }
    }
    @Override
    @OnClick({R.id.button, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button1:
                getEmoji();
                break;
            case R.id.button2:
                sendEmoji();
                break;
            case R.id.button3:
                getPhoneContacts();
                break;
            case R.id.button4:
                getSIMContacts();
                break;
            case R.id.button5:
                testTxtWH(29);
                break;
            case R.id.button6:
                testTxtWH(30);
                testTxtWH(45);
                break;
            case R.id.button7:
                testTxtWH(31);
                break;

        }
    }

    private static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER,
//            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, Phone.CONTACT_ID
    };

    /**得到手机通讯录联系人信息**/
    private void getPhoneContacts() {
        ContentResolver resolver = getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            int PHONES_NUMBER_INDEX = phoneCursor.getColumnIndex(Phone.NUMBER);
            int PHONES_DISPLAY_NAME_INDEX = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);
//            int PHONES_CONTACT_ID_INDEX = phoneCursor.getColumnIndex(Phone.CONTACT_ID);
//            int PHONES_PHOTO_ID_INDEX = phoneCursor.getColumnIndex(Phone.PHOTO_ID);

            int i = 1;
            MyLog.i("得到手机通讯录联系人信息-----: ");
            while (phoneCursor.moveToNext()) {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);

                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

//                //得到联系人ID
//                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
//
//                //得到联系人头像ID
//                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
//
//                //得到联系人头像Bitamp
//                Bitmap contactPhoto = null;
//
//                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
//                if(photoid > 0 ) {
//                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
//                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
//                    contactPhoto = BitmapFactory.decodeStream(input);
//                }else {
//                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_favor);
//                }

                MyLog.i(i++ + "-----: " + contactName + ", " + phoneNumber);
            }

            phoneCursor.close();
        }
    }
    /**得到手机SIM卡联系人人信息**/
    private void getSIMContacts() {

        try {
            ContentResolver resolver = getContentResolver();
            // 获取Sims卡联系人
            Uri uri = Uri.parse("content://icc/adn");
            Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);

            if (phoneCursor != null) {
                int i = 1;
                MyLog.i("得到手机SIM卡联系人信息-----: ");
                int PHONES_NUMBER_INDEX = phoneCursor.getColumnIndex(Phone.NUMBER);
                int PHONES_DISPLAY_NAME_INDEX = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);
                MyLog.i("phoneCursor.getCount-----: " + phoneCursor.getCount());
                String[] columns = phoneCursor.getColumnNames();
                for (int j = 0; j < columns.length; j++) {
                    String column = columns[j];
                    MyLog.i(j+" column--: " + column + ", index= " + phoneCursor.getColumnIndex(column));
                }
//                I/test    ( 3445): 0 column--: name, index= 0 (TestActivity.java:147)
//                I/test    ( 3445): 1 column--: number, index= 1 (TestActivity.java:147)
//                I/test    ( 3445): 2 column--: emails, index= 2 (TestActivity.java:147)
//                I/test    ( 3445): 3 column--: _id, index= 3 (TestActivity.java:147)
//                I/test    ( 3445): 4 column--: anr, index= 4 (TestActivity.java:147)

                while (phoneCursor.moveToNext()) {


                    // 得到手机号码
                    String phoneNumber = phoneCursor.getString(1);
                    // 当手机号码为空的或者为空字段 跳过当前循环
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;
                    // 得到联系人名称
                    String contactName = phoneCursor.getString(0);

                    //Sim卡中没有联系人头像
                    MyLog.i(i++ + "*****: " + contactName + ", " + phoneNumber);
                }

                phoneCursor.close();
            }
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    class Idols {
        private int result;
        private List<UserInfo> idol;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public List<UserInfo> getIdol() {
            return idol;
        }

        public void setIdol(List<UserInfo> idol) {
            this.idol = idol;
        }
    }

    private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//            LoginUserBean loginUserBean = XmlUtils.toBean(LoginUserBean.class, arg2);
            Log.i("ljc","recevied response =" + new String(arg2));
            GetMyFansResult userInfo = XmlUtils.JsontoBean(GetMyFansResult.class, arg2);
            if (userInfo != null) {
                Log.i("ljc", "userinfo =" + userInfo.getFans().get(0).getUsername());
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            AppContext.showToast("网络出错" + arg0);
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    };

    private AsyncHttpResponseHandler mHandlerLogin = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            String response = new String(arg2);
            Log.i("ljc", "recevied response =" + response);
            if (!StringUtils.isEmpty(response)) {
                BaseLoginResult result =  new Gson().fromJson(response, BaseLoginResult.class);
                if (result.getResult() == 1) {
                    ClientInfo.setUID(result.getUid());
                    ClientInfo.setPassport(result.getPassport());
                    Log.i("ljc", "set uid =" + result.getUid());
                    Log.i("ljc", "set password =" + result.getPassport());
                }
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            AppContext.showToast("网络出错" + arg0);
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    };

    //测试登陆
    private void testLogin() {
        LoginApi.login("13683179542", MD5.md5("12345678"), mHandlerLogin);
    }

    //测试注册
    private void testRigister() {
//        LoginApi.registerUser("15601036268", MD5.md5("12345678"), "9905", mHandler);
    }

    //测试电话号码唯一性检查
    private void checkPhone() {
        LoginApi.checkPhone("15601036268", mHandler);
    }

    //得到短信验证码
    private void getSms() {
        LoginApi.getPhoneSms("15601036268", mHandler);
    }

    //通过手机找回密码 ??????????????????????
    private void retrievePasswordByPhone() {
        LoginApi.retrievePasswordByPhone("13683179542", "3956", mHandlerLogin);
    }

    //用户退出登陆
    private void userLogout() {
        LoginApi.userLogout(mHandler);
    }

    //更新密码
    private void updatePassword() {
        MineApi.updatePassword(MD5.md5("12345678"), mHandler);
    }

    //上传头像
    private void uploadAvatar() {
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator +
                "Moin/MOIN/1.jpg";
        File f = new File(path);
        if (f != null && f.exists()) {
            Log.i("ljc","avatar file exists!!");
        }
        MineApi.uploadAvatar(path, mHandler);
    }

    //更新用户信息
    private void updateUserInfo() {
        UserInfo user = new UserInfo();
        user.setUsername("liujiancheng");
        user.setSex("male");
        MineApi.updateUserInfo(user, mHandler);
    }

    //检查用户名的唯一性 ?????????????????
    private void checkUsername() {
        LoginApi.checkUserName("liujiancheng", mHandler);
    }

    //检查用户类型
    private void checkUserType() {
        LoginApi.getUserLoginType(mHandler);
    }

    //用户反馈
    private void userFeedback() {
        LoginApi.userFeedback("吐槽", "zhengwen:fsdfsdfsd", mHandler);
    }

    // 获取用户信息
    private void getUserInfo() {
        MineApi.getUserInfo("555c032d9f8bdff37813cfde", mHandler);
    }

    // 获取我关注的人列表 ????????????????
    private void getMyIdols() {
//        LoginApi.getMyIdols(null,null, mHandler);
    }

    // 获取我的粉丝列表 ?????????????????
//    private void getMyFans() {
//        LoginApi.getMyFans("555c032d9f8bdff37813cfde",null, mHandler);
//    }

    // 关注／取消关注 ?????????????????
    private void FollowUser() {
        MineApi.followUser("555c032d9f8bdff37813cfde", 1, mHandler);
        MineApi.followUser("55ebfcba80ea79fc71492e82", 1, mHandler);
        MineApi.followUser("55ade9be0bdcb6025e2f0f61", 1, mHandler);
        MineApi.followUser("55e5669908204a70286c0f18", 1, mHandler);
    }

    // 获取表情专辑 ?????????????????
    private void getEmoji() {
//        LoginApi.getEmoji("花千骨", "花千骨", "2D", null, mHandler);
    }

    // 发送表情 ?????????????????
    private void sendEmoji() {
//        LoginApi.sendEmoji(1, "555c032d9f8bdff37813cfde", "QQ", mHandler);
    }
}
