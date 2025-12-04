package com.khanh.exercise_jwts.repository;

import com.khanh.exercise_jwts.entity.Token;
import com.khanh.exercise_jwts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    List<Token> findAllByUserAndRevokedIsFalseAndExpiredIsFalse(User user);

    @Modifying
    @Query("update Token t set t.revoked = true where t.user = ?1")
    void revokeAllByUser(User user);

    // Delete all tokens owned by a user (used before deleting the user)
    void deleteAllByUser(User user);
}
