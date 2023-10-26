package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentStatus;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    Optional<Comment> findByIdAndStatus(Long id, CommentStatus status);

    Optional<Comment> findByIdAndAuthorIdAndStatus(Long id, Long authorId, CommentStatus status);

    @Modifying
    @Query("UPDATE Comment cmt SET cmt.status = :status WHERE cmt.id = :commentId")
    void updateCommentStatus(@Param("commentId") Long commentId, @Param("status") CommentStatus status);
}