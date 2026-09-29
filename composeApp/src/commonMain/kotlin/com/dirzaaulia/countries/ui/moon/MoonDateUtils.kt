package com.dirzaaulia.countries.ui.moon

internal fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

internal fun getDaysInMonths(year: Int): IntArray {
    val isLeap = isLeapYear(year)
    return intArrayOf(31, if (isLeap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
}

internal fun epochMillisToYearAndDay(epochMillis: Long): Triple<Int, Int, Int> {
    var days = epochMillis / 86400000L
    var year = 1970
    while (true) {
        val leap = isLeapYear(year)
        val yearDays = if (leap) 366 else 365
        if (days >= yearDays) {
            days -= yearDays
            year++
        } else {
            break
        }
    }
    val dayOfYear = days.toInt() + 1
    val daysInMonth = getDaysInMonths(year)
    var d = dayOfYear
    for (m in 0 until 12) {
        if (d <= daysInMonth[m]) {
            break
        }
        d -= daysInMonth[m]
    }
    val dayOfMonth = d
    return Triple(year, dayOfYear, dayOfMonth)
}

internal fun dayOfYearToEpochMillis(
    year: Int,
    dayOfYear: Int,
    hour: Int = 12,
): Long {
    var days = 0L
    for (y in 1970 until year) {
        val leap = isLeapYear(y)
        days += if (leap) 366 else 365
    }
    days += dayOfYear - 1
    return days * 86400000L + hour * 3600000L
}

internal fun formatDayAndMonth(
    year: Int,
    dayOfYear: Int,
): String {
    val monthNames =
        listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec",
        )
    val daysInMonth = getDaysInMonths(year)
    val totalDays = if (isLeapYear(year)) 366 else 365
    var d = dayOfYear.coerceIn(1, totalDays)
    for (m in 0 until 12) {
        if (d <= daysInMonth[m]) {
            return "$d ${monthNames[m]} $year"
        }
        d -= daysInMonth[m]
    }
    return "31 Dec $year"
}

internal fun formatIsoDate(
    year: Int,
    dayOfYear: Int,
): String {
    val daysInMonth = getDaysInMonths(year)
    var remainingDays = dayOfYear.coerceIn(1, daysInMonth.sum())
    for (monthIndex in daysInMonth.indices) {
        if (remainingDays <= daysInMonth[monthIndex]) {
            val mStr = (monthIndex + 1).toString().padStart(2, '0')
            val dStr = remainingDays.toString().padStart(2, '0')
            return "$year-$mStr-$dStr"
        }
        remainingDays -= daysInMonth[monthIndex]
    }
    return "$year-12-31"
}

internal fun getMonthFromDayOfYear(
    year: Int,
    dayOfYear: Int,
): Int {
    val daysInMonth = getDaysInMonths(year)
    var d = dayOfYear
    for (m in 0 until 12) {
        if (d <= daysInMonth[m]) {
            return m
        }
        d -= daysInMonth[m]
    }
    return 11
}

internal fun getFirstDayOfMonth(
    year: Int,
    monthIndex: Int,
): Int {
    val daysInMonth = getDaysInMonths(year)
    var day = 1
    for (m in 0 until monthIndex.coerceIn(0, 11)) {
        day += daysInMonth[m]
    }
    return day
}
