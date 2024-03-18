package it.epicode.capstoneproject.services;

import it.epicode.capstoneproject.entities.Comic;
import it.epicode.capstoneproject.entities.Comment;
import it.epicode.capstoneproject.entities.User;
import it.epicode.capstoneproject.exceptions.ComicNotFoundException;
import it.epicode.capstoneproject.exceptions.CommentNotFoundException;
import it.epicode.capstoneproject.exceptions.UnauthorizedException;
import it.epicode.capstoneproject.repositories.ComicRepository;
import it.epicode.capstoneproject.repositories.CommentRepository;
import it.epicode.capstoneproject.repositories.UserRepository;
import it.epicode.capstoneproject.requestDTO.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {


    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private ComicRepository comicRepository;
    @Autowired
    private ComicService comicService;
    @Autowired
    private UserRepository userRepository;


    public List<Comment> getAllCommentsForComic(Long comicId) {
        return commentRepository.findAllByComicId(comicId);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    private final JwtTokenService jwtTokenService;

    public CommentService(JwtTokenService jwtTokenService, CommentRepository commentRepository) {
        this.jwtTokenService = jwtTokenService;
        this.commentRepository = commentRepository;
    }
    public Comment createComment(Long comicId, Comment comment) {
        // Retrieve the comic by its ID
        Optional<Comic> optionalComic = comicService.findById(comicId);

        // Check if the comic exists
        if (optionalComic.isPresent()) {
            Comic comic = optionalComic.get();

            // Set the comic for the comment
            comment.setComic(comic);

            // Save the comment
            return commentRepository.save(comment);
        } else {
            throw new ComicNotFoundException("Comic not found with ID: " + comicId);
        }
    }
    @Transactional
    public void deleteComment(Long commentId, Long userId) throws UnauthorizedException {
        // Check if the comment exists
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ComicNotFoundException("Comment not found"));

        // Check if the comment belongs to the user
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        // Delete the comment
        commentRepository.delete(comment);
    }


    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }


    public Comment updateComment(Long id, Comment updatedComment) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + id));

        // Set the text field
        existingComment.setText(updatedComment.getText());
        // You may need to update other fields if necessary

        // Set createdAt only if it's null in the updatedComment
        if (existingComment.getCreatedAt() == null) {
            existingComment.setCreatedAt(LocalDate.now()); // or LocalDateTime.now() depending on your field type
        }

        // Save the existing comment
        return commentRepository.save(existingComment);
    }
    public void deleteCommentById(Long id) {
        commentRepository.deleteById(id);
    }



}
