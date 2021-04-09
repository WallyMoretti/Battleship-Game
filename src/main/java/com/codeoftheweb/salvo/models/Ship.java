package com.codeoftheweb.salvo.models;

import com.codeoftheweb.salvo.enums.ShipType;
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

    private ShipType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "location")
    private List<String> locations;


    // -- Constructores -- //
    public Ship() {
        locations = new ArrayList<>();
    }

    public Ship(ShipType type, GamePlayer gamePlayer, List<String> locations) {
        this();
        this.type = type;
        this.gamePlayer = gamePlayer;
        this.locations = locations;
    }


    // -- Getters -- //
    public long getId() {
        return id;
    }

    public ShipType getType() {
        return type;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }


    // -- Setters -- //
    public void setType(ShipType type) {
        this.type = type;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }


    // -- Metodos -- //
    public Map<String, Object> makeShipDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        dto.put("type", getType());
        dto.put("locations", getLocations());

        return dto;
    }
}
