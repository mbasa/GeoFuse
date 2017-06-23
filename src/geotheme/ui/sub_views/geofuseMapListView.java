/**
 * パッケージ名：geotheme.ui.sub_views
 * ファイル名  ：geofuseMapListView.java
 * 
 * @author mbasa
 * @since Jun 22, 2017
 */
package geotheme.ui.sub_views;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.themes.ValoTheme;

import geotheme.db.connectionPoolHolder;

/**
 * 説明：
 *
 */
public class geofuseMapListView extends VerticalLayout implements View {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "geofuseMapListView";
    
    private Logger LOGGER     = LogManager.getLogger();
    private ResourceBundle rb = ResourceBundle.getBundle( 
            "properties/geofuseUI",
            UI.getCurrent().getSession().getLocale()
     );

    /**
     * コンストラクタ
     *
     */
    public geofuseMapListView() {
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

        Label tab_title = new Label( rb.getString("SLDR.PREVIOUS_CAPTION") );
        tab_title.addStyleName(ValoTheme.LABEL_H2);
        tab_title.addStyleName(ValoTheme.LABEL_COLORED);
        tab_title.setSizeUndefined();

        this.addComponents(tlayout,tab_title);
        this.setSpacing(true);
        this.setMargin(true);
        this.setSizeFull();
        
        this.setupTable();
    }

    private final String geofuseURL = "/geofuse/ui/showtheme?layer=";    
    private final Button vueBtn = new Button( rb.getString("BTN.DISPLAY_MAP") );
    
    private void setupTable() {
        
        final Table baseTable = new Table();
        final TableQuery tq   = new TableQuery(null,"geofuse","metadata",
                connectionPoolHolder.getConnectionPool(), 
                new DefaultSQLGenerator() );

        try{
            SQLContainer sqlContainer = new SQLContainer( tq );
            sqlContainer.sort(new Object[] {"ddate"}, new boolean[] {false} );

            baseTable.setContainerDataSource(sqlContainer);
        }
        catch( Exception e ) {
            LOGGER.error( e );
        }
        final BrowserWindowOpener bOpener = new BrowserWindowOpener("");
        bOpener.setWindowName("_geotab");
        bOpener.extend(vueBtn);
        
        baseTable.setSelectable(true);
        baseTable.setNullSelectionAllowed(false);
        baseTable.setSizeFull();
       
        baseTable.setRowHeaderMode(RowHeaderMode.INDEX);
        baseTable.addStyleName(ValoTheme.TABLE_SMALL);
        baseTable.setVisibleColumns("layername","ddate","layertype",
                "linkcolumn", "colnames");
        baseTable.setColumnHeader("layername",  rb.getString("GRID.COL_LAYERNAME"));
        baseTable.setColumnHeader("ddate",      rb.getString("GRID.COL_DDATE"));
        baseTable.setColumnHeader("layertype",  rb.getString("GRID.COL_LAYERTYPE"));
        baseTable.setColumnHeader("linkcolumn", rb.getString("GRID.COL_LINKCOLUMN"));
        baseTable.setColumnHeader("colnames",   rb.getString("GRID.COL_COLNAMES"));
        
        if( baseTable.getItemIds() != null && !baseTable.getItemIds().isEmpty() ) {
            baseTable.select( baseTable.getItemIds().iterator().next()  );
            
            Item row = baseTable.getItem(baseTable.getValue());
            
            bOpener.setUrl( geofuseURL +
                    row.getItemProperty("tabid").getValue() );
        }
        
        baseTable.addItemClickListener( new ItemClickListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void itemClick(ItemClickEvent event) {                
                bOpener.setUrl( geofuseURL + 
                        event.getItem().getItemProperty("tabid").getValue() ); 
            }
        });
        
        vueBtn.setEnabled(true);

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSpacing(true);
        btnLayout.setMargin( new MarginInfo(false,false,true,false) );
        btnLayout.addComponents(vueBtn);
        btnLayout.setSizeUndefined();
        
        this.addComponents(baseTable,btnLayout);
        
        this.setExpandRatio(baseTable, 2.0f);
        this.setExpandRatio(btnLayout, 1.0f);
    }

    /**
     * コンストラクタ
     *
     * @param children
     */
    public geofuseMapListView(Component... children) {
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
