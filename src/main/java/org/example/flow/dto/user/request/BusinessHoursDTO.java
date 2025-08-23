package org.example.flow.dto.user.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.flow.entity.BusinessHours;

import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@Setter
@Getter
@Data
public class BusinessHoursDTO {
    private BusinessHours.Week week;
    private LocalTime openTime;
    private LocalTime closeTime;
}
