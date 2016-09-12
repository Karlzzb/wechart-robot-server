package com.karl.fx.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ChatGroupModel {
    public static final String selectorColumnKey = "selector";
    public static final String groupIDColumnKey = "groupId";
    public static final String groupNameColumnKey = "groupName";
    public static final String groupSizeColumnKey = "groupSize";
    private SimpleBooleanProperty selector;
    private final SimpleStringProperty groupId;
    private final SimpleStringProperty groupName;
    private final SimpleIntegerProperty groupSize;

    public ChatGroupModel(String groupId, String groupName, Integer groupSize) {
        super();
        this.selector = new SimpleBooleanProperty(Boolean.FALSE);
        this.groupId = new SimpleStringProperty(groupId);
        this.groupName = new SimpleStringProperty(groupName);
        this.groupSize = new SimpleIntegerProperty(groupSize);
    }

    public String getGroupId() {
        return groupId.getValue();
    }

    public String getGroupName() {
        return groupName.getValue();
    }

    public Integer getGroupSize() {
        return groupSize.getValue();
    }

    public Boolean getSelector() {
        return selector.getValue();
    }

    public void setSelector(Boolean selector) {
        this.selector = new SimpleBooleanProperty(selector);
    }
}