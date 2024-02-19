package kaulikeLion.Backend.file.repository;

import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByAssignmentOrderByIdAsc(Assignment assignment);
    File findByFileUrl(String fileUrl);
}