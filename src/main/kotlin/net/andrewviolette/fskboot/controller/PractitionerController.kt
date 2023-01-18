package net.andrewviolette.fskboot.controller

import net.andrewviolette.fskboot.model.Practitioner
import net.andrewviolette.fskboot.service.PractitionerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/practitioners")
class PractitionerController(val practitionerService: PractitionerService) {

    @GetMapping("/{practitionerId}")
    fun findPractitioner(@PathVariable practitionerId: UUID): Practitioner? =
        practitionerService.findById(practitionerId)

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    fun createPractitioner(@RequestBody practitioner: Practitioner): Practitioner =
        practitionerService.create(practitioner)

    @DeleteMapping(value = ["/{practitionerId}"])
    fun deletePractitioner(@PathVariable practitionerId: UUID) {
        practitionerService.delete(practitionerId)
    }
}
