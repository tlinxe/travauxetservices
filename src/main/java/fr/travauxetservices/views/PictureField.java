package fr.travauxetservices.views;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.component.PictureUploadWindow;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.tools.IOToolkit;

/**
 * Created by Phobos on 18/12/14.
 */
public class PictureField extends CustomField<byte[]> {
    private Image image;
    private Button upload;

    public PictureField(String caption) {
        CustomEventBus.register(this);
        setCaption(caption);
        image = new Image(null);
        image.setWidth(100.0f, Unit.PIXELS);
    }

    @Override
    protected Component initContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.setSpacing(true);
        layout.addComponent(image);
        upload = new Button("Change…", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                PictureUploadWindow.open(getValue());
            }
        });
        upload.addStyleName(ValoTheme.BUTTON_TINY);
        layout.addComponent(upload);
        upload.setVisible(!isReadOnly());
        return layout;
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
        if (upload != null) upload.setVisible(!b);
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
