package net.andrewviolette.fskboot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(prefix = "aws.cloud")
class AwsPropertyConfiguration(val endpoint: URI?, val tableName: String) {
}