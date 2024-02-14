package kaulikeLion.Backend.assignment.service;

import jakarta.transaction.Transactional;
import kaulikeLion.Backend.assignment.domain.AssignmentPhoto;
import kaulikeLion.Backend.assignment.dto.AssignmentDto;
import kaulikeLion.Backend.assignment.repository.AssignmentPhotoRepository;
import kaulikeLion.Backend.assignment.repository.AssignmentRepository;
import kaulikeLion.Backend.global.exception.GeneralException;
import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.global.api_payload.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentPhotoRepository assignmentPhotoRepository;

    // 과제 만들기
    @Transactional
    public void save(AssignmentDto assignmentDto) throws IOException {
        // 파일 첨부 여부에 따라 로직 분리
        if (assignmentDto.getAssignmentPhoto().isEmpty()){
            // 첨부 파일이 없음
            Assignment assignment = Assignment.toSaveEntity(assignmentDto);
            assignmentRepository.save(assignment);
        } else{
            // 첨부 파일이 있음
            /*
                1. Dto에 담기 파일을 꺼냄
                2. 파일의 이름 가져옴
                3. 서버 저장용 이름을 만듦
                // 내사진.jpa => 난수_내사진.jpg
                4. 저장 경로 설정
                5. 해당 경로에 파일 저장
                6. board_table에 해당 데이터 save 처리
                7. board_file_table에 해당 데이터 save 처리
             */
            Assignment assignmentEntity = Assignment.toSavePhotoEntity(assignmentDto);
            Long savedId = assignmentRepository.save(assignmentEntity).getId();
            Assignment assignment = assignmentRepository.findById(savedId).get();

            for (MultipartFile assignmentPhoto: assignmentDto.getAssignmentPhoto()) {
//                MultipartFile boardFile = boardDto.getBoardFile(); // 1.
                String originalPhotoname = assignmentPhoto.getOriginalFilename(); // 2.
                String storedPhotoName = System.currentTimeMillis() + "_" + originalPhotoname; // 3.
                String savePath = "C:/Users/양지원/Desktop/likelion_springboot_img/" + storedPhotoName; //4. C:/Users/양지원/OneDrive/바탕 화면/likelion_assignment_img//338493020_내사진.jpg
                assignmentPhoto.transferTo(new File(savePath)); // 5.

                AssignmentPhoto assignmentPhotoEntity = AssignmentPhoto.toAssignmentPhotoEntity(assignment, originalPhotoname, storedPhotoName);
                assignmentPhotoRepository.save(assignmentPhotoEntity);
            }
        }
    }

    // 과제 목록 조회 - 홈 화면
    @Transactional
    public List<AssignmentDto> findAll() {
        List<Assignment> assignmentList = assignmentRepository.findAll();
        List<AssignmentDto> assignmentDtoList = new ArrayList<>();
        for(Assignment assignment: assignmentList) {
            assignmentDtoList.add(AssignmentDto.toAssignmentDto(assignment));
        }
        return assignmentDtoList;
    }

    // 과제 수정
    public AssignmentDto update(AssignmentDto assignmentDto) {
        Assignment assignment = Assignment.toUpdateEntity(assignmentDto);
        assignmentRepository.save(assignment);
        return findById(assignmentDto.getId());
    }

    // 과제 삭제
    public void delete(Long id) {
        assignmentRepository.deleteById(id);
    }


    // 과제 게시물 조회수 올리기
    @Transactional
    public void updateHits(Long id) {
        assignmentRepository.updateHits(id);
    }

    // id로 게시물 데이터 찾기
        @Transactional
    public AssignmentDto findById(Long id) {
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(id);
        if (optionalAssignment.isPresent()){
            Assignment assignment = optionalAssignment.get();
            AssignmentDto assignmentDto = AssignmentDto.toAssignmentDto(assignment);
            return assignmentDto;
        } else return null;
    }
    /*
    public Assignment findById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorCode.ASSIGNMENT_NOT_FOUND));
    }
     */
}
