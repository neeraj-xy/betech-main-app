package com.spring.bioMedical.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Transient;

@Entity
@Table(name = "user")
public class Admin {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;
	
	@Column(name = "username", nullable = false, unique = true)
	@Email(message = "Please provide a valid e-mail")
	@NotEmpty(message = "Please provide an e-mail")
	private String email;
	
	@Column(name = "password")
	@Transient
	private String password;
	
	@Column(name = "mobile")
	@NotEmpty(message = "Please enter your mobile number")
	private String mobile;
	
	@Column(name = "prefix")
	@NotEmpty(message = "Please select prefix")
	private String prefix;
	
	@Column(name = "first_name")
	@NotEmpty(message = "Please provide your first name")
	private String firstName;
	
	@Column(name = "middle_name", columnDefinition = "varchar(255) default ''")
	private String middleName;

	@Column(name = "last_name")
	@NotEmpty(message = "Please provide your last name")
	private String lastName;
	
	@Column(name = "preferred_name")
	@NotEmpty(message = "Please provide your preferred name")
	private String prefName;
	
	@Column(name = "enabled")
	private boolean enabled;
	
	@Column(name = "confirmation_token")
	private String confirmationToken;

	@Column(name = "gender")
	private String gender;
	
	@Column(name = "dob")
	private String dob;
	
	@Column(name = "authority")
	private String role;
	
	@Column(name = "lastseen")
	@Transient
	private String lastseen;
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getPrefName() {
		return prefName;
	}

	public void setPrefName(String prefName) {
		this.prefName = prefName;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	public String getLastseen() {
		return lastseen;
	}

	public void setLastseen(String lastseen) {
		this.lastseen = lastseen;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	
	public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		this.enabled = value;
	}
	
	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}	

	@Override
	public String toString() {
		return "Admin [id=" + id + ", email=" + email + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", enabled=" + enabled + ", confirmationToken=" + confirmationToken
				+ ", gender=" + gender + ", role=" + role + ", lastseen=" + lastseen + "]";
	}

	

}