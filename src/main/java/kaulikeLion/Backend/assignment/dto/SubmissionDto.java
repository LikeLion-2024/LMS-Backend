package kaulikeLion.Backend.assignment.dto;

import kaulikeLion.Backend.assignment.domain.Submission;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class SubmissionDto {
    private Long id;
    private String submissionWriter;
    private String submissionContents;
    private Long assignmentId;
    private LocalDateTime submissionCreatedAt;

    public static SubmissionDto toSubmissionDto(Submission submission, Long boardId) {
        SubmissionDto submissionDto = new SubmissionDto();
        submissionDto.setId(submission.getId());
        submissionDto.setSubmissionWriter(submission.getSubmissionWriter());
        submissionDto.setSubmissionContents(submission.getSubmissionContents());
        submissionDto.setSubmissionCreatedAt(submission.getAssignment().getCreatedAt());
        submissionDto.setAssignmentId(boardId);
        // commentDto.setBoardId(comment.getBoard().getId()); // Service 메서드에 @Transactional 붙여야 함
        return submissionDto;
    }
}
