package com.karl.fx.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;


public class PlayRule {
    public PlayRule(String ruleKey, Integer times, Boolean ruleCheck,
			String ruleName, String ruleDetail) {
		super();
		this.ruleKey = ruleKey;
		this.times = times;
		this.ruleCheck = new SimpleBooleanProperty(ruleCheck);
		this.ruleName = new SimpleStringProperty(ruleName);
		this.ruleDetail = new SimpleStringProperty(ruleDetail);
	}
	public static final String RULECHECKKEY = "ruleCheck";
    public static final String RULENAMEKEY = "ruleName";
    public static final String RULEDETAILKEY = "ruleDetail";
    private  SimpleBooleanProperty ruleCheck;
    private  SimpleStringProperty ruleName;
    private  SimpleStringProperty ruleDetail;
    
    private String ruleKey;
    private Integer times;
    
    public BooleanProperty ruleCheckProperty() { return ruleCheck; }
    
	public Boolean getRuleCheck() {
		return ruleCheck.getValue();
	}
	public void setRuleCheck(Boolean ruleCheck) {
		this.ruleCheck.set(ruleCheck);
	}
	public String getRuleName() {
		return ruleName.getValue();
	}
	public void setRuleName(String ruleName) {
		this.ruleName.set(ruleName);
	}
	public String getRuleDetail() {
		return ruleDetail.getValue();
	}
	public void setRuleDetail(String ruleDetail) {
		this.ruleDetail.set(ruleDetail);
	}
	public String getRuleKey() {
		return ruleKey;
	}
	public void setRuleKey(String ruleKey) {
		this.ruleKey = ruleKey;
	}
	public Integer getTimes() {
		return times;
	}
	public void setTimes(Integer times) {
		this.times = times;
	}

}
