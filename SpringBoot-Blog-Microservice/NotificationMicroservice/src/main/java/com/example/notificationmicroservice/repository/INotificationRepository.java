package com.example.notificationmicroservice.repository;

import com.example.notificationmicroservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByBloggerId(Integer bloggerId);
}
