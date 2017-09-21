/**
 * 
 */
package geotheme.ui;

import geotheme.bean.baseLayerBean;
import geotheme.bean.metaDataBean;
import geotheme.bean.overlayLayerBean;
import geotheme.bean.showthemeReqBean;
import geotheme.bean.themeBean;
import geotheme.db.baseLayerQuery;
import geotheme.db.featureQuery;
import geotheme.db.markerLayerQuery;
import geotheme.db.metaDataCtl;
import geotheme.db.overlayLayerQuery;
import geotheme.sld.generateSLD;
import geotheme.ui.windows.showthemeGraphWin;
import geotheme.ui.windows.showthemeLegendWin;
import geotheme.ui.windows.showthemePdfWin;
import geotheme.ui.windows.showthemeShareWin;
import geotheme.util.UrlUtil;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.addon.leaflet.*;
import org.vaadin.addon.leaflet.control.LScale;
import org.vaadin.addon.leaflet.shared.Bounds;
import org.vaadin.addon.leaflet.shared.ControlPosition;
import org.vaadin.addon.leaflet.shared.Point;
import org.vaadin.addon.leaflet.shared.PopupState;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author mbasa
 *
 */


@SuppressWarnings("serial")
@Theme("showtheme")
public class showthemeUI extends UI {

    private final Logger LOGGER = LogManager.getLogger();
    
    private ResourceBundle showthemeProps;

    private String geoserverURL  = new String();
    private String googleKey     = new String();
    private String colorNames    = new String();
    private String colorVals     = new String(); 
    private String labelScale    = new String();
    private String themeRanges   = new String();
    private String db_metadata   = new String();
    private String selBaseLayer  = new String();
    
    private LMarker   lmarker    = null;
    private LWmsLayer geofuseWMS = null;

    @WebServlet(value = "/ui/showtheme/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = showthemeUI.class, 
    widgetset = "geotheme.ui.widgetset.GeofuseWidgetset")

    public static class Servlet extends VaadinServlet {
    }

    /**
     * 
     */
    public showthemeUI() {
    }

    /**
     * @param content
     */
    public showthemeUI(Component content) {
        super(content);
    }

