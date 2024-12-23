package ru.antoncharov.socks.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public record SocksDto(@NotEmpty String color, @Min(0) @Max(100) Integer cottonPercentage, @Min(0) Integer quantity){}
