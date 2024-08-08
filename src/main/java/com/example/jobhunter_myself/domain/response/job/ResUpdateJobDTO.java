package com.example.jobhunter_myself.domain.response.job;

import java.time.Instant;
import java.util.List;

import com.example.jobhunter_myself.util.constant.LevelEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateJobDTO {
    private long id;

    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;

    private Instant startDate;
    private Instant endDate;

    private boolean active;
    private Instant updatedAt;

    private String updatedBy;

    private List<String> skills;
}
