/**
 * パッケージ名：geotheme.ui.sub_views
 * ファイル名  ：geofuseLinkerView.java
 * 
 * @author mbasa
 * @since Jun 23, 2017
 */
package geotheme.ui.sub_views;

import geotheme.bean.linkColBean;
import geotheme.db.DBTools;
import geotheme.db.connectionPoolHolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.StreamResource.StreamSource;
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

/**
 * 説明：
 *
 */
public class geofuseLinkerView extends VerticalLayout implements View {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "geofuseLinkerView";
    
    private Logger LOGGER     = LogManager.getLogger();
    private ResourceBundle rb = ResourceBundle.getBundle( 
            "properties/geofuseUI",
            UI.getCurrent().getSession().getLocale()
     );

    /**
     * コンストラクタ
     *
     */
    public geofuseLinkerView() {
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

        Label tab_title = new Label( rb.getString("SLDR.LINKER") );
        tab_title.addStyleName(ValoTheme.LABEL_H2);
        tab_title.addStyleName(ValoTheme.LABEL_COLORED);
        tab_title.setSizeUndefined();
        
        this.addComponents(tlayout,tab_title);
        this.setSpacing(true);
        this.setMargin(true);
        this.setSizeFull();
        
        this.setupTable();
    }
    
    private void setupTable() {
        final Table baseTable = new Table();        
        final TableQuery tq   = new TableQuery(null,"geofuse","maplinker",
                connectionPoolHolder.getConnectionPool(), 
                new DefaultSQLGenerator() );

        try{
            SQLContainer sqlContainer = new SQLContainer( tq );
            sqlContainer.sort(new Object[] {"mapname"}, new boolean[] {true} );
            /**
             * filtering out the latlon x,y point linker
             */
            sqlContainer.addContainerFilter(new Not(
                    new Compare.Equal("colname","latlon")));

            baseTable.setContainerDataSource(sqlContainer);
        }
        catch( Exception e ) {
            LOGGER.error( e );
        }
        
        baseTable.setSelectable(true);
        baseTable.setNullSelectionAllowed(false);
        baseTable.setSizeFull();
        
        baseTable.setRowHeaderMode(RowHeaderMode.INDEX);
        baseTable.addStyleName(ValoTheme.TABLE_SMALL);
        baseTable.setVisibleColumns("mapname","colname");
        baseTable.setColumnHeader("mapname", rb.getString("GRID.COL_TABLENAME"));
        baseTable.setColumnHeader("colname", rb.getString("GRID.COL_LINKCOLUMN") );
        
        if( baseTable.getItemIds() != null && !baseTable.getItemIds().isEmpty() ) {
            baseTable.select( baseTable.getItemIds().iterator().next()  );
        }
        
        Button exportBtn = new Button( rb.getString("BTN.EXPORT") );
        
        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSpacing(true);
        btnLayout.setMargin( new MarginInfo(false,false,true,false) );
        btnLayout.addComponents( exportBtn );
        btnLayout.setSizeUndefined();
        
        this.addComponents(baseTable,btnLayout);
        StreamResource streamRes = getExcelResource( baseTable );
        
        if( streamRes != null ) {
            final FileDownloader filed = new FileDownloader( streamRes );
            filed.extend( exportBtn );

            baseTable.addItemClickListener(new ItemClickListener() {

                private static final long serialVersionUID = 1L;

                @Override
                public void itemClick(ItemClickEvent event) {
                    LOGGER.debug( event.getItemId() );
                    filed.setFileDownloadResource( getExcelResource(baseTable) );
                }            
            });
        }
        
        this.setExpandRatio(baseTable, 2.0f);
        this.setExpandRatio(btnLayout, 1.0f);
    }
        
    private StreamResource getExcelResource( final Table baseTable ) {
        LOGGER.debug("In getExcelResource");
        
        final int limit = Integer.parseInt( rb.getString("MAX_EXCEL_RECORDS") );
        
        if( baseTable.getValue() == null ) {
            return null;
        }
        
        StreamSource streamSource = new StreamSource() {

            private static final long serialVersionUID = 1L;

            @Override
            public InputStream getStream() {
                LOGGER.debug("Creating excel file");
                
                SXSSFWorkbook workbook = null;
                
                try {
                    Item item = baseTable.getItem( baseTable.getValue() );
                    
                    String colname = (String) item.getItemProperty("colname").getValue();
                    String mapname = (String) item.getItemProperty("mapname").getValue();
                    String sql = "select "+ colname +" as colname from "+ mapname +
                            " where "+ colname +" is not null group by "+ colname +
                            " order by "+ colname +" limit "+ limit;
                    
                    LOGGER.debug("Excel SQL: {}",sql);
                    
                    List<linkColBean> res = DBTools.getRecords(sql, linkColBean.class);

                    if( res == null ) {
                        LOGGER.debug("No MapLinker Data found");
                        return null;
                    }

                    //XSSFWorkbook workbook = new XSSFWorkbook();
                    //XSSFSheet sheet = workbook.createSheet("MapLink");
                    
                    workbook = new SXSSFWorkbook();
                    SXSSFSheet sheet = workbook.createSheet("MapLink");
                    
                    int rowNum = 0;
                    Row row = sheet.createRow(rowNum++);
                    Cell cell = row.createCell(0);
                    cell.setCellValue( colname );
                    
                    for ( linkColBean datatype : res ) {
                        row  = sheet.createRow(rowNum++);
                        cell = row.createCell(0);
                        cell.setCellValue( datatype.getColname() );
                        
                        if( rowNum > limit ) {
                            row  = sheet.createRow(rowNum++);
                            cell = row.createCell(0);
                            cell.setCellValue( 
                                    rb.getString( "MAX_EXCEL_RECORDS_MSG" ) );
                            break;
                        }
                    }
                    
                    ByteArrayOutputStream arrayOutputStream = 
                            new ByteArrayOutputStream();

                    workbook.write( arrayOutputStream );
                    workbook.close();
                    workbook.dispose();

                    return new ByteArrayInputStream(
                            arrayOutputStream.toByteArray());
                }
                catch(Exception e) {
                    LOGGER.error( e,e );
                }
                finally {
                    if( workbook != null ) {
                        try {
                            workbook.close();
                            workbook.dispose();
                        } catch (IOException e) {
                            LOGGER.error( e );
                        }                        
                    }
                }
                return null;
            }
            
        };
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String filen = "table_"+sdf.format( new Date() )+".xlsx";
        
        StreamResource sr = new StreamResource(streamSource, filen );
        sr.setMIMEType("application/vnd.ms-excel");

        return sr;
    }

    /**
     * コンストラクタ
     *
     * @param children
     */
    public geofuseLinkerView(Component... children) {
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
