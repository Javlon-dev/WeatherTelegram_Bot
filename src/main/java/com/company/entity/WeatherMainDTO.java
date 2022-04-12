package com.company.entity;


import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString


public class WeatherMainDTO {
    private Double temp;
    private Double feels_like;
    private Double pressure;
    private Double humidity;

}
