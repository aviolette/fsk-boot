package net.andrewviolette.fskboot.model

import java.util.UUID

data class Practitioner(val uuid: UUID?, val givenName: String, val familyName: String, val email: String)
