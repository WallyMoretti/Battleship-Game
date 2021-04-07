package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired // Permite inyectar unas dependencias con otras dentro.
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;


    // -- Metodos -- //
    @GetMapping("/games") // Lo mismo que @RequestMapping, pero solo a nivel método.
    public Map<String, Object> getGames(Authentication authentication) {

        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        if (!isGuest(authentication)) {
            dto.put("player", playerRepository.findByUserName(authentication.getName()).makePlayerDTO());
        } else {
            dto.put("player", "Guest");
        }
        dto.put("games", gameRepository.findAll().stream().map(game -> game.makeGameDTO()).collect(Collectors.toList()));

        return dto;
    }

    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {

        ResponseEntity<Map<String, Object>> response;
        Game game;
        Player player;
        GamePlayer gamePlayer;

        if (!isGuest(authentication)) {

            game = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"))));
            player = playerRepository.findByUserName(authentication.getName());
            gamePlayer = gamePlayerRepository.save(new GamePlayer(player, game));

            response = new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        } else {

            response = new ResponseEntity<>(makeMap("error", "player not authorized"), HttpStatus.UNAUTHORIZED);
        }

        return response;
    }

    @PostMapping("/game/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {

        ResponseEntity<Map<String, Object>> response;
        Optional<Game> game = gameRepository.findById(gameId);
        Player player;
        GamePlayer gamePlayer;

        if (isGuest(authentication)) {

            response = new ResponseEntity<>(makeMap("error", "player is unauthorized"), HttpStatus.UNAUTHORIZED);
        } else if (!game.isPresent()) {

            response = new ResponseEntity<>(makeMap("error", "there is no game"), HttpStatus.FORBIDDEN);
        } else if (game.get().getGamePlayers().size() == 2) {

            response = new ResponseEntity<>(makeMap("error", "game is full"), HttpStatus.FORBIDDEN);
        } else {

            player = playerRepository.findByUserName(authentication.getName());
            gamePlayer = gamePlayerRepository.save(new GamePlayer(player, game.get()));
            response = new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        }

        return response;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}
