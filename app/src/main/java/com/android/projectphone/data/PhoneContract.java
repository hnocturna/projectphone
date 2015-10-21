package com.android.projectphone.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import org.jsoup.Connection;

/**
 * Created by hnoct on 9/29/2015.
 */
public class PhoneContract {
    public static final String CONTENT_AUTHORITY = "com.android.projectphone.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths for each of the groups of specs
    public static final String PATH_AVAILABILITY = "availability";
    public static final String PATH_BATTERY = "battery";
    public static final String PATH_CAMERA = "camera";
    public static final String PATH_CONNECTIVITY = "connectivity";
    public static final String PATH_DESIGN = "design";
    public static final String PATH_DISPLAY = "display";
    public static final String PATH_HARDWARE = "hardware";
    public static final String PATH_FEATURES = "other_features";
    public static final String PATH_TECHNOLOGY = "technology";
    public static final String PATH_INTERNET = "internet_browsing";
    public static final String PATH_MULTIMEDIA = "multimedia";

    /*
    * Inner classes that define the table of contents for each of the tables
     */
    public static final class AvailabilityEntry implements BaseColumns {
        public static final String TABLE_NAME = "availability";

        // Columns within the table
        public static final String COLUMN_PHONE_KEY = "phone_model";
        public static final String COLUMN_ANNOUNCED = "Officially_announced";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_SCHEDULED_RELEASE = "Scheduled_release";

        // Uri to access the table and its contents
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AVAILABILITY).build();

        // Type to return when accessing this table
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_AVAILABILITY;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_AVAILABILITY;

        // Used to find the specific rows within the table using a given id
        public static Uri buildAvailabilityUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class BatteryEntry implements BaseColumns {
        public static final String TABLE_NAME = "battery";

        // Columns within the table
        public static final String COLUMN_PHONE_KEY = "phone_model";
        public static final String COLUMN_TALK_2G = "Talk_time";
        public static final String COLUMN_STAND_BY_2G = "Stand_by_time";
        public static final String COLUMN_TALK_3G = "Talk_time_3G";
        public static final String COLUMN_MUSIC_PLAYBACK = "Music_playback";
        public static final String COLUMN_VIDEO_PLAYBACK = "Video_playback";
        public static final String COLUMN_CAPACITY = "Capacity";
        public static final String COLUMN_WIRELESS_CHARGING = "Wireless_charging";
        public static final String COLUMN_REPLACEABLE = "Not_user_replaceable";
        public static final String COLUMN_STAND_BY_3G = "Stand_by_time_3G";
        public static final String COLUMN_STAND_BY_4G = "Stand_by_time_4G";
        public static final String COLUMN_VIDEO_CALL = "Video_call_time";
        public static final String COLUMN_ENDURANCE = "Endurance_Rating";

        // Uri to access the table and its contents
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BATTERY).build();

        // Type to return when accessing this table
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_BATTERY;

        // Used to find the specific rows within the table using a given id
        public static Uri buildBatteryUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CameraEntry implements BaseColumns {
        public static final String TABLE_NAME = "camera";

        // Columns within the table
        public static final String COLUMN_PHONE_KEY = "phone_model";
        public static final String COLUMN_RESOLUTION = "Camera";
        public static final String COLUMN_FLASH = "Flash";
        public static final String COLUMN_APERTURE = "Aperture_size";
        public static final String COLUMN_FOCAL_LENGTH = "Focal_length_35mm_equivalent";
        public static final String COLUMN_SENSOR_SIZE = "Camera_sensor_size";
        public static final String COLUMN_PIXEL_SIZE = "Pixel_size";
        public static final String COLUMN_FEATURES = "Features";
        public static final String COLUMN_SETTINGS = "Settings";
        public static final String COLUMN_MODES = "Shooting_modes";
        public static final String COLUMN_CAMCORDER_RES = "Camcorder";
        public static final String COLUMN_CAMCORDER_FEATURES = "CC_Features";
        public static final String COLUMN_FRONT_FACING_RESOLUTION = "Front_facing_resolution";
        public static final String COLUMN_FRONT_FACING_FEATURES = "FFC_Features";

        // Uri to access the table and its contents
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAMERA).build();

        // Type to return when accessing this table
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_CAMERA;