    /* (non-Javadoc)
     * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
     */
    @Override
    protected void init(VaadinRequest request) {
        
        final showthemeReqBean strb = new showthemeReqBean();
        
        try {
            BeanUtils.populate( strb, request.getParameterMap() );
        } catch (IllegalAccessException e) {
            LOGGER.error( e );
        } catch (InvocationTargetException e) {
            LOGGER.error( e.getCause() );
        }
        
        if( strb.getLayer() == null ) {
            Notification.show("Layer Parameter Required", Type.ERROR_MESSAGE);
            return;
        }
        
        LOGGER.debug( "processing {}",strb.getLayer() );
        
        final VaadinServletRequest req = 
                (VaadinServletRequest)VaadinService.getCurrentRequest();
        final HttpSession session = req.getSession(true);

        this.showthemeProps = ResourceBundle.getBundle("properties.showtheme",
                req.getLocale());

        final String fullUrl = UrlUtil.getFullUrl(req);
        final String thisUrl = UrlUtil.getUrl(req);
        
        final themeBean tb = 
                this.getThemeBean(req.getParameter("layer"), 
                        thisUrl );
        
        if( tb == null ) {
            Notification.show("Layer is not found in catalog",Type.ERROR_MESSAGE);
            return;
        }

        /**
         * Creating an initial SLD and saving to Session
         */
        final generateSLD gsld = new generateSLD(
                this.geoserverURL,
                this.colorNames,
                this.colorVals);

        gsld.setTypeName  (tb.getLayerName());
        gsld.setGeoType   (tb.getLayerType());
        gsld.setLabScale  (tb.getLabelScale());
        gsld.setViewParams(tb.getViewParams());
        if( strb.getPropName() != null ) {
            gsld.setPropName( strb.getPropName() );
        }
        else {
            gsld.setPropName  ("col1");
        }
        
        if( strb.getCqlString() != null ) {
            tb.setCql( strb.getCqlString() );
            gsld.setCqlString( strb.getCqlString() );
        }
        else {
            gsld.setCqlString ("");
        }
        
        if( strb.getTypeRange() != null ) {
            gsld.setTypeRange( strb.getTypeRange() );
        }
        else {
            gsld.setTypeRange ("EQRange");
        }
        
        if( strb.getColor() != null ) {
            gsld.setColor( strb.getColor() );
        }
        else {
            //gsld.setColor("#fdffe1","#dd0000");
            gsld.setColor     (this.colorNames.split(",")[0]);
        }
        
        if( strb.getNumRange() != null ) {
            gsld.setNumRange( Integer.parseInt(strb.getNumRange()) );
        }
        else {
            gsld.setNumRange  (Integer.parseInt(tb.getThemeRangesArray()[0]) );
        }

        session.setAttribute( tb.getLayerName(), gsld.getSLD() );

        /**
         * Creating the Page Layout and Content
         */

        MarginInfo marginInfo = new MarginInfo(true);
        marginInfo.setMargins(true);

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(marginInfo);
        mainLayout.setSizeFull();

        setContent(mainLayout);

        final HorizontalLayout lowerLayout = new HorizontalLayout();
        lowerLayout.setSizeFull();
        lowerLayout.setSpacing(true);

        final VerticalLayout mapArea = new VerticalLayout();
        final VerticalLayout attArea = new VerticalLayout();
        final VerticalLayout legArea = new VerticalLayout();

        mapArea.setSpacing(true);
        attArea.setSpacing(true);
        attArea.setMargin(true);
        legArea.setSpacing(true);
        legArea.setMargin(true);

        /**
         * Setting the Map Portion
         */
        final LMap lmap = this.setLeaflet( tb,gsld,
                strb.getBaselayer(), strb.getBnd() );

        mapArea.addComponent(lmap);
        mapArea.setComponentAlignment(lmap, Alignment.MIDDLE_CENTER);
        mapArea.setExpandRatio(lmap, 1);
        mapArea.setSizeFull();
        /*
		Panel mapPanel = 
				new Panel(showthemeProps.getString("MW.MAP_TITLE"),mapArea);
		mapPanel.setSizeFull();
         */

        /**
         * Setting the Parameters Portion
         */
        Panel attPanel = 
                new Panel(showthemeProps.getString("MW.PARAMETERS"),attArea);
        
        final ComboBox cbCriteria = new ComboBox(showthemeProps.getString("PW.CRITERIA"));
        cbCriteria.setNullSelectionAllowed(false);
        cbCriteria.setTextInputAllowed(false);
        cbCriteria.setWidth("100%");

        LinkedHashMap<String,String> pm = tb.getPropMap();

        for(String s : pm.keySet()) {
            cbCriteria.addItem(s);
            cbCriteria.setItemCaption(s, pm.get(s));
        }
        cbCriteria.setValue( gsld.getPropName() );

        final ComboBox cbType = new ComboBox(showthemeProps.getString("PW.TYPE"));
        cbType.setNullSelectionAllowed(false);
        cbType.setTextInputAllowed(false);
        cbType.setWidth("100%");

        cbType.addItem("EQRange");
        cbType.setItemCaption("EQRange", showthemeProps.getString("TY.EQ_RANGE"));
        cbType.addItem("EQCount");
        cbType.setItemCaption("EQCount", showthemeProps.getString("TY.EQ_COUNT"));
        cbType.addItem("Natural");
        cbType.setItemCaption("Natural", showthemeProps.getString("TY.NATURAL"));
        cbType.addItem("Geometric");
        cbType.setItemCaption("Geometric", showthemeProps.getString("TY.GEOMETRIC"));
        cbType.addItem("Standard");
        cbType.setItemCaption("Standard", showthemeProps.getString("TY.STANDARD"));

        cbType.setValue( gsld.getTypeRange() );

        final ComboBox cbRanges = new ComboBox(showthemeProps.getString("PW.RANGES"));
        cbRanges.setNullSelectionAllowed(false);
        cbRanges.setTextInputAllowed(false);
        cbRanges.setWidth("100%");

        for(String s : tb.getThemeRangesArray() ) {
            cbRanges.addItem(s);
        }
        cbRanges.setValue( Integer.toString( gsld.getNumRange() ) );

        final ComboBox cbColors = new ComboBox(showthemeProps.getString("PW.COLOR"));
        cbColors.setNullSelectionAllowed(false);
        cbColors.setTextInputAllowed(false);
        cbColors.setWidth("100%");

        for( String s : this.colorNames.split(",")){
            cbColors.addItem(s);
        }
        cbColors.setValue( gsld.getColor() );

        final SimpleDateFormat dateFormat = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = new Date();
        Date toDate   = new Date();

        if( tb.getFromDate() != "" && tb.getToDate() != "" ) {
            try{
                fromDate = dateFormat.parse(tb.getFromDate());
                toDate   = dateFormat.parse(tb.getToDate());
            }
            catch( Exception ex ) {
                ex.printStackTrace();
            }
        }

        final DateField startDate = new DateField(
                showthemeProps.getString("PW.FROMDATE"), fromDate );
        startDate.setDateFormat("yyyy-MM-dd HH:mm");
        startDate.setResolution(Resolution.MINUTE);
        startDate.setRangeStart(fromDate);
        startDate.setRangeEnd(toDate);
        startDate.setWidth("80%");

        final DateField endDate = new DateField(
                showthemeProps.getString("PW.TODATE"), toDate );
        endDate.setDateFormat("yyyy-MM-dd HH:mm");
        endDate.setResolution(Resolution.MINUTE);
        endDate.setRangeStart(fromDate);
        endDate.setRangeEnd(toDate);
        endDate.setWidth("80%");

        if( strb.getFromdate() != null && strb.getTodate() != null ) {
            try {
                startDate.setValue(dateFormat.parse(strb.getFromdate()) );
                endDate.setValue(dateFormat.parse(strb.getTodate()) );
            } catch (Exception e) {
                LOGGER.error( e );
                startDate.setValue( fromDate );
                endDate.setValue( toDate );
            }
        }

        attPanel.setHeight("100%");

        /**
         * setting up the Legend
         */
        ExternalResource legendRes =
                new ExternalResource( setLegendURL(tb) );

        final Image legendImg = new Image( null,legendRes);
        final Button legButton = new Button(
                showthemeProps.getString("MW.LEGEND_BUTTON"));
        final Button legGraphButton = new Button(
                showthemeProps.getString("MW.LEGEND_GRAPH_BUTTON"));

        legButton.addStyleName(ValoTheme.BUTTON_TINY);
        legGraphButton.addStyleName(ValoTheme.BUTTON_TINY);
        
        legButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                final showthemeLegendWin legendWin = new showthemeLegendWin(
                        showthemeProps.getString("LEGEND_WIN.TITLE") );

                legendWin.init( 
                        gsld.getRangeColorsList(),
                        gsld.getRangeValuesList(),
                        showthemeProps );

                Button b = legendWin.getButton1();
                b.addClickListener( new com.vaadin.ui.Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {

                        Table table     = legendWin.getTable();
                        String COLOR_ID = legendWin.getCOLOR_ID();
                        String RANGE_ID = legendWin.getRANGE_ID();

                        ArrayList<String> colorVals = new ArrayList<String>(); 
                        ArrayList<Double> rangeVals = new ArrayList<Double>();

                        for( Object id : table.getItemIds() ) {
                            Item item = table.getItem( id );

                            ColorPicker cp = 
                                    (ColorPicker) item.getItemProperty(COLOR_ID).getValue();

                            cp.hidePopup();
                            colorVals.add( cp.getColor().getCSS() );

                            Double dVal = 
                                    (Double)item.getItemProperty(RANGE_ID).getValue();

                            rangeVals.add( dVal );
                        }

                        legendWin.close();

                        session.setAttribute( tb.getLayerName(), gsld.getSLD(
                                rangeVals,colorVals) );
                        setGeofuseLayer(lmap, tb);

                        legendImg.setVisible(false);

                        ExternalResource legendRes = 
                                new ExternalResource( setLegendURL(tb) );

                        legendImg.setSource(legendRes);
                        legendImg.setVisible(true);
                    }
                });

