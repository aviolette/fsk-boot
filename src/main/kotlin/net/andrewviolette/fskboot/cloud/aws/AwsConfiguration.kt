package net.andrewviolette.fskboot.cloud.aws

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

@Configuration
class AwsConfiguration {

    @Bean
    fun provideDynamodbClient(): DynamoDbClient = DynamoDbClient.builder()
        .endpointOverride(URI("http://localhost:8000"))
        .build()
}
