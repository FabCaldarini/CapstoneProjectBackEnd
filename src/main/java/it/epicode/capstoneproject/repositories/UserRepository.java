package it.epicode.capstoneproject.repositories;

import it.epicode.capstoneproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameAndPassword(String username, String password);

    Optional<User> findByUsername(String username);
@Query("select e.email from User e")
    List<Object> getAllEmails();
@Query("select u.username from User u")
    List<String> getAllUsernames();


}
