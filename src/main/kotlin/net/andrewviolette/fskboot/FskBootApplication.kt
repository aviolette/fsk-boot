package net.andrewviolette.fskboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FskBootApplication

fun main(args: Array<String>) {
    runApplication<FskBootApplication>(*args)
}
