import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


@WireMockTest(proxyMode = true)
class JUnitJupiterExtensionJvmProxyDeclarativeTest {
    var client: CloseableHttpClient? = null

    @BeforeEach
    fun init() {
        client = HttpClientBuilder.create()
            .useSystemProperties() // This must be enabled for auto proxy config
            .build()
    }


    @Test
    @Throws(Exception::class)
    fun configures_jvm_proxy_and_enables_browser_proxying() {
        stubFor(
            get("/things")
                .withHost(equalTo("one.my.domain"))
                .willReturn(ok("1"))
        )
        stubFor(
            get("/things")
                .withHost(equalTo("two.my.domain"))
                .willReturn(ok("2"))
        )
        assertEquals("1", getContent("http://one.my.domain/things"))
        assertEquals("2", getContent("http://two.my.domain/things"))
    }

    @Throws(Exception::class)
    private fun getContent(url: String): String {
        client!!.execute(HttpGet(url)).use { response ->
            return EntityUtils.toString(
                response.entity
            )
        }
    }
}