        // Used to find the specific rows within the table using a given id
        public static Uri buildCameraUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ConnectivityEntry implements BaseColumns {
        public static final String TABLE_NAME = "connectivity";

        // Columns within the table
        public static final String COLUMN_PHONE_KEY = "phone_model";
        public static final String COLUMN_BLUETOOTH = "Bluetooth";
        public static final String COLUMN_WIFI = "Wi_Fi";
        public static final String COLUMN_HOTSPOT = "Mobile_hotspot";
        public static final String COLUMN_USB = "USB";
        public static final String COLUMN_CONNECTOR = "Connector";
        public static final String COLUMN_FEATURES = "Features";
        public static final String COLUMN_HDMI = "HDMI";
        public static final String COLUMN_OTHER = "Other";

        // Uri to access the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONNECTIVITY).build();

        // Type to return when accessing this table
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_CONNECTIVITY;

        // Used to find the specific rows within the table using a given id
        public static Uri buildConnectivityUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class DesignEntry implements BaseColumns {
        public static final String TABLE_NAME = "design";

        // Columns within the table
        public static final String COLUMN_PHONE_KEY = "phone_model";
        public static final String COLUMN_DEVICE_TYPE = "Device_type";
        public static final String COLUMN_OS = "OS";
        public static final String COLUMN_DIMENSIONS = "Dimensions";
        public static final String COLUMN_WEIGHT = "Weight";
        public static final String COLUMN_MATERIALS = "Materials";
        public static final String COLUMN_RUGGED = "Rugged";
        public static final String COLUMN_IP_CERT = "IP_certified";
        public static final String COLUMN_MIL_STD_810 = "MIL_STD_810_certified";
        public static final String COLUMN_PHONE_FUNCTION = "Phone_functionality";

        // Uri to access the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DESIGN).build();

        // Type to return when accessing this table
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_DESIGN;

        // Used to find the specific rows within the table using a given id
        public static Uri buildDesignUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class DisplayEntry implements BaseColumns {
        public static final String TABLE_NAME = "display";

        // Columns within the table
        public static final String COLUMN_PHONE_KEY = "phone_model";
        public static final String COLUMN_SIZE = "Physical_size";
        public static final String COLUMN_RESOLUTION = "Resolution";
        public static final String COLUMN_PIXEL_DENSITY = "Pixel_density";
        public static final String COLUMN_TECHNOLOGY = "Technology";
        public static final String COLUMN_SCREEN_RATIO = "Screen_to_body_ratio";
        public static final String COLUMN_COLORS = "Colors";
        public static final String COLUMN_TOUCHSCREEN = "Touchscreen";
        public static final String COLUMN_FEATURES = "Features";

        // Uri to access the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DISPLAY).build();

        // Type to return when accessing this table
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_DISPLAY;

        // Used to find the specific rows within the table using a given id
        public static Uri buildDisplayUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class HardwareEntry implements BaseColumns {
        public static final String TABLE_NAME = "hardware";

        // Columns within the table
        public static final String COLUMN_PHONE_KEY = "phone_model";
        public static final String COLUMN_SOC = "System_chip";
        public static final String COLUMN_PROCESSOR = "Processor";
        public static final String COLUMN_GPU = "Graphics_processor";
        public static final String COLUMN_RAM = "System_memory";
        public static final String COLUMN_STORAGE_MEMORY = "Built_in_storage";
        public static final String COLUMN_STORAGE_EXPANSION = "Storage_expansion";
        public static final String COLUMN_MAX_STORAGE = "Maximum_User_Storage";

        // Uri to access the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HARDWARE).build();

        // Type to return when accessing this table
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_HARDWARE;

        // Used to find the specific rows within the table using a given id
        public static Uri buildHardwareUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class InternetEntry implements BaseColumns {
        public static final String TABLE_NAME = "internet_browsing";

        // Columns within the table
        public static final String COLUMN_PHONE_KEY = "phone_model";
        public static final String COLUMN_BROWSER = "Browser";
        public static final String COLUMN_ONLINE_SERVICES = "Built_in_online_services_support";

        // Uri to access the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INTERNET).build();

        // Type to return when accessing this table
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_INTERNET;

        // Used to find the specific rows within the table using a given id
        public static Uri buildInternetUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class MultimediaEntry implements BaseColumns {
        public static final String TABLE_NAME = "multimedia";

        // Columns within the table
        public static final String COLUMN_PHONE_KEY = "phone_model";
        public static final String COLUMN_FILTER = "Filter_by";
        public static final String COLUMN_FEATURES = "Features";
        public static final String COLUMN_SPEAKERS = "Speakers";
        public static final String COLUMN_YOUTUBE = "YouTube_player";
        public static final String COLUMN_RADIO = "Radio";
        public static final String COLUMN_VIDEO_PLAYBACK = "Video_playback";
        public static final String COLUMN_VP_FEATURES = "VP_Features";

        // Uri to access the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MULTIMEDIA).build();

        // Type to return when accessing this table
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_MULTIMEDIA;

        // Used to find the specific rows within the table using a given id
        public static Uri buildMultimediaUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class FeaturesEntry implements BaseColumns {
        public static final String TABLE_NAME = "other_features";

        // Columns within the table
        public static final String COLUMN_PHONE_KEY = "phone_model";
        public static final String COLUMN_NOTIFICATIONS = "Notifications";
        public static final String COLUMN_MICROPHONES = "Additional_microphones";
        public static final String COLUMN_SENSORS = "Sensors";
        public static final String COLUMN_VOICE = "Voice";
        public static final String COLUMN_HEARING_AID = "Hearing_aid_compatibility";

        // Uri to access the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FEATURES).build();

        // Type to return when accessing this table
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_FEATURES;

        // Used to find the specific rows within the table using a given id
        public static Uri buildFeaturesUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TechnologyEntry implements BaseColumns {
        public static final String TABLE_NAME = "technology";

        // Columns within the table
        public static final String COLUMN_PHONE_KEY = "phone_model";
        public static final String COLUMN_GSM = "GSM";
        public static final String COLUMN_UMTS = "UMTS";
        public static final String COLUMN_FDD_LTE = "FDD_LTE";
        public static final String COLUMN_DATA = "Data";
        public static final String COLUMN_MICRO_SIM = "Micro_SIM";
        public static final String COLUMN_VOLTE = "VoLTE";
        public static final String COLUMN_POSITIONING = "Positioning";
        public static final String COLUMN_NAVIGATION = "Navigation";
        public static final String COLUMN_NANO_SIM = "nano_SIM";
        public static final String COLUMN_HD_VOICE = "HD_Voice";
        public static final String COLUMN_TDD_LTE = "TDD_LTE";
        public static final String COLUMN_CDMA = "CDMA";
        public static final String COLUMN_MULTI_SIM = "Multiple_SIM_cards";

        // Uri to access the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TECHNOLOGY).build();

        // Type to return when accessing this table
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_TECHNOLOGY;

        // Used to find the specific rows within the table using a given id
        public static Uri buildTechnologyUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
