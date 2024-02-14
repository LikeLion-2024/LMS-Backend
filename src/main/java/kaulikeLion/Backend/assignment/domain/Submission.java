package kaulikeLion.Backend.assignment.domain;

import jakarta.persistence.*;
import kaulikeLion.Backend.assignment.dto.SubmissionDto;
import kaulikeLion.Backend.oauth.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "submission_table")
public class Submission { // 과제 제출
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String submissionWriter;

    @Column
    private String submissionContents;

//    private String submissionTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 과제를 제출한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment; // 과제명

    public static Submission toSaveEntity(SubmissionDto submissionDto, Assignment assignment){
        Submission submission = new Submission();
        submission.setSubmissionWriter(submissionDto.getSubmissionWriter());
        submission.setSubmissionContents(submissionDto.getSubmissionContents());
        submission.setAssignment(assignment); // 과제번호로 조회한 부모 엔티티
        return submission;
    }
}
