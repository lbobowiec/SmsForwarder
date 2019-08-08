package com.lubosoft.smsforwarder.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PhoneNumberData::class, TimeOffRule::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun phoneNumberDao(): PhoneNumberDao

    abstract fun timeOffRuleDao(): TimeOffRuleDao

    companion object {
        private const val DATABASE_NAME = "phone_number_data.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                // TODO .fallbackToDestructiveMigration()
                .build()

    }

}