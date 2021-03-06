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
    void ?????????_??????_????????????() throws Exception {

        BoardRequestDTO boardRequestDTO = makeBoardRequestDTO("test");

        String body = new ObjectMapper().writeValueAsString(boardRequestDTO);

        ResultActions resultActions = mvc.perform(post("/boards")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());

    }

    @Test
    void ?????????_??????_????????????() throws Exception {

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
                                             headerWithName("access-token").description("????????? ??? ????????? ??????")
                                     ),
                                     requestFields(
                                             fieldWithPath("title").description("??????"),
                                             fieldWithPath("content").description("??????"),
                                             fieldWithPath("writer").description("?????????")
                                     ),
                                     responseFields(
                                             fieldWithPath("message").description("?????????"),
                                             fieldWithPath("responseData").description("?????????"),
                                             fieldWithPath("errorMessage").description("?????? ?????????")
                                     )
                             )
                     );
    }

    @Test
    void ?????????_?????????() throws Exception {
        final int pagingId = 1;
        Page<BoardPagingDTO> boardPagingDTO = makePageingDTO();
        given(boardService.paging(any(Integer.class))).willReturn(boardPagingDTO);

        //then
        ResultActions resultActions = mvc.perform(get("/boards/page/{pagingid}", pagingId));

        resultActions.andExpect(status().isOk())
                     .andDo(document("board-paging-success",
                             preprocessResponse(prettyPrint()),
                             pathParameters(
                                     parameterWithName("pagingid").description("????????? ??????")
                             ),
                             responseFields(
                                     fieldWithPath("message").description("?????????"),

                                     fieldWithPath("responseData").description("?????????"),
                                     fieldWithPath("responseData.content.[].id").description("????????? ??????"),
                                     fieldWithPath("responseData.content.[].title").description("??????"),
                                     fieldWithPath("responseData.content.[].content").description("??????"),
                                     fieldWithPath("responseData.content.[].writer").description("?????????"),
                                     fieldWithPath("responseData.content.[].hits").description("?????????"),

                                     fieldWithPath("responseData.pageable.sort.sorted").description("?????? ????????? ??????"),
                                     fieldWithPath("responseData.pageable.sort.unsorted").description("?????? ???????????? ??????"),
                                     fieldWithPath("responseData.pageable.sort.empty").description("???????????? ???????????? ??????"),

                                     fieldWithPath("responseData.pageable.pageNumber").description("?????? ????????? ??????"),
                                     fieldWithPath("responseData.pageable.pageSize").description("??? ???????????? ????????? ????????? ??????"),
                                     fieldWithPath("responseData.pageable.offset").description("????????? ??????????????? (0?????? ??????)"),
                                     fieldWithPath("responseData.pageable.paged").description("????????? ????????? ??????????????? ??????"),
                                     fieldWithPath("responseData.pageable.unpaged").description("????????? ????????? ?????????????????? ??????"),

                                     fieldWithPath("responseData.last").description("????????? ????????? ?????? ??????"),
                                     fieldWithPath("responseData.totalPages").description("?????? ????????? ??????"),
                                     fieldWithPath("responseData.totalElements").description("????????? ????????? ??? ??????"),
                                     fieldWithPath("responseData.first").description("????????? ??????????????? ??????"),
                                     fieldWithPath("responseData.numberOfElements").description("?????? ??????????????? ?????? ??? ????????? ??????"),
                                     fieldWithPath("responseData.number").description("?????? ????????? ??????"),
                                     fieldWithPath("responseData.size").description("??? ???????????? ????????? ????????? ??????"),

                                     fieldWithPath("responseData.sort.sorted").description("?????? ????????? ??????"),
                                     fieldWithPath("responseData.sort.unsorted").description("?????? ???????????? ??????"),
                                     fieldWithPath("responseData.sort.empty").description("???????????? ???????????? ??????"),

                                     fieldWithPath("responseData.empty").description("???????????? ???????????? ??????"),

                                     fieldWithPath("errorMessage").description("?????? ?????????")

                             )
                     ));


    }

    @Test
    void ?????????_??????_??????() throws Exception {
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
                                     parameterWithName("boardid").description("????????? ??????")
                             ),
                             responseFields(
                                     fieldWithPath("message").description("?????????"),
                                     fieldWithPath("responseData").description("?????????"),
                                     fieldWithPath("responseData.id").description("????????? ??????"),
                                     fieldWithPath("responseData.title").description("??????"),
                                     fieldWithPath("responseData.content").description("??????"),
                                     fieldWithPath("responseData.writer").description("?????????"),
                                     fieldWithPath("responseData.hits").description("?????????"),
                                     fieldWithPath("errorMessage").description("?????? ?????????")

                             )
                     ));
        ;
    }

    @Test
    void ?????????_??????_??????() throws Exception {
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
    void ?????????_??????_??????() throws Exception {
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
                                             headerWithName("access-token").description("????????? ??? ????????? ??????")
                                     ),
                                     requestFields(
                                             fieldWithPath("id").description("?????????"),
                                             fieldWithPath("title").description("??????"),
                                             fieldWithPath("content").description("??????"),
                                             fieldWithPath("writer").description("?????????")
                                     ),
                                     responseFields(
                                             fieldWithPath("message").description("?????????"),
                                             fieldWithPath("responseData").description("?????????"),
                                             fieldWithPath("errorMessage").description("?????? ?????????")
                                     )
                             )
                     );
    }

    @Test
    void ?????????_??????_??????() throws Exception {
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
                                             headerWithName("access-token").description("????????? ??? ????????? ??????")
                                     ),
                                     requestFields(
                                             fieldWithPath("id").description("?????????"),
                                             fieldWithPath("writer").description("?????????")
                                     ),
                                     responseFields(
                                             fieldWithPath("message").description("?????????"),
                                             fieldWithPath("responseData").description("?????????"),
                                             fieldWithPath("errorMessage").description("?????? ?????????")
                                     )
                             )
                     );

    }

    private BoardResponseDTO makeBoardResponseDTO() {

        BoardResponseDTO boardResponseDTO = BoardResponseDTO.builder()
                                                            .id(1L)
                                                            .title("??????")
                                                            .content("??????")
                                                            .writer("?????????")
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
                                   .title("??????")
                                   .content("??????")
                                   .writer("?????????")
                                   .hits(i)
                                   .build());
        }


        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<BoardPagingDTO>(data, pageable, 2);
    }

}