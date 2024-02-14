package kaulikeLion.Backend.assignment.dto;

import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.AssignmentPhoto;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor //모든 필드를 매개변수로 하는 생성자
public class AssignmentDto {
    private Long id;
    private String assignmentWriter;
    private String assignmentPass;
    private String assignmentTitle;
    private String assignmentContents;
    private int assignmentHits;
//    private LocalDateTime dueDateTime;

    private LocalDateTime assignmentCreatedAt;
    private LocalDateTime assignmentUpdatedAt;

    private List<MultipartFile> assignmentPhoto; // save.html -> controller 파일 담는 용도
    private List<String> originalPhotoName; // 원본 파일 이름
    private List<String> storedPhotoName; // 서버 저장용 파일 이름 -> 파일명 중복문제 해소
    private int photoAttached; // 사진 첨부 여부(첨부 1, 미첨부 0)

    public static AssignmentDto toAssignmentDto(Assignment assignment) {
        AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setId(assignment.getId());
        assignmentDto.setAssignmentWriter(assignment.getAssignmentWriter());
        assignmentDto.setAssignmentPass(assignment.getAssignmentPass());
        assignmentDto.setAssignmentTitle(assignment.getAssignmentTitle());
        assignmentDto.setAssignmentContents(assignment.getAssignmentContents());
        assignmentDto.setAssignmentHits(assignment.getAssignmentHits());
        assignmentDto.setAssignmentCreatedAt(assignment.getCreatedAt());
        assignmentDto.setAssignmentUpdatedAt(assignment.getUpdatedAt());

        if (assignment.getPhotoAttached() == 0){
            assignmentDto.setPhotoAttached(assignment.getPhotoAttached()); // 0
        } else{
            assignmentDto.setPhotoAttached(assignment.getPhotoAttached()); // 1

            List<String> originalPhotoNameList = new ArrayList<>();
            List<String> storedPhotoNameList = new ArrayList<>();
            // 파일 이름을 가져가야 함
            // originalFileName, storedFileName: board_file_table(BoardFile)
            // join
            // select * from board_table b, board_file_table bf where b.id=bf.board_id and where b.id=?
            for (AssignmentPhoto assignmentPhoto: assignment.getAssignmentPhotoList()) {
                originalPhotoNameList.add(assignmentPhoto.getOriginalPhotoName());
                storedPhotoNameList.add(assignmentPhoto.getStoredPhotoName());
            }
            assignmentDto.setOriginalPhotoName(originalPhotoNameList);
            assignmentDto.setStoredPhotoName(storedPhotoNameList);
        }
        return assignmentDto;
    }
}
