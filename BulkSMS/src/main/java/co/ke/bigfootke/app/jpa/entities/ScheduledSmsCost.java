package co.ke.bigfootke.app.jpa.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
//import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name="schedule_cost")
public class ScheduledSmsCost {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "cost_id")
	private Long costId;
	
	@Column(name = "cost")
	private int cost;
	
	@Column(name = "date_sent")
	private Date dateSent;
	
//	@OneToOne
//	@JoinColumn(name = "schedule")
	@ManyToOne(fetch = FetchType.LAZY, 
			cascade = {CascadeType.PERSIST, CascadeType.MERGE})
			@JoinTable(name="recurring_sms_costs",
				inverseJoinColumns=@JoinColumn(name = "sms_fk"),
				joinColumns=@JoinColumn(name = "recurring_cost_fk"))
	private ScheduledSms schedule;

	public ScheduledSmsCost() {
	}

	public Long getCostId() {
		return costId;
	}

	public void setCostId(Long costId) {
		this.costId = costId;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public Date getDateSent() {
		return dateSent;
	}

	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}

	public ScheduledSms getSchedule() {
		return schedule;
	}

	public void setSchedule(ScheduledSms schedule) {
		this.schedule = schedule;
	}

	@Override
	public String toString() {
		return "ScheduleCost [costId=" + costId + ", cost=" + cost + ", dateSent=" + dateSent + "]";
	}

}
