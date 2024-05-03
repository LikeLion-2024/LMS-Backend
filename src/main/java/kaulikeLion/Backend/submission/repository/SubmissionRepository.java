package kaulikeLion.Backend.submission.repository;

import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.submission.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findAllByAssignmentOrderByIdAsc(Assignment assignment);
    Submission findBySubmissionUrl(String submissionUrl);
}