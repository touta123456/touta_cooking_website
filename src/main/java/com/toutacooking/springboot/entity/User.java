package com.toutacooking.springboot.entity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity(name = "users")
public class User implements UserDetails{

	private static final long serialVersionUID = 5023533534398435246L;

	public User() {
	}

	public User(
			@NotEmpty String username,
			@NotEmpty String firstName,
			@NotEmpty String lastName,
			@NotEmpty @Email String email, 
			@Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$") String password
			) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
		
	@NotEmpty @Email
	private String email;
	    
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$")
	private String password;
	    
	@NotEmpty
	private String firstName;
	    
	@NotEmpty
	private String lastName;
	
	@NotEmpty
	@Column(name = "user_name")
	private String username;

	@NotNull
	@ManyToOne(targetEntity = Role.class)
	private Role role; 
	    
	@OneToMany(mappedBy = "author")
	private List<Recipe> recipes;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	@Override
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, firstName, id, lastName, password, recipes, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName)
				&& Objects.equals(id, other.id) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(password, other.password) && Objects.equals(recipes, other.recipes)
				&& role == other.role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getLibelle());
	    return List.of(authority);
	}

}