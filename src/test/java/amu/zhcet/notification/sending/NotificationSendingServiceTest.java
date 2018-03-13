package amu.zhcet.notification.sending;

import amu.zhcet.common.UserExtractor;
import amu.zhcet.data.user.User;
import amu.zhcet.notification.ChannelType;
import amu.zhcet.notification.Notification;
import amu.zhcet.notification.recipient.CachedNotificationService;
import amu.zhcet.notification.recipient.NotificationRecipient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NotificationSendingServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock private EmailNotificationSender emailSendingService;
    @Mock private FirebaseNotificationSender firebaseNotificationSender;
    @Mock private CachedNotificationService cachedNotificationService;
    @Mock private UserExtractor userExtractor;

    private NotificationSendingService notificationSendingService;

    private User sender = User.builder()
            .userId("test")
            .build();

    @Before
    public void setup() {
        notificationSendingService = new NotificationSendingService(emailSendingService, firebaseNotificationSender, cachedNotificationService, userExtractor);
    }

    private Notification getNotification() {
       return Notification.builder()
                .automated(true)
                .title("Test")
                .message("Gordon")
                .channelType(ChannelType.TAUGHT_COURSE)
                .recipientChannel("CO432")
                .sender(sender)
                .build();
    }

    @Test
    public void saveNotificationPrimary() {
        Notification notification = getNotification();
        notificationSendingService.sendNotification(notification);

        assertNotNull(notification.getSentTime());
        verify(cachedNotificationService).save(notification);
    }

    private String[] userIds = {"Prakash", "Mahesh", "Rajesh"};
    private List<User> userList = Stream.of(userIds)
            .map(id -> User.builder().userId(id).build())
            .collect(Collectors.toList());


    private void setBehaviour() {
        when(userExtractor.fromTaughtCourse(any(), any()))
                .thenReturn(userList.stream());
    }

    @SuppressWarnings("unchecked")
    private List<NotificationRecipient> getRecipients() {
        ArgumentCaptor<List> notificationRecipientArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(cachedNotificationService).saveAll(notificationRecipientArgumentCaptor.capture());

        return (List<NotificationRecipient>) notificationRecipientArgumentCaptor.getValue();
    }

    @Test
    public void saveNotificationRecipient() {
        Notification notification = getNotification();

        setBehaviour();
        notificationSendingService.sendNotification(notification);

        Stream.of(userIds).forEach(id -> verify(cachedNotificationService).resetUnreadCount(id));

        List<NotificationRecipient> recipients = getRecipients();

        verify(emailSendingService).sendEmailForNotification(notification, userList);

        recipients.forEach(notificationRecipient -> {
            assertTrue(Arrays.asList(userIds).contains(notificationRecipient.getRecipient().getUserId()));
            assertEquals(notification, notificationRecipient.getNotification());
            verify(cachedNotificationService).resetUnreadCount(notificationRecipient.getRecipient().getUserId());
            verify(firebaseNotificationSender).sendFirebaseNotification(notificationRecipient);
        });
    }

    @Test
    public void saveNotificationWithoutEmailPropagation() {
        Notification notification = getNotification();
        notification.setStopEmailPropagation(true);

        setBehaviour();
        notificationSendingService.sendNotification(notification);

        List<NotificationRecipient> recipients = getRecipients();

        verify(emailSendingService, never()).sendEmailForNotification(notification, userList);
        recipients.forEach(notificationRecipient -> {
            verify(emailSendingService, never()).sendEmailForNotification(notificationRecipient);
        });
    }

    @Test
    public void saveNotificationWithoutFirebasePropagation() {
        Notification notification = getNotification();
        notification.setStopFirebasePropagation(true);

        setBehaviour();
        notificationSendingService.sendNotification(notification);

        List<NotificationRecipient> recipients = getRecipients();

        recipients.forEach(notificationRecipient -> {
            verify(firebaseNotificationSender, never()).sendFirebaseNotification(notificationRecipient);
        });
    }
}