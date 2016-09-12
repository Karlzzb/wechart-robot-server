/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.karl.fx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.springframework.stereotype.Component;

import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;

import com.karl.fx.model.PlayerModel;

@Component
public class PlayerController extends FxmlController {

    @FXML
    private TableView<PlayerModel> playerTab;
    @FXML
    private TableColumn<PlayerModel, String> colPalyerName;
    @FXML
    private TableColumn<PlayerModel, Integer> colPalyerPoint;

    private ObservableList<PlayerModel> playerList;

    @Override
    public void initialize() {
        assemableTable();
    }

    private void assemableTable() {
        playerTab.setEditable(true);
        playerTab.setItems(generateDataInMap());
        colPalyerName.setCellValueFactory(new PropertyValueFactory<PlayerModel, String>(
                PlayerModel.playerNameColumnKey));
        colPalyerPoint.setCellValueFactory(new PropertyValueFactory<PlayerModel, Integer>(
                PlayerModel.playerPointColumnKey));

        playerTab.getColumns().addAll(colPalyerName, colPalyerPoint);
    }

    private ObservableList<PlayerModel> generateDataInMap() {
        if (playerList != null)
            playerList.clear();
        playerList = FXCollections.observableArrayList();

        if (runtimeDomain.getCurrentGroupId() != null
                && !runtimeDomain.getCurrentGroupId().isEmpty()) {
            JSONObject currentGroupNode = runtimeDomain.getGroupMap().get(
                    runtimeDomain.getCurrentGroupId());
            JSONArray playerCollection = currentGroupNode.getJSONArray("MemberList");

            if (null != playerCollection) {
                JSONObject contact = null;
                JSONObject playFriend = null;
                for (int i = 0, len = playerCollection.size(); i < len; i++) {
                    contact = playerCollection.getJSONObject(i);
                    playFriend = runtimeDomain.getAllUsrMap().get(contact.getString("UserName"));
                    if (playFriend == null) {
                        continue;
                    }
                    // TODO get the current player, and Need to check if the
                    // player is existed in database
                }
            }

        }

        return playerList;
    }
}
