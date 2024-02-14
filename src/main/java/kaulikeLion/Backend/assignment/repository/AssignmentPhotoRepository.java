package kaulikeLion.Backend.assignment.repository;

import kaulikeLion.Backend.assignment.domain.AssignmentPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentPhotoRepository extends JpaRepository<AssignmentPhoto, Long> {
}
