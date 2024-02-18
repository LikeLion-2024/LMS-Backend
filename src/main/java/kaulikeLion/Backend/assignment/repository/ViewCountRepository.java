package kaulikeLion.Backend.assignment.repository;


import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.ViewCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewCountRepository extends JpaRepository<ViewCount, Long> {
    Long countAllByAssignment(Assignment assignment);

    List<ViewCount> findAllByAssignmentId(Long id);
}
