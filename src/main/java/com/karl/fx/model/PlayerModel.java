package com.karl.fx.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PlayerModel {
    public static final String playerIDColumnKey = "playerId";
    public static final String playerNameColumnKey = "playerName";
    public static final String playerPointColumnKey = "playerPoint";
    private final SimpleStringProperty playerId;
    private final SimpleStringProperty playerName;
    private final SimpleIntegerProperty playerPoint;

    public PlayerModel(String playerId, String playerName, Integer playerSize) {
        super();
        this.playerId = new SimpleStringProperty(playerId);
        this.playerName = new SimpleStringProperty(playerName);
        this.playerPoint = new SimpleIntegerProperty(playerSize);
    }

    public String getplayerId() {
        return playerId.getValue();
    }

    public String getplayerName() {
        return playerName.getValue();
    }

    public Integer getplayerSize() {
        return playerPoint.getValue();
    }
}