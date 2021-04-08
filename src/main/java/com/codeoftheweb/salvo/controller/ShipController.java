package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ShipController {

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;


    // -- Metodos -- //
    @GetMapping("/games/players/{gamePlayerId}/ships")
    public Map<String, Object> getShips(@PathVariable Long gamePlayerId) {
        return Utils.makeMap("ships", gamePlayerRepository.getOne(gamePlayerId).getShips().stream().map(ship -> ship.makeShipDTO()).collect(Collectors.toList()));
    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> placeShips(@PathVariable Long gamePlayerId, @RequestBody Set<Ship> ships, Authentication authentication) {

        ResponseEntity<Map<String, Object>> response;
        Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);
        Player currentPlayer = playerRepository.findByUserName(authentication.getName());

        if (Utils.isGuest(authentication)) {

            response = new ResponseEntity<>(Utils.makeMap("error", "no player logged in"), HttpStatus.UNAUTHORIZED);
        } else if (!gp.isPresent()) {

            response = new ResponseEntity<>(Utils.makeMap("error", "Game Player ID doesn't exist"), HttpStatus.UNAUTHORIZED);
        } else if (gp.get().getPlayer().getId() != currentPlayer.getId()) {

            response = new ResponseEntity<>(Utils.makeMap("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
        } else if (gp.get().getShips().size() > 0) {

            response = new ResponseEntity<>(Utils.makeMap("error", "user already has ships placed"), HttpStatus.FORBIDDEN);
        } else {

            if (ships.size() > 0) {

                gp.get().addShips(ships);
                gamePlayerRepository.save(gp.get());

                response = new ResponseEntity<>(Utils.makeMap("message", "success"), HttpStatus.ACCEPTED);
            } else {

                response = new ResponseEntity<>(Utils.makeMap("error", "you don't send any ship"), HttpStatus.FORBIDDEN);
            }
        }

        return response;
    }
}
