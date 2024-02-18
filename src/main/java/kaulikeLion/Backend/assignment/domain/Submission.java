package kaulikeLion.Backend.assignment.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kaulikeLion.Backend.assignment.dto.SubmissionResponseDto;
import kaulikeLion.Backend.global.entity.BaseEntity;
import kaulikeLion.Backend.oauth.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "submission")
public class Submission extends BaseEntity { // 과제 제출
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String submissionWriter;

    @Column
    private String submissionContents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 과제를 제출한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment; // 과제명

}
