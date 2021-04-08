package com.codeoftheweb.salvo.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShipType {

    @JsonProperty("carrier") // Carrier (5)
    CARRIER,
    @JsonProperty("battleship") // Battleship (4)
    BATTLESHIP,
    @JsonProperty("submarine") // Submarine (3)
    SUBMARINE,
    @JsonProperty("destroyer") // Destroyer (3)
    DESTROYER,
    @JsonProperty("patrol boat") // Patrol Boat (2)
    PATROL_BOAT
}
