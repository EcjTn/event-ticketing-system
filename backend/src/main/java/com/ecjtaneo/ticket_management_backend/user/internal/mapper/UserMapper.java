package com.ecjtaneo.ticket_management_backend.user.internal.mapper;

import com.ecjtaneo.ticket_management_backend.user.UserBasicInfo;
import com.ecjtaneo.ticket_management_backend.user.internal.dto.UserInfoResponseDto;

import org.mapstruct.Mapper;

import com.ecjtaneo.ticket_management_backend.user.internal.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    public UserBasicInfo toBasicInfo(User user);
    public UserInfoResponseDto toUserInfoResponseDto(User user);

}
