package fr.travauxetservices.component;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Responsive;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Phobos on 17/12/14.
 */
@SuppressWarnings("serial")
public class PictureUploadWindow extends Window {
    public static final String ID = "pictureuploadwindow";
    private byte[] bytes;

    public PictureUploadWindow(final byte[] bytes) {
        addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setHeight(90.0f, Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(new MarginInfo(true, false, false, false));
        content.setSpacing(true);
        setContent(content);

        Upload upload = new Upload("Upload the image here", null);
        content.addComponent(upload);

        // Show uploaded file in this placeholder
        final Embedded image = new Embedded("Uploaded Image");
        image.setWidth(100.0f, Unit.PIXELS);
        content.addComponent(image);

        // Put upload in this memory buffer that grows automatically
        final ByteArrayOutputStream os = new ByteArrayOutputStream(10240);

        // Implement receiver that stores in the memory buffer
        class ImageReceiver implements Upload.Receiver {
            private static final long serialVersionUID = -1276759102490466761L;

            public String filename; // The original filename

            public OutputStream receiveUpload(String filename, String mimeType) {
                this.filename = filename;
                os.reset(); // If re-uploading
                return os;
            }
        }
        final ImageReceiver receiver = new ImageReceiver();
        upload.setReceiver(receiver);

        // Handle success in upload
        upload.addSucceededListener(new Upload.SucceededListener() {
            private static final long serialVersionUID = 6053253347529760665L;

            public void uploadSucceeded(Upload.SucceededEvent event) {
                image.setCaption("Uploaded Image " + receiver.filename + " has length " + os.toByteArray().length);

                // Display the image in the feedback component
                StreamResource.StreamSource source = new StreamResource.StreamSource() {
                    private static final long serialVersionUID = -4905654404647215809L;

                    public InputStream getStream() {
                        return new ByteArrayInputStream(os.toByteArray());
                    }
                };

                if (image.getSource() == null)
                    image.setSource(new StreamResource(source, receiver.filename));
                else { // Replace picture
                    StreamResource resource = (StreamResource) image.getSource();
                    resource.setStreamSource(source);
                    resource.setFilename(receiver.filename);
                }

                image.markAsDirty();
            }
        });


        content.addComponent(buildFooter(os));
    }

    private Component buildFooter(final ByteArrayOutputStream os) {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        Button ok = new Button("OK");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                CustomEventBus.post(new CustomEvent.PictureUpdatedEvent(os.toByteArray()));
                close();
            }
        });
        ok.focus();
        footer.addComponent(ok);
        footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
        return footer;
    }

    static public void open(final byte[] bytes) {
        CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());
        Window w = new PictureUploadWindow(bytes);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
