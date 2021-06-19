package com.leeloo.vkupload.data.local

object VideoContract {

    object VideoEntry {
        const val TABLE_NAME = "video"
        const val COLUMN_NAME_ID = "_id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_URI = "uri"
        const val COLUMN_NAME_TOTAL_SIZE = "total_size"
        const val COLUMN_NAME_TRANSFERRED_SIZE = "transferred_size"
        const val COLUMN_NAME_SESSION_UUID = "session_uuid"
    }

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${VideoEntry.TABLE_NAME} (" +
                "${VideoEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "${VideoEntry.COLUMN_NAME_TITLE} TEXT," +
                "${VideoEntry.COLUMN_NAME_URI} TEXT," +
                "${VideoEntry.COLUMN_NAME_TOTAL_SIZE} INTEGER," +
                "${VideoEntry.COLUMN_NAME_TRANSFERRED_SIZE} INTEGER," +
                "${VideoEntry.COLUMN_NAME_SESSION_UUID} TEXT)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${VideoEntry.TABLE_NAME}"

}