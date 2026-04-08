package com.dormitory.service.impl;

import com.dormitory.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息通知服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    @Override
    public void sendToAll(String destination, Object message) {
        try {
            messagingTemplate.convertAndSend(destination, message);
            log.info("发送全局消息: {} -> {}", destination, message);
        } catch (Exception e) {
            log.error("发送全局消息失败: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public void sendToUser(Long userId, String destination, Object message) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    destination,
                    message
            );
            log.info("发送用户消息: userId={}, destination={}, message={}", userId, destination, message);
        } catch (Exception e) {
            log.error("发送用户消息失败: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public void sendToRole(String role, String destination, Object message) {
        try {
            // 发送到角色主题
            messagingTemplate.convertAndSend(destination + "/" + role, message);
            log.info("发送角色消息: role={}, destination={}, message={}", role, destination, message);
        } catch (Exception e) {
            log.error("发送角色消息失败: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public void sendRepairNotification(Long repairId, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "REPAIR");
        notification.put("repairId", repairId);
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        
        // 发送给所有管理员和宿管
        sendToRole("ADMIN", "/topic/notifications", notification);
        sendToRole("DORM_MANAGER", "/topic/notifications", notification);
    }
    
    @Override
    public void sendAnnouncementNotification(Long announcementId, String title) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ANNOUNCEMENT");
        notification.put("announcementId", announcementId);
        notification.put("title", title);
        notification.put("timestamp", System.currentTimeMillis());
        
        // 发送给所有用户
        sendToAll("/topic/announcements", notification);
    }
    
    @Override
    public void sendFeeReminder(Long studentId, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "FEE_REMINDER");
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        
        // 发送给指定学生
        sendToUser(studentId, "/queue/notifications", notification);
    }
}
