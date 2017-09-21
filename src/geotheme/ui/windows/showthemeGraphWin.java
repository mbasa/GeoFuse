/**
 * パッケージ名：geotheme.ui.windows
 * ファイル名  ：showthemeGraphWin.java
 * 
 * @author mbasa
 * @since Sep 7, 2017
 */
package geotheme.ui.windows;

import geotheme.db.DBTools;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Highlighter;
import org.dussan.vaadin.dcharts.options.Legend;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.dussan.vaadin.dcharts.renderers.series.PieRenderer;
import com.vaadin.annotations.Theme;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 説明：
 *
 */
@SuppressWarnings("serial")
@Theme("geofuse")
public class showthemeGraphWin extends Window {

    private final Logger LOGGER = LogManager.getLogger();

    /**
     * コンストラクタ
     *
     */
    public showthemeGraphWin() {
    }

    /**
     * コンストラクタ
     *
     * @param caption
     */
    public showthemeGraphWin(String caption) {
        super(caption);
    }

    /**
     * コンストラクタ
     *
     * @param caption
     * @param content
     */
    public showthemeGraphWin(String caption, Component content) {
        super(caption, content);
    }
    
    public void init(String title, String layerName,
            String propName, ArrayList<Double> ranges,
            ArrayList<String> colors,
            String cql,
            ResourceBundle showthemeProps) {
        
        this.setModal(true);
        this.setResizable(false);
        this.setClosable(false);
        
        this.setHeight("420px");
        this.setWidth("750px");
        
        String sql     = createCountSql(layerName,propName,ranges,cql);
        DCharts dchart = makePieGraph(sql, ranges, colors);
        
        LOGGER.debug( sql );
        
        dchart.setWidth("720px");
        dchart.setHeight("350px");
        dchart.show();
        
        Label labTitle = new Label( title );
        labTitle.setSizeUndefined();
        labTitle.addStyleName( ValoTheme.LABEL_BOLD );
        labTitle.addStyleName( ValoTheme.LABEL_SMALL );
        labTitle.addStyleName( ValoTheme.LABEL_NO_MARGIN );
            
        Button closeBtn = new Button(
                showthemeProps.getString("SHR.CLOSE_BTN"));
        closeBtn.addClickListener(new Button.ClickListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeWin();                
            }
        });
        
        VerticalLayout winLayout = new VerticalLayout();
        winLayout.setSizeFull();
        winLayout.addComponents(labTitle,dchart,closeBtn);
        winLayout.setComponentAlignment(labTitle, Alignment.MIDDLE_CENTER);
        winLayout.setComponentAlignment(dchart,   Alignment.MIDDLE_CENTER);
        winLayout.setComponentAlignment(closeBtn, Alignment.MIDDLE_CENTER);
        winLayout.setMargin(new MarginInfo(true,true,false,false));
        winLayout.setExpandRatio(closeBtn, 1.0f);
        
        this.setContent(winLayout);
    }

    private DCharts makePieGraph( String sql, 
            ArrayList<Double> ranges,
            ArrayList<String> colors) {
        
        Object dataDB[] = DBTools.getSingleRecord( sql );
        DataSeries dataSeries = new DataSeries();

        NumberFormat formatter = new DecimalFormat("###,###,###.###");
        
        for(int k=0;k<dataDB.length;k++) {
            StringBuffer sb = new StringBuffer();
            sb.append(formatter.format(ranges.get(k) ) );
            sb.append(" ~ ");
            sb.append(formatter.format(ranges.get(k+1) ) );
            
            dataSeries.newSeries().add(sb.toString(),dataDB[k]);
        }

        PieRenderer pieRenderer = new PieRenderer();
        pieRenderer.setShowDataLabels(true);
        pieRenderer.setFill( true );
        pieRenderer.setSliceMargin(7);
        pieRenderer.setLineWidth(1);
        pieRenderer.setShadowDepth(8);
        pieRenderer.setStartAngle(90);
        
        SeriesDefaults seriesDefaults = new SeriesDefaults();
        seriesDefaults.setRenderer(SeriesRenderers.PIE);
        seriesDefaults.setRendererOptions( pieRenderer );
        
        Legend legend = new Legend();
        legend.setShow(true);
        
        Highlighter hLighter = new Highlighter();
        hLighter.setShow(true);
        hLighter.setShowTooltip(true);
        hLighter.setTooltipAlwaysVisible(true);
        hLighter.setKeepTooltipInsideChart(true);
        
        Options options = new Options();
        options.setSeriesDefaults(seriesDefaults);
        options.setLegend(legend);
        options.setHighlighter(hLighter);
        //options.setSeriesColors("#CCAACC","#FFDDDD","#CCBBFF");
        
        String clr[] = new String[ colors.size() ];
        for(int c=0;c<colors.size();c++) {
            clr[c] = colors.get(c);
        }
        options.setSeriesColors( clr );
        
        DCharts dChart = new DCharts();
        dChart.setDataSeries(dataSeries);
        dChart.setOptions(options);
        
        return dChart;

    }
        
    private String createCountSql(String layerName,
            String propName, ArrayList<Double> ranges,
            String cql ) {
        
        int count = ranges.size() - 1;
        StringBuffer sb = new StringBuffer();
        
        sb.append("select ");
        
        for(int i=0;i<count-1;i++) {
            sb.append("count(").append(propName).append(" >= ");
            sb.append(ranges.get(i));
            sb.append(" and ").append(propName).append( " < ");
            sb.append(ranges.get(i+1));
            sb.append(" or null), ");
        }
        
        sb.append("count(").append(propName).append(" >= ");
        sb.append(ranges.get(count-1));
        sb.append(" and ").append(propName).append( " <= ");
        sb.append(ranges.get(count));
        sb.append(" or null) from ").append( layerName );
                        
        if( cql != null && !cql.isEmpty() ) {
            sb.append(" where ").append( cql );
        }
        return sb.toString();
    }
    
    private void closeWin() {
        this.close();
    }

}
