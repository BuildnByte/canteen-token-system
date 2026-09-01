package com.canteen.canteentokensystem.repository;

import com.canteen.canteentokensystem.model.Token;
import com.canteen.canteentokensystem.model.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByStudentId(Long studentId);

    List<Token> findByStatusIn(List<TokenStatus> statuses);

    @Query("SELECT t FROM Token t WHERE CAST(t.id AS string) LIKE %:query% " +
           "OR LOWER(t.student.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Token> searchByIdOrStudentName(@Param("query") String query);
}
