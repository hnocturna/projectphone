package com.android.projectphone;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hnocturna on 4/23/2015.
 */
public final class MySQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db";
    private Context context;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String key : MainActivity.map.keySet()) {
            StringBuilder CREATE_BOOK_TABLE = new StringBuilder();
            Log.d("Test", "Key: " + key.replace("SpecList", ""));
            CREATE_BOOK_TABLE.append("CREATE TABLE " + key);
            CREATE_BOOK_TABLE.append("( phone_model TEXT PRIMARY KEY NOT NULL"); // Primary key will be the phone's model for all tables (easier to join)

            // Creates tables from sectionTitles and columns from specs
            for (int j = 0; j < MainActivity.map.get(key).size(); j++) {
                CREATE_BOOK_TABLE.append(", " + MainActivity.map.get(key).get(j) + " TEXT");
            }

            // Appends some columns that for whatever reason, are not correctly added to the specArray
            /* if (key.equals("technology")) {
                CREATE_BOOK_TABLE.append(", cdma TEXT, micro_sim TEXT, multiple_sim_cards TEXT, hd_voice TEXT");
            }
            if (key.equals("battery")) {
                CREATE_BOOK_TABLE.append(", not_user_replaceable TEXT");
            }
            if (key.equals("connectivity")) {
                CREATE_BOOK_TABLE.append(", usb3 TEXT");
            } */

            CREATE_BOOK_TABLE.append(")");
            // Log.d("MySQLiteHelper", CREATE_BOOK_TABLE.toString()); // Debug purposes
            db.execSQL(CREATE_BOOK_TABLE.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS phones");
        this.onCreate(db);
    }

    public void addPhones(ContextWrapper contextWrapper, Set<String> phoneArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> tablePhoneArray = new ArrayList<String>();
        ArrayList<String> oldPhones = new ArrayList<String>();
        ArrayList<String> newPhones = new ArrayList(phoneArray);
        String table = null;

        /* ContentValues values = new ContentValues();
        values.put("phone_model", phoneArray.get(0).replace("_",""));
        db.insert(MainActivity.sectionTitles.get(0), null, values); */

        File dbFile = contextWrapper.getDatabasePath("db");
        Boolean dbFileExists = dbFile.exists();
        Log.d("dbFileCheck", "DB Exists: " + dbFileExists.toString());
        if (dbFile.exists()) {
            Cursor cursor = db.rawQuery("SELECT * FROM availability", null);
            if (cursor.moveToFirst()) {
                do {
                    tablePhoneArray.add(cursor.getString(0).replace(" ", "_"));
                } while (cursor.moveToNext());
            }
            oldPhones = new ArrayList(tablePhoneArray);
            Log.d("SQLiteHelper.addPhones", "Existing phones: " + oldPhones.toString());
            newPhones.removeAll(oldPhones);
            Log.d("SQLiteHelper.addPhones", "New phones: " + newPhones.toString());
        }
        for (String phone : newPhones) {
            ContentValues values = new ContentValues();
            values.put("phone_model", phone);
            try {
                for (String sectionTitle : MainActivity.sectionTitles) {
                    table = sectionTitle;
                    // Log.d("table", "Table: " + table);
                    db.insert(table, null, values);
                }
            } catch (ConcurrentModificationException cme) {
                cme.printStackTrace();
            }


        }
        db.close();
    }

    public void addSpec(String phoneSpecArrayTitle, ArrayList phoneSpecArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Log.d(phoneSpecArrayTitle, phoneSpecArray.toString());
        String phoneModel = null;
        String table = null;
        String spec = null;

        // Convert the phoneSpecArrayTitle into usable strings to identify where the phones' specs should be added
        // phoneSpecArrayTitle ex. Sony_Xperia_Z4 hardware gpu = phoneModel table spec
        Pattern regex = Pattern.compile("([^\\s]+).([^\\s]+).([^\\s]+)");
        Matcher tokens = regex.matcher(phoneSpecArrayTitle);
        boolean found = tokens.matches();
        if (found) {
            phoneModel = tokens.group(1);
            table = tokens.group(2);
            spec = tokens.group(3);
        }
        // Log.d("SQLiteHelper", phoneSpecArrayTitle);
        // Log.d("SQLiteHelper", phoneModel + " " + table + " " + spec + ": " + phoneSpecArray);
        // For some reason some specs have a digit at the end, this cycle removes that digit
        /* try {
            if (spec != null) {
                char lastChar = spec.charAt(spec.length() - 1);
                while (Character.isDigit(lastChar)) {
                    spec = spec.substring(0,(spec.length() - 1));
                    lastChar = spec.charAt(spec.length() - 1);
                }
            }
        } catch (StringIndexOutOfBoundsException soob) {
            Log.d("SQLiteException", phoneModel + " " + table + " " + spec);
        } */

        if (spec != null && phoneSpecArray != null) { // Confirm that *something* is being added
            try {
                ContentValues values = new ContentValues();
                values.put(spec, phoneSpecArray.toString().replace("[", "").replace("]", ""));

                db.update(table, values, "phone_model = ?", new String[]{String.valueOf(phoneModel)});

            } catch (SQLiteException sqe) {
                Log.d("SQLiteException", phoneModel + " " + table + " " + spec);
            }
        }

        db.close();
    }

    public ArrayList<String> returnPhones(ContextWrapper contextWrapper) {
        ArrayList<String> tablePhoneArray = new ArrayList<String>();
        File dbFile = contextWrapper.getDatabasePath("db");
        Boolean dbFileExists = dbFile.exists();
        Log.d("dbFileCheck", "DB Exists: " + dbFileExists.toString());
        if (dbFile.exists()) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM availability", null);
            if (cursor.moveToFirst()) {
                do {
                    tablePhoneArray.add(cursor.getString(0).replace(" ", "_"));
                } while (cursor.moveToNext());
            }
            return tablePhoneArray;
        }
        return tablePhoneArray;
    }

    public ArrayList<String> returnSectionTitles() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> tableNames = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tableNames.add(cursor.getString(cursor.getColumnIndex("name")));
            cursor.moveToNext();
        }
        tableNames.remove("android_metadata");
        db.close();
        return tableNames;

    }

    public ArrayList<String> returnColumns(String sectionTitle) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> tableNames = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + sectionTitle + " WHERE 0", null);
        ArrayList<String> columns = new ArrayList<>();
        for (String column : cursor.getColumnNames()) {
            columns.add(column);
        }
        db.close();
        columns.remove(0);
        return columns;
    }

    public List<String> getMinMax(String table, String column) {
        List<String> minMax = new ArrayList<String>();
        double min = 0;
        double max = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, Double> specMap = new HashMap<String, Double>();
        Cursor cursor = db.query(table, new String[]{column}, null, null, null, null, null);
        cursor.moveToFirst();
        int i = 0;
        Double doubleValue = null;
        int magnitude = 0;
        while (!cursor.isAfterLast()) {
            if (column.equals("screen_to_body_ratio") || column.equals("physical_size") || column.equals("pixel_density") || column.equals("camera") || column.equals("aperture_size") || column.equals("pixel_size") || column.equals("front_facing_camera") ||
                    column.equals("bluetooth") || column.equals("usb") || column.equals("ip_certified") || column.equals("capacity") || column.equals("system_memory") || column.equals("built_int_storage")) {
                String stringValue = cursor.getString(cursor.getColumnIndex(column)).replaceAll(" ", "");
                stringValue = stringValue.replaceAll("[^\\d.]", "");
                stringValue = stringValue.replaceAll("%", "");
                stringValue = stringValue.trim();

                if (stringValue != null && !stringValue.equals("")) {
                    if (magnitude == 0 && stringValue.contains(".")) {
                        for (int j = 0; j < stringValue.length(); j++) {
                            magnitude++;
                            char c = stringValue.charAt(j);
                            Log.d("Test", Character.toString(c));
                            if (c == '.') {
                                Log.d("Magnitude", "Magnitude set to 0");
                                magnitude = 0;
                            }
                        }
                    }
                    doubleValue = Double.parseDouble(stringValue);
                }

                // Log.d("Test", "Current Value: " + doubleValue);
                if (i == 0 && doubleValue != null) {
                    min = doubleValue;
                    i++;
                } else if (doubleValue != null && doubleValue < min) {
                    min = doubleValue;
                } else if (doubleValue != null && doubleValue > max) {
                    max = doubleValue;
                }
            }
            cursor.moveToNext();
        }
        db.close();
        min = min * (Math.pow(10, magnitude));
        max = max * (Math.pow(10, magnitude));
        int intMin = (int) min;
        int intMax = (int) max;
        // Double dbMagnitude = (double) magnitude;
        minMax.add(Integer.toString(intMin));
        minMax.add(Integer.toString(intMax));
        minMax.add(Integer.toString(magnitude));
        Log.d("Test", "Min: " + intMin + " | Max: " + intMax + " | Magnitude: " + magnitude);
        return minMax;
    }

    public HashMap<String, Double> returnDecimalSpecs(String table, String column, double min, double max) {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, Double> specMap = new HashMap<String, Double>();
        Cursor cursor = db.query(table, new String[]{"phone_model", column}, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // Log.d("SQLite Test", cursor.getString(cursor.getColumnIndex("phone_model")));
            // Log.d("SQLite Test", cursor.getString(cursor.getColumnIndex(column)));
            String stringValue = cursor.getString(cursor.getColumnIndex(column)).replace(" ", "");
            stringValue = stringValue.replace("%", "");
            Double doubleValue = Double.parseDouble(stringValue);
            specMap.put(cursor.getString(cursor.getColumnIndex("phone_model")), doubleValue);
            cursor.moveToNext();
        }
        db.close();
        for (String phone : specMap.keySet()) {
            if (specMap.get(phone) < min || specMap.get(phone) > max) {
                specMap.remove(phone);
            }
        }
        for (String phone : specMap.keySet()) {
            // Log.d("WORK!!!", phone + ": " + specMap.get(phone));
        }

        return specMap;
    }
}


