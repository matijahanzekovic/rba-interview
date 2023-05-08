package hr.rba.interview.repository;

import hr.rba.interview.domain.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentStatusRepository extends JpaRepository<DocumentStatus, Long> {

}
