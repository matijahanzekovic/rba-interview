package hr.rba.interview.repository;

import hr.rba.interview.domain.Document;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DocumentRepository extends JpaRepository<Document, Long> {

  Optional<Document> findByPersonOib(String oib);

  @Modifying
  @Query("""
        DELETE
        FROM Document d
        WHERE d.location LIKE CONCAT('%', :processedDocumentLocationPath, '%')
  """)
  void deleteProcessedDocuments(String processedDocumentLocationPath);

}
