package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    @Id // Identificador que diferencia una entidad del resto.
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "location")
    private List<String> shipLocation;


    // -- Constructores -- //
    public Ship() {
        shipLocation = new ArrayList<>();
    }

    public Ship(String type, GamePlayer gamePlayer, List<String> shipLocation) {
        this();
        this.type = type;
        this.gamePlayer = gamePlayer;
        this.shipLocation = shipLocation;
    }


    // -- Getters -- //
    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public List<String> getShipLocation() {
        return shipLocation;
    }


    // -- Setters -- //
    public void setType(String type) {
        this.type = type;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }


    // -- Metodos -- //
    public Map<String, Object> makeShipDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        dto.put("id", getId());
        dto.put("type", getType());
        dto.put("location", getShipLocation());

        return dto;
    }
}
