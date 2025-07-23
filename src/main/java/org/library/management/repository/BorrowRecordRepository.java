package org.library.management.repository;

import org.library.management.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    Optional<BorrowRecord> findByBookIdAndMemberId(Long bookId, Long memberId);

    List<BorrowRecord> findByMemberId(Long memberId);

    List<BorrowRecord> findByFineAmountGreaterThan(BigDecimal amount);
}
