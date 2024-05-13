package kaulikeLion.Backend.assignment.service;

import jakarta.transaction.Transactional;
import kaulikeLion.Backend.assignment.converter.CommentConverter;
import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.Comment;
import kaulikeLion.Backend.assignment.dto.CommentRequestDto;
import kaulikeLion.Backend.assignment.repository.AssignmentRepository;
import kaulikeLion.Backend.assignment.repository.CommentRepository;
import kaulikeLion.Backend.global.api_payload.ErrorCode;
import kaulikeLion.Backend.global.exception.GeneralException;
import kaulikeLion.Backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AssignmentRepository assignmentRepository;

    @Transactional
    public Comment createComment(CommentRequestDto.CommentDto commentReqDto, User user) {
        Assignment assignment = assignmentRepository.findById(commentReqDto.getAssignmentId())
                .orElseThrow(() -> GeneralException.of(ErrorCode.ASSIGNMENT_NOT_FOUND));
        Comment comment = CommentConverter.saveComment(commentReqDto, assignment, user);

        return commentRepository.save(comment);
    }

    @Transactional
    public Long deleteComment(Long id, User user){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> GeneralException.of(ErrorCode.COMMENT_NOT_FOUND));

        Long assignmentId = comment.getAssignment().getId();

        log.info("commentWriter: " + comment.getCommentWriter());
        log.info("Nickname: " + user.getNickname());
        if(Objects.equals(comment.getCommentWriter(), user.getNickname())){
            commentRepository.delete(comment);}

        return assignmentId;
    }

    public List<Comment> findAllByAssignmentId(Long id){
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ASSIGNMENT_NOT_FOUND));
        return commentRepository.findAllByAssignmentOrderByIdDesc(assignment);
    }

}
