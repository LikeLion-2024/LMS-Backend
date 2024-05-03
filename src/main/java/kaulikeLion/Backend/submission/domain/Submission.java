package kaulikeLion.Backend.submission.domain;

import jakarta.persistence.*;
import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.global.entity.BaseEntity;
import kaulikeLion.Backend.oauth.domain.User;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "submission")
public class Submission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String submitter;

    @Column
    private String submissionUrl;

    @Column
    private Integer isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
