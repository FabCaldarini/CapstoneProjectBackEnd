package it.epicode.capstoneproject.repositories;


import it.epicode.capstoneproject.entities.Comic;
import it.epicode.capstoneproject.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByComicId(Long comicId);



}
