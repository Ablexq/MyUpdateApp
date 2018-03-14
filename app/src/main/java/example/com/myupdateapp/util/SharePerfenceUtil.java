package example.com.myupdateapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import example.com.myupdateapp.MyApplication;

public class SharePerfenceUtil {

    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "share_date";


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public static void setParam(String key, Object object) {

        if (object == null || TextUtils.isEmpty(key)) {
            return;
        }

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }

        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    public static Object getParam(String key, Object defaultObject) {

        if (TextUtils.isEmpty(key)) {
            return defaultObject;
        }

        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }


    /*****************************************缓存***************************************************************/

    //存缓存
    public static void putBean(Context context, String key, Object obj) {
        if (obj instanceof Serializable) {// obj必须实现Serializable接口，否则会出问题
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                String string64 = new String(Base64.encode(baos.toByteArray(), 0));
                SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
                editor.putString(key, string64).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new IllegalArgumentException("the obj must implement Serializble");
        }

    }

    //取缓存
    public static Object getBean(Context context, String key) {
        Object obj = null;
        try {
            String base64 = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString(key, "");
            if (base64.equals("")) {
                return null;
            }
            byte[] base64Bytes = Base64.decode(base64.getBytes(), 1);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 清除sp缓存
     */
    public static void clearSharePrefrences() {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

}
