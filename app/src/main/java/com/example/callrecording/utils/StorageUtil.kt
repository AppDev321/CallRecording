package com.example.callrecording.utils
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream

class StorageUtil(private val context: Context) {

    private val directory = Environment.DIRECTORY_DOWNLOADS +"/AlphaCall/"



    @RequiresApi(Build.VERSION_CODES.Q)
    fun writeFileToExternalStorage(filename: String, fileContents: ByteArray): Uri? {
        val contentResolver = context.contentResolver
        // Define the file metadata
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, "audio/mpeg") // Replace with the appropriate MIME type for your file
            put(MediaStore.Downloads.RELATIVE_PATH, directory)
        }
        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        val outputStream: OutputStream? = uri?.let { contentResolver.openOutputStream(it) }
        outputStream?.write(fileContents)
        outputStream?.close()
        return uri
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun readFileFromExternalStorage(filename: String): ByteArray? {
        val contentResolver = context.contentResolver

        val queryUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME)
        val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(filename)
        val sortOrder = null as String?
        val query: Cursor? = contentResolver.query(queryUri, projection, selection, selectionArgs, sortOrder)


        if (query != null && query.moveToFirst()) {
            val fileId = query.getLong(query.getColumnIndexOrThrow(MediaStore.Downloads._ID))
            val downloadUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, fileId)
            val inputStream = contentResolver.openInputStream(downloadUri)

            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            val outputStream = ByteArrayOutputStream()
            var bytesRead = inputStream!!.read(buffer, 0, bufferSize)
            while (bytesRead != -1) {
                outputStream.write(buffer, 0, bytesRead)
                bytesRead = inputStream.read(buffer, 0, bufferSize)
            }
            val fileContents = outputStream.toByteArray()


            inputStream.close()
            return fileContents
        } else {

            return null
        }
    }
}
