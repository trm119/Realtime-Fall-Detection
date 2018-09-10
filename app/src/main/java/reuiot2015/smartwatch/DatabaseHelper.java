package reuiot2015.smartwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "fallDetectionREU2018.db";

    public static final String TABLE1_NAME = "user_profiles";
    public static final String COLUMN_UP1 = "UUID";
    public static final String COLUMN_UP2 = "Name";
    public static final String COLUMN_UP3 = "Date_of_Birth";
    public static final String COLUMN_UP4 = "Sex";
    public static final String COLUMN_UP5 = "Height_Feet";
    public static final String COLUMN_UP6 = "Height_Inches";
    public static final String COLUMN_UP7 = "Weight";
    public static final String COLUMN_UP8 = "Email";
    public static final String COLUMN_UP9 = "Phone_number";

    public static final String TABLE2_NAME = "user_emergency_contacts";
    public static final String COLUMN_UEC1 = "Name";
    public static final String COLUMN_UEC2 = "Email";
    public static final String COLUMN_UEC3 = "Phone_number";
    public static final String COLUMN_UEC4 = "RELATED_TO_USER";

    public static final String TABLE3_NAME = "true_positives";
    public static final String COLUMN_TP1 = "FALL_ID";
    public static final String COLUMN_TP2 = "UUID";
    public static final String COLUMN_TP3 = "acc_x";
    public static final String COLUMN_TP4 = "acc_y";
    public static final String COLUMN_TP5 = "acc_z";

    public static final String TABLE4_NAME = "false_positives";
    public static final String COLUMN_FP1 = "FALL_ID";
    public static final String COLUMN_FP2 = "UUID";
    public static final String COLUMN_FP3 = "acc_x";
    public static final String COLUMN_FP4 = "acc_y";
    public static final String COLUMN_FP5 = "acc_z";

    public static final String TABLE5_NAME = "raw_data";
    public static final String COLUMN_RD1 = "ID";
    public static final String COLUMN_RD2 = "UUID";
    public static final String COLUMN_RD3 = "ms_accelerometer_x";
    public static final String COLUMN_RD4 = "ms_accelerometer_y";
    public static final String COLUMN_RD5 = "ms_accelerometer_z";
    public static final String COLUMN_RD6 = "ms_gyroscope_acceleration_x";
    public static final String COLUMN_RD7 = "ms_gyroscope_acceleration_y";
    public static final String COLUMN_RD8 = "ms_gyroscope_acceleration_z";
    public static final String COLUMN_RD9 = "ms_gyroscope_velocity_x";
    public static final String COLUMN_RD10 = "ms_gyroscope_velocity_y";
    public static final String COLUMN_RD11 = "ms_gyroscope_velocity_z";
    public static final String COLUMN_RD12 = "ms_distance_pace";
    public static final String COLUMN_RD13 = "ms_distance_speed";
    public static final String COLUMN_RD14 = "ms_distance_total_distance";
    public static final String COLUMN_RD15 = "ms_distance_motion_type";
    public static final String COLUMN_RD16 = "ms_heart_rate_bpm";
    public static final String COLUMN_RD17 = "ms_heart_rate_quality";
    public static final String COLUMN_RD18 = "ms_pedometer_steps";
    public static final String COLUMN_RD19 = "ms_skin_temperature_celsius";
    public static final String COLUMN_RD20 = "ms_ultraviolet_level";
    public static final String COLUMN_RD21 = "ms_contact_state";
    public static final String COLUMN_RD22 = "ms_calories_burned";
    public static final String COLUMN_RD23 = "timestamp";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        Log.d("DATABSE OPERATIONS", "DATABASE HAS BEEN CREATED/OPENED...");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +TABLE1_NAME+ " (UUID TEXT PRIMARY KEY, NAME TEXT, DATE_OF_BIRTH TEXT, SEX TEXT, HEIGHT_FEET INT, HEIGHT_INCHES INT, WEIGHT INT, EMAIL TEXT, PHONE_NUMBER TEXT)");
        db.execSQL("CREATE TABLE " +TABLE2_NAME+ " (NAME TEXT, EMAIL TEXT, PHONE_NUMBER TEXT, RELATED_TO_USER TEXT)");
        db.execSQL("CREATE TABLE " +TABLE3_NAME+ " (FALL_ID TEXT, UUID TEXT, ACC_X FLOAT, ACC_Y FLOAT, ACC_Z FLOAT)");
        db.execSQL("CREATE TABLE " +TABLE4_NAME+ " (FALL_ID TEXT, UUID TEXT, ACC_X FLOAT, ACC_Y FLOAT, ACC_Z FLOAT)");
        db.execSQL("CREATE TABLE " +TABLE5_NAME+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT, UUID TEXT, MS_ACCELEROMETER_X FLOAT, MS_ACCELEROMETER_Y FLOAT, MS_ACCELEROMETER_Z FLOAT, MS_GYROSCOPE_ACCELERATION_X FLOAT, MS_GYROSCOPE_ACCELERATION_Y FLOAT, MS_GYROSCOPE_ACCELERATION_Z FLOAT, MS_GYROSCOPE_VELOCITY_X FLOAT, MS_GYROSCOPE_VELOCITY_Y FLOAT, MS_GYROSCOPE_VELOCITY_Z FLOAT, MS_DISTANCE_PACE FLOAT, MS_DISTANCE_SPEED FLOAT, MS_DISTANCE_TOTAL_DISTANCE INTEGER, MS_DISTANCE_MOTION_TYPE TEXT, MS_HEART_RATE_BPM TEXT, MS_HEART_RATE_QUALITY TEXT, MS_PEDOMETER_STEPS INTEGER, MS_SKIN_TEMPERATURE_CELSIUS FLOAT, MS_ULTRAVIOLET_LEVEL TEXT, MS_CONTACT_STATE TEXT, MS_CALORIES_BURNED INTEGER, TIMESTAMP TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE3_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE4_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE5_NAME);
        onCreate(db);

    }

}
