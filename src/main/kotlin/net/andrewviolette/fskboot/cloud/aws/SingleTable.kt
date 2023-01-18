package net.andrewviolette.fskboot.cloud.aws

import net.andrewviolette.fskboot.config.AwsPropertyConfiguration
import net.andrewviolette.fskboot.exception.ObjectNotFoundException
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

@Component
class SingleTable(private val config: AwsPropertyConfiguration, val dynamoDbClient: DynamoDbClient) {

    fun getById(type: String, key: Any): Map<String, AttributeValue> {
        val resp = dynamoDbClient.getItem(
            getItemRequest("PRACTITIONER", key.toString())
        )
        if (!resp.hasItem()) {
            throw ObjectNotFoundException(key.toString())
        }
        return resp.item()
    }

    private fun getItemRequest(type: String, key: String): GetItemRequest =
        AttributeValue.fromS("$type#$key").let {
            GetItemRequest.builder()
                .tableName(config.tableName)
                .key(mapOf("pk" to it, "sk" to it))
                .build()
        }

    fun toAttributeValues(entry: Map.Entry<String, Any>): AttributeValue =
        when (entry.value) {
            is AttributeValue -> entry.value as AttributeValue
            is String -> AttributeValue.fromS(entry.value as String)
            else -> throw RuntimeException("Type not supported")
        }


    fun transcatWrite(vararg transactions: Transaction) {
        dynamoDbClient.transactWriteItems(
            TransactWriteItemsRequest.builder().transactItems(transactions.map { transaction ->
                if (transaction is Transaction.Create) {
                    TransactWriteItem.builder().put(
                        Put.builder()
                            .tableName(config.tableName)
                            .item(
                                transaction.items.mapValues { toAttributeValues(it) }
                            ).conditionExpression("attribute_not_exists(#pk)")
                            .expressionAttributeNames(mapOf("#pk" to "pk"))
                            .build()
                    ).build()
                } else {
                    null
                }
            }).build()
        )
    }
}