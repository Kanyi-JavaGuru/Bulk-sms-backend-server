package co.ke.bigfootke.app.jpa.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="userId")
@Entity
@Table(name="user")
public class User {
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_id")
	private Long userId;
	
	@NotBlank(message="Please enter first name")
	@Column(name="first_name", nullable=false)
	private String firstName;
	
	@NotBlank(message="Please enter last name")
	@Column(name="last_name", nullable=false)
	private String lastName;
	
	@NotBlank(message="Please enter email address")
	@Column(name="email", nullable=false, unique=true)
	private String email;
	
	@OneToOne(mappedBy = "user",
	        cascade = CascadeType.ALL, orphanRemoval = true)
	private Credentials credentials;	
	
	@OneToMany(mappedBy = "sender",
		fetch = FetchType.LAZY,
		cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<Sms> sms;
	
	public User() {
	}
	
	public User(User user) {
		this.userId = user.getUserId();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.credentials = user.getCredentials();
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public void setId(long id) {
		this.userId = id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String first_name) {
		this.firstName = first_name;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String last_name) {
		this.lastName = last_name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	@JsonIgnore
	public Set<Sms> getSms() {
		return sms;
	}

	public void setSms(Set<Sms> sms) {
		this.sms = sms;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ "]";
	}	
	
	
}
