package it.epicode.capstoneproject.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Define a ManyToOne relationship with Comic entity
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comic_id")
    @JsonBackReference
    private Comic comic;

    // Define a ManyToOne relationship with User entity
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private String text;

    @Column(name = "created_at")
    private LocalDate createdAt;




    // Constructors, getters, and setters
}
