package kaulikeLion.Backend.assignment.converter;

import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.Comment;
import kaulikeLion.Backend.assignment.dto.CommentRequestDto;
import kaulikeLion.Backend.assignment.dto.CommentResponseDto;
import kaulikeLion.Backend.assignment.dto.CommentResponseDto.*;
import kaulikeLion.Backend.user.domain.User;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class CommentConverter {

    public static Comment saveComment(CommentRequestDto.CommentDto comment, Assignment assignment, User user){
        return Comment.builder()
                .commentWriter(user.getNickname()) // 작성자
                .commentContents(comment.getCommentContents())
                .assignment(assignment)
                .build();
    }

    public static SimpleCommentDto simpleCommentDto(Comment comment) {
        return CommentResponseDto.SimpleCommentDto.builder()
                .id(comment.getId())
                .commentWriter(comment.getCommentWriter())
                .commentContents(comment.getCommentContents())
                .commentCreatedAt(comment.getCreatedAt())
                .build();
    }

    public static CommentListResDto commentListResDto(List<Comment> comments){
        List<SimpleCommentDto> commentDtos
                = comments.stream().map(CommentConverter::simpleCommentDto).toList();

        return CommentResponseDto.CommentListResDto.builder()
                .commentList(commentDtos)
                .build();
    }
}
