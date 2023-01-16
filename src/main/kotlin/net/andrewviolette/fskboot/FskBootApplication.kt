package net.andrewviolette.fskboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("net.andrewviolette.fskboot.config")
class FskBootApplication

fun main(args: Array<String>) {
    runApplication<FskBootApplication>(*args)
}
