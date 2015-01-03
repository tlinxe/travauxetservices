package fr.travauxetservices.event;

import fr.travauxetservices.model.Ad;
import fr.travauxetservices.views.ViewType;

/*
 * Event bus events used in Dashboard are listed here as inner classes.
 */
public abstract class CustomEvent {

    public static final class UserLoginRequestedEvent {
        private final String userName, password;

        public UserLoginRequestedEvent(final String userName, final String password) {
            this.userName = userName;
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class BrowserResizeEvent {

    }

    public static class UserLoggedOutEvent {

    }

    public static class NotificationsCountUpdatedEvent {
    }

    public static final class ReportsCountUpdatedEvent {
        private final int count;

        public ReportsCountUpdatedEvent(final int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }

    }

    public static final class PostViewChangeEvent {
        private final ViewType view;

        public PostViewChangeEvent(final ViewType view) {
            this.view = view;
        }

        public ViewType getView() {
            return view;
        }
    }

    public static class CloseOpenWindowsEvent {
    }

    public static class ProfileUpdatedEvent {
    }

    public static final class PictureUpdatedEvent {
        private final byte[] bytes;

        public PictureUpdatedEvent(final byte[] bytes) {
            this.bytes = bytes;
        }

        public byte[] getBytes() {
            return bytes;
        }
    }

    public static final class OpenAdEvent {
        private final Ad ad;

        public OpenAdEvent(final Ad ad) {
            this.ad = ad;
        }

        public Ad getAd() {
            return ad;
        }
    }
}
