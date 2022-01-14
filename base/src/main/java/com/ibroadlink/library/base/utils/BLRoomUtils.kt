package com.ibroadlink.library.base.utils

import androidx.collection.ArrayMap
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.blankj.utilcode.util.Utils

/**
 * 便捷创建 room 实例的工具，通常来说只需要实例化一次即可，数据库也应该只有一个
 */
object BLRoomUtils {

    const val DATABASE_VERSION = 3

    private val mDBEntityMap = ArrayMap<String, RoomDatabase>()

    @Synchronized
    fun <T : RoomDatabase> getCoreDB(
        cls: Class<T>, dbName: String = "core.db",
        callback: RoomDatabase.Callback? = null,
        migrations: Array<Migration>? = null,
        allowMainThread: Boolean = false
    ): T {
        val name = cls.name
        var database: RoomDatabase? = mDBEntityMap[name]
        if (database == null) {
            val builder = Room.databaseBuilder(
                Utils.getApp(), cls,
                dbName
            )
            migrations?.forEach {
                builder.addMigrations(it)
            }
            if (callback != null) {
                builder.addCallback(callback)
            }
            if (allowMainThread) {
                builder.allowMainThreadQueries()
            }
            database = builder.build()
            mDBEntityMap[name] = database
        }
        @Suppress("UNCHECKED_CAST")
        return database as T
    }
}
