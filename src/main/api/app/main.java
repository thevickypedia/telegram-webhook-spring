package main.api.app;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class main {
    @GetMapping("/health")
    public Object health() {
        return ResponseEntity.ok().body("Healthy");
    }
}
