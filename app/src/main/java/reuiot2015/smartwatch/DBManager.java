package reuiot2015.smartwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

public class DBManager {

    private Context context;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public DBManager(Context c) {
        this.context = c;
    }

    public DBManager open() throws SQLException {
        this.dbHelper = new DatabaseHelper(this.context);
        this.database = this.dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.dbHelper.close();
    }

    public boolean tableList(SQLiteDatabase db, String db_name){

        db = this.dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        boolean found = false;

        int i = 0;
        String [] names = new String[7];
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                if(names[i] == db_name)
                    found = true;
                names[i] = c.getString(0);
                c.moveToNext();
            }
        }

        return found;

    }

    public Cursor uploadRawData(String start_time, String end_time) {
        String [] whereArgs = {start_time, end_time};
        String[] raw_columns = {DatabaseHelper.COLUMN_RD2, DatabaseHelper.COLUMN_RD3, DatabaseHelper.COLUMN_RD4, DatabaseHelper.COLUMN_RD5, DatabaseHelper.COLUMN_RD6, DatabaseHelper.COLUMN_RD7, DatabaseHelper.COLUMN_RD8, DatabaseHelper.COLUMN_RD9, DatabaseHelper.COLUMN_RD10, DatabaseHelper.COLUMN_RD11, DatabaseHelper.COLUMN_RD12, DatabaseHelper.COLUMN_RD13, DatabaseHelper.COLUMN_RD14, DatabaseHelper.COLUMN_RD15, DatabaseHelper.COLUMN_RD16, DatabaseHelper.COLUMN_RD17, DatabaseHelper.COLUMN_RD18, DatabaseHelper.COLUMN_RD19, DatabaseHelper.COLUMN_RD20, DatabaseHelper.COLUMN_RD21, DatabaseHelper.COLUMN_RD22, DatabaseHelper.COLUMN_RD23};
        Cursor cursor = this.database.query(DatabaseHelper.TABLE5_NAME, raw_columns, "timestamp >= ? AND timestamp < ?", whereArgs, null, null, null);
        return cursor;
    }

    public void insertUserProfile(String uuid, String name, String dateBirth, String sex, int height_feet, int height_inches, int weight, String email, String phone){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_UP1, uuid);
        contentValues.put(DatabaseHelper.COLUMN_UP2, name);
        contentValues.put(DatabaseHelper.COLUMN_UP3, dateBirth);
        contentValues.put(DatabaseHelper.COLUMN_UP4, sex);
        contentValues.put(DatabaseHelper.COLUMN_UP5, height_feet);
        contentValues.put(DatabaseHelper.COLUMN_UP6, height_inches);
        contentValues.put(DatabaseHelper.COLUMN_UP7, weight);
        contentValues.put(DatabaseHelper.COLUMN_UP8, email);
        contentValues.put(DatabaseHelper.COLUMN_UP9, phone);
        this.database.insert(DatabaseHelper.TABLE1_NAME, null, contentValues);
        Log.d("DATABASE OPERATIONS", "One row inserted in USER PROFILES ...");

    }

    public Cursor fetchUserInfo() {
        MainActivity.dbManager.open();
        String[] user_columns = {DatabaseHelper.COLUMN_UP1, DatabaseHelper.COLUMN_UP2, DatabaseHelper.COLUMN_UP3, DatabaseHelper.COLUMN_UP4, DatabaseHelper.COLUMN_UP5, DatabaseHelper.COLUMN_UP6, DatabaseHelper.COLUMN_UP7, DatabaseHelper.COLUMN_UP8, DatabaseHelper.COLUMN_UP9};
        Cursor cursor = this.database.query(DatabaseHelper.TABLE1_NAME, user_columns, null, null, null, null, null);

        return cursor;
    }

    public void insertEmergencyContacts(String contact_name, String contact_email, String contact_phone, String uuid){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_UEC1, contact_name);
        contentValues.put(DatabaseHelper.COLUMN_UEC2, contact_email);
        contentValues.put(DatabaseHelper.COLUMN_UEC3, contact_phone);
        contentValues.put(DatabaseHelper.COLUMN_UEC4, uuid);
        this.database.insert(DatabaseHelper.TABLE2_NAME, null, contentValues);
        Log.d("DATABASE OPERATIONS", "One row inserted in EMERGENCY CONTACTS...");

    }

    public Cursor fetchEmergencyContacts() {

        String[] emergency_columns = {DatabaseHelper.COLUMN_UEC1, DatabaseHelper.COLUMN_UEC2, DatabaseHelper.COLUMN_UEC3, DatabaseHelper.COLUMN_UEC4};
        Cursor cursor = this.database.query(DatabaseHelper.TABLE2_NAME, emergency_columns, null, null, null, null, null);

        return cursor;
    }

    public void insertTruePositives(String fall_id, String uuid, float acc_x, float acc_y, float acc_z){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_TP1, fall_id);
        contentValues.put(DatabaseHelper.COLUMN_TP2, uuid);
        contentValues.put(DatabaseHelper.COLUMN_TP3, acc_x);
        contentValues.put(DatabaseHelper.COLUMN_TP4, acc_y);
        contentValues.put(DatabaseHelper.COLUMN_TP5, acc_z);
        this.database.insert(DatabaseHelper.TABLE3_NAME, null, contentValues);
        Log.d("DATABASE OPERATIONS", "One row inserted in TRUE POSITIVES...");

    }

    public Cursor fetchTruePositives() {

        String[] true_positive_columns = {DatabaseHelper.COLUMN_TP1, DatabaseHelper.COLUMN_TP2, DatabaseHelper.COLUMN_TP3, DatabaseHelper.COLUMN_TP4, DatabaseHelper.COLUMN_TP5};
        Cursor cursor = this.database.query(DatabaseHelper.TABLE3_NAME, true_positive_columns, null, null, null, null, null);

        return cursor;

    }

    public void insertFalsePositives(String fall_id, String uuid, float acc_x, float acc_y, float acc_z){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_FP1, fall_id);
        contentValues.put(DatabaseHelper.COLUMN_FP2, uuid);
        contentValues.put(DatabaseHelper.COLUMN_FP3, acc_x);
        contentValues.put(DatabaseHelper.COLUMN_FP4, acc_y);
        contentValues.put(DatabaseHelper.COLUMN_FP5, acc_z);
        this.database.insert(DatabaseHelper.TABLE4_NAME, null, contentValues);
        Log.d("DATABASE OPERATIONS", "One row inserted in FALSE POSITIVES...");

    }

    public Cursor fetchFalsePositives() {

        String[] false_positive_columns = {DatabaseHelper.COLUMN_FP1, DatabaseHelper.COLUMN_FP2, DatabaseHelper.COLUMN_FP3, DatabaseHelper.COLUMN_FP4, DatabaseHelper.COLUMN_FP5};
        Cursor cursor = this.database.query(DatabaseHelper.TABLE3_NAME, false_positive_columns, null, null, null, null, null);

        return cursor;

    }

    public Cursor fetchFallData(double acc_x, double acc_y, double acc_z){

        String [] raw_data = {"ID", DatabaseHelper.COLUMN_RD2, DatabaseHelper.COLUMN_RD3, DatabaseHelper.COLUMN_RD4, DatabaseHelper.COLUMN_RD5, DatabaseHelper.COLUMN_RD6};
        String [] args = {String.valueOf(acc_x), String.valueOf(acc_y), String.valueOf(acc_z)};
        Cursor cursor = this.database.query(DatabaseHelper.TABLE5_NAME, raw_data, "ms_accelerometer_x = ? AND ms_accelerometer_y = ? AND ms_accelerometer_z = ?", args, null, null, null);

        return cursor;
    }

    public Cursor fetchSpecificFall(int id_start, int id_end){
        String [] raw_data = {"ID", DatabaseHelper.COLUMN_RD2, DatabaseHelper.COLUMN_RD3, DatabaseHelper.COLUMN_RD4, DatabaseHelper.COLUMN_RD5, DatabaseHelper.COLUMN_RD6};
        String [] args = {String.valueOf(id_start), String.valueOf(id_end)};
        Cursor cursor = this.database.query(DatabaseHelper.TABLE5_NAME, raw_data, "ID >= ? AND ID <= ?", args, null, null, null);
        return cursor;
    }


    public void insertRawData(String uuid, float acc_x, float acc_y, float acc_z, float gyro_acc_x, float gyro_acc_y, float gyro_acc_z, float gyro_vel_x, float gyro_vel_y, float gyro_vel_z, float distance_pace, float distance_speed, int total_distance, String distance_motion_type, String heart_rate_bpm, String heart_rate_quality, int pedometer_steps, float skin_temp, String ultraviolet_level, String contact_state, int calories_burned, String timestamp){
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.COLUMN_RD2, uuid);
            contentValues.put(DatabaseHelper.COLUMN_RD3, acc_x);
            contentValues.put(DatabaseHelper.COLUMN_RD4, acc_y);
            contentValues.put(DatabaseHelper.COLUMN_RD5, acc_z);
            contentValues.put(DatabaseHelper.COLUMN_RD6, gyro_acc_x);
            contentValues.put(DatabaseHelper.COLUMN_RD7, gyro_acc_y);
            contentValues.put(DatabaseHelper.COLUMN_RD8, gyro_acc_z);
            contentValues.put(DatabaseHelper.COLUMN_RD9, gyro_vel_x);
            contentValues.put(DatabaseHelper.COLUMN_RD10, gyro_vel_y);
            contentValues.put(DatabaseHelper.COLUMN_RD11, gyro_vel_z);
            contentValues.put(DatabaseHelper.COLUMN_RD12, distance_pace);
            contentValues.put(DatabaseHelper.COLUMN_RD13, distance_speed);
            contentValues.put(DatabaseHelper.COLUMN_RD14, total_distance);
            contentValues.put(DatabaseHelper.COLUMN_RD15, distance_motion_type);
            contentValues.put(DatabaseHelper.COLUMN_RD16, heart_rate_bpm);
            contentValues.put(DatabaseHelper.COLUMN_RD17, heart_rate_quality);
            contentValues.put(DatabaseHelper.COLUMN_RD18, pedometer_steps);
            contentValues.put(DatabaseHelper.COLUMN_RD19, skin_temp);
            contentValues.put(DatabaseHelper.COLUMN_RD20, ultraviolet_level);
            contentValues.put(DatabaseHelper.COLUMN_RD21, contact_state);
            contentValues.put(DatabaseHelper.COLUMN_RD22, calories_burned);
            contentValues.put(DatabaseHelper.COLUMN_RD23, timestamp);


            this.database.insert(DatabaseHelper.TABLE5_NAME, null, contentValues);
        }
        catch (Exception e) {
            Log.e("INSERT RAW", e.getMessage());
        }

    }

    public Cursor fetchRawData() {

        String[] raw_columns = {DatabaseHelper.COLUMN_RD1, DatabaseHelper.COLUMN_RD2, DatabaseHelper.COLUMN_RD3, DatabaseHelper.COLUMN_RD4, DatabaseHelper.COLUMN_RD5, DatabaseHelper.COLUMN_RD6, DatabaseHelper.COLUMN_RD7, DatabaseHelper.COLUMN_RD8, DatabaseHelper.COLUMN_RD9, DatabaseHelper.COLUMN_RD10, DatabaseHelper.COLUMN_RD11, DatabaseHelper.COLUMN_RD12, DatabaseHelper.COLUMN_RD13, DatabaseHelper.COLUMN_RD14, DatabaseHelper.COLUMN_RD15, DatabaseHelper.COLUMN_RD16, DatabaseHelper.COLUMN_RD17, DatabaseHelper.COLUMN_RD18, DatabaseHelper.COLUMN_RD19, DatabaseHelper.COLUMN_RD20, DatabaseHelper.COLUMN_RD21, DatabaseHelper.COLUMN_RD22, DatabaseHelper.COLUMN_RD23};
        Cursor cursor = this.database.query(DatabaseHelper.TABLE5_NAME, raw_columns, null, null, null, null, null);

        return cursor;

    }

}
