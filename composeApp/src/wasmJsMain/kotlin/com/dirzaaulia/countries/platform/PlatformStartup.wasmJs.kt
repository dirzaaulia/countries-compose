package com.dirzaaulia.countries.platform

import kotlinx.browser.document
import org.w3c.dom.HTMLElement

actual fun markStartupReady() {
    (document.getElementById("StartupSplash") as? HTMLElement)?.classList?.add("is-hidden")
}
