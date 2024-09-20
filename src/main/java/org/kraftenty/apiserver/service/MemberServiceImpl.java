package org.kraftenty.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.kraftenty.apiserver.domain.Member;
import org.kraftenty.apiserver.domain.MemberRole;
import org.kraftenty.apiserver.dto.MemberDTO;
import org.kraftenty.apiserver.dto.MemberModifyDTO;
import org.kraftenty.apiserver.repository.MemberRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO getKakaoMember(String accessToken) {
        // accessToken을 이용해서 사용자의 정보를 가져온다
        String nickName = getNicknameFromKakaoAccessToken(accessToken);

        // 기존에 DB에 이미 회원 정보가 있는경우/ 없는경우
        Optional<Member> result = memberRepository.findById(nickName);
        if(result.isPresent()) {
            MemberDTO memberDTO = entityToDTO(result.get());
            log.info("existed................... {}", memberDTO);
            return memberDTO;
        }
        // DB에 없는 경우, 새로 만들어 줌
        Member socialMember = makeSocialMember(nickName);
        memberRepository.save(socialMember);
        return entityToDTO(socialMember);
    }

    @Override
    public void modifyMember(MemberModifyDTO memberModifyDTO) {
        Optional<Member> result = memberRepository.findById(memberModifyDTO.getEmail());
        Member member = result.orElseThrow();

        member.changeNickname(memberModifyDTO.getNickname());
        member.changeSocial(false); // 회원정보 수정하면 더 이상 소셜회원이 아닌 것으로 간주한다.
        member.changePw(passwordEncoder.encode(memberModifyDTO.getPw()));

        memberRepository.save(member);
    }

    private Member makeSocialMember(String nickName) {
        String tempPassword = makeTempPassword();
        log.info("tempPassword: {}", tempPassword);
        Member member = Member.builder()
                .email(nickName)
                .pw(passwordEncoder.encode(tempPassword))
                .nickname("Social Member")
                .social(true)
                .build();
        member.addRole(MemberRole.USER);
        return member;
    }

    private String getNicknameFromKakaoAccessToken(String accessToken) {
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();
        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info("response....................");
        log.info(response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        LinkedHashMap<String, String> properties = bodyMap.get("properties");
        log.info("properties : " + properties);
        String nickName = properties.get("nickname");
        log.info("nickName : " + nickName);

        return nickName;
    }

    private String makeTempPassword() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            buffer.append((char) ((int) (Math.random() * 55) + 65));
        }
        return buffer.toString();
    }
}
