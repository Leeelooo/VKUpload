package com.leeloo.vkupload.data.local

object VideoContract {

    object VideoEntry {
        const val TABLE_NAME = "video"
        const val COLUMN_NAME_ID = "_id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_URI = "uri"
        const val COLUMN_NAME_TOTAL_SIZE = "total_size"
        const val COLUMN_NAME_TRANSFERRED_SIZE = "transferred_size"
        const val COLUMN_NAME_STATUS = "status"

        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_NAME_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "$COLUMN_NAME_TITLE TEXT," +
                    "$COLUMN_NAME_URI TEXT," +
                    "$COLUMN_NAME_TOTAL_SIZE INTEGER," +
                    "$COLUMN_NAME_TRANSFERRED_SIZE INTEGER," +
                    "$COLUMN_NAME_STATUS INTEGER)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    object VideoUploadEntry {
        const val TABLE_NAME = "video_upload"
        const val COLUMN_NAME_ID = "_id"
        const val COLUMN_NAME_ACCESS_KEY = "access_key"
        const val COLUMN_NAME_UPLOAD_URL = "upload_url"
        const val COLUMN_NAME_SESSION_UUID = "session_uuid"
        const val COLUMN_NAME_REMOTE_VIDEO_ID = "remote_video_id"
        const val COLUMN_NAME_LOCAL_VIDEO_ID = "local_video_id"

        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_NAME_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "$COLUMN_NAME_ACCESS_KEY TEXT," +
                    "$COLUMN_NAME_UPLOAD_URL TEXT," +
                    "$COLUMN_NAME_SESSION_UUID TEXT," +
                    "$COLUMN_NAME_REMOTE_VIDEO_ID INTEGER," +
                    "$COLUMN_NAME_LOCAL_VIDEO_ID INTEGER)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

}