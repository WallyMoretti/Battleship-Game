package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {

    @Id // Identificador que diferencia una entidad del resto.
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "location")
    private List<String> salvoLocation;


    // -- Constructores -- //
    public Salvo() {
        salvoLocation = new ArrayList<>();
    }

    public Salvo(int turn, GamePlayer gamePlayer, List<String> salvoLocation) {
        this();
        this.turn = turn;
        this.gamePlayer = gamePlayer;
        this.salvoLocation = salvoLocation;
    }


    // -- Getters -- //
    public long getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public List<String> getSalvoLocation() {
        return salvoLocation;
    }


    // -- Setters -- //
    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setSalvoLocation(List<String> salvoLocation) {
        this.salvoLocation = salvoLocation;
    }


    // -- Metodos -- //
    public Map<String, Object> makeSalvoDTO() {

        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("turn", getTurn());
        dto.put("player", getGamePlayer().getPlayer().getId());
        dto.put("locations", getSalvoLocation());

        return dto;
    }
}
