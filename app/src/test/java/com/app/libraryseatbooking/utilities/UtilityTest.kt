package com.app.libraryseatbooking.utilities

import junit.framework.TestCase
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class UtilityTest {

    @Test
    fun getMinBetweenDates_shouldReturn2Min() {
        val startTime = 1628349527000L
        val endTime = 1628349661000L
        val min = Utility.getMinBetweenDates(endTime = endTime, startTime = startTime)
        Assert.assertEquals(2, min)
    }

    @Test
    fun `getFormattedTime_shouldReturn 07 Aug 2021, 10_37 PM`() {
        val pattern = Config.TIME_FORMAT
        val timeStamp = 1628356062388
        val expectedDate = "07 Aug 2021, 10:37 PM"
        val actualDate = Utility.getFormattedTime(pattern, timeStamp)
        Assert.assertEquals(expectedDate, actualDate)
    }
}