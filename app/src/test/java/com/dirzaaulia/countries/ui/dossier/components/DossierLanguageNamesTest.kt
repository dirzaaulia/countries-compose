package com.dirzaaulia.countries.ui.dossier.components

import org.junit.Assert.assertEquals
import org.junit.Test

class DossierLanguageNamesTest {
    @Test
    fun knownLanguageCodesAreReadable() {
        assertEquals("Arabic", dossierLanguageName("ara"))
        assertEquals("Finnish", dossierLanguageName("FIN"))
        assertEquals("French", dossierLanguageName("fra"))
        assertEquals("Portuguese", dossierLanguageName("pt-BR"))
    }

    @Test
    fun unknownCodesRemainIdentifiable() {
        assertEquals("XYZ", dossierLanguageName("xyz"))
    }

    @Test
    fun onlyHttpsLinksWithHostsAreAccepted() {
        assertEquals(true, isSafeDossierUrl("https://en.wikipedia.org/wiki/Indonesia"))
        assertEquals(false, isSafeDossierUrl("http://example.com"))
        assertEquals(false, isSafeDossierUrl("https://user@host.com"))
        assertEquals(false, isSafeDossierUrl("https://"))
    }
}
