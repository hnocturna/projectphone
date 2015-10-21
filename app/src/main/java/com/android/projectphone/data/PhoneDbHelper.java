package com.android.projectphone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.projectphone.data.PhoneContract.AvailabilityEntry;
import com.android.projectphone.data.PhoneContract.BatteryEntry;
import com.android.projectphone.data.PhoneContract.CameraEntry;
import com.android.projectphone.data.PhoneContract.ConnectivityEntry;
import com.android.projectphone.data.PhoneContract.DesignEntry;
import com.android.projectphone.data.PhoneContract.DisplayEntry;
import com.android.projectphone.data.PhoneContract.HardwareEntry;
import com.android.projectphone.data.PhoneContract.InternetEntry;
import com.android.projectphone.data.PhoneContract.MultimediaEntry;
import com.android.projectphone.data.PhoneContract.FeaturesEntry;
import com.android.projectphone.data.PhoneContract.TechnologyEntry;

import java.sql.SQLDataException;

/**
 * Created by hnoct on 9/30/2015.
 *
 * Class to assist in managing the SQLite database for all the phones and their specifications.
 *
 * Copying and altering methods from PhoneDbHelperTest and will eventually supersede and deprecate
 * that class altogether.
 *
 * Other methds from that class will be moved to the Provider class.
 */
public class PhoneDbHelper extends SQLiteOpenHelper {

    // Update the database version whenever the database schema is altered.
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "phone.db";

    public PhoneDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_AVAILABILITY_TABLE =

                "CREATE TABLE " + AvailabilityEntry.TABLE_NAME + " (" +
                        // The phone model is used as the primary key and the foreign key in all
                        // other tables
                        AvailabilityEntry.COLUMN_PHONE_KEY + " TEXT PRIMARY KEY, " +
                        // Columns involving dates will be converted to integers before being added
                        AvailabilityEntry.COLUMN_ANNOUNCED + " INTEGER, " +
                        // Stores the
                        AvailabilityEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                        AvailabilityEntry.COLUMN_SCHEDULED_RELEASE + " INTEGER, " +
                        AvailabilityEntry.COLUMN_POPULARITY + " INTEGER UNIQUE" +
                        ");";

        final String SQL_CREATE_BATTERY_TABLE =

                "CREATE TABLE " + BatteryEntry.TABLE_NAME + " (" +
                        BatteryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BatteryEntry.COLUMN_PHONE_KEY + "TEXT NOT NULL, " +
                        BatteryEntry.COLUMN_CAPACITY + " INTEGER, " +
                        BatteryEntry.COLUMN_ENDURANCE + " INTEGER, " +
                        BatteryEntry.COLUMN_MUSIC_PLAYBACK + " REAL, " +
                        BatteryEntry.COLUMN_REPLACEABLE + " BOOLEAN NOT NULL " +
                        BatteryEntry.COLUMN_STAND_BY_2G + " REAL, " +
                        BatteryEntry.COLUMN_STAND_BY_3G + " REAL, " +
                        BatteryEntry.COLUMN_STAND_BY_4G + " REAL, " +
                        BatteryEntry.COLUMN_TALK_2G + " REAL, " +
                        BatteryEntry.COLUMN_TALK_3G + " REAL, " +
                        BatteryEntry.COLUMN_VIDEO_CALL + " REAL, " +
                        BatteryEntry.COLUMN_VIDEO_PLAYBACK + " REAL, " +
                        BatteryEntry.COLUMN_WIRELESS_CHARGING + " BOOLEAN NOT NULL, " +

                        // Set the phone model as the foreign key to the availability table
                        "FOREIGN KEY (" + BatteryEntry.COLUMN_PHONE_KEY + ") REFERENCES " +
                        AvailabilityEntry.TABLE_NAME + " (" + AvailabilityEntry.COLUMN_PHONE_KEY +
                        ");";

        final String SQL_CREATE_CAMERA_TABLE =

