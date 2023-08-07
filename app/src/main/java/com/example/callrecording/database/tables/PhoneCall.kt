package com.example.callrecording.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(tableName = "phone_call")
data class PhoneCall(
    @PrimaryKey val id: Long?=0,

    @ColumnInfo(name = "caller") var callerNumber: String?="",
    @ColumnInfo(name = "Number") var number: String?="",
    @ColumnInfo(name = "Type") var type: String?="",
    @ColumnInfo(name = "Date") var date: String?="",
    @ColumnInfo(name = "Time") var time: String?="",
    @ColumnInfo(name = "Duration") var duration: String?="",
    @ColumnInfo(name = "Link") var driveLink: String?="",


    @ColumnInfo(name = "filePath") var filePath: String?="",
    @ColumnInfo(name = "uploadStatus") var upload: Boolean?=false,
    @ColumnInfo(name = "fileSize") var fileSize: Long?=0,
)
