package co.ke.bigfootke.app.jpa.entities;

import java.util.Date;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="smsId")
@Entity
@Table(name="ondemand_sms")
public class Sms {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="sms_id")
	private Long smsId;
	
	@NotBlank(message="Please enter message")
	@Column(name="message")
	private String message;
	
	@Column(name="cost", nullable = true)
	private int cost;
	
	@Column(name="date")
	private Date date;
	
	@OneToMany(mappedBy = "sms",
			fetch = FetchType.LAZY, 
			cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private Set<Group> groups;
	
	@ManyToOne
	(fetch = FetchType.LAZY, 
			cascade = {CascadeType.PERSIST, CascadeType.MERGE})
			@JoinTable(name="user_sms",
				inverseJoinColumns=@JoinColumn(name = "user_fk"),
				joinColumns=@JoinColumn(name = "sms_fk"))
	private User sender;
	
	public Sms() {
	}
	
	public Sms(String message, int cost, Date date) {
		super();
		this.message = message;
		this.cost = cost;
		this.date = date;
	}

	public Long getSmsId() {
		return smsId;
	}

	public void setSmsId(Long smsId) {
		this.smsId = smsId;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@JsonIgnore
	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	@JsonIgnore
	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	@Override
	public String toString() {
		return "OnDemandSms [smsId=" + smsId + ", message=" + message + ", cost=" + cost + ", date=" + date
				+ ", sender=" + sender + "]";
	}
	
}
