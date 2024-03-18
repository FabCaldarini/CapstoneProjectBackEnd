package it.epicode.capstoneproject.services;

import it.epicode.capstoneproject.entities.Comic;
import it.epicode.capstoneproject.entities.User;
import it.epicode.capstoneproject.exceptions.ComicNotFoundException;
import it.epicode.capstoneproject.exceptions.NotFoundException;
import it.epicode.capstoneproject.exceptions.UserNotFoundException;
import it.epicode.capstoneproject.repositories.ComicRepository;
import it.epicode.capstoneproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComicService {
    @Autowired
    private ComicRepository comicRepository;
    @Autowired
    private UserRepository userRepository;
    private  UserService userService;

    public Comic publishComic(Comic comic) {
        return comicRepository.save(comic);
    }

    public Comic createComic(Comic comic) {
        return comicRepository.save(comic);
    }

    public List<Comic> getAllComics() {
        return comicRepository.findAll();
    }

    public Optional<Comic> getComicById(Long id) {
        return comicRepository.findById(id);
    }

    public void deleteComicById(Long id) {
        comicRepository.deleteById(id);

    }

    public void deleteComic(Long id) {
        Comic existingComic = comicRepository.findById(id)
                .orElseThrow(() -> new ComicNotFoundException("Comic not found with ID: " + id));

        comicRepository.delete(existingComic);
    }

    public Comic updateComic(Long id, Comic updatedComic) {
        Comic existingComic = comicRepository.findById(id)
                .orElseThrow(() -> new ComicNotFoundException("Comic not found with ID: " + id));
        existingComic.setTitle(updatedComic.getTitle());
        existingComic.setAuthor(updatedComic.getAuthor());
        existingComic.setPublicationDate(updatedComic.getPublicationDate());
        existingComic.setImageUrl(updatedComic.getImageUrl());

        return comicRepository.save(existingComic);
    }

    public List<Comic> getComicsByAuthor(String author) {
        return comicRepository.findByAuthor(author);
    }

    public List<Comic> getComicsByTitle(String title) {
        return comicRepository.findByTitle(title);
    }

    public Comic addLikeToComic(Long comicId, Long userId) {
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new ComicNotFoundException("Comic not found with ID: " + comicId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        comic.getLikes().add(user);

        return comicRepository.save(comic);
    }

    public Comic removeLikeFromComic(Long comicId, Long userId) {
        // Log the start of the method
        System.out.println("Removing like from comic...");

        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new ComicNotFoundException("Comic not found with ID: " + comicId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        comic.getLikes().remove(user);

        // Log the evaluation of isAllowedToRemove
        boolean isAllowedToRemove = isUserAllowedToRemoveLike(comicId, userId);
        System.out.println("isAllowedToRemove: " + isAllowedToRemove);

        Comic savedComic = comicRepository.save(comic);

        // Log the end of the method
        System.out.println("Like removed successfully.");

        return savedComic;
    }
    public Long findUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return user.getId();
    }
    public boolean isUserAllowedToRemoveLike(Long comicId, Long userId) {
        Optional<Comic> optionalComic = comicRepository.findById(comicId);
        if (optionalComic.isPresent()) {
            Comic comic = optionalComic.get();
            List<User> likesList = comic.getLikes();
            Set<User> likes = new HashSet<>(likesList);
            for (User user : likes) {
                if (user.getId().equals(userId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<Comic> findById(Long id) {
        return comicRepository.findById(id);
    }



    public List<Comic> getAllComicsWithComments() {
        return comicRepository.findAll();
    }


    public Optional<Comic> uploadImage(String imageUrl, Comic comic){
        comic.setImageUrl(imageUrl);
        return Optional.of(comicRepository.save(comic));
    }


    @Transactional
    public boolean deleteImage(Long comicId) throws NotFoundException {
        // Retrieve the Comic entity from the database
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new NotFoundException("Comic not found with id: " + comicId));

        // Get the URL of the image
        String imageUrl = comic.getImageUrl();

        // Perform the actual deletion of the image from your storage system
        // Here you should write code to delete the image file or its reference
        if (imageUrl != null) {
            File imageFile = new File(imageUrl);
            if (imageFile.exists()) {
                boolean deleted = imageFile.delete();
                if (!deleted) {
                    // Handle the case where deletion fails
                    return false;
                }
            }
        }

        // Clear the imageUrl field in the Comic entity
        comic.setImageUrl(null);

        // Save the changes to the Comic entity in the database
        comicRepository.save(comic);

        return true; // Deletion successful
    }
}


