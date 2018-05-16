package co.ke.bigfootke.app.pojos;

import java.util.List;

public class SmsContainer {
	
	private String message;
	private int cost;
	private List<Long> groupIds;
	
	public SmsContainer() {
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public List<Long> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<Long> groupIds) {
		this.groupIds = groupIds;
	}
}
