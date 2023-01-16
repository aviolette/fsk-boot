package net.andrewviolette.fskboot.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws.cloud")
class AwsPropertyConfiguration(val endpoint: String) {
}