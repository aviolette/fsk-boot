package net.andrewviolette.fskboot.service

import net.andrewviolette.fskboot.model.Practitioner
import org.springframework.stereotype.Service
import org.w3c.dom.Attr
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.util.UUID

@Service
class PractitionerService(val dynamoDbClient: DynamoDbClient) {
    fun createPractitioner(practitioner: Practitioner): Practitioner {
        val uuid = UUID.randomUUID()
        dynamoDbClient.putItem(
            PutItemRequest.builder().tableName("FreudianSearchDev").item(
                mapOf(
                    "pk" to AttributeValue.fromS(uuid.toString()),
                    "sk" to AttributeValue.fromS(uuid.toString()),
                    "gsi1pk" to AttributeValue.fromS(practitioner.email),
                    "gsi1sk" to AttributeValue.fromS(practitioner.email),
                    "email" to AttributeValue.fromS(practitioner.email),
                    "givenName" to AttributeValue.fromS(practitioner.givenName),
                    "familyName" to AttributeValue.fromS(practitioner.familyName)
                )
            ).conditionExpression("attribute_not_exists(#gsi1pk)")
                .expressionAttributeNames(mapOf("#gsi1pk" to "gsi1pk"))
                .build()
        )
        return practitioner
    }
}
