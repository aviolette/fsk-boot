package net.andrewviolette.fskboot.cloud.aws

import net.andrewviolette.fskboot.config.AwsPropertyConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

@Configuration
class AwsConfiguration {

    @Bean
    fun provideDynamodbClient(config: AwsPropertyConfiguration): DynamoDbClient = DynamoDbClient.builder()
        .endpointOverride(URI(config.endpoint))
        .build()
}
