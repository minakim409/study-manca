package com.study.manca.service;

import com.study.manca.dto.MemberResponse;
import com.study.manca.dto.MemberRequest;
import com.study.manca.entity.Member;
import com.study.manca.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // 전체 사용자 조회 (GET)
    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 사용자 조회 (GET)
    public MemberResponse findById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));
        return MemberResponse.from(member);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));
    }

    // 사용자 생성 (POST)
    @Transactional
    public MemberResponse create(MemberRequest request) {
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        Member member = request.toEntity();
        Member savedUser = memberRepository.save(member);
        return MemberResponse.from(savedUser);
    }

    // 사용자 전체 수정 (PUT)
    @Transactional
    public MemberResponse update(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());

        return MemberResponse.from(member);
    }

    // 사용자 부분 수정 (PATCH)
    @Transactional
    public MemberResponse updatePartial(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        if (request.getName() != null) {
            member.setName(request.getName());
        }
        if (request.getEmail() != null) {
            member.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            member.setPhone(request.getPhone());
        }

        return MemberResponse.from(member);
    }

    // 사용자 삭제 (DELETE)
    @Transactional
    public void delete(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }
}
