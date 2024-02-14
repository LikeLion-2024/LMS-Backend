package kaulikeLion.Backend.assignment.domain;

import jakarta.persistence.*;
import kaulikeLion.Backend.global.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "assignment_photo_table")
public class AssignmentPhoto extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalPhotoName;

    @Column
    private String storedPhotoName;

    @ManyToOne(fetch = FetchType.LAZY) // 필요한 상황에 사용할 수 있음
    @JoinColumn(name = "assignment_id") // db에 들어가는 column 이름
    private Assignment assignment;

    public static AssignmentPhoto toAssignmentPhotoEntity(Assignment assignment, String originalPhotoName, String storedPhotoName) {
        AssignmentPhoto assignmentPhoto = new AssignmentPhoto();
        assignmentPhoto.setOriginalPhotoName(originalPhotoName);
        assignmentPhoto.setStoredPhotoName(storedPhotoName);
        assignmentPhoto.setAssignment(assignment); // 부모 엔티티를 넣어줘야함
        return assignmentPhoto;
    }
}
