package com.example.sdk

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataExtractor(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("data_cache", Context.MODE_PRIVATE)
    private var cachedDeviceData: Map<String, String>? = null

    fun getDeviceData(): Map<String, String> {
        if (cachedDeviceData == null) {
            cachedDeviceData = loadDeviceDataFromCache()
            if (cachedDeviceData == null) {
                cachedDeviceData = queryDeviceData()
                saveDeviceDataToCache(cachedDeviceData!!)
            }
        }
        return cachedDeviceData!!
    }

    private fun queryDeviceData(): Map<String, String> {
        return mapOf(
            "Device Model" to Build.MODEL,
            "OS Version" to Build.VERSION.RELEASE,
            "Manufacturer" to Build.MANUFACTURER,
            "Screen Resolution" to getScreenResolution()
        )
    }

    private fun saveDeviceDataToCache(deviceData: Map<String, String>) {
        val editor = sharedPreferences.edit()
        deviceData.forEach { (key, value) ->
            editor.putString(key, value)
        }
        editor.apply()
    }

    private fun loadDeviceDataFromCache(): Map<String, String>? {
        val deviceData = mutableMapOf<String, String>()
        val keys = listOf("Device Model", "OS Version", "Manufacturer", "Screen Resolution")
        keys.forEach { key ->
            sharedPreferences.getString(key, null)?.let { value ->
                deviceData[key] = value
            }
        }
        return if (deviceData.isNotEmpty()) deviceData else null
    }

    suspend fun getContactsAsync(contentResolver: ContentResolver): List<Map<String, Any>> = withContext(
        Dispatchers.IO) {
        val contacts = mutableListOf<Map<String, Any>>()
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val hasPhoneNumberIndex = it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)

            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex)
                val hasPhoneNumber = it.getInt(hasPhoneNumberIndex) > 0

                if (hasPhoneNumber) {
                    val phoneNumbers = getPhoneNumbers(contentResolver, id)
                    contacts.add(mapOf("name" to name, "phoneNumbers" to phoneNumbers))
                }
            }
        }
        contacts
    }

    private fun getPhoneNumbers(contentResolver: ContentResolver, contactId: String): List<String> {
        val phoneNumbers = mutableListOf<String>()
        val phoneCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )

        phoneCursor?.use {
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                phoneNumbers.add(it.getString(numberIndex))
            }
        }
        return phoneNumbers
    }

    fun countContactsWithMultiplePhoneNumbers(contacts: List<Map<String, Any>>): Int {
        return contacts.count { it["phoneNumbers"] is List<*> && (it["phoneNumbers"] as List<*>).size > 1 }
    }

    fun analyzeImageResolutionDistribution(mediaMetadata: List<Map<String, String>>): Map<String, Int> {
        val resolutionDistribution = mutableMapOf<String, Int>()
        mediaMetadata.filter { it["Duration"] == "N/A" }
            .forEach { metadata ->
                val resolution = metadata["Dimensions"] ?: "Unknown"
                resolutionDistribution[resolution] = resolutionDistribution.getOrDefault(resolution, 0) + 1
            }
        return resolutionDistribution.toList().sortedByDescending { it.second }.toMap()
    }

    // Extract images and videos metadata
    fun getMediaMetadata(): List<Map<String, String>> {
        val mediaMetadata = mutableListOf<Map<String, String>>()
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.Video.VideoColumns.DURATION
        )

        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection, null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                val date = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED))
                val size = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                val width = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH))
                val height = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT))
                val duration = it.getString(it.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION))

                mediaMetadata.add(
                    mapOf(
                        "File Name" to name,
                        "Date Created" to date,
                        "File Size" to size,
                        "Dimensions" to "$width x $height",
                        "Duration" to (duration ?: "N/A")
                    )
                )
            }
        }
        return mediaMetadata
    }

    private fun getScreenResolution(): String {
        val metrics = context.resources.displayMetrics
        return "${metrics.widthPixels} x ${metrics.heightPixels}"
    }
}