                "CREATE TABLE " + CameraEntry.TABLE_NAME + " (" +
                        CameraEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CameraEntry.COLUMN_PHONE_KEY + " TEXT NOT NULL, " +
                        CameraEntry.COLUMN_APERTURE + " REAL, " +
                        CameraEntry.COLUMN_CAMCORDER_FEATURES + " TEXT, " +
                        // Cameras can record in multiple resolutions so are stored as a String
                        CameraEntry.COLUMN_CAMCORDER_RES + " TEXT, " +
                        CameraEntry.COLUMN_FEATURES + " TEXT, " +
                        CameraEntry.COLUMN_FLASH + " TEXT, " +
                        CameraEntry.COLUMN_FOCAL_LENGTH  + " INTEGER " +
                        CameraEntry.COLUMN_FRONT_FACING_FEATURES + " TEXT, " +
                        // Stored as megapixels
                        CameraEntry.COLUMN_FRONT_FACING_RESOLUTION + " REAL, " +
                        CameraEntry.COLUMN_MODES + " TEXT, " +
                        CameraEntry.COLUMN_PIXEL_SIZE + " REAL, " +
                        // Stored as megapixels
                        CameraEntry.COLUMN_RESOLUTION + " REAL, " +
                        CameraEntry.COLUMN_SENSOR_SIZE + " TEXT, " +
                        CameraEntry.COLUMN_SETTINGS + " TEXT, " +

                        // Set the phone model as the foreign key to the availability table
                        "FOREIGN KEY (" + CameraEntry.COLUMN_PHONE_KEY + ") REFERENCES " +
                        AvailabilityEntry.TABLE_NAME + " (" + AvailabilityEntry.COLUMN_PHONE_KEY +
                        ");";

        final String SQL_CREATE_CONNECTIVITY_TABLE =

                "CREATE TABLE " + ConnectivityEntry.TABLE_NAME + " (" +
                        ConnectivityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ConnectivityEntry.COLUMN_PHONE_KEY + " TEXT NOT NULL, " +
                        ConnectivityEntry.COLUMN_BLUETOOTH + " TEXT, " +
                        ConnectivityEntry.COLUMN_CONNECTOR + " TEXT, " +
                        ConnectivityEntry.COLUMN_FEATURES + " TEXT, " +
                        ConnectivityEntry.COLUMN_HDMI + " TEXT, " +
                        ConnectivityEntry.COLUMN_HOTSPOT + " BOOLEAN, " +
                        ConnectivityEntry.COLUMN_OTHER + " TEXT, " +
                        ConnectivityEntry.COLUMN_USB + " TEXT, " +
                        ConnectivityEntry.COLUMN_WIFI + " TEXT, " +

                        // Set the phone model as the foreign key to the availability table
                        "FOREIGN KEY (" + ConnectivityEntry.COLUMN_PHONE_KEY + ") REFERENCES " +
                        AvailabilityEntry.TABLE_NAME + " (" + AvailabilityEntry.COLUMN_PHONE_KEY +
                        ");";

        final String SQL_CREATE_DESIGN_TABLE =

                "CREATE TABLE " + DesignEntry.TABLE_NAME + " (" +
                        DesignEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DesignEntry.COLUMN_PHONE_KEY + " TEXT NOT NULL, " +
                        DesignEntry.COLUMN_DEVICE_TYPE + " TEXT, " +
                        DesignEntry.COLUMN_DIMENSIONS + " TEXT, " +
                        DesignEntry.COLUMN_IP_CERT + " TEXT, " +
                        DesignEntry.COLUMN_MATERIALS + " TEXT, " +
                        DesignEntry.COLUMN_MIL_STD_810 + " BOOLEAN, " +
                        DesignEntry.COLUMN_OS + " TEXT, " +
                        DesignEntry.COLUMN_PHONE_FUNCTION + " BOOLEAN, " +
                        DesignEntry.COLUMN_RUGGED + " TEXT, " +
                        DesignEntry.COLUMN_WEIGHT + " INTEGER " +

