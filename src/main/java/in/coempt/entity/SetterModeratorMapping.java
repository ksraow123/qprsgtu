
package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_setter_moderator_mapping")
@Data
public class SetterModeratorMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="moderator_id")
    private Long  moderatorId;
    @Column(name="setter_id")
    private Long setterId ;
    @Column(name="subject_id")
    private Long subjectId;
    private String assigned_date, assigned_by;
}
