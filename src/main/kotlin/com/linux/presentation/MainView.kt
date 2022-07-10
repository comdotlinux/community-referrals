package com.linux.presentation

import com.linux.auth.control.AuthenticationControl
import com.linux.contact.control.ContactDataControl
import com.linux.contact.entity.Contact
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentUtil
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.component.tabs.TabsVariant
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLink
import io.quarkus.narayana.jta.QuarkusTransaction
import javax.annotation.PostConstruct
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations
import kotlin.streams.asSequence


//@CssImport("./styles/views/main/main-view.css")
//@JsModule("./styles/shared-styles.js")
@Route(value = "main")
class MainView(@Inject var authControl: AuthenticationControl, @Inject var contactDataControl: ContactDataControl) : AppLayout() {

    val menu: Tabs = createMenu()
    val viewTitle: H1 = H1()

    @PostConstruct
    fun init() {
        QuarkusTransaction.run {
            if (contactDataControl.contactAbsent(authControl.email())) {
                contactDataControl.persistAndFlush(Contact(email = authControl.email(), givenName = authControl.givenName(), familyName = authControl.familyName()))
            }
        }
        primarySection = Section.DRAWER
        isDrawerOpened = false
        addToNavbar(true, createHeaderContent())
        addToDrawer(createDrawerContent(menu))
    }

    private fun createHeaderContent(): Component {
        val layout = HorizontalLayout()

        // Configure styling for the header
        layout.setId("header")
        layout.themeList["dark"] = true
        layout.setWidthFull()
        layout.isSpacing = false
        layout.alignItems = FlexComponent.Alignment.CENTER

        // Have the drawer toggle button on the left

        // Have the drawer toggle button on the left
        layout.add(DrawerToggle())

        // Placeholder for the title of the current view.
        // The title will be set after navigation.

        // Placeholder for the title of the current view.
        // The title will be set after navigation.
        layout.add(viewTitle)

        // A user icon

        // A user icon
        layout.add(Image("images/Community-Logo.jpeg", "Community Logo").apply { maxHeight = "5em"; maxWidth = "5em" })

        return layout
    }

    private fun createDrawerContent(menu: Tabs): Component = VerticalLayout().apply {
        setSizeFull()
        isPadding = false
        isSpacing = false
        themeList["spacing-s"] = true
        alignItems = FlexComponent.Alignment.STRETCH

        // Have a drawer header with an application logo
        add(HorizontalLayout().apply {
            setId("logo")
            alignItems = FlexComponent.Alignment.CENTER
            add(Icon(VaadinIcon.CALENDAR_USER))
            add(H4("Community Referrals"))
        }, menu)
    }

    private fun createMenu(): Tabs = Tabs().apply {
        orientation = Tabs.Orientation.VERTICAL
        addThemeVariants(TabsVariant.LUMO_MINIMAL)
        setId("tabs")
        val jobsTab = createTab("Jobs", JobsView::class)
        add(jobsTab)
        selectedTab = jobsTab
//            createTab("Card List", CardListView::class.java),
//            createTab("About", AboutView::class.java)
    }

    private fun createTab(text: String, navigationTarget: KClass<JobsView>): Tab = Tab().apply {
        add(RouterLink(text, navigationTarget.java))
        ComponentUtil.setData(this, Class::class.java, navigationTarget.java)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun afterNavigation() {
        super.afterNavigation()

        // Select the tab corresponding to currently shown view
        val component = menu.children.asSequence().firstOrNull() { ComponentUtil.getData(it, Class::class.java) == content.javaClass }
        if (component is Tab) {
            menu.selectedTab = component
        }

        // Set the view title in the header

        viewTitle.text = content::class.findAnnotations<PageTitle>().firstOrNull()?.value ?: ""
    }
}