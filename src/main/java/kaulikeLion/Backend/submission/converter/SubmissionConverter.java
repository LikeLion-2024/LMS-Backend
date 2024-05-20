package kaulikeLion.Backend.submission.converter;

import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.submission.domain.Submission;
import kaulikeLion.Backend.submission.dto.SubmissionResponseDto;
import kaulikeLion.Backend.user.domain.User;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
public class SubmissionConverter {
    public static Submission saveSubmission(String uploadSubmissionUrl, Assignment assignment, User user) {
        return Submission.builder()
                .user(user)
                .assignment(assignment)
                .submitter(user.getNickname())
                .isDeleted(0)
                .submissionUrl(uploadSubmissionUrl)
                .build();
    }

    public static SubmissionResponseDto.SimpleSubmissionDto simpleSubmissionDto(Submission submission) {
        return SubmissionResponseDto.SimpleSubmissionDto.builder()
                .id(submission.getId())
                .submitter(submission.getSubmitter())
                .isDeleted(submission.getIsDeleted())
                .submissionUrl(submission.getSubmissionUrl())
                .build();
    }

    public static SubmissionResponseDto.SubmissionListResDto submissionListResDto(List<Submission> submissions){
        List<SubmissionResponseDto.SimpleSubmissionDto> submissionDtos
                = submissions.stream().map(SubmissionConverter::simpleSubmissionDto).toList();

        return SubmissionResponseDto.SubmissionListResDto.builder()
                .submissionList(submissionDtos)
                .build();
    }
}
