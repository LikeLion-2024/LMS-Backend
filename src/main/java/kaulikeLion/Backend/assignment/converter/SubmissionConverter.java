package kaulikeLion.Backend.assignment.converter;

import io.swagger.v3.oas.annotations.media.Schema;
import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.Submission;
import kaulikeLion.Backend.assignment.dto.AssignmentRequestDto;
import kaulikeLion.Backend.assignment.dto.AssignmentResponseDto;
import kaulikeLion.Backend.assignment.dto.SubmissionRequestDto;
import kaulikeLion.Backend.assignment.dto.SubmissionRequestDto.SubmissionDto;
import kaulikeLion.Backend.assignment.dto.SubmissionResponseDto;
import kaulikeLion.Backend.assignment.dto.SubmissionResponseDto.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
public class SubmissionConverter {

    public static Submission toSubmission(SubmissionDto submission, Assignment assignment){
        return Submission.builder()
                .submissionWriter(submission.getSubmissionWriter())
                .submissionContents(submission.getSubmissionContents())
                .assignment(assignment)
                .build();
    }

    public static SimpleSubmissionDto simpleSubmissionDto(Submission submission) {
        return SubmissionResponseDto.SimpleSubmissionDto.builder()
                .id(submission.getId())
                .submissionWriter(submission.getSubmissionWriter())
                .submissionContents(submission.getSubmissionContents())
                .submissionCreatedAt(submission.getCreatedAt())
                .build();
    }

    public static SubmissionListResDto submissionListResDto(List<Submission> submissions){
        List<SimpleSubmissionDto> submissionDtos
                = submissions.stream().map(SubmissionConverter::simpleSubmissionDto).toList();

        return SubmissionResponseDto.SubmissionListResDto.builder()
                .submissionList(submissionDtos)
                .build();
    }

}
