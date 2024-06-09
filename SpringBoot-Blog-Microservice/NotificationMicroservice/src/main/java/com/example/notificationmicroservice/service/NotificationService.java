package com.example.notificationmicroservice.service;

import com.example.notificationmicroservice.dto.NotificationDTO;
import com.example.notificationmicroservice.model.Notification;
import com.example.notificationmicroservice.repository.INotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    INotificationRepository notificationRepository;

    //create notification
    public Notification createNotification(NotificationDTO notificationDTO) {
        if(notificationDTO.getBloggerId() == null || notificationDTO.getCommenterUsername() == null
                || notificationDTO.getCommentId() == null || notificationDTO.getBlogId() == null) {
            throw new IllegalArgumentException("Notification DTO must contain blogger id,commenter username, comment id and blog id");
        }
        Notification notification = new Notification();
        notification.setCommenterUsername(notificationDTO.getCommenterUsername());
        notification.setBloggerId(notificationDTO.getBloggerId());
        notification.setCommentId(notificationDTO.getCommentId());
        notification.setBlogId(notificationDTO.getBlogId());

        return notificationRepository.save(notification);
    }

    //get all notifications
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    //get notifications by userId of blogger
    public List<Notification> getNotificationsByBloggerId(Integer bloggerId) {
        return notificationRepository.findAllByBloggerId(bloggerId);
    }

    //get notification by id
    public Notification getNotificationsById(Integer id) throws Exception {
        return notificationRepository.findById(id).orElseThrow(()-> new Exception("Notification with id " + id + " not found"));
    }

    //update notification - intentionally not adding, notifications won't need to be updated (wouldn't logically edit and attributes)

    //delete notification
    public void deleteNotificationById(Integer id) throws Exception {
        Notification existingNotification = notificationRepository.findById(id).orElseThrow(()-> new Exception("Notification with id " + id + " not found"));
        notificationRepository.delete(existingNotification);
    }
}
