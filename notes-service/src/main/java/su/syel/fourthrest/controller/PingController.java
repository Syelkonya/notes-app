package su.syel.fourthrest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import su.syel.fourthrest.dto.response.PingResponseDTO;

import java.time.LocalDateTime;

@RestController
public class PingController {

    @GetMapping("/ping")
    public ResponseEntity<PingResponseDTO> index() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new PingResponseDTO(HttpStatus.OK.value(), LocalDateTime.now()));
    }

}
