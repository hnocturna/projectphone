package com.android.projectphone.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;

import com.android.projectphone.Phone;

/**
 * Created by hnoct on 10/1/2015.
 */
public class PhoneProvider extends ContentProvider {
    final String LOG_TAG = PhoneProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PhoneDbHelper mOpenHelper = new PhoneDbHelper(getContext());

    static final int AVAILABILTY = 100;
    static final int AVAILABILTY_WITH_PHONE = 101;
    static final int BATTERY = 200;
    static final int CAMERA = 300;
    static final int CONNECTIVITY = 400;
    static final int DESIGN = 500;
    static final int DISPLAY = 600;
    static final int HARDWARE = 700;
    static final int INTERNET = 800;
    static final int MULTIMEDIA = 900;
    static final int FEATURES = 1000;
    static final int TECHNOLOGY = 1100;

    static SQLiteQueryBuilder sQueryBuilder(String table) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Inner join for tables should look like:
        // availability INNER JOIN [table] ON availability.phone_model = [table].phone_model
        queryBuilder.setTables(
                PhoneContract.AvailabilityEntry.TABLE_NAME + " INNER JOIN " +
                        table +
                        " ON " + PhoneContract.AvailabilityEntry.TABLE_NAME +
                        "." + PhoneContract.AvailabilityEntry.COLUMN_PHONE_KEY +
                        " = " + table +
                        "." + "phone_model");

