package com.linux.presentation;

import com.linux.auth.control.AuthenticationControl;
import com.linux.contact.control.ContactDataControl;
import com.linux.contact.entity.Contact;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.router.*;
import io.quarkus.narayana.jta.QuarkusTransaction;

import javax.inject.Inject;
import java.util.*;

//@CssImport("./styles/views/main/main-view.css")
//@JsModule("./styles/shared-styles.js")
@Route(value = "main")
public class MainView extends AppLayout {
    private final AuthenticationControl authControl;
    private final ContactDataControl contactDataControl;

    private final Tabs menu;

    private H1 viewTitle;


    @Inject
    public MainView(AuthenticationControl authControl, ContactDataControl contactDataControl) {
        this.authControl = authControl;
        this.contactDataControl = contactDataControl;

        QuarkusTransaction.run(() -> {
            var contact = contactDataControl.findOrCreate(authControl.email());
            contact.familyName = authControl.familyName();
            contact.givenName = authControl.givenName();
            contactDataControl.persistAndFlush(contact);
        });

        viewTitle = new H1();
        setPrimarySection(Section.DRAWER);
        setDrawerOpened(false);
        addToNavbar(true, createHeaderContent());

        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
    }

    private Component createHeaderContent() {
        HorizontalLayout header = new HorizontalLayout();
        header.setId("header");
        header.getThemeList().set("dark", true);
        header.setWidthFull();
        header.setSpacing(false);
//        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        // Have the drawer toggle button on the left
        header.add(new DrawerToggle());

        // Placeholder for the title of the current view.
        // The title will be set after navigation.
        viewTitle = new H1();
        header.add(viewTitle);

        // A user icon
        var logoImage = new Image("images/Community-Logo.jpeg", "Community Logo");
        logoImage.setMaxHeight("5em");
        logoImage.setMaxWidth("5em");
        header.add(logoImage);
        header.expand(logoImage);
        header.add(new Button("Log out", e -> authControl.logout()));
        return header;
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();

        // Configure styling for the drawer
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        // Have a drawer header with an application logo
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new Icon(VaadinIcon.CALENDAR_USER));
        logoLayout.add(new H4("Community Referrals"));

        // Display the logo and the menu in the drawer
        layout.add(logoLayout, menu);
        return layout;
    }

    private Tabs createMenu() {
        var tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        var jobsTab = createTab("Jobs", JobsView.class);
        tabs.add(jobsTab);
        tabs.setSelectedTab(jobsTab);
        return tabs;
    }

    private Tab createTab(String text, Class<? extends Component> navigationTarget) {
        var tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);

        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
            .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return Optional.ofNullable(getContent().getClass().getAnnotation(PageTitle.class)).map(PageTitle::value).orElse("");
    }
}