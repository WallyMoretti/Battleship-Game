package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.repository.ShipRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {

        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean // @Bean --> Permite encapsular el contenido, con la finalidad de otorgar una mejor estructura.
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository) {
        return (args) -> {

            // Players
            Player player1 = playerRepository.save(new Player("j.bauer@gmail.com"));
            Player player2 = playerRepository.save(new Player("c.obrian@gmail.com"));
            Player player3 = playerRepository.save(new Player("k.bauer@gmail.com"));
            Player player4 = playerRepository.save(new Player("d.palmer@gmail.com"));
            Player player5 = playerRepository.save(new Player("m.dessler@gmail.com"));

            // Games
            Game game1 = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"))));
            Game game2 = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(1)));
            Game game3 = gameRepository.save(new Game(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(2)));

            //GamePlayer
            GamePlayer gamePlayer1 = gamePlayerRepository.save(new GamePlayer(player1, game1));
            GamePlayer gamePlayer2 = gamePlayerRepository.save(new GamePlayer(player2, game1));
            GamePlayer gamePlayer3 = gamePlayerRepository.save(new GamePlayer(player3, game2));
            GamePlayer gamePlayer4 = gamePlayerRepository.save(new GamePlayer(player4, game2));
            GamePlayer gamePlayer5 = gamePlayerRepository.save(new GamePlayer(player5, game3));

            // Ship
            List<String> shipPosition;
            Ship ship1 = shipRepository.save(new Ship("Battleship", gamePlayer1, shipPosition = Arrays.asList("B5", "B6", "B7", "B8")));
            Ship ship2 = shipRepository.save(new Ship("Cruiser", gamePlayer1, shipPosition = Arrays.asList("F3", "G3", "H3")));
        };
    }
}