        return queryBuilder;
    }

    public static String testString() {
        return
            PhoneContract.AvailabilityEntry.TABLE_NAME +
                " INNER JOIN " + PhoneContract.BatteryEntry.TABLE_NAME +
                " ON " + PhoneContract.AvailabilityEntry.TABLE_NAME +
                "." + PhoneContract.AvailabilityEntry.COLUMN_PHONE_KEY +
                " = " + PhoneContract.BatteryEntry.TABLE_NAME +
                "." + PhoneContract.BatteryEntry.COLUMN_PHONE_KEY +

                " INNER JOIN " + PhoneContract.CameraEntry.TABLE_NAME +
                " ON " + PhoneContract.BatteryEntry.TABLE_NAME +
                "." + PhoneContract.BatteryEntry.COLUMN_PHONE_KEY +
                " = " + PhoneContract.CameraEntry.TABLE_NAME +
                "." + PhoneContract.CameraEntry.COLUMN_PHONE_KEY +

                " INNER JOIN " + PhoneContract.ConnectivityEntry.TABLE_NAME +
                " ON " + PhoneContract.CameraEntry.TABLE_NAME +
                "." + PhoneContract.CameraEntry.COLUMN_PHONE_KEY +
                " = " + PhoneContract.ConnectivityEntry.TABLE_NAME +
                "." + PhoneContract.ConnectivityEntry.COLUMN_PHONE_KEY +

                " INNER JOIN " + PhoneContract.DesignEntry.TABLE_NAME +
                " ON " + PhoneContract.ConnectivityEntry.TABLE_NAME +
                "." + PhoneContract.ConnectivityEntry.COLUMN_PHONE_KEY +
                " = " + PhoneContract.DesignEntry.TABLE_NAME +
                "." + PhoneContract.DesignEntry.COLUMN_PHONE_KEY +

                " INNER JOIN " + PhoneContract.DisplayEntry.TABLE_NAME +
                " ON " + PhoneContract.DesignEntry.TABLE_NAME +
                "." + PhoneContract.DesignEntry.COLUMN_PHONE_KEY +
                " = " + PhoneContract.DisplayEntry.TABLE_NAME +
                "." + PhoneContract.DisplayEntry.COLUMN_PHONE_KEY +

                " INNER JOIN " + PhoneContract.HardwareEntry.TABLE_NAME +
                " ON " + PhoneContract.DisplayEntry.TABLE_NAME +
                "." + PhoneContract.DisplayEntry.COLUMN_PHONE_KEY +
                " = " + PhoneContract.HardwareEntry.TABLE_NAME +
                "." + PhoneContract.HardwareEntry.COLUMN_PHONE_KEY +

                " INNER JOIN " + PhoneContract.InternetEntry.TABLE_NAME +
                " ON " + PhoneContract.HardwareEntry.TABLE_NAME +
                "." + PhoneContract.HardwareEntry.COLUMN_PHONE_KEY +
                " = " + PhoneContract.InternetEntry.TABLE_NAME +
                "." + PhoneContract.InternetEntry.COLUMN_PHONE_KEY +

                " INNER JOIN " + PhoneContract.MultimediaEntry.TABLE_NAME +
                " ON " + PhoneContract.InternetEntry.TABLE_NAME +
                "." + PhoneContract.InternetEntry.COLUMN_PHONE_KEY +
                " = " + PhoneContract.MultimediaEntry.TABLE_NAME +
                "." + PhoneContract.MultimediaEntry.COLUMN_PHONE_KEY +

                " INNER JOIN " + PhoneContract.FeaturesEntry.TABLE_NAME +
                " ON " + PhoneContract.MultimediaEntry.TABLE_NAME +
                "." + PhoneContract.MultimediaEntry.COLUMN_PHONE_KEY +
                " = " + PhoneContract.FeaturesEntry.TABLE_NAME +
                "." + PhoneContract.FeaturesEntry.COLUMN_PHONE_KEY +

                " INNER JOIN " + PhoneContract.TechnologyEntry.TABLE_NAME +
                " ON " + PhoneContract.FeaturesEntry.TABLE_NAME +
                "." + PhoneContract.FeaturesEntry.COLUMN_PHONE_KEY +
                " = " + PhoneContract.TechnologyEntry.TABLE_NAME +
                "." + PhoneContract.TechnologyEntry.COLUMN_PHONE_KEY;
    }

    private static final SQLiteQueryBuilder sAllTableQueryBuilder;

    static {
        sAllTableQueryBuilder = new SQLiteQueryBuilder();

        // Inner join between all tables to access all the information within each table
        // for pulling up all specifications of a phone. See above for example.

        sAllTableQueryBuilder.setTables(
                PhoneContract.AvailabilityEntry.TABLE_NAME +
                        " INNER JOIN " + PhoneContract.BatteryEntry.TABLE_NAME +
                        " ON " + PhoneContract.AvailabilityEntry.TABLE_NAME +
                        "." + PhoneContract.AvailabilityEntry.COLUMN_PHONE_KEY +
                        " = " + PhoneContract.BatteryEntry.TABLE_NAME +
                        "." + PhoneContract.BatteryEntry.COLUMN_PHONE_KEY +

                        " INNER JOIN " + PhoneContract.CameraEntry.TABLE_NAME +
                        " ON " + PhoneContract.BatteryEntry.TABLE_NAME +
                        "." + PhoneContract.BatteryEntry.COLUMN_PHONE_KEY +
                        " = " + PhoneContract.CameraEntry.TABLE_NAME +
                        "." + PhoneContract.CameraEntry.COLUMN_PHONE_KEY +

                        " INNER JOIN " + PhoneContract.ConnectivityEntry.TABLE_NAME +
                        " ON " + PhoneContract.CameraEntry.TABLE_NAME +
                        "." + PhoneContract.CameraEntry.COLUMN_PHONE_KEY +
                        " = " + PhoneContract.ConnectivityEntry.TABLE_NAME +
                        "." + PhoneContract.ConnectivityEntry.COLUMN_PHONE_KEY +

                        " INNER JOIN " + PhoneContract.DesignEntry.TABLE_NAME +
                        " ON " + PhoneContract.ConnectivityEntry.TABLE_NAME +
                        "." + PhoneContract.ConnectivityEntry.COLUMN_PHONE_KEY +
                        " = " + PhoneContract.DesignEntry.TABLE_NAME +
                        "." + PhoneContract.DesignEntry.COLUMN_PHONE_KEY +

                        " INNER JOIN " + PhoneContract.DisplayEntry.TABLE_NAME +
                        " ON " + PhoneContract.DesignEntry.TABLE_NAME +
                        "." + PhoneContract.DesignEntry.COLUMN_PHONE_KEY +
                        " = " + PhoneContract.DisplayEntry.TABLE_NAME +
                        "." + PhoneContract.DisplayEntry.COLUMN_PHONE_KEY +

                        " INNER JOIN " + PhoneContract.HardwareEntry.TABLE_NAME +
                        " ON " + PhoneContract.DisplayEntry.TABLE_NAME +
                        "." + PhoneContract.DisplayEntry.COLUMN_PHONE_KEY +
                        " = " + PhoneContract.HardwareEntry.TABLE_NAME +
                        "." + PhoneContract.HardwareEntry.COLUMN_PHONE_KEY +

                        " INNER JOIN " + PhoneContract.InternetEntry.TABLE_NAME +
                        " ON " + PhoneContract.HardwareEntry.TABLE_NAME +
                        "." + PhoneContract.HardwareEntry.COLUMN_PHONE_KEY +
                        " = " + PhoneContract.InternetEntry.TABLE_NAME +
                        "." + PhoneContract.InternetEntry.COLUMN_PHONE_KEY +

                        " INNER JOIN " + PhoneContract.MultimediaEntry.TABLE_NAME +
                        " ON " + PhoneContract.InternetEntry.TABLE_NAME +
                        "." + PhoneContract.InternetEntry.COLUMN_PHONE_KEY +
                        " = " + PhoneContract.MultimediaEntry.TABLE_NAME +
                        "." + PhoneContract.MultimediaEntry.COLUMN_PHONE_KEY +

                        " INNER JOIN " + PhoneContract.FeaturesEntry.TABLE_NAME +
                        " ON " + PhoneContract.MultimediaEntry.TABLE_NAME +
                        "." + PhoneContract.MultimediaEntry.COLUMN_PHONE_KEY +
                        " = " + PhoneContract.FeaturesEntry.TABLE_NAME +
                        "." + PhoneContract.FeaturesEntry.COLUMN_PHONE_KEY +

                        " INNER JOIN " + PhoneContract.TechnologyEntry.TABLE_NAME +
                        " ON " + PhoneContract.FeaturesEntry.TABLE_NAME +
                        "." + PhoneContract.FeaturesEntry.COLUMN_PHONE_KEY +
                        " = " + PhoneContract.TechnologyEntry.TABLE_NAME +
                        "." + PhoneContract.TechnologyEntry.COLUMN_PHONE_KEY);
    }

    static UriMatcher buildUriMatcher() {
        // Root of the Uri to return is NO_MATCH
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PhoneContract.CONTENT_AUTHORITY;

        // Only in the availability table will we need to return a single item ("phone").
        matcher.addURI(authority, PhoneContract.PATH_AVAILABILITY, AVAILABILTY);
        matcher.addURI(authority, PhoneContract.PATH_AVAILABILITY + "/*", AVAILABILTY_WITH_PHONE);

        matcher.addURI(authority, PhoneContract.PATH_BATTERY, BATTERY);
        matcher.addURI(authority, PhoneContract.PATH_CAMERA, CAMERA);
        matcher.addURI(authority, PhoneContract.PATH_CONNECTIVITY, CONNECTIVITY);
        matcher.addURI(authority, PhoneContract.PATH_DESIGN, DESIGN);
        matcher.addURI(authority, PhoneContract.PATH_DISPLAY, DISPLAY);
        matcher.addURI(authority, PhoneContract.PATH_HARDWARE, HARDWARE);
        matcher.addURI(authority, PhoneContract.PATH_INTERNET, INTERNET);
        matcher.addURI(authority, PhoneContract.PATH_MULTIMEDIA, MULTIMEDIA);
        matcher.addURI(authority, PhoneContract.PATH_FEATURES, FEATURES);
        matcher.addURI(authority, PhoneContract.PATH_TECHNOLOGY, TECHNOLOGY);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PhoneDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        // Utilize matcher to determine the type of URI to be returned.
        switch (match) {
            case AVAILABILTY:
                return PhoneContract.AvailabilityEntry.CONTENT_TYPE;
            case AVAILABILTY_WITH_PHONE:
                return PhoneContract.AvailabilityEntry.CONTENT_ITEM_TYPE;
            case BATTERY:
                return PhoneContract.BatteryEntry.CONTENT_TYPE;
            case CAMERA:
                return PhoneContract.CameraEntry.CONTENT_TYPE;
            case CONNECTIVITY:
                return PhoneContract.ConnectivityEntry.CONTENT_TYPE;
            case DESIGN:
                return PhoneContract.DesignEntry.CONTENT_TYPE;
            case DISPLAY:
                return PhoneContract.DisplayEntry.CONTENT_TYPE;
            case HARDWARE:
                return PhoneContract.HardwareEntry.CONTENT_TYPE;
            case INTERNET:
                return PhoneContract.InternetEntry.CONTENT_TYPE;
            case MULTIMEDIA:
                return PhoneContract.MultimediaEntry.CONTENT_TYPE;
            case FEATURES:
                return PhoneContract.FeaturesEntry.CONTENT_TYPE;
            case TECHNOLOGY:
                return PhoneContract.TechnologyEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Selects item(s) based on the query utilizing a switch statement to return the cursor at different locations.

        Cursor retCursor;

        switch(sUriMatcher.match(uri)) {
            // "availability"
            case AVAILABILTY:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PhoneContract.AvailabilityEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            // "availability/*"
            case AVAILABILTY_WITH_PHONE:
                // ret
        }


        return null;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }
}
