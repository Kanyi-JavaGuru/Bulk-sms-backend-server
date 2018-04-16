package co.ke.bigfootke.app.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

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
	
	@NotBlank(message="Please enter password")
	@Size(min=6, message="Invalid password enter at least 6 characters")
	@Column(name="password")
	private String password;
	
	@NotBlank(message="Please choose user role")
	@Column(name="user_role")
	private String role;
	
	@Column(name="last_signin")
	private String lastSignIn;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Credentials() {
	}

	@JsonIgnore
	public int getCredentialsId() {
		return credentialsId;
	}

	@JsonSetter
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

	public String getLastSignIn() {
		return lastSignIn;
	}

	public void setLast_sign_in(String lastSignIn) {
		this.lastSignIn = lastSignIn;
	}

	@Override
	public String toString() {
		return "Credentials [credentialsId=" + credentialsId + ", active=" + active + ", loggedIn=" + loggedIn
				+ ", role=" + role + ", lastSignIn=" + lastSignIn + ", user=" + user + "]";
	}	
	
}
