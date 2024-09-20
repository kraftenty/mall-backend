package org.kraftenty.apiserver.service;

import org.kraftenty.apiserver.domain.Member;
import org.kraftenty.apiserver.dto.MemberDTO;
import org.kraftenty.apiserver.dto.MemberModifyDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Transactional
public interface MemberService {

    MemberDTO getKakaoMember(String accessToken);

    void modifyMember(MemberModifyDTO memberModifyDTO);

    default MemberDTO entityToDTO(Member member) {
        MemberDTO memberDTO = new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList().stream().map(memberRole -> memberRole.name()).collect(Collectors.toList())
        );
        return memberDTO;
    }
}
