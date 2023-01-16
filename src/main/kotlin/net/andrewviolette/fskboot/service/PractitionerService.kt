package net.andrewviolette.fskboot.service

import net.andrewviolette.fskboot.config.AwsPropertyConfiguration
import net.andrewviolette.fskboot.exception.ObjectExistsException
import net.andrewviolette.fskboot.exception.ObjectNotFoundException
import net.andrewviolette.fskboot.model.Practitioner
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException.NotFound
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.util.*

@Service
class PractitionerService(val dynamoDbClient: DynamoDbClient, val config: AwsPropertyConfiguration) {
    fun create(practitioner: Practitioner): Practitioner {
        val uuid = UUID.randomUUID()
        val keyValue = AttributeValue.fromS("PRACTITIONER#${uuid}")
        try {
            dynamoDbClient.transactWriteItems(
                TransactWriteItemsRequest.builder()
                    .transactItems(
                        TransactWriteItem.builder().put(
                            Put.builder()
                                .tableName(config.tableName)
                                .item(
                                    mapOf(
                                        "pk" to keyValue,
                                        "sk" to keyValue,
                                        "uuid" to AttributeValue.fromS(uuid.toString()),
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
                                .tableName(config.tableName)
                                .item(
                                    mapOf(
                                        "pk" to AttributeValue.fromS("USEREMAIL#${practitioner.email}"),
                                        "sk" to AttributeValue.fromS("USEREMAIL#${practitioner.email}"),
                                        "user" to keyValue
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

    fun findById(practitionerId: UUID): Practitioner? {
        val keyValue = AttributeValue.fromS("PRACTITIONER#${practitionerId}")

        val resp = dynamoDbClient.getItem(
            GetItemRequest.builder()
                .tableName(config.tableName)
                .key(mapOf("pk" to keyValue, "sk" to keyValue))
                .build()
        )
        if (!resp.hasItem()) {
            throw ObjectNotFoundException(practitionerId.toString())
        }
        return convert(resp.item())
    }

    private fun convert(item: Map<String, AttributeValue>): Practitioner = Practitioner(
        UUID.fromString(item.get("uuid")!!.s()),
        givenName = item.get("givenName")!!.s(),
        familyName = item.get("familyName")!!.s(),
        email = item.get("email")!!.s()
    )
}
