package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    // -- gamePlayers -- //
    @GetMapping("/gamePlayers") // Lo mismo que @RequestMapping, pero solo a nivel método.
    public List<Object> getGamePlayers() {
        return gamePlayerRepository.findAll().stream().map(gamePlayer -> gamePlayer.makeGamePlayerDTO()).collect(Collectors.toList());
    }


    // -- game_view -- //
    @GetMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> findGamePlayer(@PathVariable Long gamePlayerId, Authentication authentication) {

        Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);
        ResponseEntity<Map<String, Object>> response;
        Map<String, Object> aux = new LinkedHashMap<String, Object>();

        if (gp.isPresent()) { // Si no es null, realizo un llamado al metodo "getMapDTOs" que devuelve un mapa con los DTO.

            if (authentication.getName().compareTo(gp.get().getPlayer().getUserName()) == 0) {

                response = new ResponseEntity<>(getMapDTOs(gamePlayerId), HttpStatus.OK);
            } else {

                aux.put("Problem", "player not authorized");
                response = new ResponseEntity<>(aux, HttpStatus.UNAUTHORIZED);
            }

        } else {

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
