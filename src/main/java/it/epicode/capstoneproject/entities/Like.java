package it.epicode.capstoneproject.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comic_likes")
public class Like {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "comic_id")
        private Comic comic;

        @ManyToOne
        @JoinColumn(name = "user_id", unique = true)
        private User user;
    }
