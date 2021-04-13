package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.enums.ShipType;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController //  Controlador para construir una API Rest que reciba y envíe peticiones requeridas.
@RequestMapping("/api") // Se encarga de relacionar una clase o método con una petición http.
public class AppController {

    @Autowired // Permite inyectar unas dependencias con otras dentro.
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;


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

    private List<Map<String, Object>> hitsAndSinks(GamePlayer self, GamePlayer opponent) {

        List<Map<String, Object>> dtoSupremo = new LinkedList<>();
        int[] totalDamages = new int[5];

        List<String> patrolBoatLocation = findShipLocations(self, ShipType.PATROL_BOAT);
        List<String> destroyerLocation = findShipLocations(self, ShipType.DESTROYER);
        List<String> submarineLocation = findShipLocations(self, ShipType.SUBMARINE);
        List<String> battleShipLocation = findShipLocations(self, ShipType.BATTLESHIP);
        List<String> carrierLocationLocation = findShipLocations(self, ShipType.CARRIER);

        for (Salvo salvo : opponent.getSalvoes()) {
            Map<String, Object> dtoHit = new LinkedHashMap<>();
            Map<String, Object> damage = new LinkedHashMap<>();
            System.out.println(salvo);
            ArrayList<String> hitCellList = new ArrayList<>();
            int[] hits = new int[5];
            int missedShots = salvo.getSalvoLocations().size();

            for (String location : salvo.getSalvoLocations()) {

                if (carrierLocationLocation.contains(location)) {
                    totalDamages[0]++;
                    hits[0]++;
                    hitCellList.add(location);
                    missedShots--;
                }
                if (battleShipLocation.contains(location)) {

                    totalDamages[1]++;
                    hits[1]++;
                    hitCellList.add(location);
                    missedShots--;
                }
                if (destroyerLocation.contains(location)) {

                    totalDamages[2]++;
                    hits[2]++;
                    hitCellList.add(location);
                    missedShots--;
                }
                if (submarineLocation.contains(location)) {

                    totalDamages[3]++;
                    hits[3]++;
                    hitCellList.add(location);
                    missedShots--;
                }
                if (patrolBoatLocation.contains(location)) {

                    totalDamages[4]++;
                    hits[4]++;
                    hitCellList.add(location);
                    missedShots--;
                }
            }

            damage.put("carrierHits", hits[0]);
            damage.put("battleShipHits", hits[1]);
            damage.put("destroyerHits", hits[2]);
            damage.put("submarineHits", hits[3]);
            damage.put("patrolboatHits", hits[4]);
            damage.put("carrier", totalDamages[0]);
            damage.put("battleship", totalDamages[1]);
            damage.put("destroyer", totalDamages[2]);
            damage.put("submarine", totalDamages[3]);
            damage.put("patrolboat", totalDamages[4]);

            dtoHit.put("turn", salvo.getTurn());
            dtoHit.put("hitLocations", hitCellList);
            dtoHit.put("damages", damage);
            dtoHit.put("missed", missedShots);

            dtoSupremo.add(dtoHit);
        }

        return dtoSupremo;
    }

    public List<String> findShipLocations(GamePlayer self, ShipType type) {

        Optional<Ship> response;
        response = self.getShips().stream().filter(ship -> ship.getType().equals(type)).findFirst();
        if (response.isEmpty()) {
            return new ArrayList<>();
        } else {
            return response.get().getLocations();
        }
    }

    private Map<String, Object> getMapDTOs(Long gamePlayerId) { // Retorna un mapa con los DTOs de las clases.

        // Creo un Mapa donde agrego los DTO, accediendo desde gamePlayerRepository.
        // Accedo a la clase Game, que posee 'makeGameDTO' donde este tiene anidados los DTO de GamePlayer y Player respectivamente.
        Map<String, Object> data = gamePlayerRepository.getOne(gamePlayerId).getGame().makeGameDTO();
        Map<String, Object> hits = new LinkedHashMap<>();

        data.put("gameState", "PLACESHIPS");

        // Agrego a 'data' el DTO de Ship.
        data.put("ships", gamePlayerRepository.getOne(gamePlayerId).getShips().stream().map(ship -> ship.makeShipDTO()).collect(Collectors.toList()));

        // Desde gamePlayerRepository, consigo un gamePlayer por ID. Desde ahi, entro a game, luego consigo los gamePlayers y para cada player, consigo sus salvoes.
        // Luego, realizo otro map y para cada salvo, llamo al metodo DTO de la clase Salvo.
        data.put("salvoes", gamePlayerRepository.getOne(gamePlayerId).getGame().getGamePlayers().stream().flatMap(player -> player.getSalvoes().stream().map(salvo -> salvo.makeSalvoDTO())).collect(Collectors.toList()));

        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        Optional<GamePlayer> opponent = gamePlayer.get().getGame().findOpponent(gamePlayer.get());
        if (opponent.isPresent()) {

            hits.put("self", hitsAndSinks(opponent.get(), gamePlayer.get()));
            hits.put("opponent", hitsAndSinks(gamePlayer.get(), opponent.get()));
        } else {

            hits.put("self", new ArrayList<>());
            hits.put("opponent", new ArrayList<>());
        }

        data.put("hits", hits);

        return data;
    }
}
