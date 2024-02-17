package kaulikeLion.Backend.assignment.domain;

import kaulikeLion.Backend.global.entity.BaseEntity;
import kaulikeLion.Backend.oauth.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assignment_table")
public class Assignment extends BaseEntity { // 과제. 게시판 같은 느낌
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false) // 크기는 20, not null
    private String assignmentWriter;

    @Column // default: 크기 255, null 가능
    private String assignmentPass;

    @Column
    private String assignmentTitle;

    @Column(length = 500)
    private String assignmentContents;

    @Column
    private Long assignmentHits;

    @Column
    private LocalDateTime dueDateTime;

    @Column
    private int photoAttached; // 1 or 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 과제를 연 사람

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    // 과제 삭제시 제출한 과제들도 사라짐
    private List<Submission> submissionList = new ArrayList<>();

    public void updateHits(Long assignmentHits) {
        this.assignmentHits = assignmentHits;
    }
}
