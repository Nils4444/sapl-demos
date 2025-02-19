package io.sapl.vaadindemo.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.vaadin.annotation.annotations.OnDenyNavigate;
import io.sapl.vaadindemo.shared.Utilities;

/**
 * This page demonstrates the rerouting with the @OnDenyNavigate annotation for all users except admins.
 */
@OnDenyNavigate(value = "/", subject = "{roles: getAuthorities().![getAuthority()]}", environment="'environment information'")
@PageTitle("Annotation Page")
@Route(value = "annotation-page", layout = MainLayout.class)
public class AnnotationPage extends VerticalLayout {

	private static final long serialVersionUID = -6554370250256114924L;

    public AnnotationPage() {
        add(Utilities.getInfoText("The \"Annotation Page\" uses the @OnDenyNavigate annotation to secure this site. " +
                "Only as an admin you will be able to see this site."));
        add(Utilities.getDefaultHeader("Annotation Page"));
        Utilities.setEmptyLayout(this);
    }
}