                getUI().addWindow( legendWin );
            }
        });

        legGraphButton.addClickListener(new Button.ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                LOGGER.debug("{}: {} '{}'",strb.getLayer(),gsld.getPropName(),
                        gsld.getCqlString());
                
                String title = (String) cbCriteria.getItemCaption(
                        cbCriteria.getValue() );
                        
                showthemeGraphWin sgw = new showthemeGraphWin();
                
                sgw.init(title, strb.getLayer(), gsld.getPropName(), 
                        gsld.getRangeValuesList(), gsld.getRangeColorsList(),
                        gsld.getCqlString(), showthemeProps);
                
                getUI().addWindow(sgw);
            }
        });
        
        final Label legLabel = new Label( (String) cbCriteria.getItemCaption(
                cbCriteria.getValue() ) );
        legLabel.addStyleName(ValoTheme.LABEL_H4);
        legLabel.setSizeUndefined();
        
        HorizontalLayout legBtnHolder = new HorizontalLayout();
        legBtnHolder.setSpacing(true);
        legBtnHolder.addComponents(legButton,legGraphButton);
        
        legArea.addComponent(legLabel);
        legArea.addComponent(legendImg);
        legArea.addComponent(legBtnHolder);
        legArea.setComponentAlignment(legLabel    ,Alignment.MIDDLE_CENTER);
        legArea.setComponentAlignment(legendImg   ,Alignment.MIDDLE_CENTER);
        legArea.setComponentAlignment(legBtnHolder,Alignment.MIDDLE_CENTER);

        Panel legPanel = 
                new Panel(showthemeProps.getString("MW.LEGEND"),legArea);	

        legPanel.setHeight("100%");

        /**
         * Setting up the Buttons and Click Events
         */
        final Button button = new Button(showthemeProps.getString("PB.SUBMIT"));
        button.addClickListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {

                if( lmarker != null ) {
                    lmap.removeComponent( lmarker );
                }

                if( tb.getFromDate() != "" && tb.getToDate() != "" ) {
                    tb.setCql("intime >= '"+
                            dateFormat.format(startDate.getValue()) +
                            "' and intime <= '"+
                            dateFormat.format(endDate.getValue()) +
                            "'");
                }

                gsld.setPropName ( (String) cbCriteria.getValue() );
                gsld.setCqlString( tb.getCql().replace("-", "/") );
                gsld.setTypeRange( (String)cbType.getValue() );
                gsld.setColor    ( (String)cbColors.getValue() );
                //gsld.setColor(startColor.getColor().getCSS(),endColor.getColor().getCSS());
                gsld.setNumRange ( Integer.parseInt((String)cbRanges.getValue()) );

                session.setAttribute( tb.getLayerName(), gsld.getSLD() );

                setGeofuseLayer(lmap, tb);

                legendImg.setVisible(false);
                legLabel.setVisible(false);
                
                ExternalResource legendRes = 
                        new ExternalResource( setLegendURL(tb) );

                legLabel.setValue( (String) cbCriteria.getItemCaption(
                        cbCriteria.getValue() ) );                
                legendImg.setSource(legendRes);
                legendImg.setVisible(true);
                legLabel.setVisible(true);
            }
        });

        Button printButton = new Button(showthemeProps.getString("PB.PDF"));

        printButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                showthemePdfWin pdfWin = new showthemePdfWin(
                        showthemeProps.getString("PDF.WIN_TITLE"));

                pdfWin.init( tb, lmap.getBounds(), showthemeProps, 
                        thisUrl );

                getUI().addWindow( pdfWin );
            }
        });
        
        Button shareButton = new Button( showthemeProps.getString("PB.SHARE") );
        shareButton.addClickListener(new Button.ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {               
                
                Bounds bounds = lmap.getBounds();
                
                StringBuffer sb = new StringBuffer();
                sb.append(fullUrl);
                sb.append("?layer=").append(strb.getLayer());
                sb.append("&baselayer=").append(selBaseLayer);
                sb.append("&color=").append(cbColors.getValue());
                sb.append("&propName=").append(cbCriteria.getValue());
                sb.append("&numRange=").append(cbRanges.getValue());
                sb.append("&typeRange=").append(cbType.getValue());
                sb.append("&bnd=");
                sb.append(bounds.getSouthWestLon()).append(",");
                sb.append(bounds.getSouthWestLat()).append(",");
                sb.append(bounds.getNorthEastLon()).append(",");
                sb.append(bounds.getNorthEastLat());
                
                if( gsld.getCqlString() != null && !gsld.getCqlString().isEmpty() ) {
                    sb.append("&cqlString=").append( gsld.getCqlString() );
                }
                                
                if( tb.getFromDate() != "" && tb.getToDate() != "" ) {
                    sb.append("&fromdate=");
                    sb.append( dateFormat.format(startDate.getValue()) );
                    sb.append("&todate=");
                    sb.append( dateFormat.format(endDate.getValue()) );
                }

                showthemeShareWin shareWin = new showthemeShareWin(
                        showthemeProps.getString("SHR.WIN_TITLE") );
                shareWin.init( sb.toString(), showthemeProps );

                getUI().addWindow( shareWin );
                
            }
        });
        button.addStyleName(ValoTheme.BUTTON_TINY);
        printButton.addStyleName(ValoTheme.BUTTON_TINY);
        shareButton.addStyleName(ValoTheme.BUTTON_TINY);
        
        HorizontalLayout buttonHolder = new HorizontalLayout();
        buttonHolder.setSpacing(true);
        buttonHolder.addComponents(button,printButton,shareButton);

        FormLayout formLayout = new FormLayout();

        if( tb.getFromDate() == "" || tb.getToDate() == "" ) {
            formLayout.addComponents(cbCriteria,cbType,cbRanges,cbColors);
        }
        else {
            formLayout.addComponents(cbCriteria,cbType,cbRanges,cbColors,
                    startDate,endDate);
        }

        attArea.addComponents( formLayout,buttonHolder );
        attArea.setComponentAlignment(buttonHolder, Alignment.MIDDLE_CENTER);

        VerticalLayout vAttLayout = new VerticalLayout();
        vAttLayout.setSpacing(true);
        vAttLayout.addComponents(attPanel,legPanel);
        vAttLayout.setHeight("100%");

        lowerLayout.addComponents(mapArea,vAttLayout);

        lowerLayout.setExpandRatio(vAttLayout, 1.0f);
        lowerLayout.setExpandRatio(mapArea   , 3.5f);

        mainLayout.addComponents(lowerLayout);	
    }

    private String setLegendURL( themeBean tb ) {

        StringBuffer isb = new StringBuffer();

        isb.append(tb.getWmsUrl());
        isb.append("?request=GetLegendGraphic");
        isb.append("&layer=").append(tb.getLayerName());
        isb.append("&format=image/png&transparent=TRUE");
        isb.append("&date=").append( (new Date()).getTime() );

        return isb.toString();
    }

    private LMap setLeaflet( final themeBean tb, 
            final generateSLD gsld, String baseLayerName,
            String bndParam ) {

        final LMap lmap = new LMap();

        lmap.addBaseLayerChangeListener(new LeafletBaseLayerChangeListener() {
            
            @Override
            public void onBaseLayerChange(LeafletBaseLayerChangeEvent event) {
                selBaseLayer = event.getName();
            }
        });
        
        /**
         * setting the Base Layers
         */
        this.setBaseMapLayers( lmap, baseLayerName );

        /**
         * setting the WMS Overlay Layers
         */
        this.setOverLayLayers(lmap);

        /**
         * setting the Marker Overlay Layers
         */
        this.setMarkerOverlay(lmap);

        /**
         * setting the GeoFuse Layer
         */
        this.setGeofuseLayer(lmap, tb);

        /**
         * adding Scale Control
         */
        LScale scale = new LScale();
        scale.setMetric(true);
        scale.setPosition(ControlPosition.bottomleft);
        lmap.addControl(scale);

        /**
         * setting EasyPrint
         *
		LEasyPrint easyPrint = new LEasyPrint();
		easyPrint.setPosition(ControlPosition.topleft);
		lmap.addControl(easyPrint);
         */

        if( bndParam != null ) {
            lmap.zoomToExtent( new Bounds( bndParam ) );
        }
        else {
            lmap.zoomToExtent( new Bounds(tb.getBounds()) );
        }
        
        /**
         * adding a click event
         */
        lmap.addClickListener( new LeafletClickListener() {

            @Override
            public void onClick(LeafletClickEvent event) {

                if( lmarker != null ) {
                    lmap.removeComponent( lmarker );
                }

                double tolerance = 10.0;
                double zoomLevel = lmap.getZoomLevel();

                if( zoomLevel <= 3 ) {
                    tolerance = 100000.0;
                }					
                else if( zoomLevel > 3 && zoomLevel <= 6 ) {
                    tolerance = 50000.0;
                }
                else if ( zoomLevel > 6 && zoomLevel <= 8 ) {
                    tolerance = 10000.0;
                }
                else if ( zoomLevel > 8 && zoomLevel <= 9 ) {
                    tolerance = 5000.0;
                }
                else if ( zoomLevel > 9 && zoomLevel <= 11 ) {
                    tolerance = 1000.0;
                }
                else if ( zoomLevel > 11 && zoomLevel <= 12 ) {
                    tolerance = 500.0;
                }
                else if ( zoomLevel > 12 && zoomLevel <= 14 ) {
                    tolerance = 100.0;
                }
                else if ( zoomLevel > 14 && zoomLevel <= 17 ) {
                    tolerance = 20.0;
                }

                ArrayList<Double> rangeList = gsld.getRangeValuesList();

                double minVal = 0.0;
                double maxVal = 0.0;

                if( rangeList != null && rangeList.size() > 0 ) {
                    minVal   = rangeList.get(0);
                    maxVal   = rangeList.get( rangeList.size() - 1 );
                }

                String propName = gsld.getPropName();

                featureQuery fq = new featureQuery( tb,tolerance,
                        propName,minVal,maxVal );

                LinkedHashMap<String,String> retVal  = 
                        fq.getFeature(event.getPoint().getLon(), 
                                event.getPoint().getLat());

                if( retVal == null || retVal.isEmpty() ) {
                    return;
                }

                Point point = event.getPoint();

                if( retVal.get("lon") != null && retVal.get("lat") != null ) { 
                    point =  new Point(Double.parseDouble(retVal.get("lat")),
                            Double.parseDouble(retVal.get("lon")));
                }

                lmarker = new LMarker( point );

                StringBuffer sb = new StringBuffer();
                sb.append("<table class='popups'>");

                for( String s : retVal.keySet() ) {
                    if( (s.equalsIgnoreCase("lon") || s.equalsIgnoreCase("lat")) && 
                            tb.getLayerType().equalsIgnoreCase("line")) {
                        continue;
                    }
                    sb.append("<tr><td><b>").append(s);
                    sb.append("</b></td>");
                    sb.append("<td>").append(retVal.get(s)).append("</td></tr>");
                }

                sb.append("</table>");

                lmarker.setPopup(sb.toString());
                lmarker.openPopup();
                lmap.setCenter( point );
                lmap.addComponent(lmarker);
            }
        });
        return lmap;
    }

    private void setOverLayLayers( LMap lmap ) {

        overlayLayerQuery olq = new overlayLayerQuery();

        ArrayList<overlayLayerBean> overlayLayers = olq.getOverlayLayers();

        if( overlayLayers.size() > 0 ) {
            for(int i=0;i<overlayLayers.size();i++) {
                LWmsLayer wms = new LWmsLayer();
                overlayLayerBean olbean = overlayLayers.get(i);

                wms.setUrl( olbean.getUrl() );
                wms.setLayers( olbean.getLayers() );
                wms.setFormat("image/png");
                wms.setTransparent(true);
                wms.setActive( olbean.isActive() );
                wms.setMinZoom( olbean.getMinZoom() );
                lmap.addOverlay(wms, olbean.getName() );
            }
        }   
    }

    /**
     * Sets the BaseMap Layers of Leaflet
     * @param lmap
     */
    private void setBaseMapLayers( LMap lmap, String baseName ) {

        baseLayerQuery blq = new baseLayerQuery();		

        ArrayList<baseLayerBean> baseLayers = blq.getBaseLayers();

        if( baseLayers.size() > 0 ) {
            LTileLayer tiles[] = new LTileLayer[baseLayers.size()];

            for(int i=0;i<baseLayers.size();i++) {
                baseLayerBean blb = baseLayers.get(i);
                tiles[i] = new LTileLayer();
                tiles[i].setUrl(blb.getUrl());
                tiles[i].setAttributionString(blb.getAttribution());				

                if(blb.getSubDomain() != null && blb.getSubDomain().length() >= 3 ) {
                    tiles[i].setSubDomains(blb.getSubDomain().split(","));
                }
                if( i == 0 && baseName == null) {
                    tiles[i].setActive(true);
                    selBaseLayer = blb.getName();
                }
                else if ( baseName != null && 
                        baseName.equalsIgnoreCase(blb.getName()) ) {
                    tiles[i].setActive(true);
                    selBaseLayer = blb.getName();                    
                }
                else {
                    tiles[i].setActive(false);
                }

                lmap.addBaseLayer(tiles[i], blb.getName());				
            }
        }
    }
    /**
     * Sets the GeoFuse Layer
     * @param lmap
     * @param tb
     */
    private void setGeofuseLayer( LMap lmap, themeBean tb ) {

        if( geofuseWMS != null ) {
            lmap.removeLayer(geofuseWMS);
        }

        HashMap<String,String> customOpts = new HashMap<String,String>();
        customOpts.put("cql_filter", tb.getCql().replace("-", "/"));
        customOpts.put("date", Long.toString(System.currentTimeMillis()));

        geofuseWMS = new LWmsLayer();
        geofuseWMS.setUrl( tb.getWmsUrl() );
        geofuseWMS.setLayers( tb.getLayerName() );
        geofuseWMS.setViewparams(tb.getViewParams());
        geofuseWMS.setCustomOptions( customOpts );
        geofuseWMS.setFormat("image/png");
        geofuseWMS.setTransparent(true);
        geofuseWMS.setActive(true);

        lmap.addOverlay(geofuseWMS, "GeoFuse");				
    }

    /**
     * Adding Marker Overlay Layers
     * 
     * 
     * @param map
     */
    private void setMarkerOverlay( LMap map ) {

        ArrayList<Map<String,String>> markerLayers = 
                markerLayerQuery.getMarkerLayers();

        for( Map<String,String> markerLayer : markerLayers ) {

            ArrayList<Map<String,String>> markers = 
                    markerLayerQuery.getMarkers(markerLayer.get("tablename"));

            if( markers.size() > 0 ) {

                LLayerGroup markerGroup = new LLayerGroup();
                markerGroup.setActive(false);

                for( Map<String,String> marker : markers ) {
                    if( marker.containsKey("lon") && 
                            marker.containsKey("lat") ) {

                        LMarker m  = new LMarker();	
                        double lon = Double.parseDouble(marker.get("lon"));
                        double lat = Double.parseDouble(marker.get("lat"));

                        m.setPoint(new Point(lat,lon));       
                        m.setIcon( new ThemeResource(
                                showthemeProps.getString("MARKER_ICON") ) );
                        m.setIconSize(new Point(20,34));
                        m.setIconAnchor(new Point(20,34));

                        PopupState pstate = new PopupState();
                        pstate.offset    = new Point(-10,-32);
                        pstate.autoClose = true;

                        m.setPopupState(pstate);

                        StringBuffer sb = new StringBuffer();
                        sb.append("<table class='popups'>");

                        for( String s : marker.keySet() ) {
                            if( (s.equalsIgnoreCase("lon") || 
                                    s.equalsIgnoreCase("lat")) ) { 
                                continue;
                            }
                            sb.append("<tr><td><b>").append(s);
                            sb.append("</b></td>");
                            sb.append("<td>").append(marker.get(s));
                            sb.append("</td></tr>");
                        }

                        sb.append("</table>");
                        m.setPopup(sb.toString());

                        markerGroup.addComponent(m);
                    }
                }
                map.addOverlay(markerGroup, markerLayer.get("layername"));
            }	        
        }
    }

    /**
     * 
     * @param layerName
     * @param thisUrl
     * @return
     */
    private themeBean getThemeBean( String layerName,String thisUrl ) {

        this.getPropertiesVals();

        metaDataCtl  mdc = new metaDataCtl(this.db_metadata);        
        metaDataBean mdb = mdc.getMetaInfo(layerName);

        if( mdb == null ) {
            return null;
        }

        String bnd[] = mdc.getLayerBnd(mdb.getMapTable(), 
                mdb.getTabid(), mdb.getLinkColumn());

        String timeStamp[] = mdc.getTimestampStat(mdb.getTabid());

        StringBuffer viewParams = new StringBuffer();

        viewParams.append("linktab:").append(mdb.getTabid()).append(";");
        viewParams.append("maptab:").append(mdb.getMapTable()).append(";");
        viewParams.append("mapcol:").append(mdb.getLinkColumn());

        themeBean tb = new themeBean();

        tb.setLayerName(mdb.getLinkLayer());
        tb.setViewParams(viewParams.toString() );
        tb.setGoogleKey(this.googleKey);
        tb.setColorNames(this.colorNames);
        tb.setGsldUrl(thisUrl + "/gsld");
        tb.setWmsUrl(thisUrl  + "/wms");

        tb.setLinkTab(mdb.getTabid());
        tb.setMapTab(mdb.getMapTable());
        tb.setMapCol(mdb.getLinkColumn());

        tb.setFromDate(timeStamp[0]);
        tb.setToDate(timeStamp[1]);

        tb.setPropList( new ArrayList<String>( 
                Arrays.asList(mdb.getColNames()) ) );

        tb.setBounds(bnd);
        tb.setLayerType(mdb.getLayerType());

        tb.setLabelScale(this.labelScale);
        tb.setThemeRanges(this.themeRanges);

        return tb;
    }
    /**
     * 
     */
    private void getPropertiesVals() {

        ResourceBundle rb = 
                ResourceBundle.getBundle("properties.thematic");

        ResourceBundle rdb = 
                ResourceBundle.getBundle("properties.database");

        this.geoserverURL = rb.getString("GEOSERVER.BASE.URL");

        this.colorNames   = rb.getString("THEMATIC.COLOR.NAMES");
        this.colorVals    = rb.getString("THEMATIC.COLORS");
        this.labelScale   = rb.getString("THEMATIC.LABEL.MAXSCALE");
        this.themeRanges  = rb.getString("THEMATIC.RANGES");

        this.db_metadata  = rdb.getString("DB.METADATA.TABLE");

        try {
            byte[] byteData = this.colorNames.getBytes("ISO_8859_1");
            this.colorNames = new String(byteData, "UTF-8");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

}
