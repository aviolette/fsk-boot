package net.andrewviolette.fskboot.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code=HttpStatus.NOT_FOUND)
class ObjectNotFoundException(name:String) : RuntimeException("Object $name not found")