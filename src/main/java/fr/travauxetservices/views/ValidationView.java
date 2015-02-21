package fr.travauxetservices.views;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.component.AdTable;
import fr.travauxetservices.component.ValidatedComboBox;
import fr.travauxetservices.component.WrapperLayout;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.User;
import fr.travauxetservices.services.Mail;
import fr.travauxetservices.tools.I18N;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class ValidationView extends Panel implements View {
    private AdTable offerTable;
    private AdTable requestTable;
    private ComboBox validatedField;

    private JPAContainer offerContainer;
    private JPAContainer requestContainer;

    public ValidationView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        CustomEventBus.register(this);

        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        setContent(root);
        Responsive.makeResponsive(root);
        root.addStyleName("mytheme-view");

        root.addComponent(buildHeader());
        root.addComponent(buildSearchLayout());
        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1.0f);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label label = new Label(I18N.getString("menu.validation"));
        label.setSizeUndefined();
        label.addStyleName(ValoTheme.LABEL_H1);
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(label);

        return header;
    }

    private Component buildSearchLayout() {
        GridLayout layout = new GridLayout(4, 2);
        //HorizontalLayout layout = new HorizontalLayout();
        layout.addStyleName("dark");

        validatedField = new ValidatedComboBox(null);
        validatedField.setInputPrompt("Validated");
        validatedField.setPageLength(20);
        validatedField.setScrollToSelectedItem(true);
        validatedField.addStyleName(ValoTheme.COMBOBOX_TINY);
        validatedField.setImmediate(true);
        validatedField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                applyFilters();
            }
        });
        validatedField.setValue(Boolean.FALSE);
        layout.addComponent(validatedField, 1, 0);

        Button search = new Button(I18N.getString("button.search"), FontAwesome.SEARCH);
        search.addStyleName(ValoTheme.BUTTON_TINY);
        search.addStyleName(ValoTheme.BUTTON_DANGER);
        search.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                applyFilters();
            }
        });
        layout.addComponent(search, 3, 0);

        return layout;
    }

    private Component buildContent() {
        TabSheet tabsheet = new TabSheet();
        tabsheet.addStyleName("framed");
        tabsheet.addTab(buildOfferContent(), I18N.getString("menu.offers"), FontAwesome.SHARE_SQUARE);
        tabsheet.addTab(buildRequestContent(), I18N.getString("menu.requests"), FontAwesome.SHARE_SQUARE_O);
        return tabsheet;
    }

    private Component buildOfferContent() {
        VerticalLayout layout = new VerticalLayout();
        offerTable = new AdTable(10);
        offerTable.setEditable(true);
        offerTable.addStyleName(ValoTheme.TABLE_SMALL);
        offerTable.setWidth(100, Unit.PERCENTAGE);

        applyFilters();
        offerTable.setContainerDataSource(getOfferContainer());

        offerTable.addGeneratedColumn("created", new AdTable.ValueColumnGenerator());
        offerTable.addGeneratedColumn("user", new AdTable.ValueColumnGenerator());
        offerTable.addGeneratedColumn("title", new AdTable.ValueColumnGenerator());
        offerTable.addGeneratedColumn("validated", new Table.ColumnGenerator() {
            public Object generateCell(Table source, Object itemId, Object columnId) {
                final CheckBox field = new CheckBox();
                final Item item = source.getItem(itemId);
                final Property property = item.getItemProperty(columnId);
                field.setValue((Boolean) property.getValue());
                field.setReadOnly((Boolean) property.getValue());
                field.addValueChangeListener(new Property.ValueChangeListener() {
                    public void valueChange(Property.ValueChangeEvent event) {
                        if ((Boolean) event.getProperty().getValue()) {
                            property.setValue(event.getProperty().getValue());

                            String title = (String) item.getItemProperty("title").getValue();
                            User user = (User) item.getItemProperty("user").getValue();
                            String url = AppUI.getEncodedUrl() + "/#!" + ViewType.OFFER.getViewName() + "/" + item;
                            String subject = I18N.getString("message.ad.online.subject", new String[]{title});
                            String text = I18N.getString("message.ad.online.text", new String[]{title, url});
                            applyFilters();
                            Mail.sendMail("smtp.numericable.fr", "thierry.linxe@numericable.fr", user.getEmail(), subject, text, false);
                        }
                    }
                });
                return field;
            }
        });
        offerTable.setTableFieldFactory(new TableFieldFactory() {
            public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                Field field = DefaultFieldFactory.get().createField(container, itemId, propertyId, uiContext);
                if (!"validated".equals(propertyId)) {
                    field.setReadOnly(true);
                }
                return field;
            }
        });
        offerTable.setCellStyleGenerator(new Table.CellStyleGenerator() {
            public String getStyle(Table source, Object itemId, Object propertyId) {
                return "view";
            }
        });
        offerTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent e) {
                UI.getCurrent().getNavigator().navigateTo(ViewType.OFFER.getViewName() + "/" + e.getItem());
            }
        });

        offerTable.setVisibleColumns("created", "user", "title", "validated");
        offerTable.setColumnHeaders(I18N.getString("ad.created"), I18N.getString("ad.user"), I18N.getString("ad.title"), I18N.getString("ad.validated"));
        offerTable.setColumnWidth("created", 120);
        offerTable.setColumnWidth("validated", 50);
        offerTable.setColumnExpandRatio("title", 1);

        layout.addComponent(offerTable);
        layout.addComponent(offerTable.createControls());
        return layout;
    }

    private Component buildRequestContent() {
        VerticalLayout layout = new VerticalLayout();

        requestTable = new AdTable(10);
        requestTable.setEditable(true);
        requestTable.addStyleName(ValoTheme.TABLE_SMALL);
        requestTable.setWidth(100, Unit.PERCENTAGE);

        applyFilters();
        requestTable.setContainerDataSource(getRequestContainer());

        requestTable.addGeneratedColumn("created", new AdTable.ValueColumnGenerator());
        requestTable.addGeneratedColumn("user", new AdTable.ValueColumnGenerator());
        requestTable.addGeneratedColumn("title", new AdTable.ValueColumnGenerator());
        requestTable.addGeneratedColumn("validated", new Table.ColumnGenerator() {
            public Object generateCell(Table source, Object itemId, Object columnId) {
                final CheckBox field = new CheckBox();
                final Item item = source.getItem(itemId);
                final Property property = item.getItemProperty(columnId);
                field.setValue((Boolean) property.getValue());
                field.setReadOnly((Boolean) property.getValue());
                field.addValueChangeListener(new Property.ValueChangeListener() {
                    public void valueChange(Property.ValueChangeEvent event) {
                        if ((Boolean) event.getProperty().getValue()) {
                            property.setValue(event.getProperty().getValue());

                            String title = (String) item.getItemProperty("title").getValue();
                            User user = (User) item.getItemProperty("user").getValue();
                            String url = AppUI.getEncodedUrl() + "/#!" + ViewType.REQUEST.getViewName() + "/" + item;
                            String subject = I18N.getString("message.ad.online.subject", new String[]{title});
                            String text = I18N.getString("message.ad.online.text", new String[]{title, url});
                            System.out.println("title: " + title + " url: " + url);
                            Mail.sendMail("smtp.numericable.fr", "thierry.linxe@numericable.fr", user.getEmail(), subject, text, false);
                            applyFilters();
                        }
                    }
                });
                return field;
            }
        });
        requestTable.setTableFieldFactory(new TableFieldFactory() {
            public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                Field field = DefaultFieldFactory.get().createField(container, itemId, propertyId, uiContext);
                if (!"validated".equals(propertyId)) {
                    field.setReadOnly(true);
                }
                return field;
            }
        });
        requestTable.setCellStyleGenerator(new Table.CellStyleGenerator() {
            public String getStyle(Table source, Object itemId, Object propertyId) {
                return "view";
            }
        });
        requestTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent e) {
                UI.getCurrent().getNavigator().navigateTo(ViewType.REQUEST.getViewName() + "/" + e.getItem());
            }
        });

        requestTable.setVisibleColumns("created", "user", "title", "validated");
        requestTable.setColumnHeaders(I18N.getString("ad.created"), I18N.getString("ad.user"), I18N.getString("ad.title"), I18N.getString("ad.validated"));
        requestTable.setColumnWidth("created", 120);
        requestTable.setColumnWidth("validated", 50);
        requestTable.setColumnExpandRatio("title", 1);

        layout.addComponent(requestTable);
        layout.addComponent(requestTable.createControls());
        return layout;
    }

    private JPAContainer getOfferContainer() {
        if (offerContainer == null) {
            offerContainer = AppUI.getDataProvider().getOfferContainer();
        }
        return offerContainer;
    }

    private JPAContainer getRequestContainer() {
        if (requestContainer == null) {
            requestContainer = AppUI.getDataProvider().getRequestContainer();
        }
        return requestContainer;
    }

    private JPAContainer applyFilters() {
        JPAContainer offerContainer = getOfferContainer();
        offerContainer.removeAllContainerFilters();
        if (validatedField.getValue() != null) {
            offerContainer.addContainerFilter(new Compare.Equal("validated", validatedField.getValue()));
        }
        if (offerTable != null) {
            offerTable.refreshRowCache();
            offerTable.setCurrentPage(1);
        }

        JPAContainer requestContainer = getRequestContainer();
        requestContainer.removeAllContainerFilters();
        if (validatedField.getValue() != null) {
            requestContainer.addContainerFilter(new Compare.Equal("validated", validatedField.getValue()));
        }
        if (requestTable != null) {
            requestTable.refreshRowCache();
            requestTable.setCurrentPage(1);
        }
        return offerContainer;
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        final User user = getCurrentUser();
        if (user == null) {
            UI.getCurrent().getNavigator().navigateTo(ViewType.HOME.getViewName());
            return;
        }
        applyFilters();
    }
}
