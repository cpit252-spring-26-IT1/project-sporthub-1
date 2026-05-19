package sa.edu.kau.fcit.cpit252.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sa.edu.kau.fcit.cpit252.project.entities.BookingRecord;

@Repository
public interface BookingRepository extends JpaRepository<BookingRecord, Long> {
    List<BookingRecord> findByFieldNameAndPlayDate(String fieldName, String playDate);
    boolean existsByFieldNameAndPlayDateAndPlayTime(String fieldName, String playDate, String playTime);
}