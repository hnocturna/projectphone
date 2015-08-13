package com.android.projectphone;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hnocturna on 4/23/2015.
 */
public final class MySQLiteHelper extends SQLiteOpenHelper {
    // Class used to help create, populate, search, and parse SQLite DB.

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "phonedb.db";
    private Context context;

    private Context mContext;
    private String DB_PATH;
    // private String DB_PATH = "/data/data/com.android.Phone4Me/databases/";
    private String DB_NAME = "phonedb.db";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("MySQL.onCreate", "Creating database!");
        for (String key : SplashScreen.map.keySet()) {
            StringBuilder CREATE_BOOK_TABLE = new StringBuilder();
            Log.d("Test", "Key: " + key.replace("SpecList", ""));
            CREATE_BOOK_TABLE.append("CREATE TABLE " + key);                        // The key of the HashMap is the name of the table, while the Hashed List-Value is the name of the individual columns.
            CREATE_BOOK_TABLE.append("( phone_model TEXT PRIMARY KEY NOT NULL");    // Primary key will be the phone's model for all tables (easier to join)

            // Creates tables from sectionTitles and columns from specs
            for (int j = 0; j < SplashScreen.map.get(key).size(); j++) {
                CREATE_BOOK_TABLE.append(", " + SplashScreen.map.get(key).get(j) + " TEXT");
            }
            if (key.equals("availability")) {
                CREATE_BOOK_TABLE.append(", image_url TEXT");
            }

            CREATE_BOOK_TABLE.append(", popularity INTEGER)");
            // Log.d("MySQLiteHelper", CREATE_BOOK_TABLE.toString()); // Debug purposes
            db.execSQL(CREATE_BOOK_TABLE.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS phones");
        this.onCreate(db);
    }

    public boolean dbExists(ContextWrapper contextWrapper) {
        File dbFile = contextWrapper.getDatabasePath("phonedb.db");
        Log.d("MySQL.dbCheck", contextWrapper.getDatabasePath("phonedb.db").toString());
        Boolean dbFileExists = dbFile.exists();
        Log.d("dbFileCheck", "DB Exists: " + dbFileExists.toString());
        return dbFileExists;

    }

    public void createDatabase(ContextWrapper getApplication) {
        // If the database does not exist, create the database then copy the existing phonedb.db from Assets to the database directory.
        this.mContext = SplashScreen.getAppContext();
        Log.d("MySQL.createDatabase", "Creating database.");
        if (!dbExists(getApplication)) {
            SQLiteDatabase db = this.getReadableDatabase();
            if (db.isOpen()) {
               db.close();
            }
            try {
                copyDatabase(getApplication);
            } catch (IOException ioe) {
                Log.d("MySQL.CreateDB", "I/O Exception copying database!");
            }
        }
    }

    private void copyDatabase(ContextWrapper contextWrapper) throws IOException {
        // Copies existing database from assets to the database folder.
        this.mContext = SplashScreen.getAppContext();
        DB_PATH = mContext.getFilesDir().getPath();
        DB_PATH = DB_PATH.substring(0, DB_PATH.lastIndexOf('/'))+ "/databases/";
        Log.d("MySQL.CopyDB", contextWrapper.getDatabasePath(DB_NAME).toString());

        InputStream myInput = mContext.getAssets().open(DB_NAME);
        // Log.d("MySQL.CopyDB", myInput.toString());
        String outFileName = DB_PATH + DB_NAME;             // Final output will be the path + the name of the db.
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
        Log.d("MySQL.CopyDB", "Database copied!");

    }

    // DEPRECATED FUNCTION. KEEP FOR REFERENCE.
    public synchronized void  addPhones (ContextWrapper contextWrapper, List<String> newPhoneList, List<String> allPhones) {
        // Adds all the phones to each table of the DB to prepare for population of specs using "update" instead of needing to check whether each phone-exists prior to adding the spec.
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> tablePhoneArray = new ArrayList<String>();                  // Identical to oldPhones -- to be removed.
        List<String> oldPhones = new ArrayList<String>();                   // Holds a list of all phones that were already in the DB.
        List<String> newPhones = newPhoneList;                              // Holds a list of all phones that are yet to be added. Currently holds all phones that are on the main page of the website via
                                                                            // the SpecsDownload() method.
        List<String> tempPhones = new LinkedList<String>();
        String table = null;
        // Check if the DB file already exists.
        File dbFile = contextWrapper.getDatabasePath("phonedb.db");
        Boolean dbFileExists = dbFile.exists();
        Log.d("dbFileCheck", "DB Exists: " + dbFileExists.toString());

        if (dbFile.exists()) {
            // Used as a secondary check for redundancy in case primary check fails -- See returnPhones() method below for primary check.
            Cursor cursor = db.rawQuery("SELECT * FROM availability", null);
            if (cursor.moveToFirst()) {
                do {
                    Log.d("MySQL.returnPhones", "Phone: " + cursor.getString(0).replace(" ", "_"));
                    tablePhoneArray.add(cursor.getString(0).replace(" ", "_"));     // Replace all spaces with underscores to keep the phone model as a single string to compare against list
                                                                                    // of new phones in the same format. -- deprecated as phone models are now stored with underscores within the DB.
                } while (cursor.moveToNext());
            }
            oldPhones = new LinkedList<String>(tablePhoneArray);                    // Will be deprecated in favor of adding directly to oldPhones Array.
            Log.d("SQLiteHelper.addPhones", "Existing phones: " + oldPhones.toString());
            newPhones.removeAll(oldPhones);                                         // New phones will obviously not include any of the old phones.
            Log.d("SQLiteHelper.addPhones", "Adding phone: " + newPhones.toString());
        }
        /* if (dbFile.exists()) {
            // Used as a secondary check for redundancy in case primary check fails -- See returnPhones() method below for primary check.
            Cursor cursor = db.rawQuery("SELECT * FROM shopping_information", null);
            if (cursor.moveToFirst()) {
                do {
                    tempPhones.add(cursor.getString(0).replace(" ", "_"));     // Replace all spaces with underscores to keep the phone model as a single string to compare against list
                    // of new phones in the same format. -- deprecated as phone models are now stored with underscores within the DB.
                } while (cursor.moveToNext());
            }
        }
        List<String> tempPhones2 = new LinkedList<>(allPhones);
        tempPhones2.removeAll(tempPhones);
        for (int i = 0; i < tempPhones2.size(); i++) {
            String phone = tempPhones2.get(i);
            ContentValues values = new ContentValues();
            values.put("phone_model", phone);
            db.insert("shopping_information", null, values);
        } */
        test2:
        for (int i = 0; i < newPhones.size(); i++) {
            // Add each new phone to the table in preparation of population of specs.
            String phone = newPhones.get(i);
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
                break test2;
            }
        }
        test:
        for (int i = 0; i < allPhones.size(); i++) {
            String phone = allPhones.get(i);
            int popularity = i;
            try {
                for (String sectionTitle : MainActivity.sectionTitles) {
                    table = sectionTitle;
                    ContentValues values = new ContentValues();
                values.put("popularity", popularity);                                               // Remove the [] signs from the list -- just looks a little cleaner, but functionally should be the same.
                    db.update(table, values, "phone_model = ?", new String[]{phone});               // Only needs to update since phone models should already be populated in every table.
                }
            } catch (SQLiteException sqe) {
                Log.d("SQLiteException", phone + " " + table + " " + "popularity");                 // Logs each phone, table, and spec that is unable to be added for debugging purposes.
            } catch (ConcurrentModificationException cme) {
                Log.d("MySQL.addPhones", "Concurrent Modification Error!");
                break test;
            }
        }
        db.close();
    }

    public synchronized void addSpec (Map<String, List<String>> phoneSpecMap, List<String> sectionTitles, String phoneModel, List<String> allPhonesList, boolean lastPhone) {
        // Adds all the specs of each phone to the database.
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
        } finally {

        }
        // Log.d("MySQL.addSpecs", "All phones: " + allPhonesList.toString());
        Map<String, List<String>> sqlStatementMap = new LinkedHashMap<>();      // Map that contains the category (key) and the list of specs (value) for that category.
                                                                                // These terms will later by compiled for a db transaction for speed. (From 23s/phone -> ~0.5s/phone).

