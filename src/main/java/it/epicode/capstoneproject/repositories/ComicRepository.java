package it.epicode.capstoneproject.repositories;

import it.epicode.capstoneproject.entities.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComicRepository extends JpaRepository<Comic, Long> {
    List<Comic> findByTitle(String title);
    List<Comic> findByAuthor(String author);


}
