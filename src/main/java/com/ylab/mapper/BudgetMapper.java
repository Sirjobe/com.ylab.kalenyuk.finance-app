package com.ylab.mapper;

import com.ylab.dto.BudgetDTO;
import com.ylab.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "default")
public interface BudgetMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "limit", source = "limit")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    @Mapping(target = "email", source = "email")
    BudgetDTO toDto(Budget budget);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "limit", source = "limit")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    @Mapping(target = "email", source = "email")
    Budget toEntity(BudgetDTO budgetDTO);
}