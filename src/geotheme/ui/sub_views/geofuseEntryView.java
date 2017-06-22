/**
 * パッケージ名：geotheme.ui.sub_views
 * ファイル名  ：geofuseMainView.java
 * 
 * @author mbasa
 * @since Jun 22, 2017
 */
package geotheme.ui.sub_views;

import geotheme.csv.processCSV;
import geotheme.util.UrlUtil;

import java.util.ResourceBundle;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 説明：
 *
 */
public class geofuseEntryView extends VerticalLayout implements View {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "geofuseEntryView";
    
    private ResourceBundle rb = ResourceBundle.getBundle( 
            "properties/geofuseUI",
            UI.getCurrent().getSession().getLocale()
     );
    /**
     * コンストラクタ
     *
     */
    public geofuseEntryView() {
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

        Label descLab = new Label();
        descLab.setContentMode(ContentMode.HTML);
        descLab.setWidth("700px");
        descLab.setValue( rb.getString("MAIN.EXPLAIN") );
        descLab.addStyleName(ValoTheme.LABEL_TINY);
        
        Label dataEntry = new Label("Data Entry");
        dataEntry.addStyleName(ValoTheme.LABEL_LARGE);
        dataEntry.addStyleName(ValoTheme.LABEL_COLORED);
        dataEntry.setSizeUndefined();
        
        Label dataView = new Label("Data View");
        dataView.addStyleName(ValoTheme.LABEL_LARGE);
        dataView.addStyleName(ValoTheme.LABEL_COLORED);
        dataView.setSizeUndefined();
        
        final TextArea csvBox = new TextArea( rb.getString("MSG.CSVBOX_TITLE") );
        csvBox.setWidth("700px");
        csvBox.setRequired(true);
        csvBox.addStyleName(ValoTheme.TEXTAREA_TINY);

        final TextArea layerNameBox = new TextArea( rb.getString("MSG.LAYERNAME_TITLE") );
        layerNameBox.setWidth("350px");
        layerNameBox.setHeight("25px");
        layerNameBox.setRequired(true);
        layerNameBox.addStyleName(ValoTheme.TEXTAREA_TINY);

        final TextArea urlBox = new TextArea();
        urlBox.setWidth("700px");
        urlBox.setHeight("35px");
        urlBox.addStyleName(ValoTheme.TEXTAREA_TINY);
        
        final BrowserWindowOpener bOpener = new BrowserWindowOpener("");
        bOpener.setWindowName("_geotab");

        final Button urlDispButton = new Button(
                rb.getString("BTN.DISPLAY_MAP") );
        urlDispButton.setEnabled(false);

        bOpener.extend(urlDispButton);

        final Button csvSubmitButton = new Button(
                rb.getString("BTN.SUBMIT") );
        
        csvSubmitButton.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {

                if( csvBox.getValue().length() < 10 ) {
                    Notification.show(
                            rb.getString("ERR.NOT_ENOUGH_DATA") );
                    return;
                }

                processCSV pcsv = new processCSV();
                String result   = pcsv.process( 
                        csvBox.getValue(),layerNameBox.getValue() );

                if( result.startsWith("Error") ) {
                    Notification.show( result );
                }
                else {
                    StringBuffer sb = new StringBuffer();

                    sb.append( UrlUtil.getUrl(
                            (VaadinServletRequest)VaadinService.getCurrentRequest()) );
                    sb.append("/ui/showtheme?layer=");
                    sb.append(result);

                    urlBox.setValue( sb.toString() );
                    urlDispButton.setEnabled(true);
                    bOpener.setUrl( sb.toString() );                    
                }
                csvSubmitButton.setEnabled(false);
            }
        });

        final Button csvResetButton = new Button(
                rb.getString("BTN.RESET") );
        
        csvResetButton.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                csvBox.clear();
                csvBox.setValue("");
                layerNameBox.clear();
                layerNameBox.setValue("");
                urlBox.clear();
                urlBox.setValue("");
                bOpener.setUrl("");

                urlDispButton.setEnabled(false);
                csvSubmitButton.setEnabled(true);
            }
        });

        urlDispButton.addStyleName(ValoTheme.BUTTON_TINY);
        csvSubmitButton.addStyleName(ValoTheme.BUTTON_TINY);
        csvResetButton.addStyleName(ValoTheme.BUTTON_TINY);
        
        final HorizontalLayout csvLayout = new HorizontalLayout();
        csvLayout.setSpacing(true);
        csvLayout.addComponents(csvSubmitButton,csvResetButton);


        this.addComponents( tlayout,descLab,dataEntry,csvBox,
                layerNameBox,csvLayout,dataView,urlBox,urlDispButton );
        
        this.setSpacing(true);
        this.setMargin( new MarginInfo(true,true) );
    }

    /**
     * コンストラクタ
     *
     * @param children
     */
    public geofuseEntryView(Component... children) {
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
