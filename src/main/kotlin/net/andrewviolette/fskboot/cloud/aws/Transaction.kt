package net.andrewviolette.fskboot.cloud.aws

sealed class Transaction {
    class Create(val items: Map<String, Any>) : Transaction()
    class Delete(val pk: String, val sk: String): Transaction()
}
