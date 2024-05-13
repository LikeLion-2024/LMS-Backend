package kaulikeLion.Backend.assignment.service;

import jakarta.transaction.Transactional;
import kaulikeLion.Backend.assignment.domain.Comment;
import kaulikeLion.Backend.assignment.domain.ViewCount;
import kaulikeLion.Backend.assignment.repository.AssignmentRepository;
import kaulikeLion.Backend.assignment.dto.AssignmentRequestDto.*;
import kaulikeLion.Backend.assignment.converter.AssignmentConverter;
import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.repository.CommentRepository;
import kaulikeLion.Backend.assignment.repository.ViewCountRepository;
import kaulikeLion.Backend.global.api_payload.ErrorCode;
import kaulikeLion.Backend.global.exception.GeneralException;
import kaulikeLion.Backend.global.s3.AmazonS3Manager;
import kaulikeLion.Backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CommentRepository commentRepository;
    private final ViewCountRepository viewCountRepository;
    private final AmazonS3Manager amazonS3Manager;

    @Transactional
    public Assignment createAssignment(AssignmentReqDto assignmentReqDto, String dirName, MultipartFile file, User user) throws IOException {
        Assignment assignment = AssignmentConverter.saveAssignment(assignmentReqDto, user);

        String uploadFileUrl = null;

        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (ObjectUtils.isEmpty(contentType)) { // 확장자명이 존재하지 않을 경우 취소 처리
                throw GeneralException.of(ErrorCode.INVALID_FILE_CONTENT_TYPE);
            }
            java.io.File uploadFile = amazonS3Manager.convert(file)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

            String fileName = dirName + amazonS3Manager.generateFileName(file);
            uploadFileUrl = amazonS3Manager.putS3(uploadFile, fileName);
        }

        assignment.setAssignmentImage(uploadFileUrl); // 사진 url 저장
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
            // 연관된 comment 엔티티들 삭제
            List<Comment> comments = assignment.getCommentList();
            commentRepository.deleteAll(comments);

            // 연관된 viewCount 엔티티 삭제
            List<ViewCount> viewCounts = viewCountRepository.findAllByAssignmentId(id);
            viewCountRepository.deleteAll(viewCounts);

            // 과제 삭제
            assignmentRepository.deleteById(id);
        }
        else throw new GeneralException(ErrorCode.BAD_REQUEST);
    }
}
