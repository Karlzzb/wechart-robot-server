package com.karl.domain;

public class MessageDomain {
	
	public MessageDomain(String content, String target) {
		super();
		this.content = content;
		this.target = target;
	}	
	private String content;
	private String target;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

}
