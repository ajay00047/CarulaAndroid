package com.pola.app.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pola.app.beans.SignUpRequestBean;
import com.pola.app.beans.UserBean;
import com.pola.app.beans.VerifyOTPResponseDataBean;

/**
 * Created by Ajay on 04-Sep-17.
 */
public class DBHelper extends SQLiteOpenHelper {

    Context context;
    private SharedPreferences sessionData;
    private SharedPreferences.Editor editor;

    public DBHelper(Context context) {
        super(context, "carula.db", null, 1);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    //compulsory methods
    @Override
    public void onCreate(SQLiteDatabase database) {

        // create user table
        String userQuery = "CREATE TABLE USERS ( user_id INTEGER PRIMARY KEY AUTOINCREMENT, first_name TEXT,last_name TEXT,mobile TEXT, dp_path TEXT, created_date TEXT, pass_key TEXT)";
        database.execSQL(userQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {

    }

    //custom methods
    public int getUserCount() {

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT user_id FROM users";

        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        Log.e(Constants.LOG_TAG, "userCount : " + count);
        database.close();
        return count;
    }

    public UserBean getUserDetails() {

        int count = getUserCount();
        SQLiteDatabase database = this.getReadableDatabase();
        UserBean bean = null;
        if(count == 1){
            bean = new UserBean();

            String selectQuery = "SELECT user_id,first_name,last_name,mobile,dp_path,pass_key FROM users";
            Cursor cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    bean.setUserId(cursor.getInt(0));
                    bean.setFirstName(cursor.getString(1));
                    bean.setLastName(cursor.getString(2));
                    bean.setMobile(cursor.getString(3));
                    bean.setDpPath(cursor.getString(4));
                    bean.setPassKey(cursor.getString(5));

                } while (cursor.moveToNext());
            }
        }
        database.close();
        return bean;
    }

    public void clearUsersTable() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("users", "1", null);
        database.close();
    }

    public void insertUser(VerifyOTPResponseDataBean bean){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("first_name", bean.getFirstName());
        values.put("last_name", bean.getLastName());
        values.put("mobile", bean.getMobile());
        values.put("dp_path", bean.getDpPath());
        values.put("pass_key", bean.getPassKey());
        database.insert("users", null, values);
        database.close();
    }
}
