package net.andrewviolette.fskboot.service

import net.andrewviolette.fskboot.cloud.aws.SingleTable
import net.andrewviolette.fskboot.cloud.aws.Transaction
import net.andrewviolette.fskboot.exception.ObjectExistsException
import net.andrewviolette.fskboot.model.Practitioner
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException
import java.util.UUID

@Service
class PractitionerService(
    val singleTable: SingleTable
) {
    fun create(practitioner: Practitioner): Practitioner {
        val uuid = UUID.randomUUID()
        val keyValue = "PRACTITIONER#${uuid}"
        val userEmailKey = "USEREMAIL#${practitioner.email}"
        try {
            singleTable.transcatWrite(
                Transaction.Create(
                    mapOf(
                        "pk" to keyValue,
                        "sk" to keyValue,
                        "uuid" to uuid.toString(),
                        "gsi1pk" to userEmailKey,
                        "gsi1sk" to userEmailKey,
                        "email" to practitioner.email,
                        "givenName" to practitioner.givenName,
                        "familyName" to practitioner.familyName
                    )
                ),
                Transaction.Create(
                    mapOf(
                        "pk" to userEmailKey,
                        "sk" to userEmailKey,
                        "user" to keyValue
                    )
                )
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

    fun delete(practitionerId: UUID) {
        val key = "PRACTITIONER#${practitionerId}"
        val practitioner = findById(practitionerId)
        val emailKey = "USEREMAIL#${practitioner.email}"
        singleTable.transcatWrite(
            Transaction.Delete(key, key),
            Transaction.Delete(emailKey, emailKey)
        )
    }

    fun findById(practitionerId: UUID): Practitioner {
        return convert(singleTable.getById("PRACTITIONER", practitionerId))
    }

    private fun convert(item: Map<String, AttributeValue>): Practitioner = Practitioner(
        UUID.fromString(item.get("uuid")!!.s()),
        givenName = item.get("givenName")!!.s(),
        familyName = item.get("familyName")!!.s(),
        email = item.get("email")!!.s()
    )
}
