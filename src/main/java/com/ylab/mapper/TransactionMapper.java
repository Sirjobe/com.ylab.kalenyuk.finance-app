package com.ylab.mapper;

import com.ylab.dto.TransactionDTO;
import com.ylab.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "default")
public interface TransactionMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "email", source = "email")
    TransactionDTO toDto(Transaction transaction);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "email", source = "email")
    Transaction toEntity(TransactionDTO transactionDTO);
}