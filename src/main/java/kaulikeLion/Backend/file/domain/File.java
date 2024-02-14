//package kaulikeLion.Backend.file.domain;
//
//import jakarta.persistence.*;
//import kaulikeLion.Backend.assignment.domain.Assignment;
//import kaulikeLion.Backend.oauth.domain.User;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class File { // 제출 차일
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String originalFileName; // 파일의 원본 이름
//
//    @Column(nullable = false)
//    private String storedFileName; // 파일의 저장 이름
//
//    @Column(nullable = false)
//    private String fileType; // 파일 타입
//
//    @Column(nullable = false)
//    private LocalDateTime uploadedDateTime; // 업로드한 시간
//
//    @Lob
//    private byte[] data; // 파일 크기
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user; // 파일을 업로드한 사용자
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "assignment_id")
//    private Assignment assignment; // 해당 파일이 속한 과제
//}
