package com.example.notificationmicroservice.controller;

import com.example.notificationmicroservice.dto.NotificationDTO;
import com.example.notificationmicroservice.model.Notification;
import com.example.notificationmicroservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    //POST
    //save notification
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody NotificationDTO notificationDTO) {
        try {
            return new ResponseEntity<>(notificationService.createNotification(notificationDTO), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //throw exception if request body is missing anything
        }
    }

    //GET
    //get all notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    //get notifications by userId (of poster)
    @GetMapping("/users/{id}")
    public ResponseEntity<List<Notification>> getNotificationByBloggerId(@PathVariable Integer id) {
        return ResponseEntity.ok(notificationService.getNotificationsByBloggerId(id));
    }

    //get notification by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(notificationService.getNotificationsById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //throw exception when notification not found
        }
    }

    //DELETE
    //delete notification
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Integer id) {
        try {
            notificationService.deleteNotificationById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //throw exception when notification not found
        }
    }

}
