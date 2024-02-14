package kaulikeLion.Backend.assignment.repository;

import kaulikeLion.Backend.assignment.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // update board_table set board_hits=board_hits+1 where id=?
    @Modifying
    @Query(value = "update Assignment b set b.assignmentHits=b.assignmentHits+1 where b.id=:id")
    void updateHits(@Param("id") Long id);
}
