package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // -- Metodos -- //
    @GetMapping("/players")
    public List<Object> getPlayers() {
        return playerRepository.findAll().stream().map(player -> player.makePlayerDTO()).collect(Collectors.toList());
    }

    @PostMapping("/players")
    public ResponseEntity<Map<String, Object>> register(@RequestParam String username, @RequestParam String password) {

        if (username.isEmpty() || password.isEmpty()) {

            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByUserName(username) != null) {

            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("message", "success, player created"), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
