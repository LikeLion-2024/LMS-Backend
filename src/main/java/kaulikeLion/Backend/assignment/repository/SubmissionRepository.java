package kaulikeLion.Backend.assignment.repository;

import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findAllByAssignmentOrderByIdDesc(Assignment assignment);
}
