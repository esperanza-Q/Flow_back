package org.example.flow.service;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.*;
import lombok.RequiredArgsConstructor;
import org.example.flow.entity.ShopInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CommentSummaryService {
    private final OpenAiService openAiService;

    public CommentSummaryService(@Value("${openai.api.key}") String openAiApiKey) {
        this.openAiService = new OpenAiService(openAiApiKey);
    }

    // 리뷰 요약 후 ShopInfo 엔티티에 코멘트 저장
    public ShopInfo generateAndSetComments(ShopInfo shopInfo, List<String> reviews) {
        String prompt =
                "다음 매장 리뷰들을 바탕으로 추천 코멘트 3개를 작성해줘.\n" +
                        "- 각 코멘트는 1문장으로, 40자 이내로 작성해.\n" +
                        "- 서로 다른 포인트(예: 분위기, 메뉴, 서비스, 위치 등)를 강조해.\n" +
                        "- 앞에 번호는 없이 문장만 작성해줘.\n" +
                        "- 동일한 키워드나 표현을 반복하지 마.\n" +
                        "- 친근하고 긍정적인 톤으로 작성해.\n\n" +
                        String.join("\n", reviews);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o-mini") // 또는 "gpt-4o"
                .messages(List.of(new ChatMessage("user", prompt)))
                .maxTokens(200)
                .build();

        ChatCompletionResult result = openAiService.createChatCompletion(request);
        String content = result.getChoices().get(0).getMessage().getContent();

        // 개행 단위로 split → 3개만 가져오기
        List<String> comments = Arrays.stream(content.split("\n"))
                .filter(c -> !c.trim().isEmpty())
                .limit(3)
                .toList();

        if (comments.size() > 0) shopInfo.setComment1(comments.get(0));
        if (comments.size() > 1) shopInfo.setComment2(comments.get(1));
        if (comments.size() > 2) shopInfo.setComment3(comments.get(2));

        return shopInfo;
    }
}
