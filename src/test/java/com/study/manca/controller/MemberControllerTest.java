package com.study.manca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.manca.dto.MemberRequest;
import com.study.manca.dto.MemberResponse;
import com.study.manca.entity.Member;
import com.study.manca.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ContextConfiguration(classes = {MemberController.class})
@DisplayName("MemberController 테스트")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private MemberResponse createMemberResponse(Long id, String name, String email, String phone) {
        Member member = Member.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .build();
        try {
            java.lang.reflect.Field idField = Member.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(member, id);

            java.lang.reflect.Field createdAtField = Member.class.getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(member, LocalDateTime.now());

            java.lang.reflect.Field updatedAtField = Member.class.getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(member, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return MemberResponse.from(member);
    }

    @Test
    @DisplayName("GET /api/users - 전체 회원 조회 성공")
    void getAllUsers_Success() throws Exception {
        // given
        List<MemberResponse> members = Arrays.asList(
                createMemberResponse(1L, "홍길동", "hong@example.com", "010-1234-5678"),
                createMemberResponse(2L, "김철수", "kim@example.com", "010-2345-6789")
        );
        given(memberService.findAll()).willReturn(members);

        // when & then
        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("홍길동"))
                .andExpect(jsonPath("$[1].name").value("김철수"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - 회원 상세 조회 성공")
    void getUserById_Success() throws Exception {
        // given
        MemberResponse member = createMemberResponse(1L, "홍길동", "hong@example.com", "010-1234-5678");
        given(memberService.findById(1L)).willReturn(member);

        // when & then
        mockMvc.perform(get("/api/users/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.email").value("hong@example.com"));
    }


    @Test
    @DisplayName("POST /api/users - 회원 등록 성공")
    void createUser_Success() throws Exception {
        // given
        MemberRequest request = new MemberRequest("홍길동", "hong@example.com", "010-1234-5678");
        MemberResponse response = createMemberResponse(1L, "홍길동", "hong@example.com", "010-1234-5678");
        given(memberService.create(any(MemberRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("홍길동"));
    }


    @Test
    @DisplayName("POST /api/users/{id}/update - 회원 정보 전체 수정 성공")
    void updateUser_Success() throws Exception {
        // given
        MemberRequest request = new MemberRequest("홍길동수정", "hong_updated@example.com", "010-9999-8888");
        MemberResponse response = createMemberResponse(1L, "홍길동수정", "hong_updated@example.com", "010-9999-8888");
        given(memberService.update(eq(1L), any(MemberRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/users/{id}/update", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("홍길동수정"))
                .andExpect(jsonPath("$.email").value("hong_updated@example.com"));
    }

    @Test
    @DisplayName("POST /api/users/{id}/update-partial - 회원 정보 부분 수정 성공")
    void updateUserPartial_Success() throws Exception {
        // given
        MemberRequest request = new MemberRequest("홍길동", null, "010-9999-8888");
        MemberResponse response = createMemberResponse(1L, "홍길동", "hong@example.com", "010-9999-8888");
        given(memberService.updatePartial(eq(1L), any(MemberRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/users/{id}/update-partial", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("010-9999-8888"));
    }

    @Test
    @DisplayName("POST /api/users/{id}/delete - 회원 삭제 성공")
    void deleteUser_Success() throws Exception {
        // given
        doNothing().when(memberService).delete(1L);

        // when & then
        mockMvc.perform(post("/api/users/{id}/delete", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
