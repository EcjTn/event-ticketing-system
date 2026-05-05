package com.ecjtaneo.ticket_management_backend.user.mapper;

import com.ecjtaneo.ticket_management_backend.user.UserBasicInfo;
import com.ecjtaneo.ticket_management_backend.user.dto.UserInfoResponseDto;

import org.mapstruct.Mapper;

import com.ecjtaneo.ticket_management_backend.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    public UserBasicInfo toBasicInfo(User user);
    public UserInfoResponseDto toUserInfoResponseDto(User user);

}
