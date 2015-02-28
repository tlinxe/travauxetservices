package fr.travauxetservices.views;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.tools.I18N;
import fr.travauxetservices.tools.IOToolkit;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by Phobos on 18/12/14.
 */
public class PictureField extends CustomField<byte[]> {
    private Embedded image;
    private HorizontalLayout layout;
    private Upload upload;

    public PictureField(String caption) {
        CustomEventBus.register(this);
        setCaption(caption);
        image = new Embedded(null);
        image.setWidth(100.0f, Unit.PIXELS);
    }

    @Override
    protected Component initContent() {
        VerticalLayout root = new VerticalLayout();
        root.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        root.setSizeUndefined();
        root.setMargin(false);
        root.setSpacing(true);
        root.addComponent(image);

        class ImageUploader implements Upload.Receiver, Upload.SucceededListener, Upload.ProgressListener {
            final static int maxLength = 10000000;
            ByteArrayOutputStream fos = null;
            String filename;
            Upload upload;

            public ImageUploader(Upload upload) {
                this.upload = upload;
            }

            public OutputStream receiveUpload(String filename, String mimeType) {
                this.filename = filename;
                fos = new ByteArrayOutputStream(maxLength + 1);
                return fos; // Return the output stream to write to
            }

            @Override
            public void updateProgress(long readBytes, long contentLength) {
                if (readBytes > maxLength) {
                    Notification.show("Too big content");
                    upload.interruptUpload();
                }
            }

            public void uploadSucceeded(Upload.SucceededEvent event) {
                byte[] bytes = fos.toByteArray();
                setImage(bytes);
                setValue(bytes);
                System.out.println("PictureField.ImageUploader.uploadSucceeded length: "+fos.toByteArray().length );
            }
        }

        layout = new HorizontalLayout();
        layout.setSpacing(true);

        upload = new Upload();
        upload.setButtonCaption(I18N.getString("button.upload") + "...");
        upload.addStyleName(ValoTheme.BUTTON_TINY);
        ImageUploader uploader = new ImageUploader(upload);
        upload.setReceiver(uploader);
        upload.addSucceededListener(uploader);
        upload.setImmediate(true); // Only button
        layout.addComponent(upload);

        Button remove = new Button(null, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                setImage(null);
                setValue(null);
            }
        });
        remove.setIcon(FontAwesome.TRASH_O);
        remove.addStyleName("borderless-colored");
        layout.addComponent(remove);

        root.addComponent(layout);
        layout.setVisible(!isReadOnly());
        return root;
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
        super.setPropertyDataSource(newDataSource);
        setImage((byte[]) newDataSource.getValue());
    }

    @Override
    public void setValue(byte[] newValue) throws ReadOnlyException, Converter.ConversionException {
        super.setValue(newValue);
        setImage(newValue);
    }

    public void setReadOnly(boolean b) {
        super.setReadOnly(b);
        if (layout != null) layout.setVisible(!b);
    }

    private void setImage(final byte[] bytes) {
        Resource resource = new ClassResource("/images/profile-pic-300px.jpg");
        if (bytes != null && bytes.length > 0) {
            resource = new StreamResource(new IOToolkit.ByteArraySource(bytes), "picture.png");
        }
        image.setSource(resource);
        image.markAsDirty();
    }


    @Override
    public Class<? extends byte[]> getType() {
        return byte[].class;
    }

    @Subscribe
    public void updatePicture(final CustomEvent.PictureUpdatedEvent event) {
        final byte[] bytes = event.getBytes();
        setImage(bytes);
        setValue(bytes);
    }
}
