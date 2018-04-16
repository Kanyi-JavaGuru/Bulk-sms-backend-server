package co.ke.bigfootke.app.pojos;

import java.util.List;

public class Family {

	private Long parentId;
	private List<Long> childrenIds;
	
	public Family() {
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public List<Long> getChildrenIds() {
		return childrenIds;
	}

	public void setChildrenIds(List<Long> childrenIds) {
		this.childrenIds = childrenIds;
	}
	
}
