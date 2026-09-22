package com.dirzaaulia.countries

import kotlin.js.JsNumber

@JsFun("() => Date.now()")
private external fun jsDateNow(): Double

actual fun currentEpochMillis(): Long = jsDateNow().toLong()
