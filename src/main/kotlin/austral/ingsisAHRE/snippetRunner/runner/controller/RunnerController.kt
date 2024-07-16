package austral.ingsisAHRE.snippetRunner.runner.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/runner")
@Validated
class RunnerController {
    @GetMapping
    fun index(): String {
        return "I'm Alive!"
    }
}
