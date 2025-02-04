package org.zerock.natureRent.service;

import org.zerock.natureRent.dto.NoteDTO;
import org.zerock.natureRent.entity.Member;
import org.zerock.natureRent.entity.Note;

import java.util.List;

public interface Noteservice {

    Long register(NoteDTO noteDTO);

    NoteDTO get(Long num);

    void modify(NoteDTO noteDTO);

    void remove(Long num);

    List<NoteDTO> getAllWithWriter(String writerEmail);


    default Note dtoToEntity(NoteDTO noteDTO){

        Note note = Note.builder()
                .num(noteDTO.getNum())
                .title(noteDTO.getTitle())
                .content(noteDTO.getContent())
                .writer(Member.builder().email(noteDTO.getWriterEmail()).build())
                .build();

        return note;
    }

    default NoteDTO entityToDTO(Note note){

        NoteDTO noteDTO = NoteDTO.builder()
                .num(note.getNum())
                .title(note.getTitle())
                .content(note.getContent())
                .writerEmail(note.getWriter().getEmail())
                .regDate(note.getRegDate())
                .modDate(note.getModDate())
                .build();

        return noteDTO;

    }

}




