                        // Set the phone model as the foreign key to the availability table
                        "FOREIGN KEY (" + DesignEntry.COLUMN_PHONE_KEY + ") REFERENCES " +
                        AvailabilityEntry.TABLE_NAME + " (" + AvailabilityEntry.COLUMN_PHONE_KEY +
                        ");";

        final String SQL_CREATE_DISPLAY_TABLE =

                "CREATE TABLE " + DisplayEntry.TABLE_NAME + " (" +
                        DisplayEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DisplayEntry.COLUMN_PHONE_KEY + " TEXT NOT NULL, " +
                        DisplayEntry.COLUMN_COLORS + " INTEGER, " +
                        DisplayEntry.COLUMN_FEATURES + " TEXT, " +
                        DisplayEntry.COLUMN_PIXEL_DENSITY + " INTEGER, " +
                        DisplayEntry.COLUMN_RESOLUTION + " TEXT, " +
                        DisplayEntry.COLUMN_SCREEN_RATIO + " REAL, " +
                        DisplayEntry.COLUMN_SIZE + " REAL, " +
                        DisplayEntry.COLUMN_TECHNOLOGY + " TEXT, " +
                        DisplayEntry.COLUMN_TOUCHSCREEN + " TEXT, " +

                        // Set the phone model as the foreign key to the availability table
                        "FOREIGN KEY (" + DisplayEntry.COLUMN_PHONE_KEY + ") REFERENCES " +
                        AvailabilityEntry.TABLE_NAME + " (" + AvailabilityEntry.COLUMN_PHONE_KEY +
                        ");";

        final String SQL_CREATE_HARDWARE_TABLE =

                "CREATE TABLE " + HardwareEntry.TABLE_NAME + " (" +
                        HardwareEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        HardwareEntry.COLUMN_PHONE_KEY + " TEXT NOT NULL, " +
                        HardwareEntry.COLUMN_GPU + " TEXT, " +
                        HardwareEntry.COLUMN_MAX_STORAGE + " INTEGER, " +
                        HardwareEntry.COLUMN_PROCESSOR + " TEXT, " +
                        HardwareEntry.COLUMN_RAM + " INTEGER, " +
                        HardwareEntry.COLUMN_SOC + " TEXT, " +
                        HardwareEntry.COLUMN_STORAGE_EXPANSION + " TEXT, " +
                        HardwareEntry.COLUMN_STORAGE_MEMORY + " INTEGER, " +

                        // Set the phone model as the foreign key to the availability table
                        "FOREIGN KEY (" + HardwareEntry.COLUMN_PHONE_KEY + ") REFERENCES " +
                        AvailabilityEntry.TABLE_NAME + " (" + AvailabilityEntry.COLUMN_PHONE_KEY +
                        ");";

        final String SQL_CREATE_INTERNET_TABLE =

                "CREATE TABLE " + InternetEntry.TABLE_NAME + " (" +
                        InternetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        InternetEntry.COLUMN_PHONE_KEY + " TEXT NOT NULL, " +
                        InternetEntry.COLUMN_BROWSER + " TEXT, " +
                        InternetEntry.COLUMN_ONLINE_SERVICES + " TEXT, " +

                        // Set the phone model as the foreign key to the availability table
                        "FOREIGN KEY (" + InternetEntry.COLUMN_PHONE_KEY + ") REFERENCES " +
                        AvailabilityEntry.TABLE_NAME + " (" + AvailabilityEntry.COLUMN_PHONE_KEY +
                        ");";

        final String SQL_CREATE_MULTIMEDIA_TABLE =

