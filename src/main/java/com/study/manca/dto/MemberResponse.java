package com.study.manca.dto;

import com.study.manca.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "회원 응답")
@Getter
public class MemberResponse {

    @Schema(description = "회원 ID", example = "1")
    private final Long id;

    @Schema(description = "회원 이름", example = "홍길동")
    private final String name;

    @Schema(description = "이메일", example = "hong@example.com")
    private final String email;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private final String phone;

    @Schema(description = "생성일시", example = "2025-01-24T10:30:00")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-01-24T10:30:00")
    private final LocalDateTime updatedAt;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.phone = member.getPhone();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(member);
    }
}
