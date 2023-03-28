package com.dotjson;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageEntity inputToEntity(MessageInput messageInput);

    MessageOutput entityToOutput(MessageEntity messageEntity);
}
