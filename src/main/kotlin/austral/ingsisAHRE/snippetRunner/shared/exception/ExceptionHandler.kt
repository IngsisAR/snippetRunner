package austral.ingsisAHRE.snippetRunner.shared.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(HttpException::class)
    fun handleHttpException(ex: HttpException): ResponseEntity<Map<String, Any>> {
        val errorDetails: Map<String, Any> =
            mapOf(
                "timestamp" to System.currentTimeMillis(),
                "status" to ex.status.value(),
                "error" to ex.status.reasonPhrase,
                "message" to ex.message.orEmpty(),
            )
        return ResponseEntity(errorDetails, ex.status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = mutableMapOf<String, String>()
        for (error in ex.bindingResult.allErrors) {
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Invalid value"
            errors[fieldName] = errorMessage
        }
        val errorDetails =
            mapOf(
                "timestamp" to System.currentTimeMillis(),
                "status" to HttpStatus.BAD_REQUEST.value(),
                "error" to "Bad Request",
                "message" to "Validation failed",
                "errors" to errors,
            )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }
}
