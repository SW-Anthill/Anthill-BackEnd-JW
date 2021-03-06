package anthill.Anthill.controller;

import anthill.Anthill.domain.member.Address;
import anthill.Anthill.dto.member.MemberLoginRequestDTO;
import anthill.Anthill.dto.member.MemberRequestDTO;
import anthill.Anthill.dto.member.MemberResponseDTO;
import anthill.Anthill.service.JwtService;
import anthill.Anthill.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtService jwtService;


    @Test
    public void ????????????_?????????_??????????????????_?????????() throws Exception {
        //given
        MemberRequestDTO memberRequestDTO = MemberRequestDTO.builder()
                                                            .build();
        String body = (new ObjectMapper()).writeValueAsString(memberRequestDTO);

        //when
        ResultActions resultActions = mvc.perform(post("/members")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ????????????_?????????_??????_?????????() throws Exception {
        //given
        MemberRequestDTO memberRequestDTO = getMemberRequestDTO();
        String body = (new ObjectMapper()).writeValueAsString(memberRequestDTO);

        //when
        ResultActions resultActions = mvc.perform(post("/members")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andExpect(status().isCreated());
    }

    @Test
    public void ??????_????????????_?????????() throws Exception {
        //given
        MemberRequestDTO memberRequestDTO = getMemberRequestDTO();
        String body = (new ObjectMapper()).writeValueAsString(memberRequestDTO);
        boolean duplicateResult = true;
        given(memberService.validateIsDuplicate(any())).willReturn(duplicateResult);


        //when
        ResultActions resultActions = mvc.perform(post("/members")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andExpect(status().isConflict());
    }

    @Test
    public void ??????_??????????????????_?????????() throws Exception {
        //given
        MemberRequestDTO memberRequestDTO = getMemberRequestDTO();
        String body = (new ObjectMapper()).writeValueAsString(memberRequestDTO);
        boolean duplicateResult = false;
        given(memberService.validateIsDuplicate(any())).willReturn(duplicateResult);

        //when
        ResultActions resultActions = mvc.perform(post("/members")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andExpect(status().isCreated())
                .andDo(document("member-join-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("userId").description("?????????"),
                                fieldWithPath("name").description("??????"),
                                fieldWithPath("nickName").description("?????????"),
                                fieldWithPath("password").description("????????????"),
                                fieldWithPath("phoneNumber").description("?????? ??????"),
                                fieldWithPath("address.address1").description("??????")
                                                                 .optional(),
                                fieldWithPath("address.address2").description("?????? ??????")
                                                                 .optional(),
                                fieldWithPath("address.zipCode").description("?????? ??????")
                                                                .optional()
                        ),
                        responseFields(
                                fieldWithPath("message").description("?????????"),
                                fieldWithPath("responseData").description("?????????"),
                                fieldWithPath("errorMessage").description("?????? ?????????")
                        )
                ));
    }

    @Test
    public void ?????????_??????_?????????() throws Exception {
        //given
        MemberLoginRequestDTO memberLoginRequestDTO = getMemberLoginRequestDto();
        String body = (new ObjectMapper()).writeValueAsString(memberLoginRequestDTO);
        String token = "header.payload.verifySignature";
        boolean loginResult = true;
        given(memberService.login(any())).willReturn(loginResult);
        given(jwtService.create(any(), any(), any())).willReturn(token);

        //when
        ResultActions resultActions = mvc.perform(post("/members/login")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("access-token", token))
                .andDo(document("member-login-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("userId").description("?????????"),
                                fieldWithPath("password").description("????????????")
                        ),
                        responseHeaders(
                                headerWithName("access-token").description("????????? ??? ????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("message").description("?????????"),
                                fieldWithPath("responseData").description("?????????"),
                                fieldWithPath("errorMessage").description("?????? ?????????")
                        )
                ));
    }

    @Test
    public void ?????????_??????_?????????() throws Exception {
        //given
        MemberLoginRequestDTO memberLoginRequestDTO = getMemberLoginRequestDto();
        String body = (new ObjectMapper()).writeValueAsString(memberLoginRequestDTO);
        given(memberService.login(any())).willThrow(new IllegalStateException());

        //when
        ResultActions resultActions = mvc.perform(post("/members/login")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void ????????????_??????_?????????() throws Exception {
        //given
        MemberResponseDTO memberResponseDTO = getMemberResponseDTO();
        given(memberService.findByUserID(any())).willReturn(memberResponseDTO);
        //when
        ResultActions resultActions = mvc.perform(RestDocumentationRequestBuilders.get("/members/{userid}", memberResponseDTO.getUserId()));
        //then
        resultActions.andExpect(status().isOk())
                     .andDo(document("member-get-by-id-success",
                             preprocessResponse(prettyPrint()),
                             pathParameters(
                                     parameterWithName("userid").description("?????????")
                             ),
                             responseFields(
                                     fieldWithPath("message").description("?????????"),
                                     fieldWithPath("responseData").description("?????????"),
                                     fieldWithPath("responseData.userId").description("?????????"),
                                     fieldWithPath("responseData.name").description("??????"),
                                     fieldWithPath("responseData.nickName").description("?????????"),
                                     fieldWithPath("responseData.phoneNumber").description("?????? ??????"),
                                     fieldWithPath("responseData.address.address1").description("??????")
                                                                                   .optional(),
                                     fieldWithPath("responseData.address.address2").description("?????? ??????")
                                                                                   .optional(),
                                     fieldWithPath("responseData.address.zipCode").description("?????? ??????")
                                                                                  .optional(),
                                     fieldWithPath("errorMessage").description("?????? ?????????")

                             )
                     ));

    }

    @Test
    public void ????????????_??????_?????????() throws Exception {
        //given
        MemberResponseDTO memberResponseDTO = getMemberResponseDTO();
        given(memberService.findByUserID(any())).willThrow(new IllegalArgumentException());
        //when
        ResultActions resultActions = mvc.perform(get("/members/" + "test"));
        //then
        resultActions.andExpect(status().isNotFound());

    }


    private MemberResponseDTO getMemberResponseDTO() {
        Address myAddress = Address.builder()
                                   .address1("????????? ?????????")
                                   .address2("XX????????? XX???")
                                   .zipCode("429-010")
                                   .build();

        MemberResponseDTO memberResponseDTO = MemberResponseDTO.builder()
                                                               .userId("test")
                                                               .name("test")
                                                               .nickName("test")
                                                               .phoneNumber("01012345678")
                                                               .address(myAddress)
                                                               .build();
        return memberResponseDTO;
    }


    private MemberRequestDTO getMemberRequestDTO() {

        Address myAddress = Address.builder()
                                   .address1("????????? ?????????")
                                   .address2("XX????????? XX???")
                                   .zipCode("429-010")
                                   .build();

        MemberRequestDTO memberRequestDTO = MemberRequestDTO.builder()
                                                            .userId("junwooKim")
                                                            .name("KIM")
                                                            .nickName("junuuu")
                                                            .password("123456789")
                                                            .phoneNumber("01012345678")
                                                            .address(myAddress)
                                                            .build();
        return memberRequestDTO;
    }

    private MemberLoginRequestDTO getMemberLoginRequestDto() {
        MemberLoginRequestDTO memberLoginRequestDTO = MemberLoginRequestDTO.builder()
                                                                           .userId("test")
                                                                           .password("123456789")
                                                                           .build();
        return memberLoginRequestDTO;
    }

}