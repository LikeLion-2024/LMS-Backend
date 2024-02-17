package kaulikeLion.Backend.assignment.converter;

import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.Submission;
import kaulikeLion.Backend.assignment.dto.AssignmentRequestDto.*;
import kaulikeLion.Backend.assignment.dto.SubmissionRequestDto.*;
import kaulikeLion.Backend.assignment.dto.AssignmentResponseDto.*;
import kaulikeLion.Backend.assignment.dto.SubmissionResponseDto.*;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import java.util.List;

@NoArgsConstructor
public class AssignmentConverter {
    public static Assignment saveAssignment(AssignmentReqDto assignment){
        return Assignment.builder()
                .assignmentWriter(assignment.getAssignmentWriter())
                .assignmentPass(assignment.getAssignmentPass())
                .assignmentTitle(assignment.getAssignmentTitle())
                .assignmentContents(assignment.getAssignmentContents())
                .dueDateTime(assignment.getDueDateTime())
                .assignmentHits(0L)
                .photoAttached(0)
                .build();
    }

    public static SimpleAssignmentDto simpleAssignmentDto(Assignment assignment) {
        return SimpleAssignmentDto.builder()
                .id(assignment.getId())
                .assignmentWriter(assignment.getAssignmentWriter())
                .assignmentPass(assignment.getAssignmentPass())
                .assignmentTitle(assignment.getAssignmentTitle())
                .assignmentContents(assignment.getAssignmentContents())
                .dueDateTime(assignment.getDueDateTime())
                .assignmentHits(assignment.getAssignmentHits())
                .photoAttached(assignment.getPhotoAttached())
                .assignmentCreatedAt(assignment.getCreatedAt())
                .build();
    }


    public static AssignmentListResDto assignmentListResDto(Page<Assignment> assignments) {
        List<SimpleAssignmentDto> assignmentDtos
                = assignments.stream().map(AssignmentConverter::simpleAssignmentDto).toList();

        return AssignmentListResDto.builder()
                .isLast(assignments.isLast())
                .isFirst(assignments.isFirst())
                .totalPage(assignments.getTotalPages())
                .totalElements(assignments.getTotalElements())
                .listSize(assignments.getSize())
                .assignmentList(assignmentDtos)
                .build();
    }

    public static DetailAssignmentDto detailAssignmentDto(Assignment assignment, List<Submission> submissions) {
        List<SimpleSubmissionDto> submissionDtos
                = submissions.stream().map(SubmissionConverter::simpleSubmissionDto).toList();

        return DetailAssignmentDto.builder()
                .id(assignment.getId())
                .assignmentWriter(assignment.getAssignmentWriter())
                .assignmentPass(assignment.getAssignmentPass())
                .assignmentTitle(assignment.getAssignmentTitle())
                .assignmentContents(assignment.getAssignmentContents())
                .dueDateTime(assignment.getDueDateTime())
                .assignmentHits(assignment.getAssignmentHits())
                .photoAttached(assignment.getPhotoAttached())
                .assignmentCreatedAt(assignment.getCreatedAt())
                .submissionList(submissionDtos)
                .build();
    }

}