                "CREATE TABLE " + MultimediaEntry.TABLE_NAME + " (" +
                        MultimediaEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MultimediaEntry.COLUMN_PHONE_KEY + " TEXT NOT NULL, " +
                        MultimediaEntry.COLUMN_FEATURES + " TEXT, " +
                        MultimediaEntry.COLUMN_FILTER + " TEXT, " +
                        MultimediaEntry.COLUMN_RADIO + " TEXT, " +
                        MultimediaEntry.COLUMN_SPEAKERS + " TEXT, " +
                        MultimediaEntry.COLUMN_VIDEO_PLAYBACK + " TEXT, " +
                        MultimediaEntry.COLUMN_VP_FEATURES + " TEXT, " +
                        MultimediaEntry.COLUMN_YOUTUBE + " TEXT, " +

                        // Set the phone model as the foreign key to the availability table
                        "FOREIGN KEY (" + MultimediaEntry.COLUMN_PHONE_KEY + ") REFERENCES " +
                        AvailabilityEntry.TABLE_NAME + " (" + AvailabilityEntry.COLUMN_PHONE_KEY +
                        ");";

        final String SQL_CREATE_FEATURES_TABLE =

                "CREATE TABLE " + FeaturesEntry.TABLE_NAME + " (" +
                        FeaturesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FeaturesEntry.COLUMN_PHONE_KEY + " TEXT NOT NULL, " +
                        FeaturesEntry.COLUMN_HEARING_AID + " TEXT, " +
                        FeaturesEntry.COLUMN_MICROPHONES + " TEXT, " +
                        FeaturesEntry.COLUMN_NOTIFICATIONS + " TEXT, " +
                        FeaturesEntry.COLUMN_SENSORS + " TEXT, " +
                        FeaturesEntry.COLUMN_VOICE + " TEXT, " +

                        // Set the phone model as the foreign key to the availability table
                        "FOREIGN KEY (" + FeaturesEntry.COLUMN_PHONE_KEY + ") REFERENCES " +
                        AvailabilityEntry.TABLE_NAME + " (" + AvailabilityEntry.COLUMN_PHONE_KEY +
                        ");";

        final String SQL_CREATE_TECHNOLOGY_TABLE =

                "CREATE TABLE " + TechnologyEntry.TABLE_NAME + " (" +
                        TechnologyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TechnologyEntry.COLUMN_PHONE_KEY + " TEXT NOT NULL, " +
                        TechnologyEntry.COLUMN_CDMA + " TEXT, " +
                        TechnologyEntry.COLUMN_DATA + " TEXT, " +
                        TechnologyEntry.COLUMN_FDD_LTE + " TEXT, " +
                        TechnologyEntry.COLUMN_GSM + " TEXT, " +
                        TechnologyEntry.COLUMN_HD_VOICE + " BOOLEAN NOT NULL, " +
                        TechnologyEntry.COLUMN_MICRO_SIM + " BOOLEAN NOT NULL, " +
                        TechnologyEntry.COLUMN_MULTI_SIM + " BOOLEAN NOT NULL, " +
                        TechnologyEntry.COLUMN_NANO_SIM + " BOOLEAN NOT NULL, " +
                        TechnologyEntry.COLUMN_NAVIGATION + " TEXT, " +
                        TechnologyEntry.COLUMN_POSITIONING + " TEXT, " +
                        TechnologyEntry.COLUMN_TDD_LTE + " TEXT, " +
                        TechnologyEntry.COLUMN_UMTS + " TEXT, " +
                        TechnologyEntry.COLUMN_VOLTE + " BOOLEAN NOT NULL, " +

                        // Set the phone model as the foreign key to the availability table
                        "FOREIGN KEY (" + TechnologyEntry.COLUMN_PHONE_KEY + ") REFERENCES " +
                        AvailabilityEntry.TABLE_NAME + " (" + AvailabilityEntry.COLUMN_PHONE_KEY +
                        ");";

        sqLiteDatabase.execSQL(SQL_CREATE_AVAILABILITY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BATTERY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CAMERA_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CONNECTIVITY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DESIGN_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DISPLAY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FEATURES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HARDWARE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INTERNET_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MULTIMEDIA_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TECHNOLOGY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
