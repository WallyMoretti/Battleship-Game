package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
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

    @GetMapping("/games") // Lo mismo que @RequestMapping, pero solo a nivel método.
    public List<Object> getGames() {
        return gameRepository.findAll().stream().map(game -> game.makeGameDTO()).collect(Collectors.toList());
    }

    @GetMapping("/gamePlayers") // Lo mismo que @RequestMapping, pero solo a nivel método.
    public List<Object> getGamePlayers() {
        return gamePlayerRepository.findAll().stream().map(gamePlayer -> gamePlayer.makeGamePlayerDTO()).collect(Collectors.toList());
    }

    @GetMapping("/players")
    public List<Object> getPlayers() {
        return playerRepository.findAll().stream().map(player -> player.makePlayerDTO()).collect(Collectors.toList());
    }

    @GetMapping("/game_view/{gamePlayerID}")
    public Map<String, Object> getGameView(@PathVariable Long gamePlayerID) { // @PathVariable obtiene el valor específico de una URL.

        // Desde gamePlayerRepository, accedo a Game para utilizar su DTO.
        Map<String, Object> data = gamePlayerRepository.getOne(gamePlayerID).getGame().makeGameDTO();

        // Agrego al Map 'data' los ships, accediendo desde gamePlayerRepository, para que 'data' guarde el retorno del DTO de la clase Ship.
        data.put("ships", gamePlayerRepository.getOne(gamePlayerID).getShips().stream().map(ship -> ship.makeShipDTO()).collect(Collectors.toList()));

        return data;
    }
}
