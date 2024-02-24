package kaulikeLion.Backend.assignment.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kaulikeLion.Backend.assignment.domain.Submission;
import kaulikeLion.Backend.assignment.domain.ViewCount;
import kaulikeLion.Backend.assignment.dto.AssignmentRequestDto;
import kaulikeLion.Backend.assignment.repository.AssignmentRepository;
import kaulikeLion.Backend.assignment.dto.AssignmentRequestDto.*;
import kaulikeLion.Backend.assignment.converter.AssignmentConverter;
import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.repository.SubmissionRepository;
import kaulikeLion.Backend.assignment.repository.ViewCountRepository;
import kaulikeLion.Backend.global.api_payload.ErrorCode;
import kaulikeLion.Backend.global.exception.GeneralException;
import kaulikeLion.Backend.oauth.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final ViewCountRepository viewCountRepository;

    @Transactional
    public Assignment createAssignment(AssignmentReqDto assignmentReqDto, User user) {
        Assignment assignment = AssignmentConverter.saveAssignment(assignmentReqDto, user);
        assignmentRepository.save(assignment);

        return assignment;
    }

    @Transactional
    public Assignment updateAssignment(Long id, DetailAssignmentReqDto detailAssignmentReqDto, User user) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ASSIGNMENT_NOT_FOUND));

        if(Objects.equals(assignment.getAssignmentPass(), detailAssignmentReqDto.getAssignmentPass())){
            // 업데이트할 내용 설정
            assignment.setAssignmentWriter(user.getNickname());
            assignment.setAssignmentTitle(detailAssignmentReqDto.getAssignmentTitle());
            assignment.setAssignmentContents(detailAssignmentReqDto.getAssignmentContents());
            assignment.setDueDateTime(detailAssignmentReqDto.getDueDateTime());
            assignment.setPhotoAttached(detailAssignmentReqDto.getPhotoAttached());

            assignmentRepository.save(assignment);

            return assignment;
        }
        return assignment;
    }

    @Transactional
    public Page<Assignment> convertAssignmentToPage(List<Assignment> assignments, Integer page, Integer pageSize){
        int start = page * pageSize;
        int end = Math.min(start + pageSize, assignments.size());
        start = Math.min(start, assignments.size() - 1);
        List<Assignment> pagedPlans = assignments.subList(start, end);

        return new PageImpl<>(pagedPlans, PageRequest.of(page, pageSize), assignments.size());
    }

    @Transactional
    public Assignment increaseViewCount(Assignment assignment) {
        viewCountRepository.save(ViewCount.builder().assignment(assignment).build());

        assignment.updateHits(viewCountRepository.countAllByAssignment(assignment));
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public List<Assignment> findAll() {
        return assignmentRepository.findAll();
    }

    @Transactional
    public Assignment findById(Long id){
        return assignmentRepository.findById(id)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ASSIGNMENT_NOT_FOUND));
    }

    @Transactional
    public void deleteAssignment(Long id, User user){
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ASSIGNMENT_NOT_FOUND));

        log.info("AssignmentWriter: " + assignment.getAssignmentWriter());
        log.info("Nickname: " + user.getNickname());

        if(Objects.equals(assignment.getAssignmentWriter(), user.getNickname())){
            // 연관된 Submission 엔티티들 삭제
            List<Submission> submissions = assignment.getSubmissionList();
            submissionRepository.deleteAll(submissions);

            // 연관된 viewCount 엔티티 삭제
            List<ViewCount> viewCounts = viewCountRepository.findAllByAssignmentId(id);
            viewCountRepository.deleteAll(viewCounts);

            // 과제 삭제
            assignmentRepository.deleteById(id);
        }
        else throw new GeneralException(ErrorCode.BAD_REQUEST);
    }
}
