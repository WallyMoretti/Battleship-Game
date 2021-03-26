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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    private int turn;

    @ElementCollection
    @Column(name = "location")
    private List<String> salvoLocation;


    // -- Constructores -- //
    public Salvo() {
        salvoLocation = new ArrayList<>();
    }

    public Salvo(GamePlayer gamePlayer, int turn, List<String> salvoLocation) {
        this();
        this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.salvoLocation = salvoLocation;
    }


    // -- Getters -- //
    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public int getTurn() {
        return turn;
    }

    public List<String> getSalvoLocation() {
        return salvoLocation;
    }


    // -- Setters -- //
    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setSalvoLocation(List<String> salvoLocation) {
        this.salvoLocation = salvoLocation;
    }


    // -- Metodos -- //
    public Map<String, Object> makeSalvoDTO() {

        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", getId());
        dto.put("turn", getTurn());
        dto.put("location", getSalvoLocation());

        return dto;
    }
}