        Pattern regex = Pattern.compile("([^\\s]+).([^\\s]+).([^\\s]+)");
        for (String sectionTitle : sectionTitles) {
            List<String> sqlSpecList = new LinkedList<>();
            for (String phoneSpecListTitle : phoneSpecMap.keySet()) {
                Matcher tokens = regex.matcher(phoneSpecListTitle);
                boolean found = tokens.matches();
                String section = tokens.group(2);
                if (section.equals(sectionTitle)) {
                    sqlSpecList.add(phoneSpecListTitle);
                }

            }
            sqlStatementMap.put(sectionTitle, sqlSpecList);
        }

        try {
            for (String section : sectionTitles) {
                // Iterates through each section to ensure that all columns exist within the table before adding the phone's specs.
                List<String> specList = new LinkedList<>();
                for (String phoneSpecListTitle : sqlStatementMap.get(section)) {
                    // Retrieves each spec the section contains and adds it to a list to compare against the columns that already exist within the table.
                    regex = Pattern.compile("([^\\s]+).([^\\s]+).([^\\s]+)");
                    Matcher tokens = regex.matcher(phoneSpecListTitle);
                    boolean found = tokens.matches();
                    String spec = tokens.group(3);
                    // Log.d("MySQL.addSpec", "Spec: " + spec + " | " + phoneSpecMap.get(phoneSpecListTitle));
                    specList.add(spec);
                }
                List<String> columnList = new LinkedList<>(returnColumns(section));
                for (String spec : specList) {
                    // Checks each spec against the list of columns to ensure they exist.
                    if (!columnList.contains(spec)) {
                        // If the column does not exist, it is created using ALTER TABLE SQL command.

                        // Log.d("MySQL.addSpecs", columnList.toString());
                        String tempStatement = "ALTER TABLE " + section + " ADD COLUMN " + spec + " TEXT";
                        Log.d("MySQL.addSpecs", "Missing column caught! Adding " + spec + " column to " + section + " table!");
                        try {
                            db.execSQL(tempStatement);
                        } catch (SQLiteException sqle) {

                        }
                        columnList = new LinkedList<>(returnColumns(section));      // Refreshes the list of columns for the next iteration.
                    }
                }
            }

            db.beginTransaction();
            for (String sectionTitle : sectionTitles) {
                String table = sectionTitle;

                List<String> specList = new LinkedList<>();
                List<List<String>> phoneSpecList = new LinkedList<>();
                Map<String, List<String>> tempPhoneSpecMap = new LinkedHashMap<>();
                int i = 0;
                for (String phoneSpecListTitle : sqlStatementMap.get(sectionTitle)) {
                    regex = Pattern.compile("([^\\s]+).([^\\s]+).([^\\s]+)");
                    Matcher tokens = regex.matcher(phoneSpecListTitle);
                    boolean found = tokens.matches();
                    String spec = tokens.group(3);
                    specList.add(spec);
                    phoneSpecList.add(phoneSpecMap.get(phoneSpecListTitle));
                    tempPhoneSpecMap.put(sectionTitle + spec, phoneSpecMap.get(phoneSpecListTitle));

                }
                // Create a raw SQL insert statement to insert entire row at once instead of by column. Also utilize transactions to speed up the process.
                String stringStatement = "REPLACE INTO " + table + " (phone_model, ";

                for (String spec : specList) {
                    // Specifies the columns to insert from the list of specs.
                    stringStatement = stringStatement + spec + ", ";
                }

                stringStatement = stringStatement.substring(0, stringStatement.length() - 2) + ") VALUES(?, ";      // Adds the popularity column and begin the VALUES statement with the phone's model.
                for (String spec : specList) {
                    List<String> columnList = new LinkedList<>(returnColumns(sectionTitle));
                    stringStatement = stringStatement + "?, ";
                }

                stringStatement = stringStatement.substring(0, stringStatement.length() - 2) + ")";
                // Log.d("MySQL.addSpecs", "String statement to be executed: " + stringStatement);
                SQLiteStatement sqLiteStatement = db.compileStatement(stringStatement);

                sqLiteStatement.bindString(1, phoneModel);
                i = 2;                                                                                              // Holds the integer being bound as the statement is compiled.
                                                                                                                    // Starts at "2" because "1" is the phone model.
                for (String spec : specList) {
                    spec = sectionTitle + spec;
                    sqLiteStatement.bindString(i, tempPhoneSpecMap.get(spec).toString().replace("[", "").replace("]", ""));
                    i++;
                }
                long entryID = sqLiteStatement.executeInsert();
                sqLiteStatement.clearBindings();
            }

            if (lastPhone) {
                // If a new phone is added, the overall placement of each phone's popularity will shift.
                // Iterates through all phones and inputs its new popularity value into the sqlite db.
                Log.d("MySQL.addSpecs", "Last phone specs being added. Popularity being re-arranged");
                for (String section : sectionTitles) {
                    // Popularity column is located in all tables, so it must be updated in each table.
                    String stringStatement = "UPDATE " + section + " SET popularity=? WHERE phone_model=?";
                    SQLiteStatement sqLiteStatement = db.compileStatement(stringStatement);;
                    for (String phone : allPhonesList) {
                        // Add binding statement for each phone along with its position in the allPhonesList.
                        sqLiteStatement.bindLong(1, allPhonesList.indexOf(phone));
                        sqLiteStatement.bindString(2, phone);
                        long entryID = sqLiteStatement.executeInsert();
                        sqLiteStatement.clearBindings();
                    }
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        }

        // db.close();
    }

    public List<String> returnPhones(ContextWrapper contextWrapper, List<String> sectionTitles) {
        // Returns a list of all phones contained within the table -- used as a primary check to save cycles if no new phones have been added.
        List<String> tablePhoneArray = new LinkedList<String>();
        Map<Integer, String> phonePopularityMap = new TreeMap<>();
        File dbFile = contextWrapper.getDatabasePath("phonedb.db");
        Boolean dbFileExists = dbFile.exists();
        // Log.d("MySQL.returnPhones", "DB Exists: " + dbFileExists.toString());
        if (dbFile.exists()) {
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                Cursor cursor = db.query("display", new String[]{"phone_model", "Physical_size", "popularity"}, null, null, null, null, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String phone = cursor.getString(cursor.getColumnIndex("phone_model")).replace(" ", "_");
                    String physical_size = cursor.getString(cursor.getColumnIndex("Physical_size"));
                    int popularity = cursor.getInt(cursor.getColumnIndex("popularity"));
                    // Log.d("MySQL.returnPhones", "Physical size of device: " + physical_size);
                    if (physical_size == null || physical_size.equals("")) {
                        // Deletes phones that have incomplete values - occurs if the download process is stopped mid-download.
                        for (String sectionTitle : sectionTitles) {
                            // Remove the phones and all its values from each table.
                            String table = sectionTitle;
                            Log.d("MySQL.returnPhones", "Deleting " + phone + " from " + table + "table");
                            db.delete(table, "phone_model" + "=?", new String[]{String.valueOf(phone)});
                        }
                    } else {
                        // Add phone to a sorted Map to retain popularity ranking of phones.
                        phonePopularityMap.put(popularity, phone);
                        // Log.d("MySQL.returnPhones", "Existing phone: " + cursor.getString(cursor.getColumnIndex("phone_model")).replace(" ", "_"));
                    }
                    cursor.moveToNext();
                }
                for (int popularity : phonePopularityMap.keySet()) {
                    tablePhoneArray.add(phonePopularityMap.get(popularity));
                }
                db.close();
                return tablePhoneArray;
            } catch (SQLiteException sqle) {
                Log.d("MySQL.returnPhones", "SQLite Database not yet populated. No phones to return!");
                return tablePhoneArray;
            }
        }
        return tablePhoneArray;
    }

