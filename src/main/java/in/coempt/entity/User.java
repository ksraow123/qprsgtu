package in.coempt.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="user_name",nullable = false, unique = true, length = 80)
	private String userName;
	@Column(name="email",nullable = false, unique = true, length = 80)
	private String email;
	
	@Column(nullable = false, length = 64)
	private String password;
	
	@Column(name = "first_name", nullable = false, length = 80)
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;

	@Column(name = "role_id", nullable = false, length = 20)
	private int roleId;
	@Column(name = "is_active", nullable = false, length = 20)
	private int isActive;
	private String resetToken;
	@Column(name = "mobile_no", nullable = false, length = 20, unique = true)
	private String mobileNo;
	private LocalDateTime tokenExpiry;
}
