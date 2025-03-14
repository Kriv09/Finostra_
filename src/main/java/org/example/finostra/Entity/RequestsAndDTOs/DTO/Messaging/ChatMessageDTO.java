package org.example.finostra.Entity.RequestsAndDTOs.DTO.Messaging;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ChatMessageDTO {
    private String sender;
    private String content;
    private org.example.finostra.Entity.DTO.Messaging.MessageType type;


}
