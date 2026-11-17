package seg3x02.tempconverterapi

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConverterSecurityTests {

    @Autowired
    private lateinit var client: TestRestTemplate

    @ParameterizedTest
    @ValueSource(strings = ["celsius-fahrenheit/50", "fahrenheit-celsius/122"])
    fun `requests without credentials are rejected`(route: String) {
        val response = client.getForEntity("/temperature-converter/$route", String::class.java)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertTrue(response.headers.getFirst("WWW-Authenticate")?.startsWith("Basic ") == true)
    }

    @ParameterizedTest
    @CsvSource(
        "user1, wrong, celsius-fahrenheit/50",
        "user1, wrong, fahrenheit-celsius/122",
        "user2, wrong, celsius-fahrenheit/50",
        "user2, wrong, fahrenheit-celsius/122",
        "unknown, pass1, celsius-fahrenheit/50",
        "unknown, pass1, fahrenheit-celsius/122"
    )
    fun `incorrect credentials are rejected`(username: String, password: String, route: String) {
        val response = client.withBasicAuth(username, password)
            .getForEntity("/temperature-converter/$route", String::class.java)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertTrue(response.headers.getFirst("WWW-Authenticate")?.startsWith("Basic ") == true)
    }

    @ParameterizedTest
    @CsvSource(
        "user1, pass1, celsius-fahrenheit/50, 122.0",
        "user1, pass1, celsius-fahrenheit/0, 32.0",
        "user1, pass1, celsius-fahrenheit/-40, -40.0",
        "user1, pass1, fahrenheit-celsius/122, 50.0",
        "user1, pass1, fahrenheit-celsius/32, 0.0",
        "user1, pass1, fahrenheit-celsius/98.6, 37.0",
        "user2, pass2, celsius-fahrenheit/50, 122.0",
        "user2, pass2, celsius-fahrenheit/0, 32.0",
        "user2, pass2, celsius-fahrenheit/-40, -40.0",
        "user2, pass2, fahrenheit-celsius/122, 50.0",
        "user2, pass2, fahrenheit-celsius/32, 0.0",
        "user2, pass2, fahrenheit-celsius/98.6, 37.0"
    )
    fun `both accounts can convert temperatures`(
        username: String,
        password: String,
        route: String,
        expected: Double
    ) {
        val response = client.withBasicAuth(username, password)
            .getForEntity("/temperature-converter/$route", String::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected, response.body!!.toDouble(), 0.000001)
    }
}
