package com.company.entity;

import java.util.Arrays;


import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString

public class WeatherDTO {
    private WeatherCoordDTO coord;
    private WeatherDesDTO[] weather;
    private WeatherMainDTO main;
    private String name;
    private String cod;

}
