package kaulikeLion.Backend.assignment.service;

import jakarta.transaction.Transactional;
import kaulikeLion.Backend.assignment.converter.SubmissionConverter;
import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.Submission;
import kaulikeLion.Backend.assignment.dto.SubmissionRequestDto.SubmissionDto;
import kaulikeLion.Backend.assignment.repository.AssignmentRepository;
import kaulikeLion.Backend.assignment.repository.SubmissionRepository;
import kaulikeLion.Backend.global.api_payload.ErrorCode;
import kaulikeLion.Backend.global.exception.GeneralException;
import kaulikeLion.Backend.oauth.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;

    @Transactional
    public Submission createSubmission(SubmissionDto submissionReqDto, User user) {
        Assignment assignment = assignmentRepository.findById(submissionReqDto.getAssignmentId())
                .orElseThrow(() -> GeneralException.of(ErrorCode.ASSIGNMENT_NOT_FOUND));
        Submission submission = SubmissionConverter.toSubmission(submissionReqDto, assignment);

        return submissionRepository.save(submission);
    }

    @Transactional
    public Long deleteSubmission(Long id){
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> GeneralException.of(ErrorCode.SUBMISSION_NOT_FOUND));

        Long assignmentId = submission.getAssignment().getId();
        submissionRepository.delete(submission);
        return assignmentId;
    }

    public List<Submission> findAllByAssignmentId(Long id){
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ASSIGNMENT_NOT_FOUND));
        return submissionRepository.findAllByAssignmentOrderByIdDesc(assignment);
    }

}
