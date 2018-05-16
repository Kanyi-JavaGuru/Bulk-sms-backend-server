package co.ke.bigfootke.app.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@Entity
@Table(name="credentials")
public class Credentials {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="credentials_id")
	private int credentialsId;
	
	@Column(name="active")
	private boolean active;
	
	@Column(name="logged_in")
	private boolean loggedIn;	
	
//	@NotBlank(message="Please enter password")
	@Column(name="password")
	private String password;
	
	@NotBlank(message="Please choose user role")
	@Column(name="user_role")
	private String role;
	
	@Column(name="last_signin")
	private Date lastSignIn;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Credentials() {
	}

	@JsonIgnore
	public int getCredentialsId() {
		return credentialsId;
	}

	public void setCredentialsId(int access_id) {
		this.credentialsId = access_id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean isActive) {
		this.active = isActive;
	}

	public boolean loggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonSetter
	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@JsonIgnore
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getLastSignIn() {
		return lastSignIn;
	}

	public void setLast_sign_in(Date lastSignIn) {
		this.lastSignIn = lastSignIn;
	}

	@Override
	public String toString() {
		return "Credentials [credentialsId=" + credentialsId + ", active=" + active + ", loggedIn=" + loggedIn
				+ ", role=" + role + ", lastSignIn=" + lastSignIn + ", user=" + user + "]";
	}	
	
}
