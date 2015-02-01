package fr.travauxetservices.component;

import com.vaadin.ui.ComboBox;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.Remuneration;

/**
 * Created by Phobos on 05/01/15.
 */
public class RemunerationComboBox extends ComboBox {
    public RemunerationComboBox(String caption) {
        setCaption(caption);
        addItem(Remuneration.TIME);
        setItemCaption(Remuneration.TIME, AppUI.I18N.getString("remuneration.time"));
        addItem(Remuneration.DAY);
        setItemCaption(Remuneration.DAY, AppUI.I18N.getString("remuneration.day"));
        addItem(Remuneration.TASK);
        setItemCaption(Remuneration.TASK, AppUI.I18N.getString("remuneration.task"));
        addItem(Remuneration.PART);
        setItemCaption(Remuneration.PART, AppUI.I18N.getString("remuneration.part"));
        addItem(Remuneration.EXCHANGE);
        setItemCaption(Remuneration.EXCHANGE, AppUI.I18N.getString("remuneration.exchange"));
    }
}
