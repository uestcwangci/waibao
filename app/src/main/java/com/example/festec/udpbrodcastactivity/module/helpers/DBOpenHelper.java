package com.example.festec.udpbrodcastactivity.module.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    //定义创建数据表tb_dict的SQL语句
    final String CREATE_TABLE_SQL = "create table tb_resource(_id integer primary key autoincrement , address , resourceCode,url)";

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version); //重写构造方法并设置工厂为null
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL); //创建系统资源编码存储表
    }

    // 重写基类的onUpgrade()方法，以便数据库版本更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //提示版本更新并输出旧版本信息与新版本信息
        System.out.println("---版本更新-----" + oldVersion + "--->" + newVersion);
    }
}
