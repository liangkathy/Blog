package com.example.notificationmicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String commenterUsername;
    private Integer bloggerId; //aka userId of poster
    private Integer commentId;
    private Integer blogId;
}
