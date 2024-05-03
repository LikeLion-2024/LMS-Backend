package kaulikeLion.Backend.assignment.domain;

import jakarta.persistence.*;
import kaulikeLion.Backend.global.entity.BaseEntity;
import kaulikeLion.Backend.oauth.domain.User;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class Comment extends BaseEntity { // 과제 제출
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String commentWriter;

    @Column
    private String commentContents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 과제를 제출한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment; // 과제명

}
