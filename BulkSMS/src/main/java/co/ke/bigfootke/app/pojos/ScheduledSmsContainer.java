package co.ke.bigfootke.app.pojos;

import java.util.Date;
import java.util.List;

public class ScheduledSmsContainer {

	private String title;
	private String message;
	private Date date;
	private String time;
	private List<Long> groupIds;
	
	public ScheduledSmsContainer() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<Long> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<Long> groupIds) {
		this.groupIds = groupIds;
	}
	
}
