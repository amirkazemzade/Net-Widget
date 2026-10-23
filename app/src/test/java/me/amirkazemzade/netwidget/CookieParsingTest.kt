package me.amirkazemzade.netwidget

import me.amirkazemzade.netwidget.data.converters.toCookie
import org.junit.Assert.assertEquals
import org.junit.Test

class CookieParsingTest {

    @Test
    fun `parses sample cookie with capitalized attributes correctly`() {
        val raw = "session=6097ff2e-4ca8-424a-9285-f441eab9eeb5.sBjY5kYO39ZdMRV1UJtQ2RNqnCU; Expires=Wed, 20-Jan-2027 17:36:02 GMT; Path=/"
        val cookie = raw.toCookie()

        assertEquals("6097ff2e-4ca8-424a-9285-f441eab9eeb5.sBjY5kYO39ZdMRV1UJtQ2RNqnCU", cookie.session)
        assertEquals("/", cookie.path)
        assertEquals("Wed, 20-Jan-2027 17:36:02 GMT", cookie.expires)
    }

    @Test
    fun `parses cookie with lowercase attributes correctly`() {
        val raw = "session=xyz123; expires=Thu, 01-Jan-2026 00:00:00 GMT; path=/api"
        val cookie = raw.toCookie()

        assertEquals("xyz123", cookie.session)
        assertEquals("/api", cookie.path)
        assertEquals("Thu, 01-Jan-2026 00:00:00 GMT", cookie.expires)
    }

    @Test
    fun `parses cookie with valueless flags and missing expires`() {
        val raw = "session=session_token; Path=/; HttpOnly; Secure; SameSite=Lax"
        val cookie = raw.toCookie()

        assertEquals("session_token", cookie.session)
        assertEquals("/", cookie.path)
        assertEquals("", cookie.expires)
    }

    @Test
    fun `parses cookie with equal signs in value`() {
        val raw = "session=tokenWithPadding==; Path=/"
        val cookie = raw.toCookie()

        assertEquals("tokenWithPadding==", cookie.session)
        assertEquals("/", cookie.path)
    }
}
