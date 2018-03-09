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
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    public void saveNotificationRecipient() {
        Notification notification = getNotification();

        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<NotificationRecipient> notificationRecipientArgumentCaptor = ArgumentCaptor.forClass(NotificationRecipient.class);

        notificationSendingService.sendNotification(notification);

        verify(userExtractor).fromTaughtCourse(eq(notification.getRecipientChannel()), eq(sender.getUserId()), consumerCaptor.capture());

        String[] userIds = {"Prakash", "Mahesh", "Rajesh"};
        Stream.of(userIds)
                .map(id -> User.builder().userId(id).build())
                .forEach(user -> consumerCaptor.getValue().accept(user));

        Stream.of(userIds).forEach(id -> verify(cachedNotificationService).resetUnreadCount(id));
        verify(cachedNotificationService, times(3)).save(notificationRecipientArgumentCaptor.capture());
        notificationRecipientArgumentCaptor.getAllValues().forEach(notificationRecipient -> {
            assertTrue(Arrays.asList(userIds).contains(notificationRecipient.getRecipient().getUserId()));
            assertEquals(notification, notificationRecipient.getNotification());
            verify(cachedNotificationService).resetUnreadCount(notificationRecipient.getRecipient().getUserId());
            verify(cachedNotificationService).save(notificationRecipient);
            verify(emailSendingService).sendEmailForNotification(notificationRecipient);
            verify(firebaseNotificationSender).sendFirebaseNotification(notificationRecipient);
        });
    }

    @Test
    public void saveNotificationWithoutEmailPropagation() {
        Notification notification = getNotification();
        notification.setStopEmailPropagation(true);

        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<NotificationRecipient> notificationRecipientArgumentCaptor = ArgumentCaptor.forClass(NotificationRecipient.class);

        notificationSendingService.sendNotification(notification);

        verify(userExtractor).fromTaughtCourse(eq(notification.getRecipientChannel()), eq(sender.getUserId()), consumerCaptor.capture());

        String[] userIds = {"Prakash", "Mahesh", "Rajesh"};
        Stream.of(userIds)
                .map(id -> User.builder().userId(id).build())
                .forEach(user -> consumerCaptor.getValue().accept(user));

        verify(cachedNotificationService, times(3)).save(notificationRecipientArgumentCaptor.capture());
        notificationRecipientArgumentCaptor.getAllValues().forEach(notificationRecipient -> {
            assertTrue(Arrays.asList(userIds).contains(notificationRecipient.getRecipient().getUserId()));
            assertEquals(notification, notificationRecipient.getNotification());
            verify(cachedNotificationService).save(notificationRecipient);
            verify(cachedNotificationService).resetUnreadCount(notificationRecipient.getRecipient().getUserId());
            verify(emailSendingService, never()).sendEmailForNotification(notificationRecipient);
            verify(firebaseNotificationSender).sendFirebaseNotification(notificationRecipient);
        });
    }

    @Test
    public void saveNotificationWithoutFirebasePropagation() {
        Notification notification = getNotification();
        notification.setStopFirebasePropagation(true);

        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<NotificationRecipient> notificationRecipientArgumentCaptor = ArgumentCaptor.forClass(NotificationRecipient.class);

        notificationSendingService.sendNotification(notification);

        verify(userExtractor).fromTaughtCourse(eq(notification.getRecipientChannel()), eq(sender.getUserId()), consumerCaptor.capture());

        String[] userIds = {"Prakash", "Mahesh", "Rajesh"};
        Stream.of(userIds)
                .map(id -> User.builder().userId(id).build())
                .forEach(user -> consumerCaptor.getValue().accept(user));

        verify(cachedNotificationService, times(3)).save(notificationRecipientArgumentCaptor.capture());
        notificationRecipientArgumentCaptor.getAllValues().forEach(notificationRecipient -> {
            assertTrue(Arrays.asList(userIds).contains(notificationRecipient.getRecipient().getUserId()));
            assertEquals(notification, notificationRecipient.getNotification());
            verify(cachedNotificationService).save(notificationRecipient);
            verify(cachedNotificationService).resetUnreadCount(notificationRecipient.getRecipient().getUserId());
            verify(emailSendingService).sendEmailForNotification(notificationRecipient);
            verify(firebaseNotificationSender, never()).sendFirebaseNotification(notificationRecipient);
        });
    }
}