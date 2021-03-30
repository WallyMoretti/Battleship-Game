package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Entity
public class Player {

    @Id // Identificador que diferencia una entidad del resto.
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName; // Email

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores;


    // -- Constructor -- //
    public Player() {
    }

    public Player(String userName) {
        this.userName = userName;
    }


    // -- Getters -- //
    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }


    // -- Setters -- //
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }


    // -- Metodos -- //
    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public Map<String, Object> makePlayerDTO() {

        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        dto.put("id", getId());
        dto.put("username", getUserName());

        return dto;
    }

    public Optional<Score> getScore(Game game) {
        return scores.stream().filter(score -> score.getGame().equals(game)).findFirst();
    }
}