    public List<String> returnSectionTitles(ContextWrapper contextWrapper) {
        // Returns a list of names of all of the tables.
        File dbFile = contextWrapper.getDatabasePath("phonedb.db");
        Boolean dbFileExists = dbFile.exists();
        Log.d("dbFileCheck", "DB Exists: " + dbFileExists.toString());
        List<String> tableNames = new ArrayList<>();
        if (dbFileExists) {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tableNames.add(cursor.getString(cursor.getColumnIndex("name")));
                cursor.moveToNext();
            }
            tableNames.remove("android_metadata");      // Metadata table is not used and therefore not needed.
            db.close();
        }
        return tableNames;
    }

    public List<String> returnColumns(String sectionTitle) {
        // Returns a list of names of every column within the specific table.
        List<String> columns = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + sectionTitle + " WHERE 0", null);

            for (String column : cursor.getColumnNames()) {
                columns.add(column);
            }
            // db.close();
            columns.remove("popularity");
            columns.remove("phone_model");      // Returning the column of phone_models is unnecessary as it is in each table and used for joining/identification purposes only.
            // Log.d("MySQL.returnColumns", columns.toString());
        } catch (SQLiteException sqle) {
            Log.d("MySQL.returnColumns", "No columns in database yet!");
        }
        return columns;
    }

    public NumericalSpec getMinMax(String table, String column) {
        // Returns a NumericalSpec Class that contains the information to be served to the user to select their requirements.
        // This can be a minimum & maximum that the user is allowed to select or only a minimum if there is no conceivable reason to have a maximum.
        // Resolutions are a special case to be described within its method below.

        double min = 0; double max = 0;
        String unit = null;                                                     // Holds the unit of each spec (e.g. "ppi", "pixels", "inches", etc.)
        NumericalSpec ns = null;                                                // Initialize the NumericalSpec Class.
        SortedSet<Integer> choices = new TreeSet<>();                           // Used to sort by numerical value each spec that has an order from least to greatest.
        HashMap<String, List<String>> resolutions = new LinkedHashMap<String, List<String>>();     // Used to hold a key of the name of the standard resolution and a value of any variation of that resolution.
        HashMap<Integer, Integer> choiceMap = new LinkedHashMap<>();            // Used to hold a key of the cardinal ordering of the value (e.g. {1 = 1GB, 2 = 2GB, 3 = 4GB, ...}) -- sorted using TreeSet above.
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(table, new String[]{column, "popularity"}, null, null, null, null, null);
        if (column.contains("Dimensions")) {
            cursor = db.query(table, new String[]{"Dimensions", "popularity"}, null, null, null, null, null);
        }
        cursor.moveToFirst();

        Double doubleValue = null;                                              // This is a temporary value that will be immediately assigned or dismissed depending on min/max values.
        int magnitude = 0;                                                      // Holds the magnitude that is multiplied to give the value an integer-value (e.g. 5.2in => 52in with a magnitude of 1 (i.e. 10^1))

        int i = 0;

        if (column.equals("System_memory") || column.equals("Built_in_storage")) {
            // These values have no conceivable reason to have a maximum value and are discrete values so they can easily selected in the SelectSpec Activity.
            while (!cursor.isAfterLast()) {
                String stringValue = cursor.getString(cursor.getColumnIndex(column));
                if (stringValue == null) {
                    // Some phones do not have a value and are therefore skipped.
                    cursor.moveToNext();
                    continue;
                }
                // Log.d("Test", stringValue);
                unit = stringValue.replaceAll("[\\d.]", "");
                unit = unit.trim();
                stringValue = stringValue.replaceAll("[^\\d.]", "");
                stringValue = stringValue.replaceAll("%", "");
                stringValue = stringValue.trim();
                Log.d("MySQLHelper.getMinMax", stringValue);
                try {
                    int intValue = Integer.parseInt(stringValue);
                    if (!choices.contains(intValue)) {
                        // Add the integer-value to a TreeSet of all the choices (will automatically sort them in numerical order).
                        choices.add(intValue);
                    }
                } catch (NumberFormatException e) {
                    // Some phones have stupid values that are decimals of a binary storage format (e.g. 0.193GB) and are therefore ignored. Also, no modern phone has less than
                    // 4GB of storage so I'm just gonna ignore those... <= Maybe I'll fix it later if I'm not feeling lazy.
                    Log.d("MySQLHelper.getMinMax", stringValue + " " + unit + " not added because it is not an integer value!");
                }
                cursor.moveToNext();
            }
            int j = 0;
            for (int choice : choices) {
                // Values are added to the HashSet with its cardinal ordering as the key and the value as the value. (e.g. {1 = 8GB, 2 = 16GB, 3 = 32GB, ...})
                choiceMap.put(j, choice);
                Log.d("Test", Integer.toString(choice) + " " + unit);
                j++;
            }

            ns = new NumericalSpec(table, column, choiceMap, unit);
        } else if (column.equals("Resolution")) {
            // Phone resolutions are stupid and often aren't exact to the standard resolution naming convention. This shoe-horns them into the closest standard value.
            // Some are in other standard resolution naming conventions, but this simplifies the process to fewer, but easier to understand categories.
            resolutions.put("ninth HD", Arrays.asList("360 x 640 pixels", "480 x 800 pixels", "320 x 480 pixels", "480 x 854 pixels"));
            resolutions.put("quarter HD", Arrays.asList("540 x 960 pixels", "640 x 960 pixels", "640 x 1136 pixels", "1024 x 600 pixels"));
            resolutions.put("HD", Arrays.asList("720 x 1280 pixels", "768 x 1280 pixels", "800 x 1280 pixels", "768 x 1024 pixels", "750 x 1334 pixels"));
            resolutions.put("Full HD", Arrays.asList("1080 x 1920 pixels", "1536 x 2048 pixels"));
            resolutions.put("Quad HD", Arrays.asList("1440 x 2560 pixels", "2560 x 1440 pixels", "1600 x 2560 pixels", "2560 x 1600 pixels"));

            ns = new NumericalSpec(table, column, resolutions);
            return ns;
        } else if (column.equals("Camcorder")) {
            // Holds all resolutions of the camcorder/camera and sorts them... hopefully.
            resolutions.put("Yes", Arrays.asList("320x240", "640x480", "720x480", "848 x 480", "854 x 480", "Resolution", "Yes"));
            resolutions.put("720p", Arrays.asList("1280x720"));
            resolutions.put("1080p", Arrays.asList("1920x1080"));
            resolutions.put("1440p (QHD)", Arrays.asList("2560x1440p"));
            resolutions.put("2160p (4K)", Arrays.asList("3840x2160"));

            ns = new NumericalSpec(table, column, resolutions);
        } else if (column.contains("Dimensions")) {
            Double height, width, depth, mHeight, mWidth, mDepth;
            Pattern regex = Pattern.compile("([\\d]*\\.[\\d]*|[\\d]*) x ([\\d]*.[\\d]*|[\\d]*) x ([\\d]*.[\\d]*|[\\d]*) inches \\(([\\d]*\\.[\\d]*|[\\d]*) x ([\\d]*\\.[\\d]*|[\\d]*) x ([\\d]*.[\\d]*|[\\d]*)(?: mm\\)| mm \\)|mm\\)|mm \\)|\\)| \\))");
            while (!cursor.isAfterLast()) {

                if (cursor.getString(cursor.getColumnIndex("Dimensions")) == null) {
                    cursor.moveToNext();
                    continue;
                } else if (cursor.getString(cursor.getColumnIndex("Dimensions")).isEmpty()) {
                    cursor.moveToNext();
                    continue;
                }

                String stringValue = cursor.getString(cursor.getColumnIndex("Dimensions"));

                if (magnitude == 0 && stringValue.contains(".")) {
                    // Counts the number of digits that appear after the decimal place -- i.e. each place the decimal must be moved in order to return an integer value. Only needs to be done once as
                    // all values within the same column will have the same magnitude.
                    for (int j = 0; j < stringValue.length(); j++) {
                        magnitude++;
                        char c = stringValue.charAt(j);
                        // Log.d("Test", Character.toString(c));
                        if (c == '.') {
                            // Log.d("Magnitude", "Magnitude set to 0");
                            magnitude = 0;
                        }
                    }
                }

                Matcher tokens = regex.matcher(stringValue);
                boolean found = tokens.matches();

                Log.d("MySQL.getMinMax", stringValue);

                height = Double.parseDouble(tokens.group(1));
                width = Double.parseDouble(tokens.group(2));
                depth = Double.parseDouble(tokens.group(3));

                mHeight = Double.parseDouble(tokens.group(4));
                mWidth = Double.parseDouble(tokens.group(5));
                mDepth = Double.parseDouble(tokens.group(6));

                if (column.contains("height")) {
                    doubleValue = height;
                } else if (column.contains("width")) {
                    doubleValue = width;
                } else if (column.contains("depth")) {
                    doubleValue = depth;
                }

                if (i == 0 && doubleValue != null) {
                    // Holds the first value as the minimum regardless.
                    min = doubleValue;
                    i++;

                    // If less than min, set as new min. If greater than max, set as max -- duh.
                } else if (doubleValue != null && doubleValue < min) {
                    min = doubleValue;
                } else if (doubleValue != null && doubleValue > max) {
                    max = doubleValue;
                }
                cursor.moveToNext();
            }

            unit = "inches";

            // Convert double values to integer values as the SeekBar can only be used on integer values. Actual values will be calculated on-the-fly in SelectSpec Activity.
            min = min * (Math.pow(10, magnitude));
            max = max * (Math.pow(10, magnitude));
            int intMin = (int) min;
            int intMax  = (int) max;
            ns = new NumericalSpec(table, column, intMin, intMax, magnitude, unit);

            // Log.d("MySQL.getMinMax", "Dimensions: " + height + " " + width + " " + depth);
        } else if (column.contains("Weight")) {
            Double weight, mWeight;
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex("Weight")) == null || cursor.getString(cursor.getColumnIndex("Weight")).isEmpty()) {
                    cursor.moveToNext();
                    continue;
                } else {
                    String stringValue = cursor.getString(cursor.getColumnIndex("Weight"));



                    Pattern regex = Pattern.compile("([\\d]*.[\\d]*) oz \\(([\\d]*) g\\)");
                    Matcher tokens = regex.matcher(stringValue);
                    boolean found = tokens.matches();

                    if (magnitude == 0 && tokens.group(1).contains(".")) {
                        // Counts the number of digits that appear after the decimal place -- i.e. each place the decimal must be moved in order to return an integer value. Only needs to be done once as
                        // all values within the same column will have the same magnitude.
                        for (int j = 0; j < tokens.group(1).length(); j++) {
                            magnitude++;
                            char c = stringValue.charAt(j);
                            // Log.d("Test", Character.toString(c));
                            if (c == '.') {
                                // Log.d("Magnitude", "Magnitude set to 0");
                                magnitude = 0;
                            }
                        }
                    }

                    weight = Double.parseDouble(tokens.group(1));
                    mWeight = Double.parseDouble(tokens.group(2));

                    doubleValue = weight;

                    if (i == 0 && doubleValue != null) {
                        // Holds the first value as the minimum regardless.
                        min = doubleValue;
                        i++;

                        // If less than min, set as new min. If greater than max, set as max -- duh.
                    } else if (doubleValue != null && doubleValue < min) {
                        min = doubleValue;
                    } else if (doubleValue != null && doubleValue > max) {
                        max = doubleValue;
                    }
                    cursor.moveToNext();
                }
            }
            unit = "oz";        // The unit for weight will always be in "oz" (grams will be calculated on-the-fly in SelectSpecs.class).

            // Convert double values to integer values as the SeekBar can only be used on integer values. Actual values will be calculated on-the-fly in SelectSpec Activity.
            min = min * (Math.pow(10, magnitude));
            max = max * (Math.pow(10, magnitude));
            int intMin = (int) min;
            int intMax  = (int) max;
            ns = new NumericalSpec(table, column, intMin, intMax, magnitude, unit);

        } else {
            while (!cursor.isAfterLast()) {
                String stringValue = cursor.getString(cursor.getColumnIndex(column));
                if (stringValue != null && !stringValue.equals("") && !stringValue.equals("Yes")) {
                    if (unit == null) {
                        unit = stringValue.replaceAll("[\\d.]", "");            // Units are usually anything that is not a decimal or digit.
                        unit = unit.trim();
                    }
                    stringValue = stringValue.replaceAll("[^\\d.]", "");        // Because the values of the table are held as String, this holds a string-value for the spec that will be converted to a double.
                    stringValue = stringValue.replaceAll("%", "");
                    stringValue = stringValue.trim();

                    if (magnitude == 0 && stringValue.contains(".")) {
                        // Counts the number of digits that appear after the decimal place -- i.e. each place the decimal must be moved in order to return an integer value. Only needs to be done once as
                        // all values within the same column will have the same magnitude.
                        for (int j = 0; j < stringValue.length(); j++) {
                            magnitude++;
                            char c = stringValue.charAt(j);
                            // Log.d("Test", Character.toString(c));
                            if (c == '.') {
                                // Log.d("Magnitude", "Magnitude set to 0");
                                magnitude = 0;
                            }
                        }
                    }
                    doubleValue = Double.parseDouble(stringValue);          // Converts the string-value of the spec to a double-value that can be used comparatively.
                } else {
                    cursor.moveToNext();
                    continue;
                }

                if (i == 0 && doubleValue != null) {
                    // Holds the first value as the minimum regardless.
                    min = doubleValue;
                    i++;

                    // If less than min, set as new min. If greater than max, set as max -- duh.
                } else if (doubleValue != null && doubleValue < min) {
                    min = doubleValue;
                } else if (doubleValue != null && doubleValue > max) {
                    max = doubleValue;
                }
                cursor.moveToNext();
            }

            // Convert double values to integer values as the SeekBar can only be used on integer values. Actual values will be calculated on-the-fly in SelectSpec Activity.
            min = min * (Math.pow(10, magnitude));
            max = max * (Math.pow(10, magnitude));
            int intMin = (int) min;
            int intMax  = (int) max;
            ns = new NumericalSpec(table, column, intMin, intMax, magnitude, unit);
        }

        db.close();
        return ns;

    }

    public CategoricalSpec getChoices(String table, String column) {
        // Returns specs of which user can select discrete requirements (e.g. USB 2.0, USB 3.0, etc)
        SQLiteDatabase db = this.getReadableDatabase();

        List<String> choices = new LinkedList<>();     // Used to hold the list of choices.

        if (column.equals("System_chip")) {
            // Collapse the myriad SoCs to fewer, recognizable SoCs.
            choices = new LinkedList<>(Arrays.asList("Apple A5", "Apple A6", "Apple A7", "Apple A8", "Exynos 3", "Exynos 4", "Exynos 5", "Exynos 6",
                    "Exynos 7", "Exynos Octa 7", "Qualcomm Snapdragon S4", "Qualcomm Snapdragon 2XX", "Qualcomm Snapdragon 4XX", "Qualcomm Snapdragon 6XX",
                    "Qualcomm Snapdragon 8XX", "Intel Atom"));

            return new CategoricalSpec(table, column, choices);

        } else if (table.equals("Display") && column.equals("Technology")) {
            // Collapse the display technologies.
            choices = new LinkedList<>(Arrays.asList("TFT", "LCD", "IPS LCD", "OLED", "AMOLED"));
            return new CategoricalSpec(table, column, choices);

        } else if (column.equals("Carrier")) {
            choices = new LinkedList<>(Arrays.asList("T-Mobile", "AT&T", "Verizon", "Sprint", "Custom"));
            return new CategoricalSpec(table, column, choices);

        } else if (table.equals("Display") && column.equals("Features")) {
            // Many of the features are the same, but named slightly different so they are combined to common, non-repetitive terms.
            choices = new LinkedList<>();
            choices.add("Light Sensor"); choices.add("Proximity Sensor"); choices.add("Oleophobic Coating"); choices.add("Scratch-resistant glass"); choices.add("Polarizing filter");
            choices.add("Corning Gorilla Glass"); choices.add("Corning Gorilla Glass 2"); choices.add("Corning Gorilla Glass 3"); choices.add("Corning Gorilla Glass 4");
            choices.add("Dragontail");
            return new CategoricalSpec(table, column, choices);

        } else if (column.equals("Wireless_charging")) {
            choices = new LinkedList<> (Arrays.asList("Optional", "Built-in"));
            return new CategoricalSpec(table, column, choices);
        } else {
            Cursor cursor = db.query(table, new String[] {column}, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(column)) == null) {
                    cursor.moveToNext();
                    continue;
                }
                String stringValue = cursor.getString(cursor.getColumnIndex(column));       // All choices are held as a single string in the table.
                List<String> tempValuesList = Arrays.asList(stringValue.split(","));        // Choices are separated by the comma so they can be added as discrete choices to the choiceList.
                for (String value : tempValuesList) {
                    if (!choices.contains(value.trim())) {
                        // Check to make sure the list doesn't already contain the value before adding it to the list.
                        choices.add(value.trim());
                    }
                }
                cursor.moveToNext();
            }
        }

        CategoricalSpec cs = new CategoricalSpec(table, column, choices);       // Added as a CategoricalSpec object holding all choices.
        return cs;
    }

    boolean firstRun;

    public List<String> getCandidatePhones(Map<Integer, List<UserReq>> inputSpecMap, List<String> specificPhones) {
        // Returns a list of all phones that meet each of the user's requirements.
        firstRun = true;        // Used to ensure that phones that meet the first requirement become the base list of phones to return in retainCandidatePhones() -- See below.

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        List<String> candidatePhonesList;

        Map<Integer, String> candidatePhonesMap = new TreeMap<>();
        Map<Integer, String> tempCandidatePhonesMap = new TreeMap<>();

        List<UserReq> physicalSizeReqList = null;

        listReqLoop:
        for (List<UserReq> specReqList : inputSpecMap.values()) {
            // Puts each group of requirements (each group is "linked" in the sense that if the phone meets any of the requirements in the group, then it qualifies)
            // in the function to return phones that meet the requirement.
            List<String> specList = new LinkedList<>();
            boolean containsPhysicalSize = false;
            for (UserReq userReq : specReqList) {
                specList.add(userReq.getSpec());
                if (userReq.getSpec().equals("Physical_size")) {
                    Log.d("MySQL.getCandPhones", "Skipping physical size trait");
                    Log.d("MySQL.getCandPhones", "List contains: " + specList);
                    physicalSizeReqList = specReqList;
                    if (specList.size() == specReqList.size()) {
                        continue listReqLoop;
                    } else {
                        containsPhysicalSize = true;
                        continue;
                    }
                }
                if (specList.size() == specReqList.size() && containsPhysicalSize) {
                    continue listReqLoop;
                }

            }
            tempCandidatePhonesMap = retainCandidatePhones(specReqList, candidatePhonesMap);
            candidatePhonesMap = tempCandidatePhonesMap;
        }

        if (specificPhones != null && !specificPhones.isEmpty()) {
            // Phones that are specifically added to the list are added to the candidatePhonesMap at the end because they qualify despite not meeting the exact requirements.
            Log.d("MySQL.retainCandPhones", "Specific phones to be added: " + specificPhones.toString());
            cursor = db.query("availability", new String[] {"phone_model", "popularity"}, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String phoneModel = cursor.getString(cursor.getColumnIndex("phone_model"));
                int popularity = cursor.getInt(cursor.getColumnIndex("popularity"));
                if (specificPhones.contains(phoneModel)) {
                    Log.d("MySQL.retainCandPhones", "Adding: " + phoneModel + ".");
                    candidatePhonesMap.put(popularity, phoneModel);
                }
                cursor.moveToNext();
            }
        }

        if (physicalSizeReqList != null) {
            Log.d("MySQL.getCandPhones", "Resuming physical size requirement");
            tempCandidatePhonesMap = retainCandidatePhones(physicalSizeReqList, candidatePhonesMap);
            candidatePhonesMap = tempCandidatePhonesMap;
        }

        candidatePhonesList = new LinkedList<>(candidatePhonesMap.values());
        return candidatePhonesList;
    }

    private Map<Integer, String> getCatSpecPhones(UserReq userReq) {
        // Returns a list of phones that match the user's categorical requirement.
        Map<Integer, String> candidatePhonesMap = new TreeMap<>();

        // Initializes the terms to query the db.
        String category = userReq.getCategory();
        String spec = userReq.getSpec();
        String operator = userReq.getOperator();

        // The user's requirements.
        List<String> choiceList = userReq.getChoice();

        Log.i("MySQL.getCatSpecPhones", "Spec: " + spec + " | User choice: " + choiceList);
        // Converts columns to be queried to a String Array.
        List tempList = new LinkedList<String>(Arrays.asList("phone_model", spec, "popularity"));
        String [] columns = new String[tempList.size()];
        tempList.toArray(columns);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(category, columns, null, null, null, null, null);
        cursor.moveToFirst();

        phoneLoop:
        while (!cursor.isAfterLast()) {
            boolean containsSpec = false;
            String phoneModel = cursor.getString(cursor.getColumnIndex("phone_model"));
            String phoneSpec = cursor.getString(cursor.getColumnIndex(spec));
            int popularity = cursor.getInt(cursor.getColumnIndex("popularity"));

            if (operator.equals("or")) {
                // If it uses the "or" operator, then if the phone contains any of the requirements, it qualifies.
                for (String choice : choiceList) {
                    if (phoneSpec != null && phoneSpec.contains(choice)) {
                        candidatePhonesMap.put(popularity, phoneModel);
                        cursor.moveToNext();
                        continue phoneLoop;
                    }
                }
            } else if (operator.equals("and")) {
                // If it uses the "and" operator, then the phone must contain ALL of the requirements to qualify.
                for (String choice : choiceList) {
                    if (phoneSpec != null && phoneSpec.contains(choice)) {
                        containsSpec = true;
                    } else {
                        cursor.moveToNext();
                        continue phoneLoop;
                    }
                }
                if (containsSpec) {
                    candidatePhonesMap.put(popularity, phoneModel);
                }
            }
            cursor.moveToNext();
        }
        // db.close();
        return candidatePhonesMap;
    }

    private Map<Integer, String> getNumSpecPhones(UserReq userReq) {
        // Returns a list of phones that meet the user's numerical requirement.
        SQLiteDatabase db = this.getReadableDatabase();

        Map<Integer, String> candidatePhonesMap = new TreeMap<>();

        double min = userReq.getMin();
        double max = userReq.getMax();
        Log.d("MySQL.getNumSpecPhones", "Spec: " + userReq.getSpec() + " | Min : " + min + " | Max: " + max);

        String category = userReq.getCategory();
        String spec = userReq.getSpec();

        List<String> choiceList;
        if (userReq.getResolutions() == null) {
            choiceList = userReq.getChoice();
        } else {
            choiceList = userReq.getResolutions();
        }

        // Place the columns to query in a String array.
        List<String> tempList;

        if (spec.contains("Dimensions")) {
            tempList = new LinkedList<>(Arrays.asList("phone_model", "Dimensions", "popularity"));
        } else {
            tempList = new LinkedList<>(Arrays.asList("phone_model", spec, "popularity"));
        }
        String[] columns = new String[tempList.size()];
        tempList.toArray(columns);

        Cursor cursor = db.query(category, columns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // Add phones that qualify to a TreeMap.
            String phoneModel = cursor.getString(cursor.getColumnIndex("phone_model"));
            String phoneSpec;
            if (spec.contains("Dimensions")) {
                if (cursor.getString(cursor.getColumnIndex("Dimensions")) == null) {
                    cursor.moveToNext();
                    continue;
                } else if (cursor.getString(cursor.getColumnIndex("Dimensions")).isEmpty()) {
                    cursor.moveToNext();
                    continue;
                }
                phoneSpec = cursor.getString(cursor.getColumnIndex("Dimensions"));
                Pattern regex = Pattern.compile("([\\d]*\\.[\\d]*|[\\d]*) x ([\\d]*.[\\d]*|[\\d]*) x ([\\d]*.[\\d]*|[\\d]*) inches \\(([\\d]*\\.[\\d]*|[\\d]*) x ([\\d]*\\.[\\d]*|[\\d]*) x ([\\d]*.[\\d]*|[\\d]*)(?: mm\\)| mm \\)|mm\\)|mm \\)|\\)| \\))");
                Matcher tokens = regex.matcher(phoneSpec);
                boolean found = tokens.matches();

                if (spec.contains("height")) {
                    phoneSpec = tokens.group(1);
                } else if (spec.contains("width")) {
                    phoneSpec = tokens.group(2);
                } else if (spec.contains("depth")) {
                    phoneSpec = tokens.group(3);
                }

            } else {
                phoneSpec = cursor.getString(cursor.getColumnIndex(spec));
            }
            int popularity = cursor.getInt(cursor.getColumnIndex("popularity"));

            if (choiceList != null) {
                // User requirement is a selection of choices.
                if (phoneSpec == null) {
                    cursor.moveToNext();
                    continue;
                }
                for (String choice : choiceList) {
                    if (phoneSpec.contains(choice)) {
                        candidatePhonesMap.put(popularity, phoneModel);
                    }
                }
            } else {
                // User requirement is a single numerical value that falls between a maximum and minimum.
                // Log.d("MySQL.getNumSpecPhones", "Phone Spec = " + phoneSpec);
                if (phoneSpec == null) {
                    cursor.moveToNext();
                    continue;
                }

                phoneSpec = phoneSpec.replaceAll("[^\\d.]", "");
                double phoneValue = Double.parseDouble(phoneSpec);
                // Log.d("MySQL.getNumSpecPhones", "Phone: " + phoneModel + " | Phone Value: " + phoneValue);
                if (phoneValue >= min && phoneValue <= max) {
                    // Log.d("MySQL.getNumSpecPhones", "Candidate phone: " + phoneModel);
                    candidatePhonesMap.put(popularity, phoneModel);
                }
            }
            cursor.moveToNext();
        }
        return candidatePhonesMap;
    }

    private Map<Integer, String> retainCandidatePhones(List<UserReq> specReqList, Map<Integer, String> candidatePhonesMap) {
        // Combines each list of phones that meet the requirements to a single comprehensive list.
        List<String> candidatePhonesList;
        List<String> tempCandidatePhonesList;

        Map<Integer, String> tempCandidatePhonesMap = new LinkedHashMap<>();        // Ensures that the phones are always sorted by popularity.

        for (UserReq userReq : specReqList) {
            // Iterates through each user requirement in each spec group and combines all phones that meet any of the requirements to a single list.
            if (userReq.getType().equals("num")) {
                tempCandidatePhonesMap.putAll(getNumSpecPhones(userReq));
            } else if (userReq.getType().equals("cat")) {
                tempCandidatePhonesMap.putAll(getCatSpecPhones(userReq));
            } else {
                Log.d("MySQL.retainCandidates", "Unknown user requirement type: " + userReq.getType());
                return null;
            }
        }

        if (candidatePhonesMap.isEmpty() && firstRun) {
            // The first list of phones to go through the function will become the candidate List/Map
            firstRun = false;
            candidatePhonesMap = tempCandidatePhonesMap;
            Log.i("MySQL.retainCandPhones", "Candidate phones: " + candidatePhonesMap.values());
            return candidatePhonesMap;
        }

        // Initialize the candidate phones list that is passed from the iterations in the getCandidatePhones function and the tempCandidatePhonesList is that created by this function.
        candidatePhonesList = new LinkedList<>(candidatePhonesMap.values());
        tempCandidatePhonesList = new LinkedList(tempCandidatePhonesMap.values());

        // Creates a list of phones that are common within both the candidatePhonesList and the tempCandidatePhonesList.
        List<String> commonPhones = new LinkedList(candidatePhonesList);
        commonPhones.retainAll(tempCandidatePhonesList);

        // Creates a list of phones that are not contained in both lists by creating a List containing all potentially candidate phones and then removing all phones common to both Lists.
        List<String> removedPhones = new LinkedList(candidatePhonesList);

        for (String phone : tempCandidatePhonesList) {
            if (!removedPhones.contains(phone) && candidatePhonesList.contains(phone)) {
                removedPhones.add(phone);
            }
        }
        // removedPhones.addAll(tempCandidatePhonesList);
        removedPhones.removeAll(commonPhones);
        Log.i("MySQL.retainCandPhones", "Removing: " + removedPhones + " from candidates.");
        for (String removedPhone : removedPhones) {
            // Remove each phone from the candidatePhonesMap that is not in either the candidatePhonesList or the tempCandidatePhonesList.
            try {
                Map<Integer, String> candidatePhonesMapCopy = new LinkedHashMap<>();
                candidatePhonesMapCopy.putAll(candidatePhonesMap);
                for (Integer removedPhonePopularity : candidatePhonesMapCopy.keySet()) {
                    if (removedPhone == candidatePhonesMapCopy.get(removedPhonePopularity)) {
                        candidatePhonesMap.remove(removedPhonePopularity);
                    }
                }
            } catch (ClassCastException cce) {
                cce.printStackTrace();
            } catch (ConcurrentModificationException cme) {
                cme.printStackTrace();
            }
        }

        return candidatePhonesMap;
    }


    // DEPRECATED FUNCTION. KEEP FOR REFERENCE.
    public LinkedList<String> returnCandidatePhones(Map<String, UserCatReq> inputCatSpecMap, Map <String, UserNumReq> inputNumSpecMap, List<String> specificPhones) {
        // Return a list of all phones that fit every requirement the user has. specificPhones contains a list of phones that should be added to the list regardless of candidacy.
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        Map<Integer, String> tempCandidatePhones = new TreeMap<>();
        Map<Integer, String> candidatePhones = new TreeMap<>();

        int i = 0;      // Used to ensure the first list of temporary candidate phones are actually added to the final list "candidatePhones."

        requirementloop:
        for (String requirement : inputCatSpecMap.keySet()) {
            // Get variables from the UserCatSpec.

            tempCandidatePhones = new TreeMap<>();
            UserCatReq userCatReq = inputCatSpecMap.get(requirement);
            String table = userCatReq.getCategory();
            List<String> tempList = new LinkedList<>(Arrays.asList("phone_model", userCatReq.getSpec()));
            tempList.add("popularity");
            String [] column = new String[tempList.size()];
            tempList.toArray(column);
            Log.d("MySQL.retCandPhones", "Columns: " + column[1].toString());
            List<String> choiceList = userCatReq.getChoice();
            String operator = userCatReq.getOperator();
            Map<String, List> carrierMap = userCatReq.getCarrierMap();

            if (column[1].equals("carrier")) {
                // Carrier is a special case containing multiple columns, so a String Array is used to contain all the values and pass them to the db query.

                Log.d("MySQL.returnPhones", "Carrier caught!");
                tempList = new LinkedList<String>();
                tempList.add("phone_model");
                for (String network : carrierMap.keySet()) {
                    tempList.add(network);
                }
                column = new String[tempList.size()];
                tempList.toArray(column);
                Log.d("MySQL.returnPhones", tempList.toString());
            }

            int k = 1;
            if (carrierMap != null) {
                // If the spec is a carrier, then utilize this loop which searches through each column.

                cursor = db.query(table, column, null, null, null, null, null);
                cursor.moveToFirst();

                carrierphoneloop:
                while (!cursor.isAfterLast()) {
                    if (cursor.isLast()) {
                        break;
                    }

                    for (String network : carrierMap.keySet()) {
                        String phone = cursor.getString(cursor.getColumnIndex("phone_model"));
                        String phoneSpec = cursor.getString(cursor.getColumnIndex(network.trim()));
                        int popularity = cursor.getInt(cursor.getColumnIndex("popularity"));

                        if (phoneSpec == null) {
                            if (candidatePhones.values().contains(phone)) {
                                candidatePhones.remove(phone);
                            } else if (tempCandidatePhones.values().contains(phone)) {
                                tempCandidatePhones.remove(phone);
                            }
                            cursor.moveToNext();
                            continue;
                        }

                        int j = 0;      // Pretty much used as a boolean value to hold whether the phone contains all required frequencies for each network or not.

                        List<String> tempPhoneFreq = new LinkedList<>(Arrays.asList(phoneSpec.split(",")));
                        List<String> phoneFreq = new LinkedList<>();

                        for (String frequency : tempPhoneFreq) {
                            frequency = frequency.replace("MHz", "").trim();
                            phoneFreq.add(frequency);
                        }

                        List<String> requiredFrequencies = new LinkedList<>(carrierMap.get(network));
                        List<String> tempReqFreq = requiredFrequencies;
                        tempReqFreq.removeAll(phoneFreq);
                        Log.i("MySQL.returnPhones", "Missing required frequencies: " + tempReqFreq.toString());

                        if (tempReqFreq.size() <= 1) {
                            j = 1;
                            Log.i("MySQL.returnPhones", network.toUpperCase() + " frequencies okay! " + phone + " added!");
                            if (i == 0 && !candidatePhones.values().contains(phone)) {
                                candidatePhones.put(popularity, phone);
                            } else if (i != 0 && !tempCandidatePhones.values().contains(phone)){
                                tempCandidatePhones.put(popularity, phone);
                            }

                        } else {
                            j = 0;
                            if (tempCandidatePhones.values().contains(phone)) {
                                tempCandidatePhones.remove(phone);
                                Log.i("MySQL.returnPhones", network.toUpperCase() + " frequencies missing! " + phone + " removed!");
                            }
                            if (candidatePhones.values().contains(phone)) {
                                candidatePhones.remove(phone);
                            }
                        }
                        if (j == 0) {
                            Log.i("MySQL.returnPhones", phone + " not compatible. Moving to next phone.");
                            cursor.moveToNext();
                            continue carrierphoneloop;
                        }
                    }
                    cursor.moveToNext();
                }

                if (i != 0) {
                    // candidatePhones.retainAll(tempCandidatePhones);
                }
                i++;

            } else {
                cursor = db.query(table, column, null, null, null, null, null);
                cursor.moveToFirst();
                // Log.d("MySQL.retCandidatePhones", "Column: " + column[1].toString());
                categorySpecPhoneCycle:
                while (!cursor.isAfterLast()) {

                    String phone = cursor.getString(cursor.getColumnIndex("phone_model"));
                    String phoneSpec = cursor.getString(cursor.getColumnIndex(column[1]));
                    int popularity = cursor.getInt(cursor.getColumnIndex("popularity"));

                    if (specificPhones != null) {
                        // Add specified phone models to the list regardless of whether they meet the minimum requirements.
                        if (specificPhones.contains(phone) && i == 0 && !candidatePhones.values().contains(phone)) {
                            candidatePhones.put(popularity, phone);
                            Log.d("MySQLiteHelper", "Specific phone: " + phone + " added!");
                            cursor.moveToNext();
                            continue;
                        } else if (specificPhones.contains(phone) && i != 0 && !tempCandidatePhones.values().contains(phone)) {
                            tempCandidatePhones.put(popularity, phone);
                            cursor.moveToNext();
                            continue;
                        }
                    }

                    if (phoneSpec == null) {
                        cursor.moveToNext();
                        continue;
                    }
                    boolean containsSpec = false;

                    if (operator.equals("and")) {
                        for (String choice : choiceList) {
                            if (phoneSpec.contains(choice)) {
                                containsSpec = true;
                            } else {
                                containsSpec = false;
                            }
                            if (!containsSpec) {
                                cursor.moveToNext();
                                continue categorySpecPhoneCycle;
                            }
                        }
                    } else if (operator.equals("or")) {
                        for (String choice : choiceList) {
                            if (phoneSpec.contains(choice)) {
                                containsSpec = true;
                                break;
                            } else {
                                containsSpec = false;
                            }
                        }
                    }

                    if (containsSpec && i == 0) {
                        candidatePhones.put(popularity, phone);
                        Log.d("debug candidatePhones", phone + " added to list");
                    } else if (containsSpec && i != 0) {
                        Log.d("debug tempPhones", phone + " added to list");
                        tempCandidatePhones.put(popularity, phone);
                    }

                    cursor.moveToNext();
                }

                if (i != 0) {
                    // candidatePhones.retainAll(tempCandidatePhones);
                    List<String> candidatePhoneList = new LinkedList<>(candidatePhones.values());
                    List<String> tempCandidatePhoneList = new LinkedList<>(tempCandidatePhones.values());
                    List<String> removePhoneList = new LinkedList<String>(candidatePhoneList);
                    removePhoneList.addAll(tempCandidatePhoneList);
                    List<String> commonPhoneList = new LinkedList<>(candidatePhoneList);
                    commonPhoneList.retainAll(tempCandidatePhoneList);
                    removePhoneList.removeAll(commonPhoneList);
                    cycle:
                    for (int j = candidatePhones.size(); j > 0; j--) {
                        Log.d("MySQL.returnCandPhones", "Testing " + candidatePhones.get(j));
                        for (String removePhone : removePhoneList) {
                            if (removePhone.equals(candidatePhones.get(j))) {
                                candidatePhones.remove(j);
                                continue cycle;
                            }
                        }
                    }
                }
                i++;
            }
        }

        for (String requirement : inputNumSpecMap.keySet()) {
            tempCandidatePhones = new TreeMap<Integer, String>();
            UserNumReq userNumReq = inputNumSpecMap.get(requirement);
            String table = userNumReq.getCategory();
            // List<String> tempSpecList = new LinkedList<String>();

            String column = userNumReq.getSpec();
            double min = userNumReq.getMin();
            double max = userNumReq.getMax();
            List<String> choiceList = null;

            // Save either the list of resolutions as the choiceList or any selection with only a minimum (because all higher values are automatically added to the list) as the choiceList.
            if (userNumReq.getResolutions() == null) {
                choiceList = userNumReq.getChoiceList();
            } else if (userNumReq.getChoiceList() == null) {
                choiceList = userNumReq.getResolutions();
            }

            cursor = db.query(table, new String[] {"phone_model", column, "popularity"}, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String phone = cursor.getString(cursor.getColumnIndex("phone_model"));
                String phoneSpec = cursor.getString(cursor.getColumnIndex(column));
                int popularity = cursor.getInt(cursor.getColumnIndex("popularity"));
                Log.d("MySQL.retCandPhones", "Columns: " + column.toString());

                if (specificPhones != null) {
                    // Add specified phone models to the list regardless of whether they meet the minimum requirements.
                    if (specificPhones.contains(phone) && i == 0 && !candidatePhones.values().contains(phone)) {
                        candidatePhones.put(popularity, phone);
                        Log.d("MySQL.retCandPhones", "Specific phone: " + phone + " added!");
                        cursor.moveToNext();
                        continue;
                    } else if (specificPhones.contains(phone) && i != 0 && !tempCandidatePhones.values().contains(phone)) {
                        tempCandidatePhones.put(popularity, phone);
                        cursor.moveToNext();
                        continue;
                    }
                }

                if (phoneSpec == null) {
                    cursor.moveToNext();
                    continue;
                }

                if (choiceList != null) {
                    // If there is a list of choices populated above, add phones to the appropriate candidate list.
                    for (String choice : choiceList) {
                        if (phoneSpec.contains(choice) && i == 0) {
                            if (!candidatePhones.values().contains(phone)) {
                                candidatePhones.put(popularity, phone);
                                Log.d("MySQL.retCandPhones", phone + " added to list");
                            }
                        } else if (phoneSpec.contains(choice) && i != 0) {
                            tempCandidatePhones.put(popularity, phone);
                            Log.d("MySQL.retCandPhones", phone + " added to list");
                        }
                    }
                } else {
                    String stringValue = phoneSpec.replaceAll("[^\\d.]", "");       // Parse the value of the column of each phone as a stringValue then to double value so it can be compared to the minimum and
                    // maximum value selected by the user.
                    double phoneValue = Double.parseDouble(stringValue);
                    Log.d("MySQL.retCandPhones", Double.toString(phoneValue));
                    if ((phoneValue >= min && phoneValue <= max) && i == 0) {
                        if (!candidatePhones.values().contains(phone)) {
                            candidatePhones.put(popularity, phone);
                            Log.d("MySQL.retCandPhones", phone + " added to list");
                        }
                    } else if ((phoneValue >= min && phoneValue <= max) && i != 0) {
                        Log.d("MySQL.retCandPhones", phone + " added to list");
                        tempCandidatePhones.put(popularity, phone);
                    }
                }

                cursor.moveToNext();
            }
            if (i != 0) {
                //candidatePhones.retainAll(tempCandidatePhones);
            }
            i++; Log.d("debug", "numspec + 1");
        }

        if (specificPhones != null && candidatePhones.isEmpty()) {
            cursor = db.query("availability", new String[] {"phone_model", "image_url", "popularity"}, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String phone = cursor.getString(cursor.getColumnIndex("phone_model"));
                int popularity = cursor.getInt(cursor.getColumnIndex("popularity"));
                if (specificPhones.contains(phone)) {
                    candidatePhones.put(popularity, phone);
                }
                cursor.moveToNext();
            }
        }

        db.close();
        Log.d("MySQL.retCandPhones", "Candidate phones: " + candidatePhones);
        return new LinkedList<String>(candidatePhones.values());
        // return candidatePhones;
    }

    public Map<String, String> getImageURL(List<String> phones) {
        // Returns the URL for the thumbnail of the queried phone.
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, String> imageURLMap = new LinkedHashMap<>();
        String imageURL = null;
        for (String phone : phones) {
            Cursor cursor = db.query("availability", new String[]{"image_url"}, "phone_model=?", new String[]{phone}, null, null, null);
            cursor.moveToFirst();
            if (cursor.getString(cursor.getColumnIndex("image_url")) != null) {
                imageURL = cursor.getString(cursor.getColumnIndex("image_url"));
                imageURLMap.put(phone, imageURL);
            } else {
                cursor.moveToNext();
                continue;
            }
        }
        db.close();
        return imageURLMap;
    }

    public List<String> getPhones() {
        // Returns a list of the 20 most popular phones.
        SQLiteDatabase db = this.getReadableDatabase();
        Map<Integer, String> phoneMap = new TreeMap<>();
        List<String> phones = new LinkedList<String>();
        Cursor cursor = db.query("camera", new String[]{"phone_model", "popularity"}, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String phone = cursor.getString(cursor.getColumnIndex("phone_model"));
            int popularity = cursor.getInt(cursor.getColumnIndex("popularity"));
            // Log.d("MySQLHelper.getPhones", "Phone: " + cursor.getString(cursor.getColumnIndex("phone_model")));
            if (popularity < 20) {
                phoneMap.put(popularity, phone);
            }
            cursor.moveToNext();
        }
        if (phoneMap != null) {
            phones = new LinkedList<>(phoneMap.values());
        }
        return phones;
    }

    public Phone getPhoneSpecs (String phone) {
        // Returns a phone object that contains all the specs of the phone.
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> tableList = new LinkedList<String>();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        Map<String, List<String>> categorySpecMap = new LinkedHashMap<>();
        Map<String, String> specPhoneSpecMap = new LinkedHashMap<>();

        if (cursor.moveToFirst()) {
            // Get a list of all tables in the db be used in the query below to return all the specifications of the phone.

            do {
                // Ignore the following tables because they do not provide useful data to the user.
                if (!cursor.getString(0).equals("availability") && !cursor.getString(0).equals("android_metadata") && !cursor.getString(0).equals("multimedia") &&
                        !cursor.getString(0).equals("internet_browsing")) {
                    // Log.d("MySQLite getPhoneSpecs", cursor.getString(0));
                    tableList.add(cursor.getString(0));
                }
            } while (cursor.moveToNext());
            // Log.d("MySQLite getPhoneSpecs", tableList.toString());
        }

        for (String table : tableList) {
            // Specs are separated by category in the table and the phone object for easier viewing by the user.
            cursor = db.query(table, null, "phone_model=?", new String[] {phone}, null, null, null);
            cursor.moveToFirst();
            List<String> columns = new LinkedList<String>();
            List<String> phoneSpecs = new LinkedList<>();
            categorySpecMap.put(table, columns);
            for (String column : cursor.getColumnNames()) {
                if (column.equals("popularity")) {
                    continue;
                }
                columns.add(column);
            }
            for (String column : columns) {
                if (column == null || column.equals("popularity")) {
                    // Skip the popularity column as it is not really a "spec."
                    continue;
                }
                // Log.d("MySQL.getPhoneSpecs", "Table: " + table + " | Column: " + column);
                String phoneSpec = cursor.getString(cursor.getColumnIndex(column));
                if (phoneSpec == null) {
                    // If the spec is null, just puts a "no" placeholder in its place. *Consider removing it from view altogether.*
                    phoneSpec = "No";
                }
                // phoneSpecs = (Arrays.asList(phoneSpec.split(",")));
                specPhoneSpecMap.put(table + column, phoneSpec);
            }


        }
        Phone selectedPhone = new Phone(phone, categorySpecMap, specPhoneSpecMap);
        return selectedPhone;
    }

    public List<String> searchPhones(String searchString) {
        // Allows the user to search the phone by its name.

        // Remove casing from the search query because users have no idea what the case should be.
        // Each term is then separated by a space (" ") and split off and trimmed so they can be matched individually and added to an Array to iterate through.
        searchString = searchString.toLowerCase();
        String[] searchArray = searchString.split(" ");
        Log.d("MySQL.SearchPhones", "Search terms: " + searchArray);

        SQLiteDatabase db = this.getReadableDatabase();
        Map<Integer, String> phoneResultsMap = new TreeMap<>();
        List<String> phones = new LinkedList<String>();
        Cursor cursor = db.query("camera", new String[]{"phone_model", "popularity"}, null, null, null, null, null);
        cursor.moveToFirst();

        nextPhone:
        while (!cursor.isAfterLast()) {
            // Phone model must contain all terms to be returned.
            String phone = cursor.getString(cursor.getColumnIndex("phone_model"));
            int popularity = cursor.getInt(cursor.getColumnIndex("popularity"));

            boolean containsTerm = false;
            // Log.d("MySQL.searchPhones", phone);
            for (String searchTerm : searchArray) {
                if (phone.toLowerCase().contains(searchTerm)) {
                    containsTerm = true;
                    // Log.d("MySQL", "Contains " + searchString);
                } else {
                    // Move on if the phone does not contain any of the terms.
                    containsTerm = false;
                    break;
                }
            }
            if (containsTerm) {
                // Add phone to the list of phones to return if it contains all search terms.
                phoneResultsMap.put(popularity, phone);
            }
            cursor.moveToNext();
        }

        if (phoneResultsMap.values() != null) {
            // Phones sorted by popularity are then placed in a list to be returned.
            phones = new LinkedList<>(phoneResultsMap.values());
        }

        if (phones.isEmpty()) {
            // If the list is null, user will be told no phones match their query.
            return null;
        }
        return phones;
    }

    public void addEnduranceRating(Map<String, String> enduranceMap) {
        // Adds Endurance Rating from GSM website for phones that match those in the database.
        SQLiteDatabase db = this.getWritableDatabase();
        String stringStatement = "UPDATE battery SET Endurance_Rating=? WHERE phone_model=?";       // Statement to be compiled that directs where to add each Endurance Rating.

        List<String> columns = new LinkedList<>(returnColumns("battery"));
        if (!columns.contains("Endurance_Rating")) {
            // If the column for some reason does not already contain the "Endurance Rating" column, the table is altered to add the column.
            Log.d("MySQL.AddEndurance", "Missing 'Endurance Rating' column. Altering battery table.");
            String sqlStatement = "ALTER TABLE battery ADD COLUMN Endurance_Rating TEXT";
            db.execSQL(sqlStatement);
        }
        db.beginTransaction();
        for (String phoneModel : enduranceMap.keySet()) {
            // Use db transactions to speed up the process. The Endurance Rating is bound to the phone model.
            String enduranceRating = enduranceMap.get(phoneModel);
            phoneModel = phoneModel.replace(" ", "_");
            // Log.d("MySQL.AddEndurance", phoneModel + " | Endurance: " + enduranceRating);
            SQLiteStatement sqLiteStatement = db.compileStatement(stringStatement);
            sqLiteStatement.bindString(1, enduranceRating);
            sqLiteStatement.bindString(2, phoneModel);
            long entryID = sqLiteStatement.executeInsert();
            sqLiteStatement.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<String> getSoCChoices(String SoC) {
        List<String> SoCSub = new LinkedList<>();


        if (SoC.equals("Qualcomm Snapdragon 2XX")) {
            SoCSub.add("Snapdragon 200"); SoCSub.add("Snapdragon 208"); SoCSub.add("Snapdragon 210"); SoCSub.add("Snapdragon 212");

        } else if (SoC.equals("Qualcomm Snapdragon 4XX")) {
            SoCSub.add("Snapdragon 400"); SoCSub.add("Snapdragon 410"); SoCSub.add("Snapdragon 412"); SoCSub.add("Snapdragon 415"); SoCSub.add("Snapdragon 425");

        } else if (SoC.equals("Qualcomm Snapdragon 6XX")) {
            SoCSub.add("Snapdragon 600"); SoCSub.add("Snapdragon 602"); SoCSub.add("Snapdragon 610"); SoCSub.add("Snapdragon 615"); SoCSub.add("Snapdragon 616");
            SoCSub.add("Snapdragon 618"); SoCSub.add("Snapdragon 620");

        } else if (SoC.equals("Qualcomm Snapdragon 8XX")) {
            SoCSub.add("Snapdragon 800"); SoCSub.add("Snapdragon 801"); SoCSub.add("Snapdragon 805"); SoCSub.add("Snapdragon 808"); SoCSub.add("Snapdragon 810");
            SoCSub.add("Snapdragon 820");

        } else if (SoC.equals("S4")) {
            // Only one processor here;
            return null;

        } else {
            Log.d("MySQL.getSoCChoices", "Unknown SoC: " + SoC);
            return null;
        }
        return SoCSub;
    }
}