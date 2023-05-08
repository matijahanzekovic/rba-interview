package hr.rba.interview.repository;

import hr.rba.interview.domain.PersonStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonStatusRepository extends JpaRepository<PersonStatus, Long> {

}
