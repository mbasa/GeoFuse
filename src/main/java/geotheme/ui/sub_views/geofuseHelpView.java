/**
 * パッケージ名：geotheme.ui.sub_views
 * ファイル名  ：geofuseHelpView.java
 * 
 * @author mbasa
 * @since Jun 26, 2017
 */
package geotheme.ui.sub_views;

import java.util.ResourceBundle;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 説明：
 *
 */
public class geofuseHelpView extends VerticalLayout implements View {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "geofuseHelpView";
    
    private ResourceBundle rb = ResourceBundle.getBundle( 
            "properties/geofuseUI",
            UI.getCurrent().getSession().getLocale()
    );
    
    /**
     * コンストラクタ
     *
     */
    public geofuseHelpView() {
        ThemeResource tr = new ThemeResource("graphics/Favicon16x16.png");
        Image logo  = new Image(null,tr);
        
        Label header = new Label( rb.getString( "MAIN.TITLE") );
        header.addStyleName(ValoTheme.LABEL_H1);
        header.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.setSizeUndefined();

        Label desc = new Label( rb.getString("MAIN.DESC") );
        desc.addStyleName(ValoTheme.LABEL_H4);
        desc.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        desc.setSizeUndefined();
        
        VerticalLayout vlayout = new VerticalLayout();
        vlayout.addComponents(header,desc);
        
        HorizontalLayout tlayout = new HorizontalLayout();
        tlayout.addComponents(logo,vlayout);
        tlayout.setComponentAlignment(logo   , Alignment.TOP_LEFT);
        tlayout.setComponentAlignment(vlayout, Alignment.MIDDLE_CENTER);
        tlayout.setSpacing(true);

        CustomLayout html = new CustomLayout( rb.getString("TUTORIAL.HTML") );
        html.setWidth("100%");
        
        this.setSpacing(true);
        this.setMargin(true);
        this.addComponents( tlayout,html );

    }

    /**
     * コンストラクタ
     *
     * @param children
     */
    public geofuseHelpView(Component... children) {
        super(children);
    }

    /* (非 Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
        // TODO 自動生成されたメソッド・スタブ

    }

}
