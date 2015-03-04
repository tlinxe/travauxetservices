package fr.travauxetservices.component;

import com.vaadin.ui.ComboBox;
import fr.travauxetservices.model.Remuneration;
import fr.travauxetservices.tools.I18N;

/**
 * Created by Phobos on 05/01/15.
 */
public class RemunerationComboBox extends ComboBox {
    public RemunerationComboBox(String caption) {
        setCaption(caption);
        addItem(Remuneration.HALF);
        setItemCaption(Remuneration.HALF, Remuneration.HALF.getLabel());
        addItem(Remuneration.TIME);
        setItemCaption(Remuneration.TIME, Remuneration.TIME.getLabel());
        addItem(Remuneration.DAY);
        setItemCaption(Remuneration.DAY, Remuneration.DAY.getLabel());
        addItem(Remuneration.WEEK);
        setItemCaption(Remuneration.WEEK, Remuneration.WEEK.getLabel());
        addItem(Remuneration.MONTH);
        setItemCaption(Remuneration.MONTH, Remuneration.MONTH.getLabel());
        addItem(Remuneration.TASK);
        setItemCaption(Remuneration.TASK, Remuneration.TASK.getLabel());
        addItem(Remuneration.PART);
        setItemCaption(Remuneration.PART, Remuneration.PART.getLabel());
        addItem(Remuneration.EXCHANGE);
        setItemCaption(Remuneration.EXCHANGE, Remuneration.EXCHANGE.getLabel());
    }
}
