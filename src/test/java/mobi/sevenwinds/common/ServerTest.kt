package mobi.sevenwinds.common

import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.restassured.RestAssured
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeAll
import java.util.concurrent.TimeUnit

open class ServerTest {
    companion object {
        private var serverStarted = false

        private lateinit var server: ApplicationEngine

        @Suppress("unused")
        @KtorExperimentalAPI
        @ExperimentalCoroutinesApi
        @BeforeAll
        @JvmStatic
        fun startServer() {
            if (!serverStarted) {
                server = embeddedServer(Netty, environment = applicationEngineEnvironment {
                    config = HoconApplicationConfig(ConfigFactory.load("test.conf"))

                    connector {
                        port = config.property("ktor.deployment.port").getString().toInt()
                        host = "127.0.0.1"
                    }
                })
                server.start()
                serverStarted = true

                RestAssured.baseURI = "http://localhost"
                RestAssured.port = server.environment.config.property("ktor.deployment.port").getString().toInt()
                RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

                Runtime.getRuntime().addShutdownHook(Thread { server.stop(0, 0, TimeUnit.SECONDS) })
            }
        }
    }
}