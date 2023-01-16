package net.andrewviolette.fskboot.service

import net.andrewviolette.fskboot.exception.ObjectExistsException
import net.andrewviolette.fskboot.model.Practitioner
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.util.*

@Service
class PractitionerService(val dynamoDbClient: DynamoDbClient) {
    fun create(practitioner: Practitioner): Practitioner {
        val tableName = "FreudianSearch"
        val uuid = UUID.randomUUID()

        try {
            dynamoDbClient.transactWriteItems(
                TransactWriteItemsRequest.builder()
                    .transactItems(
                        TransactWriteItem.builder().put(
                            Put.builder()
                                .tableName(tableName)
                                .item(
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
                        ).build(), TransactWriteItem.builder().put(
                            Put.builder()
                                .tableName(tableName)
                                .item(
                                    mapOf(
                                        "pk" to AttributeValue.fromS("USEREMAIL#${practitioner.email}"),
                                        "sk" to AttributeValue.fromS("USEREMAIL#${practitioner.email}"),
                                        "user" to AttributeValue.fromS(uuid.toString())
                                    )
                                ).conditionExpression("attribute_not_exists(#pk)")
                                .expressionAttributeNames(mapOf("#pk" to "pk"))
                                .build()
                        ).build()
                    )
                    .build()
            )

        } catch (tc: TransactionCanceledException) {
            if (tc.hasCancellationReasons()) {
                if (tc.cancellationReasons()[1].code() == "ConditionalCheckFailed") {
                    throw ObjectExistsException("User", practitioner.email)
                }
            }
            throw tc
        }
        return practitioner.copy(
            uuid = uuid,
            givenName = practitioner.givenName,
            familyName = practitioner.familyName,
            email = practitioner.email
        )
    }
}
