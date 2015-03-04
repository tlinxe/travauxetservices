package fr.travauxetservices.component;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by Phobos on 25/02/15.
 */
public class WrapperFormLayout extends FormLayout {
    public WrapperFormLayout() {

    }

    public void addWrapComponent(final Component c) {
        final Component old = getComponent(getComponentCount() - 1);
        if (old != null) {
            WrapperField field = new WrapperField(old, c);
            field.setSizeUndefined();
            replaceComponent(old, field);
        }
    }

    private class WrapperField extends CustomField {
        private HorizontalLayout layout;

        public WrapperField(Component c1, Component c2) {
            setCaption(c1.getCaption());
            String stylename = c1.getStyleName();
            if (stylename == null || !stylename.contains(ValoTheme.TEXTFIELD_INLINE_ICON)) {
                setIcon(c1.getIcon());
            }
            if (c1 instanceof AbstractField) {
                setRequired(((AbstractField) c1).isRequired());
                ((AbstractField) c1).setRequired(false);

                setValidationVisible(((AbstractField) c1).isValidationVisible());
                ((AbstractField) c1).setValidationVisible(false);
            }
            c1.setCaption(null);
            if (stylename == null || !stylename.contains(ValoTheme.TEXTFIELD_INLINE_ICON)) {
                c1.setIcon(null);
            }
            layout = new HorizontalLayout();
            layout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
            layout.setWidthUndefined();
            layout.setSpacing(true);
            layout.addComponent(c1);
            layout.addComponent(buildForm(c2));
        }

        private FormLayout buildForm(Component c) {
            c.setSizeUndefined();
            FormLayout form = new FormLayout();
            form.setSizeUndefined();
            form.setSpacing(false);
            form.setMargin(false);
            form.addComponent(c);
            return form;
        }

        @Override
        protected Component initContent() {
            return layout;
        }

        public Class<Object> getType() {
            return Object.class;
        }
    }
}
