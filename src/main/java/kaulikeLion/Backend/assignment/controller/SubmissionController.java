package kaulikeLion.Backend.assignment.controller;

import kaulikeLion.Backend.assignment.dto.SubmissionDto;
import kaulikeLion.Backend.assignment.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submission")
public class SubmissionController {

    private final SubmissionService submissionService;

    // 과제 제출하기 - 제목, 내용, 파일
    @PostMapping("/save")
    public ResponseEntity save(@ModelAttribute SubmissionDto submissionDto){
        System.out.println("submissionDto = " + submissionDto);
        Long saveResult = submissionService.save(submissionDto);
        if (saveResult != null){
            // 작성 성공하면 댓글 목록을 가져와서 리턴
            // 댓글 목록: 해당 게시글의 댓글 전체
            List<SubmissionDto> submissionDtoList = submissionService.findAll(submissionDto.getAssignmentId()); // 게시글 id가 기준이됨
            return new ResponseEntity<>(submissionDtoList, HttpStatus.OK);
        } else{
            return new ResponseEntity<>("해당 과제가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
    }
}
