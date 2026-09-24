package com.dirzaaulia.countries

/**
 * Multiplatform system clock provider.
 */
expect fun currentEpochMillis(): Long
expect fun formatLocalTime(epochMillis: Long): String
