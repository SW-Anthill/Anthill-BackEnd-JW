package anthill.Anthill.controller;

import anthill.Anthill.dto.board.*;
import anthill.Anthill.service.BoardService;
import anthill.Anthill.service.JwtService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(BoardController.class)
class BoardControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    private BoardService boardService;

    @MockBean
    private JwtService jwtService;

    @Test
    void 게시글_작성_인증실패() throws Exception {

        BoardRequestDTO boardRequestDTO = makeBoardRequestDTO("test");

        String body = new ObjectMapper().writeValueAsString(boardRequestDTO);

        ResultActions resultActions = mvc.perform(post("/boards")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());

    }

    @Test
    void 게시글_작성_인증성공() throws Exception {

        BoardRequestDTO boardRequestDTO = makeBoardRequestDTO("test");
        String token = "header.payload.sign";
        String accessTokenHeader = "access-token";
        String body = new ObjectMapper().writeValueAsString(boardRequestDTO);

        given(jwtService.isUsable(any())).willReturn(true);

        ResultActions resultActions = mvc.perform(post("/boards")
                .content(body)
                .header(accessTokenHeader, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isCreated())
                     .andDo(document("board-posting-success",
                                     preprocessRequest(prettyPrint()),
                                     preprocessResponse(prettyPrint()),
                                     requestHeaders(
                                             headerWithName("access-token").description("로그인 시 발급된 토큰")
                                     ),
                                     requestFields(
                                             fieldWithPath("title").description("제목"),
                                             fieldWithPath("content").description("본문"),
                                             fieldWithPath("writer").description("작성자")
                                     ),
                                     responseFields(
                                             fieldWithPath("message").description("메시지"),
                                             fieldWithPath("responseData").description("반환값"),
                                             fieldWithPath("errorMessage").description("에러 메시지")
                                     )
                             )
                     );
    }

    @Test
    void 게시글_페이징() throws Exception {
        final int pagingId = 1;
        Page<BoardPagingDTO> boardPagingDTO = makePageingDTO();
        given(boardService.paging(any(Integer.class))).willReturn(boardPagingDTO);

        //then
        ResultActions resultActions = mvc.perform(get("/boards/page/{pagingid}", pagingId));

        resultActions.andExpect(status().isOk())
                     .andDo(document("board-paging-success",
                             preprocessResponse(prettyPrint()),
                             pathParameters(
                                     parameterWithName("pagingid").description("페이징 번호")
                             ),
                             responseFields(
                                     fieldWithPath("message").description("메시지"),

                                     fieldWithPath("responseData").description("반환값"),
                                     fieldWithPath("responseData.content.[].id").description("게시글 번호"),
                                     fieldWithPath("responseData.content.[].title").description("제목"),
                                     fieldWithPath("responseData.content.[].content").description("본문"),
                                     fieldWithPath("responseData.content.[].writer").description("작성자"),
                                     fieldWithPath("responseData.content.[].hits").description("조회수"),

                                     fieldWithPath("responseData.pageable.sort.sorted").description("정렬 됬는지 여부"),
                                     fieldWithPath("responseData.pageable.sort.unsorted").description("정렬 안됬는지 여부"),
                                     fieldWithPath("responseData.pageable.sort.empty").description("데이터가 비었는지 여부"),

                                     fieldWithPath("responseData.pageable.pageNumber").description("현재 페이지 번호"),
                                     fieldWithPath("responseData.pageable.pageSize").description("한 페이지당 조회할 데이터 개수"),
                                     fieldWithPath("responseData.pageable.offset").description("몇번째 데이터인지 (0부터 시작)"),
                                     fieldWithPath("responseData.pageable.paged").description("페이징 정보를 포함하는지 여부"),
                                     fieldWithPath("responseData.pageable.unpaged").description("페이징 정보를 안포함하는지 여부"),

                                     fieldWithPath("responseData.last").description("마지막 페이지 인지 여부"),
                                     fieldWithPath("responseData.totalPages").description("전체 페이지 개수"),
                                     fieldWithPath("responseData.totalElements").description("테이블 데이터 총 개수"),
                                     fieldWithPath("responseData.first").description("첫번째 페이지인지 여부"),
                                     fieldWithPath("responseData.numberOfElements").description("요청 페이지에서 조회 된 데이터 개수"),
                                     fieldWithPath("responseData.number").description("현재 페이지 번호"),
                                     fieldWithPath("responseData.size").description("한 페이지당 조회할 데이터 개수"),

                                     fieldWithPath("responseData.sort.sorted").description("정렬 됬는지 여부"),
                                     fieldWithPath("responseData.sort.unsorted").description("정렬 안됬는지 여부"),
                                     fieldWithPath("responseData.sort.empty").description("데이터가 비었는지 여부"),

                                     fieldWithPath("responseData.empty").description("데이터가 비었는지 여부"),

                                     fieldWithPath("errorMessage").description("에러 메시지")

                             )
                     ));


    }

    @Test
    void 게시글_조회_성공() throws Exception {
        //given
        final Long boardId = 1L;
        BoardResponseDTO boardResponseDTO = makeBoardResponseDTO();
        given(boardService.select(any())).willReturn(boardResponseDTO);

        //then
        ResultActions resultActions = mvc.perform(get("/boards/{boardid}", boardId));

        //when
        resultActions.andExpect(status().isOk())
                     .andDo(document("board-get-by-id-success",
                             preprocessResponse(prettyPrint()),
                             pathParameters(
                                     parameterWithName("boardid").description("게시글 번호")
                             ),
                             responseFields(
                                     fieldWithPath("message").description("메시지"),
                                     fieldWithPath("responseData").description("반환값"),
                                     fieldWithPath("responseData.id").description("게시글 번호"),
                                     fieldWithPath("responseData.title").description("제목"),
                                     fieldWithPath("responseData.content").description("본문"),
                                     fieldWithPath("responseData.writer").description("작성자"),
                                     fieldWithPath("responseData.hits").description("조회수"),
                                     fieldWithPath("errorMessage").description("에러 메시지")

                             )
                     ));
        ;
    }

    @Test
    void 게시물_수정_실패() throws Exception {
        //given
        BoardUpdateDTO boardUpdateDTO = makeBoardUpdateDTO();
        String body = new ObjectMapper().writeValueAsString(boardUpdateDTO);
        given(jwtService.isUsable(any())).willReturn(false);

        //when
        ResultActions resultActions = mvc.perform(put("/boards/{boardId}", boardUpdateDTO.getId())
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void 게시물_수정_성공() throws Exception {
        //given
        BoardUpdateDTO boardUpdateDTO = makeBoardUpdateDTO();
        String token = "header.payload.sign";
        String accessTokenHeader = "access-token";
        String body = new ObjectMapper().writeValueAsString(boardUpdateDTO);
        given(jwtService.isUsable(any())).willReturn(true);

        //when
        ResultActions resultActions = mvc.perform(put("/boards/{boardId}", boardUpdateDTO.getId())
                .content(body)
                .header(accessTokenHeader, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                     .andDo(document("board-update-success",
                                     preprocessRequest(prettyPrint()),
                                     preprocessResponse(prettyPrint()),
                                     requestHeaders(
                                             headerWithName("access-token").description("로그인 시 발급된 토큰")
                                     ),
                                     requestFields(
                                             fieldWithPath("id").description("글번호"),
                                             fieldWithPath("title").description("제목"),
                                             fieldWithPath("content").description("본문"),
                                             fieldWithPath("writer").description("작성자")
                                     ),
                                     responseFields(
                                             fieldWithPath("message").description("메시지"),
                                             fieldWithPath("responseData").description("반환값"),
                                             fieldWithPath("errorMessage").description("에러 메시지")
                                     )
                             )
                     );
    }

    @Test
    void 게시물_삭제_성공() throws Exception {
        //given
        BoardDeleteDTO boardDeleteDTO = makeBoardDeleteDTO();
        String token = "header.payload.sign";
        String accessTokenHeader = "access-token";
        String body = new ObjectMapper().writeValueAsString(boardDeleteDTO);
        given(jwtService.isUsable(any())).willReturn(true);

        //when
        ResultActions resultActions = mvc.perform(delete("/boards/{boardId}", boardDeleteDTO.getId())
                .content(body)
                .header(accessTokenHeader, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                     .andDo(document("board-delete-success",
                                     preprocessRequest(prettyPrint()),
                                     preprocessResponse(prettyPrint()),
                                     requestHeaders(
                                             headerWithName("access-token").description("로그인 시 발급된 토큰")
                                     ),
                                     requestFields(
                                             fieldWithPath("id").description("글번호"),
                                             fieldWithPath("writer").description("작성자")
                                     ),
                                     responseFields(
                                             fieldWithPath("message").description("메시지"),
                                             fieldWithPath("responseData").description("반환값"),
                                             fieldWithPath("errorMessage").description("에러 메시지")
                                     )
                             )
                     );

    }

    private BoardResponseDTO makeBoardResponseDTO() {

        BoardResponseDTO boardResponseDTO = BoardResponseDTO.builder()
                                                            .id(1L)
                                                            .title("제목")
                                                            .content("본문")
                                                            .writer("작성자")
                                                            .hits(1L)
                                                            .build();

        return boardResponseDTO;
    }

    private BoardDeleteDTO makeBoardDeleteDTO() {
        BoardDeleteDTO boardDeleteDTO = BoardDeleteDTO.builder()
                                                      .id(1L)
                                                      .writer("test")
                                                      .build();
        return boardDeleteDTO;
    }


    private BoardUpdateDTO makeBoardUpdateDTO() {
        BoardUpdateDTO boardUpdateDTO = BoardUpdateDTO.builder()
                                                      .id(1L)
                                                      .title("changedTitle")
                                                      .content("changedContent")
                                                      .writer("test")
                                                      .build();
        return boardUpdateDTO;
    }

    private BoardRequestDTO makeBoardRequestDTO(String value) {
        BoardRequestDTO boardRequestDTO = BoardRequestDTO.builder()
                                                         .title(value)
                                                         .content(value)
                                                         .writer(value)
                                                         .build();
        return boardRequestDTO;
    }

    private Page<BoardPagingDTO> makePageingDTO() {

        List<BoardPagingDTO> data = new ArrayList<>();
        for (long i = 1; i <= 2; i++) {
            data.add(BoardPagingDTO.builder()
                                   .id(i)
                                   .title("본문")
                                   .content("제목")
                                   .writer("작성자")
                                   .hits(i)
                                   .build());
        }


        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<BoardPagingDTO>(data, pageable, 2);
    }

}