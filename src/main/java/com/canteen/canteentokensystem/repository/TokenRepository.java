package com.canteen.canteentokensystem.repository;

import com.canteen.canteentokensystem.model.Token;
import com.canteen.canteentokensystem.model.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    /** All orders by a specific student (for "My Orders" page). */
    List<Token> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Token> findByStatusIn(List<TokenStatus> statuses);

    /** Global queue: orders not yet claimed by any staff, ordered oldest-first. */
    @Query("SELECT t FROM Token t WHERE t.acceptedBy IS NULL AND t.status = 'PENDING' ORDER BY t.createdAt ASC")
    List<Token> findUnacceptedOrders();

    /** A specific staff member's own queue (accepted by them, not yet collected). */
    @Query("SELECT t FROM Token t WHERE t.acceptedBy.id = :staffId AND t.status <> 'COLLECTED' ORDER BY t.createdAt ASC")
    List<Token> findByAcceptedByIdAndNotCollected(@Param("staffId") Long staffId);

    /** Admin: all active orders (accepted + unaccepted, not COLLECTED). */
    @Query("SELECT t FROM Token t WHERE t.status <> 'COLLECTED' ORDER BY t.createdAt ASC")
    List<Token> findAllActiveOrders();

    /** How many orders each staff member has accepted. */
    @Query("SELECT t.acceptedBy.id, t.acceptedBy.name, COUNT(t) " +
           "FROM Token t WHERE t.acceptedBy IS NOT NULL GROUP BY t.acceptedBy.id, t.acceptedBy.name")
    List<Object[]> countByStaff();

    /** How many orders each staff has fully completed (COLLECTED). */
    @Query("SELECT t.acceptedBy.id, t.acceptedBy.name, COUNT(t) " +
           "FROM Token t WHERE t.acceptedBy IS NOT NULL AND t.status = 'COLLECTED' " +
           "GROUP BY t.acceptedBy.id, t.acceptedBy.name")
    List<Object[]> countCompletedByStaff();

    @Query("SELECT t FROM Token t WHERE CAST(t.id AS string) LIKE %:query% " +
           "OR LOWER(t.student.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Token> searchByIdOrStudentName(@Param("query") String query);

    /** Legacy alias kept for backward compat. */
    default List<Token> findByStudentId(Long studentId) {
        return findByStudentIdOrderByCreatedAtDesc(studentId);
    }
}
