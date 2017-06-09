package org.elasticsoftwarefoundation.elasticactors.container.healthcheck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Joost van de Wijgerd
 */
@RestController
public final class HealthCheckController {
    @GetMapping(path = "/healthcheck")
    public Mono<String> healthCheck() {
        return Mono.empty();
    }
}
