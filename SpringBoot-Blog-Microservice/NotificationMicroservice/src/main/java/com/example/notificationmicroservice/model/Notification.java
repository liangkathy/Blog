package com.example.notificationmicroservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_notif")
    @SequenceGenerator(name = "seq_notif", allocationSize = 1)
    private Integer id;
    private String commenterUsername;
    private Integer bloggerId; //aka userId of poster
    private Integer commentId;
    private Integer blogId;
}
