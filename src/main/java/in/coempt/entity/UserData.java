package in.coempt.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "tbl_appointments_bulk",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_user_subject_sets_role",
                columnNames = { "user_id", "subject_id", "no_of_sets", "role_id" }
        )
)
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int user_id;
    private int subject_id;
    private int no_of_sets;
    private int role_id;

    private String office_order_date;
    private String last_date_to_submit;
    private String college_id;
    private String curriculam;
    private String status_date;
    private String current_status;
    private String is_appointment_sent;
    private String appointment_sent_date;
}
