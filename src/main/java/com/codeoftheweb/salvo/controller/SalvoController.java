package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;


    // -- Metodos -- //
    @GetMapping("/games/players/{gamePlayerId}/salvos")
    public Map<String, Object> getSalvoes(@PathVariable Long gamePlayerId) {

        return Utils.makeMap("salvoes", gamePlayerRepository.getOne(gamePlayerId).getGame().getGamePlayers().stream().flatMap(player -> player.getSalvoes().stream().map(salvo -> salvo.makeSalvoDTO())).collect(Collectors.toList()));
    }

    @PostMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> placeSalvoes(@PathVariable Long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {

        ResponseEntity<Map<String, Object>> response;
        Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);
        Player currentPlayer = playerRepository.findByUserName(authentication.getName());

        if (Utils.isGuest(authentication)) {

            response = new ResponseEntity<>(Utils.makeMap("error", "no player logged in"), HttpStatus.UNAUTHORIZED);
        } else if (gp.isEmpty()) {

            response = new ResponseEntity<>(Utils.makeMap("error", "Game Player not found"), HttpStatus.UNAUTHORIZED);
        } else if (gp.get().getPlayer().getId() != currentPlayer.getId()) {

            response = new ResponseEntity<>(Utils.makeMap("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
        } else {

            // Busco al gp enemigo.
            Optional<GamePlayer> opponent = gp.get().getGame().getGamePlayers().stream().filter(gamePlayer -> gamePlayer.getId() != gp.get().getId()).findFirst();

            if (opponent.isPresent()) {

                if (gp.get().getSalvoes().size() <= opponent.get().getSalvoes().size()) {

                    salvo.setTurn(gp.get().getSalvoes().size() + 1);
                    gp.get().addSalvo(salvo);
                    gamePlayerRepository.save(gp.get());
                    response = new ResponseEntity<>(Utils.makeMap("OK", "success"), HttpStatus.CREATED);
                } else {

                    response = new ResponseEntity<>(Utils.makeMap("error", "it is not your turn"), HttpStatus.FORBIDDEN);
                }
            } else {

                response = new ResponseEntity<>(Utils.makeMap("error", "the is no opponent"), HttpStatus.FORBIDDEN);
            }

        }

        return response;
    }
}

