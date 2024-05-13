package kaulikeLion.Backend.assignment.domain;

import kaulikeLion.Backend.global.entity.BaseEntity;
import kaulikeLion.Backend.user.domain.User;
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
@Table(name = "assignment")
public class Assignment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String assignmentWriter;

    @Column
    private String assignmentPass;

    @Column
    private String assignmentTitle;

    @Column(length = 1000)
    private String assignmentContents;

    @Column
    private Long assignmentHits;

    @Column
    private LocalDateTime dueDateTime;

    @Column
    private String assignmentImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> commentList = new ArrayList<>();

    public void updateHits(Long assignmentHits) {
        this.assignmentHits = assignmentHits;
    }
}
