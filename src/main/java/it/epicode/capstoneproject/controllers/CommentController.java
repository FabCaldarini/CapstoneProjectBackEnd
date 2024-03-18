package it.epicode.capstoneproject.controllers;

import it.epicode.capstoneproject.entities.Comic;
import it.epicode.capstoneproject.entities.Comment;
import it.epicode.capstoneproject.entities.User;
import it.epicode.capstoneproject.exceptions.CommentNotFoundException;
import it.epicode.capstoneproject.repositories.ComicRepository;
import it.epicode.capstoneproject.requestDTO.CommentDTO;
import it.epicode.capstoneproject.services.ComicService;
import it.epicode.capstoneproject.services.CommentService;
import it.epicode.capstoneproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comics/{comicId}/comments")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    private CommentService commentService;
    @Autowired
    private ComicService comicService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Comment> createComment(@PathVariable Long comicId, @RequestBody CommentDTO commentDto, Authentication authentication) {
        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // If the user is not authenticated, respond with 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extract the username from the authenticated user's details
        String username = authentication.getName();

        // Retrieve the user object based on the username
        Optional<User> userOptional = userService.getUserByUsername(username);
        Optional<Comic> comicOptional = comicService.getComicById(comicId);

        if (userOptional.isPresent() && comicOptional.isPresent()) {
            User user = userOptional.get();
            Comic comic = comicOptional.get();

            // Create the comment
            Comment comment = new Comment();
            comment.setText(commentDto.getText());
            comment.setCreatedAt(LocalDate.now()); // Set the current date
            comment.setUser(user); // Associate the user
            comment.setComic(comic); // Associate the comic

            // Save the prepared comment
            Comment savedComment = commentService.saveComment(comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } else {
            // If either the user or comic could not be found, respond with 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        commentService.deleteCommentById(id);
        String message = "Comment with ID " + id + " deleted successfully.";
        return ResponseEntity.ok().body(message);
    }

    // Endpoint to retrieve all comments for a given comic
    @GetMapping("")
    public ResponseEntity<List<Comment>> getAllCommentsForComic(@PathVariable Long comicId) {
        List<Comment> comments = commentService.getAllCommentsForComic(comicId);
        return ResponseEntity.ok(comments);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + id));
        return ResponseEntity.ok(comment);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment updatedComment) {
        Comment updated = commentService.updateComment(id, updatedComment);
        return ResponseEntity.ok(updated);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getId();
        } else {
            throw new RuntimeException("User details not found in authentication object");
        }
    }



}
