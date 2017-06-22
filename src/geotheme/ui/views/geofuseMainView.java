/**
 * パッケージ名：geotheme.views
 * ファイル名  ：geofuseMainView.java
 * 
 * @author mbasa
 * @since Jun 21, 2017
 */
package geotheme.ui.views;

import geotheme.ui.sub_views.geofuseEntryView;
import geotheme.ui.sub_views.geofuseMapListView;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 説明：
 *
 */
public class geofuseMainView extends HorizontalLayout {

    private static final long serialVersionUID = 1L;

    private Navigator navigator = null;
    private ResourceBundle rb   = ResourceBundle.getBundle( 
            "properties/geofuseUI",
            UI.getCurrent().getSession().getLocale()
          );

    /**
     * コンストラクタ
     *
     * @param children
     */
    public geofuseMainView(Component... children) {
        super(children);
    }

    /**
     * コンストラクタ
     *
     */
    public geofuseMainView() {
        final CssLayout menuLayout = new CssLayout();
        final CssLayout contentLayout = new CssLayout();
        
        menuLayout.addStyleName(ValoTheme.MENU_ROOT);
        menuLayout.addStyleName(ValoTheme.MENU_PART);
        
        contentLayout.addStyleName("valo-content");
        contentLayout.addStyleName("v-scrollable");
        contentLayout.setSizeFull();
        
        this.setSizeFull();
        this.addComponents(menuLayout,contentLayout);
        this.setExpandRatio(contentLayout, 1.0f);
        this.addStyleName(ValoTheme.UI_WITH_MENU);
        
        Responsive.makeResponsive(this);
        
        menuLayout.addComponent( buildMenu( rb ) );
        
        ComponentContainer viewDisplay = contentLayout;
        navigator = new Navigator(UI.getCurrent(),viewDisplay);

        navigator.addView(geofuseEntryView.NAME, geofuseEntryView.class);
        navigator.addView(geofuseMapListView.NAME, geofuseMapListView.class);
        
        navigator.navigateTo(geofuseEntryView.NAME);
        navigator.setErrorView(geofuseEntryView.class);
    }

    private CssLayout buildMenu( final ResourceBundle rb ) {
        //LOGGER.debug("In buidlMenu()");
        
        final CssLayout menu = new CssLayout();
        final CssLayout menuItemsLayout = new CssLayout();
        
        final LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
        
        Button showMenu = new Button("Menu", new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (menu.getStyleName().contains("valo-menu-visible")) {
                    menu.removeStyleName("valo-menu-visible");
                } else {
                    menu.addStyleName("valo-menu-visible");
                }
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName("valo-menu-toggle");
        showMenu.setIcon(FontAwesome.LIST);
        menu.addComponent(showMenu);

        // Add items
        menuItems.put( geofuseEntryView.NAME  , "GeoFuse Input" );
        menuItems.put( geofuseMapListView.NAME, "Map View" );
        menuItems.put( "Three", "Link Data" );
        menuItems.put( "Four", "Help" );

        final Button buttons[] = new Button[menuItems.size()];
        
        final HorizontalLayout top = new HorizontalLayout();
        top.setWidth("100%");
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        menu.addComponent(top);
        
        final Label title = new Label("GeoFuse");
        title.addStyleName(ValoTheme.LABEL_H2);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.setSizeUndefined();
        
        top.addComponent(title);
        top.setExpandRatio(title, 1);

        menuItemsLayout.setPrimaryStyleName("valo-menuitems");
        menu.addComponent(menuItemsLayout);

        int count = 0;
        int icon  = 87;

        List<FontAwesome> ICONS = Collections.unmodifiableList(Arrays
                .asList(FontAwesome.values()));
              
        for (final Entry<String, String> item : menuItems.entrySet()) {
            
            buttons[count] = new Button(item.getValue(), new ClickListener() {
                
                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(final ClickEvent event) {

                    navigator.navigateTo(item.getKey());
                    
                    for(int i=0; i<buttons.length;i++) {
                        buttons[i].removeStyleName("selected");
                        event.getButton().addStyleName("selected");
                    }
                    menu.removeStyleName("valo-menu-visible");
                }                
            });
            
            int selected = 0;

            if( count == selected ) {
                buttons[count].addStyleName("selected");
            }

            buttons[count].setHtmlContentAllowed(true);
            buttons[count].setPrimaryStyleName("valo-menu-item");
            buttons[count].setIcon( ICONS.get(icon++) );
            menuItemsLayout.addComponent(buttons[count]);
            count++;
        }

        return menu;
    }

}
