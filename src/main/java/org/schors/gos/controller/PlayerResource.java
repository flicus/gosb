package org.schors.gos.controller;

import lombok.RequiredArgsConstructor;
import org.schors.gos.model.Player;
import org.schors.gos.service.PlayerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerResource {

    private final PlayerService playerService;

    @GetMapping(path = "/{playerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Player> getPlayerById(@PathVariable String playerId){
        return Mono.just(playerService.getPlayer(playerId));
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Player> getAllPlayers() {
        return Flux.fromIterable(playerService.listAllPlayers());
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Player> createPlayer(@RequestBody Mono<Player> playerRequest) {
        return playerRequest
            .flatMap(player -> Mono.just(playerService.createPlayer(player)))
            .switchIfEmpty(Mono.error(new RuntimeException("unable to create")));
    }

    @PutMapping(path = "/{playerId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Player> updatePlayer(@PathVariable String playerId, @RequestBody Mono<Player> playerRequest) {
        return playerRequest
            .flatMap(player -> Mono.just(playerService.updatePlayer(playerId, player)))
            .switchIfEmpty(Mono.error(new RuntimeException("unable to update")));
    }

    @DeleteMapping(path = "/{playerId}")
    public Mono<Boolean> deletePlayer(@PathVariable String playerId) {
        return Mono.just(playerService.deletePlayer(playerId));
    }
}
