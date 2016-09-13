package com.karl.fx.model;

public class ChatGroupModel {
    @Override
	public String toString() {
		return getGroupName();
	}
    private String groupId;
    private String groupName;
    private Integer groupSize;

    public ChatGroupModel(String groupId, String groupName, Integer groupSize) {
        super();
        this.groupId = groupId;
        this.groupName =groupName;
        this.groupSize = groupSize;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public Integer getGroupSize() {
        return groupSize;
    }
}