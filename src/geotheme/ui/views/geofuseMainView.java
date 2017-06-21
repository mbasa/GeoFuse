/**
 * パッケージ名：geotheme.views
 * ファイル名  ：geofuseMainView.java
 * 
 * @author mbasa
 * @since Jun 21, 2017
 */
package geotheme.ui.views;

import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 説明：
 *
 */
public class geofuseMainView extends HorizontalLayout {

    private static final long serialVersionUID = 1L;

    private Navigator navigator = null;
    private ResourceBundle rb   = ResourceBundle.getBundle( 
            "properties/lang/MainView",
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

    }

    private CssLayout buildMenu( final ResourceBundle rb ) {
        //LOGGER.debug("In buidlMenu()");
        
        final CssLayout menu = new CssLayout();
        final CssLayout menuItemsLayout = new CssLayout();
        
        final LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
        
        return menu;
    }

}
