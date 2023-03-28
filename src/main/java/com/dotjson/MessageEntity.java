package com.dotjson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "message")
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {

    @Id
    private String id;
    private String text;
    private String time;

}
