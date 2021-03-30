package com.codeoftheweb.salvo.models;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {

    @Id // Identificador que diferencia una entidad del resto.
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    private double score;
    private LocalDateTime finishDate;


    // -- Constructores -- //
    public Score() {
    }

    public Score(Game game, Player player, double score, LocalDateTime finishDate) {
        this.game = game;
        this.player = player;
        this.score = score;
        this.finishDate = finishDate;
    }


    // -- Getters -- //
    public long getId() {
        return id;
    }

    @JsonIgnore
    public Game getGame() {
        return game;
    }

    @JsonIgnore
    public Player getPlayer() {
        return player;
    }

    public double getScore() {
        return score;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }


    // -- Setters -- //
    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }


    // -- Metodos -- //
    public Map<String, Object> makeScoreDTO() {

        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        dto.put("player", player.getId());
        dto.put("score", getScore());
        dto.put("finishDate", getFinishDate());

        return dto;
    }
}
