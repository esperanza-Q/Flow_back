package org.example.flow.service.userMypage;

import lombok.*;
import org.example.flow.dto.userMypage.response.UserMypageResponse;
import org.example.flow.entity.*;
import org.example.flow.apiPayload.exception.GeneralException;
import org.example.flow.apiPayload.code.ErrorStatus;
import org.example.flow.repository.ProfileRepository;
import org.example.flow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserMypageService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public UserMypageResponse getUserMypage(Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }

        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PROFILE_NOT_FOUND));

        return new UserMypageResponse(
                user.getUserId(),
                user.getNickname(),
                new UserMypageResponse.ProfileDto(
                        profile.getProfileId(),
                        profile.getLocalConsumption(),
                        profile.getFunding(),
                        profile.getSeeds(),
                        profile.getPoint()
                )
        );
    }
}
