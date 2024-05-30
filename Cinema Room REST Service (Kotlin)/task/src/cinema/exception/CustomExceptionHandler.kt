package cinema.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(RuntimeException::class)
    fun runtimeExceptionHandler(e: RuntimeException, request: WebRequest): ResponseEntity<CustomErrorMessage> {
        val body = CustomErrorMessage(
            e.message?:""
        )
        return ResponseEntity<CustomErrorMessage>(body, HttpStatus.BAD_REQUEST)
    }
}