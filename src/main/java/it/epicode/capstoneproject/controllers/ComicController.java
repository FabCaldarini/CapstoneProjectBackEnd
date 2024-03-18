package it.epicode.capstoneproject.controllers;

import com.cloudinary.Cloudinary;
import it.epicode.capstoneproject.entities.Comic;
import it.epicode.capstoneproject.entities.User;
import it.epicode.capstoneproject.exceptions.ComicNotFoundException;
import it.epicode.capstoneproject.exceptions.NotFoundException;
import it.epicode.capstoneproject.exceptions.UnauthorizedException;
import it.epicode.capstoneproject.repositories.ComicRepository;
import it.epicode.capstoneproject.services.ComicService;
import it.epicode.capstoneproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comics")
public class ComicController {

    @Autowired
    private ComicService comicService;

    @Autowired
    private UserService userService;
    private final ComicRepository comicRepository;
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    public ComicController(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }



    @PostMapping("/createComic")
    public ResponseEntity<Comic> createComic(@RequestBody Comic newComic) {
        Comic createdComic = comicService.createComic(newComic);
        return new ResponseEntity<>(createdComic, HttpStatus.CREATED);
    }
    @GetMapping("/getAllComics")
    public ResponseEntity<List<Comic>> getAllComics() {
        List<Comic> comics = comicService.getAllComicsWithComments();
        return new ResponseEntity<>(comics, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getComicById(@PathVariable Long id) {
        try {
            Comic comic = comicService.getComicById(id)
                    .orElseThrow(() -> new ComicNotFoundException("Comic not found with ID: " + id));
            return ResponseEntity.ok(comic);
        } catch (ComicNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comic with id: " + id + " not found");
        }
    }

    @DeleteMapping("/{id}")
    public String deleteComic(@PathVariable Long id) {
        comicService.deleteComicById(id);
        return "Comic deleted successfully";
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Comic> updateComicDetails(@PathVariable Long id, @RequestBody Comic updatedComicDetails) {
        Comic updatedComic = comicService.updateComic(id, updatedComicDetails);
        return ResponseEntity.ok(updatedComic);
    }
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeComic(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Long userId = userService.findUserIdByUsername(username);
        comicService.addLikeToComic(id, userId);

        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{comicId}/like")
    public ResponseEntity<?> removeLikeFromComic(@PathVariable Long comicId) {
        // Retrieve authentication from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            // If authentication is null or represents an anonymous user, handle it accordingly
            System.out.println("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            // User is authenticated, proceed with the logic
            System.out.println("User is authenticated");

            // Continue with the rest of your logic for removing the like
            Long userId = getUserIdFromAuthentication(authentication);
            System.out.println("Authenticated userId: " + userId);

            boolean isAllowedToRemove = comicService.isUserAllowedToRemoveLike(comicId, userId);

            if (isAllowedToRemove) {
                Comic updatedComic = comicService.removeLikeFromComic(comicId, userId);
                System.out.println("Like removed from comicId: " + comicId + " by userId: " + userId);
                return ResponseEntity.ok(updatedComic);
            } else {
                System.out.println("Unauthorized to remove like from comicId: " + comicId + " by userId: " + userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            Long userId = user.getId(); // Assuming the user ID is stored in a field called "id"
            System.out.println("User ID from UserDetails: " + userId);
            return userId;
        } else {
            System.out.println("Principal is not an instance of User");
            return null;
        }
    }


    // IMAGE METHOD ----------------------------------------------------------------
    @PatchMapping("/{id}/upload")
    @PreAuthorize("hasAuthority('USER')")
    public Optional<Comic> upload(@RequestParam("file") MultipartFile file, @PathVariable Long id)
            throws IOException, UnauthorizedException, NotFoundException {
        Optional<Comic> comicOptional = comicService.findById(id);
        Comic comic = comicOptional.orElseThrow(() -> new NotFoundException("Comic not found with id: " + id));
        String url = (String) cloudinary.uploader().upload(file.getBytes(), new HashMap<>()).get("url");
        return comicService.uploadImage(url, comic);
    }
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> deleteImage(@PathVariable Long id) throws NotFoundException {
        Optional<Comic> comicOptional = comicService.findById(id);
        Comic comic = comicOptional.orElseThrow(() -> new NotFoundException("Comic not found with id: " + id));

        boolean imageDeleted = comicService.deleteImage(comic.getId());

        if (imageDeleted) {
            return ResponseEntity.ok("Image deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image");
        }
    }







}
