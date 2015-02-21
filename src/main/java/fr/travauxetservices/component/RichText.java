package fr.travauxetservices.component;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.tools.I18N;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Created by Phobos on 13/02/15.
 */
@SuppressWarnings("serial")
public class RichText extends CustomField<String> {

    private VerticalLayout content;
    final private Label label = new Label();
    final private RichTextArea editor = new RichTextArea();
    private Button edit;

    @Override
    protected Component initContent() {
        setReadOnly(true);
        // main layout
        content = new VerticalLayout();
        content.setSpacing(true);

        editor.setWidth(100, Unit.PERCENTAGE);

        // Add the label
        label.setValue(getValue());
        label.setContentMode(ContentMode.HTML);
        content.addComponent(label, 0);
        // Edit button with inline click-listener
        edit = new Button(I18N.getString("button.save"), new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                setValue(editor.getValue());
                fireEvent(new EditorSavedEvent(editor, getValue()));
            }
        });
        edit.setVisible(false);
        edit.addStyleName(ValoTheme.BUTTON_TINY);
        content.addComponent(edit, 1);
        content.setComponentAlignment(edit, Alignment.MIDDLE_RIGHT);
        return content;
    }

    public void setEdit(boolean b) {
        setReadOnly(!b);
        if (content != null) {
            if (b) {
                editor.setValue(label.getValue());
                content.replaceComponent(label, editor);
                edit.setVisible(true);
            } else {
                label.setValue(editor.getValue());
                content.replaceComponent(editor, label);
                edit.setVisible(false);
            }
        }
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    public void addEditorSavedListener(EditorSavedListener listener) {
        try {
            Method method = EditorSavedListener.class.getDeclaredMethod("editorSaved", new Class[]{EditorSavedEvent.class});
            addListener(EditorSavedEvent.class, listener, method);
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException("Internal error, editor saved method not found");
        }
    }

    public void removeListener(EditorSavedListener listener) {
        removeListener(EditorSavedEvent.class, listener);
    }

    public static class EditorSavedEvent extends Component.Event {

        private String newValue;

        public EditorSavedEvent(Component source, String newValue) {
            super(source);
            this.newValue = newValue;
        }

        public String getValue() {
            return newValue;
        }
    }

    public interface EditorSavedListener extends Serializable {
        public void editorSaved(EditorSavedEvent event);
    }
}