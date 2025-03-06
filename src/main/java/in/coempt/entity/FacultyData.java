package in.coempt.entity;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_faculty_data")
@Data
public class FacultyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile_number", unique = true)
    private String mobileNumber;

    @Column(name = "email" )
    private String email;

    @Column(name = "college_code")
    private String collegeCode;

    @Column(name = "teaching_exp")
    private String teachingExp;

    @Column(name = "industry_exp")
    private String industryExp;

    @Column(name = "designation")
    private String designation;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

}
