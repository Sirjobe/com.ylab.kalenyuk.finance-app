package com.ylab.mapper;

import com.ylab.dto.GoalDTO;
import com.ylab.entity.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "default")
public interface GoalMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "targetAmount", source = "targetAmount")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "email", source = "email")
    GoalDTO toDto(Goal goal);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "targetAmount", source = "targetAmount")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "email", source = "email")
    Goal toEntity(GoalDTO goalDTO);
}