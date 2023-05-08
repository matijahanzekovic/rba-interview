package hr.rba.interview.repository;

import hr.rba.interview.domain.Person;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

  Optional<Person> findByOib(String oib);
  void deleteByOib(String oib);

}
