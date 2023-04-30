package com.test.project.noteservice.controller;

import com.test.project.noteservice.dto.NoteDTO;
import com.test.project.noteservice.entity.Note;
import com.test.project.noteservice.exception.UnauthorizedException;
import com.test.project.noteservice.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public List<Note> findAllNotes() {
        return noteService.getAllNotes();
    }

    @GetMapping("/{id}")
    public Note findNoteById(@PathVariable Long id) {
        return noteService.findNoteById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note saveNote(@RequestBody NoteDTO note) {
        return noteService.createNote(note);
    }

    @Operation(summary = "Only for testing 'like' functionality for multiple users")
    @PostMapping("/like")
    public void toggleLike(@RequestParam Long noteId,
                           @RequestParam Long userId) {
        noteService.toggleLikeForAuthorizedUser(noteId, userId);
    }

    @PreAuthorize("hasAuthority('READ')")
    @Operation(summary = "Only authorized user allow",
               description = "Add/Remove like from note.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/{noteId}/likes")
    public void toggleLike(@PathVariable Long noteId, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Not authorized user!");
        }
        noteService.toggleLike(noteId, principal.getName());
    }
}
