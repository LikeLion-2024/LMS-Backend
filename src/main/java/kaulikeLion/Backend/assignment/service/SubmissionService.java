package kaulikeLion.Backend.assignment.service;

import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.Submission;
import kaulikeLion.Backend.assignment.dto.SubmissionDto;
import kaulikeLion.Backend.assignment.repository.AssignmentRepository;
import kaulikeLion.Backend.assignment.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;

    public Long save(SubmissionDto submissionDto) {
        // 부모엔티티(Board) 조회
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(submissionDto.getAssignmentId());
        if (optionalAssignment.isPresent()){
            Assignment assignment = optionalAssignment.get();
            Submission submission = Submission.toSaveEntity(submissionDto, assignment);
            return submissionRepository.save(submission).getId();
        } else {
            return null;
        }
    }

    public List<SubmissionDto> findAll(Long boardId) {
        // select * from comment_table where board_id =? order by id desc; // 최근 작성한 댓글이 먼저 보이도록
        Assignment assignment = assignmentRepository.findById(boardId).get();
        List<Submission> submissionList = submissionRepository.findAllByAssignmentOrderByIdDesc(assignment);
        // EntityList -> DtoList
        List<SubmissionDto> submissionDtoList = new ArrayList<>();
        for (Submission submission: submissionList){
            SubmissionDto submissionDto = SubmissionDto.toSubmissionDto(submission, boardId);
            submissionDtoList.add(submissionDto);
        }
        return submissionDtoList;
    }
}
