package fr.travauxetservices.event;

import com.vaadin.tapio.googlemaps.client.LatLon;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.views.ViewType;

/*
 * Event bus events used in mytheme are listed here as inner classes.
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

    public static final class currentPostionEvent {
        private final LatLon position;

        public currentPostionEvent(final LatLon postion) {
            this.position = postion;
        }

        public LatLon getPostion() {
            return position;
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
