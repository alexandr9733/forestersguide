package com.example.forestersguide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    //имя файла
    private static String DB_NAME = "db.sqlite3";
    //контекст бд
    final Context context;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 3);
        this.context = context;
    }

    @Override //перегруженный метод когда бд создается
    public void onCreate(SQLiteDatabase db) {
        //создаем таблицы
        db.execSQL("CREATE TABLE 'Культура' (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'наименование' text NOT NULL," +
                "'описание' text," +
                "'изображение' blob);");

    }
    //перегруженный метод при обновлении бд
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //удаляем все таблицы
        db.execSQL("DROP TABLE IF EXISTS 'Культура'");
        //вызываем создание бд
        this.onCreate(db);
    }

    @SuppressLint("Range")
    public List<ListCulture> getListMashrooms(String strLike){
        ListCulture listM = null;
        List<ListCulture> mList = new ArrayList<>();
        Cursor cursor;

        if (strLike.equals(null) || strLike.equals(""))
            cursor = this.getWritableDatabase().rawQuery("SELECT * FROM Культура", null);
        else
            cursor = this.getWritableDatabase().rawQuery("SELECT * FROM Культура where наименование LIKE '%"+strLike+"%'", null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){

            Bitmap blob=null;
            if(!cursor.isNull(cursor.getColumnIndex("изображение")))
                blob=BitmapFactory.decodeByteArray(cursor.getBlob(cursor.getColumnIndex("изображение")),
                    0,cursor.getBlob(cursor.getColumnIndex("изображение")).length);

            listM = new ListCulture(cursor.getInt(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("наименование")),
                    cursor.getString(cursor.getColumnIndex("описание")), blob);

            mList.add(listM);
            cursor.moveToNext();
        }
        cursor.close();
        return mList;
    }

    @SuppressLint("Range")
    public byte[] getBlob(String id, String tableName, String colName){
        byte[] arr;

        try{
            ListCulture listM = null;
            List<ListCulture> mList = new ArrayList<>();
            Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM "+tableName+" where _id="+id, null);
            cursor.moveToFirst();
            arr=cursor.getBlob(cursor.getColumnIndex(colName));
            cursor.close();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return arr;
    }

    public boolean updateBlob(String id, String tableName, String colName, byte[] bytes){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try{
            String sql   =   "UPDATE  "+tableName+" set "+colName+"=? where _id="+id;

            SQLiteStatement insertStmt      =   db.compileStatement(sql);
            insertStmt.clearBindings();
            insertStmt.bindBlob(1, bytes);
            insertStmt.executeInsert();

            db.setTransactionSuccessful();
            db.endTransaction();

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //метод для открытия курсора по выборке
    public Cursor query(String query)
    {
        Cursor cr=null;
        try //пытаемся открыть курсор
        { cr=this.getWritableDatabase().rawQuery(query, null);}
        catch (Exception e) //ловим исключения
        {   //собщаем пользователю ошибку во всплываем сообщении, если она случилась
            Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
            //позиционрование сообщения
            toast.setGravity(Gravity.CENTER, 0, 0);
            //отображаем
            toast.show();
            return null;
        }
        return cr;
    }

    //метод для вызова вставки, обновления данных и прочих команд
    public boolean sqlExec(String query)
    {
        String err;
        try{this.getWritableDatabase().execSQL(query);}
        catch (Exception e)
        {
            Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
        return true;
    }

    //метод получения одних данных из курсора, по заданной выборке и полю
    @SuppressLint("Range")
    public String getOneData(String query, String nameColumn, boolean printErr)
    {
        String result=null;
        try {//открываем курсор
            Cursor cr = this.getWritableDatabase().rawQuery(query, null);
            //перемещаем указатель на начало данных
            cr.moveToFirst();
            //если в курсоре пусто то возвратим null
            if (cr.getCount() == 0) return null;
            //получаем данные
            result=cr.getString(cr.getColumnIndex(nameColumn));
        }
        catch (Exception e)
        {
            if (printErr) {
                Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            return null;
        }
        return result;
    }
}