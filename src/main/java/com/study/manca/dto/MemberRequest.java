package com.study.manca.dto;

import com.study.manca.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원 등록/수정 요청")
@Getter
@NoArgsConstructor
public class MemberRequest {

    @Schema(description = "회원 이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일", example = "hong@example.com")
    private String email;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    public MemberRequest(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .build();
    }
}
