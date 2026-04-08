package com.dormitory.service;

/**
 * 消息通知服务接口
 */
public interface NotificationService {
    
    /**
     * 发送给所有用户
     */
    void sendToAll(String destination, Object message);
    
    /**
     * 发送给指定用户
     */
    void sendToUser(Long userId, String destination, Object message);
    
    /**
     * 发送给角色用户
     */
    void sendToRole(String role, String destination, Object message);
    
    /**
     * 发送报修通知
     */
    void sendRepairNotification(Long repairId, String message);
    
    /**
     * 发送公告通知
     */
    void sendAnnouncementNotification(Long announcementId, String title);
    
    /**
     * 发送缴费提醒
     */
    void sendFeeReminder(Long studentId, String message);
}
