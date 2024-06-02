package com.sq.simataquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CriticalDB extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "Critical.db";
    public static final int DATABASE_VERSION = 1;

    public  static final String TABLE_NAME = "my_table";
    public  static final String COLUMN_ID = "_id";
    public  static final String COLUMN_NUM = "question_num";


    public CriticalDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_NUM + " INTEGER, UNIQUE("+COLUMN_ID +", "+COLUMN_NUM+"));";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public void add_question_num(int num){
        if(!CheckIsDataAlreadyInDBorNot(num)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_NUM,num);
            db.insert(TABLE_NAME, null,cv);
        }
    }

    public Cursor get_nums(){
        Cursor cursor = null;
        String query = "SELECT * FROM " +TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        if( db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }

    public void delete_question(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "_id=?", new String[]{id});
    }

    public boolean CheckIsDataAlreadyInDBorNot(int num) {
        SQLiteDatabase sqldb = this.getReadableDatabase();
        String Query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NUM + " = " + num;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void deleteALL(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME);
    }

}
