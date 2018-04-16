package co.ke.bigfootke.app.jpa.entities;

import java.util.Set;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="clientId")
@Entity
@Table(name="client")
@Cacheable
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Client {

	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="client_id")
	private Long clientId;

	@NotBlank(message="Please enter country code")
	@Column(name="country_code", nullable = false)
	private String countryCode;
	
	@NotBlank(message="Please enter phone number")
	@Column(name="phone_no", nullable = false, unique=true)
	private String phoneNo;
	
	@Column(name="full_name")
	private String fullName;
	
	@Column(name="telecom")
	private String telecom;

	@ManyToMany(mappedBy = "clients",
			fetch = FetchType.LAZY, 
			cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private Set<Group> groups;
	
	public Client() {
	}
	
	public Client(Client client) {
		this.clientId = client.clientId;
		this.countryCode = client.countryCode;
		this.phoneNo = client.phoneNo;
		this.fullName = client.fullName;
		this.telecom = client.telecom;
		this.groups = client.groups;
	}
	
	public Client(Long clientId, String countryCode, String phoneNo, String fullName, String telecom) {
		this.clientId = clientId;
		this.countryCode = countryCode;
		this.phoneNo = phoneNo;
		this.fullName = fullName;
		this.telecom = telecom;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long id) {
		this.clientId = id;
	}

	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getTelecom() {
		return telecom;
	}

	public void setTelecom(String telecom) {
		this.telecom = telecom;
	}
	
	@JsonIgnore
	public Set<Group> getGroups() {
		return groups;
	}
	
	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	@Override
	public String toString() {
		return "Client [clientId=" + clientId + ", countryCode=" + countryCode + ", phoneNo=" + phoneNo + ", fullName="
				+ fullName + ", telecom=" + telecom +"]";
	}
	
}
