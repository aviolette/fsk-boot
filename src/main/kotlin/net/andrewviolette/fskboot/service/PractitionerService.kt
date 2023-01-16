package net.andrewviolette.fskboot.service

import net.andrewviolette.fskboot.model.Practitioner
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.util.*

@Service
class PractitionerService(val dynamoDbClient: DynamoDbClient) {
    fun createPractitioner(practitioner: Practitioner): Practitioner {
        val query = dynamoDbClient.query(
            QueryRequest.builder().tableName("FreudianSearch").indexName("GSI1")
                .keyConditionExpression("#gsi1pk = :email")
                .expressionAttributeNames(mapOf("#gsi1pk" to "gsi1pk"))
                .expressionAttributeValues(mapOf(":email" to AttributeValue.fromS(practitioner.email))).build()
        )
        if (query.items().size > 0) {
            throw RuntimeException("Email exists")
        }
        val uuid = UUID.randomUUID()
        dynamoDbClient.putItem(
            PutItemRequest.builder().tableName("FreudianSearch").item(
                mapOf(
                    "pk" to AttributeValue.fromS(uuid.toString()),
                    "sk" to AttributeValue.fromS(uuid.toString()),
                    "gsi1pk" to AttributeValue.fromS(practitioner.email),
                    "gsi1sk" to AttributeValue.fromS(practitioner.email),
                    "email" to AttributeValue.fromS(practitioner.email),
                    "givenName" to AttributeValue.fromS(practitioner.givenName),
                    "familyName" to AttributeValue.fromS(practitioner.familyName)
                )
            ).conditionExpression("attribute_not_exists(#pk)")
                .expressionAttributeNames(mapOf("#pk" to "pk"))
                .build()
        )
        return practitioner
    }
}
