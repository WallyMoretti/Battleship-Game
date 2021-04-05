package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController //  Controlador para construir una API Rest que reciba y envíe peticiones requeridas.
@RequestMapping("/api") // Se encarga de relacionar una clase o método con una petición http.
public class SalvoController {

    @Autowired // Permite inyectar unas dependencias con otras dentro.
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // -- games -- //
    @GetMapping("/games") // Lo mismo que @RequestMapping, pero solo a nivel método.
    public Map<String, Object> getGames(Authentication authentication) {

        Map<String, Object> aux = new LinkedHashMap<String, Object>();
        aux.put("games", gameRepository.findAll().stream().map(game -> game.makeGameDTO()).collect(Collectors.toList()));
        aux.put("player", getMap(authentication));

        return aux;
    }

    private Map<String, Object> getMap(Authentication authentication) {
        if (!isGuest(authentication)) {
            return playerRepository.findByUserName(authentication.getName()).makePlayerDTO();
        } else {

            return null;
        }
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    // -- gamePlayers -- //
    @GetMapping("/gamePlayers") // Lo mismo que @RequestMapping, pero solo a nivel método.
    public List<Object> getGamePlayers() {
        return gamePlayerRepository.findAll().stream().map(gamePlayer -> gamePlayer.makeGamePlayerDTO()).collect(Collectors.toList());
    }


    // -- players -- //
    @GetMapping("/players")
    public List<Object> getPlayers() {
        return playerRepository.findAll().stream().map(player -> player.makePlayerDTO()).collect(Collectors.toList());
    }

    @PostMapping("/players")
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String username, @RequestParam String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByUserName(username) != null) {
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(username, passwordEncoder.encode("password")));

        return new ResponseEntity<>(makeMap("message", "success, player created"), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }


    // -- game_view -- //
    @GetMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> findGamePlayer(@PathVariable Long gamePlayerId) {

        Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);
        ResponseEntity<Map<String, Object>> response;

        if (gp.isPresent()) { // Si no es null, realizo un llamado al metodo "getMapDTOs" que devuelve un mapa con los DTO.

            response = new ResponseEntity<>(getMapDTOs(gamePlayerId), HttpStatus.OK);
        } else {

            Map<String, Object> aux = new LinkedHashMap<String, Object>();
            aux.put("Problem", "gamePlayer does not exist");
            response = new ResponseEntity<>(aux, HttpStatus.UNAUTHORIZED);
        }
        return response;
    }


    private Map<String, Object> getMapDTOs(Long gamePlayerId) { // Retorna un mapa con los DTOs de las clases.

        // Creo un Mapa donde agrego los DTO, accediendo desde gamePlayerRepository.
        // Accedo a la clase Game, que posee 'makeGameDTO' donde este tiene anidados los DTO de GamePlayer y Player respectivamente.
        Map<String, Object> data = gamePlayerRepository.getOne(gamePlayerId).getGame().makeGameDTO();

        // Agrego a 'data' el DTO de Ship.
        data.put("ships", gamePlayerRepository.getOne(gamePlayerId).getShips().stream().map(ship -> ship.makeShipDTO()).collect(Collectors.toList()));

        // Desde gamePlayerRepository, consigo un gamePlayer por ID. Desde ahi, entro a game, luego consigo los gamePlayers y para cada player, consigo sus salvoes.
        // Luego, realizo otro map y para cada salvo, llamo al metodo DTO de la clase Salvo.
        data.put("salvoes", gamePlayerRepository.getOne(gamePlayerId).getGame().getGamePlayers().stream().flatMap(player -> player.getSalvoes().stream().map(salvo -> salvo.makeSalvoDTO())).collect(Collectors.toList()));

        return data;
    }
}
