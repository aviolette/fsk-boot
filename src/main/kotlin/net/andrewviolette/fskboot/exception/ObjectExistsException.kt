package net.andrewviolette.fskboot.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT)
class ObjectExistsException(type: String, value: String) : RuntimeException("${type} already exists with key ${value}")