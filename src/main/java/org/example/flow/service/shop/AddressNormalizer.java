package org.example.flow.service.shop;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 주소 문자열에서 불필요한 괄호, 호수, 공백 등을 제거해
 * 카카오 지오코딩 API 매칭률을 높여주는 유틸
 */
@Component
public class AddressNormalizer {

    private static final Pattern PAREN = Pattern.compile("\\s*\\([^)]*\\)\\s*"); // 괄호 안 제거
    private static final Pattern EXTRA_SPACES = Pattern.compile("\\s+");
    private static final Pattern ROOM = Pattern.compile("\\s*(\\d+호|\\d+-\\d+호|\\d+층)\\b");

    public String normalize(String s) {
        if (s == null) return null;
        s = PAREN.matcher(s).replaceAll(" ");    // (동선동1가, 미래하이츠) → 제거
        s = ROOM.matcher(s).replaceAll(" ");     // "2층", "101호" → 제거
        s = EXTRA_SPACES.matcher(s.trim()).replaceAll(" ");
        return s;
    }
}
