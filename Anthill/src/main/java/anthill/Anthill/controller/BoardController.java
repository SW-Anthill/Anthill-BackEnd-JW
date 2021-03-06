package anthill.Anthill.controller;

import anthill.Anthill.dto.board.*;
import anthill.Anthill.dto.common.BasicResponseDTO;
import anthill.Anthill.service.BoardService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;

    private final static String FAIL = "failure";
    private final static String SUCCESS = "success";

    @GetMapping("/{boardid}")
    public ResponseEntity<BasicResponseDTO> select(@PathVariable("boardid") Long boardId) {

        BoardResponseDTO boardResponseDTO = boardService.select(boardId);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(makeSelectResponseDTO(SUCCESS, boardResponseDTO));

    }

    @GetMapping({"/page/{pagingid}"})
    public ResponseEntity<BasicResponseDTO> paging(@PathVariable("pagingid") Integer pagingId) {

        Page<BoardPagingDTO> resultPage = boardService.paging(pagingId - 1);

        if (pagingId > resultPage.getTotalPages()) {
            throw new IllegalStateException();
        }

        return ResponseEntity.status(HttpStatus.OK)
                             .body(makeSelectResponseDTO(SUCCESS, resultPage));
    }

    @PostMapping
    public ResponseEntity<BasicResponseDTO> posting(@Valid @RequestBody BoardRequestDTO boardRequestDTO) {

        boardService.posting(boardRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(makeBasicResponseDTO(SUCCESS));

    }

    @PutMapping("/{boardid}")
    public ResponseEntity<BasicResponseDTO> update(@RequestBody BoardUpdateDTO boardUpdateDTO) throws Exception {
        boardService.changeInfo(boardUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK)
                             .body(makeBasicResponseDTO(SUCCESS));
    }


    @DeleteMapping("/{boardid}")
    public ResponseEntity<BasicResponseDTO> delete(@RequestBody BoardDeleteDTO boardDeleteDTO) throws Exception {
        boardService.delete(boardDeleteDTO);
        return ResponseEntity.status(HttpStatus.OK)
                             .body(makeBasicResponseDTO(SUCCESS));
    }


    private BasicResponseDTO makeBasicResponseDTO(String message) {
        return BasicResponseDTO.builder()
                               .message(message)
                               .build();
    }

    private <T> BasicResponseDTO makeSelectResponseDTO(String message, T responseData) {
        return BasicResponseDTO.builder()
                               .message(message)
                               .responseData(responseData)
                               .build();
    }

}
