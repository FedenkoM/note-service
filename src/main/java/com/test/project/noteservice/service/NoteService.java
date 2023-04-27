package com.test.project.noteservice.service;

import com.test.project.noteservice.dto.NoteDTO;
import com.test.project.noteservice.entity.Like;
import com.test.project.noteservice.entity.Note;
import com.test.project.noteservice.exception.NotFoundException;
import com.test.project.noteservice.repository.LikeRepository;
import com.test.project.noteservice.repository.NoteRepository;
import com.test.project.noteservice.repository.UserRepository;
import com.test.project.noteservice.utils.sequence.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final SequenceGeneratorService sequenceGenerator;

    public List<Note> getAllNotes() {
        log.info("Finding all notes...");
        return noteRepository.findAllByOrderByCreatedDateDesc();
    }

    public Note findNoteById(Long id) {
        return noteRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Note not found for id: %s", id)));
    }


    public Note createNote(NoteDTO noteDTO) {
        log.info("Creating new note...");
        var noteId = sequenceGenerator.generateSequence(Note.SEQUENCE_NAME);
        var note = new Note();
        note.setId(noteId);
        note.setContent(noteDTO.getContent());
        note.setUserId(noteDTO.getUserId());
        return noteRepository.save(note);
    }

    @Transactional
    public Note updateNote(Long noteId, Note note) {
        var existingNote = findNoteById(noteId);
        existingNote.setContent(note.getContent());
        existingNote.setLastModifiedDate(now());
        log.info("Note with id: {} was updated", noteId);
        return noteRepository.save(existingNote);
    }

    public void deleteNote(Long noteId) {
        noteRepository.deleteById(noteId);
        log.info("Note with id: {} was deleted", noteId);
    }

    @Transactional
    public void toggleLikeForAuthorizedUser(Long noteId, Long userId) {
        var likeOptional = likeRepository.findByNoteIdAndUserId(noteId, userId);
        var note = findNoteById(noteId);
        if (likeOptional.isPresent()) {
            likeRepository.delete(likeOptional.get());
            note.decrementLikes();
            log.info("User: {} dislike note!", userId);
        } else {
            var id = sequenceGenerator.generateSequence(Like.SEQUENCE_NAME);
            likeRepository.save(new Like(id, noteId, userId));
            note.incrementLikes();
            log.info("User: {} like note!", userId);
        }
        noteRepository.save(note);
    }

    public int countLikes(Long noteId) {
        return likeRepository.countByNoteId(noteId);
    }

    @Transactional
    public void toggleLike(Long noteId, String email) {
        var user = userRepository.findByEmail(email).orElseThrow();
        toggleLikeForAuthorizedUser(noteId, user.getId());
    }
}
