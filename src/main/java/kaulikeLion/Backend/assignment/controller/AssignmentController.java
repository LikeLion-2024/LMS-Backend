package kaulikeLion.Backend.assignment.controller;

import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.dto.AssignmentDto;
import kaulikeLion.Backend.assignment.service.AssignmentService;
import kaulikeLion.Backend.assignment.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/assignment")
public class AssignmentController {

    private final AssignmentService assignmentService;
//    private final SubmissionService submissionService;

    // 과제 만들기 ing - 사진 (+파일..?)
    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }

    // 과제 만들기 end
    @PostMapping("/save")
    public String save(@ModelAttribute AssignmentDto assignmentDto) throws IOException {
        System.out.println("assignmentDto = " + assignmentDto);
        assignmentService.save(assignmentDto);
        return "index"; // 과제 목록 나옴
    }

    // 과제 목록 조회 - 홈 화면
    @GetMapping("/")
    public String findAll(Model model){ //데이터를 가져올때 model 객체 사용
        // DB에서 전체 게시글 데이터를 가져와서 index.html에 보여준다
        List<AssignmentDto> assignmentDtoList = assignmentService.findAll();
        model.addAttribute("assignmentList", assignmentDtoList);
        return "list";
    }
    // 페이징
//    @GetMapping("/paging") // 기본적으로 1페이지 보여줌
//    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model){
//        Page<AssignmentDto> boardList = boardService.paging(pageable);
//
//        int blockLimit = 3; // 보여지는 페이지 번호 개수
//        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
//        int endPage = Math.min((startPage + blockLimit - 1), boardList.getTotalPages()); // 3 6 9 ~~
//
//        model.addAttribute("boardList", boardList);
//        model.addAttribute("startPage", startPage);
//        model.addAttribute("endPage", endPage);
//        return "paging";
//    }

    // 과제 상세 조회 - 과제 제출란 있어야
    @GetMapping("/{id}")
    public String findById(@PathVariable(name = "id") Long id, Model model) { // 경로상에 있는 값 가져올땐 PathVariable사용
        // 해당 게시글의 조회수를 하나 올리기
        assignmentService.updateHits(id);
        // 게시글 데이터를 가져와서 detail.html에 출력
        AssignmentDto assignmentDto = assignmentService.findById(id);
        // 과제 제출 목록 가져오기
//        List<SubmissionDto> submissionDtoList = submissionService.findAll(id);
//        model.addAttribute("submissionList", submissionDtoList);
        model.addAttribute("assignment", assignmentDto);

        return "detail";
    }

    // 과제 수정 ing
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable(name = "id") Long id, Model model){
        AssignmentDto assignmentDto = assignmentService.findById(id);
        model.addAttribute("assignmentUpdate", assignmentDto);
        return "update";
    }

    // 과제 수정 end
    @PostMapping("/update")
    public String update(@ModelAttribute AssignmentDto assignmentDto, Model model){
        AssignmentDto assignment = assignmentService.update(assignmentDto);
        model.addAttribute("assignment", assignment);
        return "detail"; //수정 반영된 객체 가지고 화면에 띄움
    }

    // 과제 삭제
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Long id){
        assignmentService.delete(id);
        return "redirect:/assignment/"; // 띄어쓰기 하면 안됨
    }
}
