package com.example.sdk

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.provider.ContactsContract
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DataExtractorTest {

    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dataExtractor: DataExtractor

    @Before
    fun setup() {
        context = mockk()
        contentResolver = mockk()
        //sharedPreferences = mockk()
        sharedPreferences = mockk(relaxed = true)

        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        dataExtractor = DataExtractor(context)
    }

    @Test
    fun `getDeviceData returns cached data`() {
        // Given
        val deviceData = mapOf("Device Model" to "Pixel 4", "OS Version" to "11",
            "Manufacturer" to "Google", "Screen Resolution" to "1080 x 1920")

        every { sharedPreferences.getString("Device Model", null) } returns "Pixel 4"
        every { sharedPreferences.getString("OS Version", null) } returns "11"
        every { sharedPreferences.getString("Manufacturer", null) } returns "Google"
        every { sharedPreferences.getString("Screen Resolution", null) } returns "1080 x 1920"

        // When
        val result = dataExtractor.getDeviceData()

        // Then
        assertEquals(deviceData, result)
    }

    @Test
    fun `countContactsWithMultiplePhoneNumbers returns correct count`() {
        // Given
        val contacts = listOf(
            mapOf("name" to "Alice", "phoneNumbers" to listOf("123", "456")),
            mapOf("name" to "Bob", "phoneNumbers" to listOf("789"))
        )

        // When
        val result = dataExtractor.countContactsWithMultiplePhoneNumbers(contacts)

        // Then
        assertEquals(1, result)
    }
}
