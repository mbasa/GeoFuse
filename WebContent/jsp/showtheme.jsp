<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="geotheme.bean.*" %>
<%@ page import="java.util.*" %>
<% 

  themeBean tb  = (themeBean)request.getAttribute("themeBean"); 
  Locale locale = request.getLocale();

  ResourceBundle rb = ResourceBundle.getBundle
          ("properties.showtheme",locale);

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<TITLE>Geoserver Thematics</TITLE>
<LINK rel="stylesheet" type="text/css" 
      href="resources/lib/extJS/resources/css/ext-all.css" />
<!-- 
<SCRIPT type="text/javascript" src="resources/lib/OpenLayers/OpenLayers.js"></SCRIPT>
 -->
<script src="http://openlayers.org/dev/OpenLayers.js"></script>

<SCRIPT type="text/javascript" src="resources/lib/extJS/ext-base.js"></SCRIPT>
<SCRIPT type="text/javascript" src="resources/lib/extJS/ext-all.js"></SCRIPT>

<script src="http://maps.googleapis.com/maps/api/js?sensor=false&v=3.6"></script>

<script type="text/javascript" 
        src="http://portal.cyberjapan.jp/sys/v4/webtis/webtis_v4.js" 
        charset="UTF-8"></script>     

<SCRIPT type="text/javascript">

  var map;
  var wmsLayer;
  var wmsLayer_stile;
  
  var wmsServer    = "<%= tb.getWmsUrl() %>";
  var sldServer    = "<%= tb.getGsldUrl()%>";  
  var mLayers      = "<%= tb.getLayerName() %>";
  var mLayerType   = "<%= tb.getLayerType() %>";
  var mViewParams  = "<%= tb.getViewParams() %>";
  var mFromDate    = "<%= tb.getFromDate() %>";
  var mToDate      = "<%= tb.getToDate() %>";
  
  var mFormat      = "image/png";
  var mSRS         = "EPSG:900913"; //"EPSG:4326";

  function showMessage() {
      Ext.MessageBox.show({
          msg: 'Creating Theme on '+Ext.get('criteriaId').dom.value,
          progressText: 'Creating...',
          width:300,
          wait:true,
          waitConfig: {interval:200},
        //icon:'ext-mb-download', //custom class in msg-box.html
          animEl: 'mb7'
      });
  }

  function hideMessage() {
          Ext.MessageBox.hide();
  }
  
  function setThematics() {
	  wmsLayer.setVisibility( false );
      wmsLayer_stile.setVisibility( false );
      
      Ext.get('mapLegend').update('&nbsp;',true);
      
      var criteria =  Ext.get('tcriteria').dom.value;
      var ranges   =  Ext.get('ranges'  ).dom.value;
      var themetype=  Ext.get('types'   ).dom.value;
      var color    =  Ext.get('colors'  ).dom.value;
      var cql      = "";
      
      if( mFromDate != "" && mToDate != "" ) {
    	  cql = "intime >= '"+Ext.get('fromDateId').dom.value+
    	   "' and intime <= '"+Ext.get('toDateId').dom.value+"'";
      }
      
      var params = "?typename=" +mLayers     +
                   "&geotype="  +mLayerType  +
                   "&propname=" +criteria    +
                   "&numrange=" +ranges      +
                   "&typerange="+themetype   +
                   "&viewparams="+mViewParams+
                   "&labscale=<%= tb.getLabelScale() %>"+
                   "&cql="+cql+
                   "&color="+color;

      var m_url = sldServer+encodeURI(params);                      

      Ext.Ajax.on('beforerequest'   , showMessage, this);
      Ext.Ajax.on('requestcomplete' , hideMessage, this);
      Ext.Ajax.on('requestexception', hideMessage, this);

      Ext.Ajax.request({
    	   url: m_url,
    	   type:'POST',
    	   success: function() {
    		   
    		   map.setCenter( map.getCenter(),
                       map.getZoom(), 
                       false, true );
    		   
    		   if( themetype != "Heatmap" ) {
    			   this.setLegend();
    			   wmsLayer.mergeNewParams({'cql_filter':cql});
                   wmsLayer.redraw( true );
                   wmsLayer.setVisibility( true );
    		   }
    		   else {
    			   wmsLayer_stile.mergeNewParams({'styles': criteria});
                   wmsLayer_stile.redraw( true );
    			   wmsLayer_stile.setVisibility(true);
    		   }    		                                 
           }
      });
  }

  function setLegend() {

      var date     = new Date();
      var imgHtml  = 
      "<center>"+Ext.get('criteriaId').dom.value+"<br /><br />"+    
      "<img border=0 src="+wmsServer+
      "?REQUEST=GetLegendGraphic"+
      "&FORMAT="+mFormat+
      "&LAYER="+mLayers+
      "&TRANSPARENT=TRUE"+
      "&SRS="+mSRS+     
      "&STYLES=aaa"+
      "&BBOX="+map.getExtent().toBBOX()+
      "&SERVICE=wms"+
      "&DATE="+date.getTime()+" id=\"legend\" /></center>";

      Ext.get("mapLegend").update(imgHtml,true);

  }
  
  function showPDFWin(){
	var formItems = [
                     {
                         fieldLabel: '<%= rb.getString("PDF.TITLE") %>',
                         id        : 'pdf_title',
                         allowBlank: false,
                         maxLength : 42,
                         minLength : 3,
                         width     : 230,
                         anchor    : '90%',
                         msgTarget : 'under'
                     },
                     {
                         fieldLabel: '<%= rb.getString("PDF.DESC") %>',
                         id        : 'pdf_description',
                         allowBlank: false,
                         maxLength : 62,
                         minLength : 3,
                         width     : 230,
                         anchor    : '90%',
                         msgTarget : 'under'
                      }];  

	var formButtons = [
	                   {   text    : "<%= rb.getString("PDF.CREATE_BTN") %>",
	                       formBind: true,
	                       disabled: false,
	                       handler : function() {
	                    	   setPDF(Ext.get("pdf_title").dom.value,
	                    			  Ext.get("pdf_description").dom.value);
	                    	   pdfWindow.close();
	                       }
	                   },
	                   { text    : "<%= rb.getString("PDF.CANCEL_BTN") %>",
                           formBind: false,
                           disabled: false,
                           handler : function() {
                        	   pdfWindow.close();
                           }
                       } ];
	
	var pdfFormPanel = new Ext.FormPanel({
        id            : 'pdfFormPanelId',
        monitorValid  : true,
        labelWidth    : 65,
        frame         : true,
        bodyStyle     : "padding:5px 5px 0",
        autoWidth     : true,
        autoHeight    : true,
        defaultType   : "textfield",
        buttonAlign   : 'center',
        items         : formItems,
        buttons       : formButtons
      });
	
	var pdfWindow = new Ext.Window({
	      id         : 'pdfWindowId',
	      title      : '<%= rb.getString("PDF.WIN_TITLE") %>',
	      width      : 450,
	      autoheight : true,
	      hideBorders: true,
	      resizable  : true,
	      closable   : true,
	      modal      : true,
	      items      : [ pdfFormPanel],
	    });
	
	pdfWindow.show();
	
  }

  function showShareWin( url ){
	    var formItems = [
	                     {
	                         fieldLabel: 'URL',
	                         id        : 'shareUrl',
	                         allowBlank: false,
	                         value     : url,
	                         maxLength : 442,
	                         minLength : 3,
	                         width     : 400,
	                         anchor    : '95%',
	                         msgTarget : 'under'
	                     }];  

	    var formButtons = [
	                       {   text    : "<%= rb.getString("SHR.SEL_BTN") %>",
	                           formBind: false,
	                           disabled: false,
	                           handler : function() {
	                        	   document.getElementById("shareUrl").select();
	                           }
	                       },
	                       { text    : "<%= rb.getString("SHR.CLOSE_BTN") %>",
	                           formBind: false,
	                           disabled: false,
	                           handler : function() {
	                               shareWindow.close();
	                           }
	                       } ];
	    
	    var shareFormPanel = new Ext.FormPanel({
	        id            : 'shareFormPanelId',
	        monitorValid  : true,
	        labelWidth    : 25,
	        frame         : true,
	        bodyStyle     : "padding:5px 5px 0",
	        autoWidth     : true,
	        autoHeight    : true,
	        defaultType   : "textfield",
	        buttonAlign   : 'center',
	        items         : formItems,
	        buttons       : formButtons
	      });
	    
	    var shareWindow = new Ext.Window({
	          id         : 'shareWindowId',
	          title      : '<%= rb.getString("SHR.WIN_TITLE") %>',
	          width      : 450,
	          autoheight : true,
	          hideBorders: true,
	          resizable  : true,
	          closable   : true,
	          modal      : true,
	          items      : [ shareFormPanel],
	        });
	    
	    shareWindow.show();
  }

  function getUrlVars() {
	    var vars = {};
	    var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, 
	    		function(m,key,value) { vars[key] = value; });
	    return vars;
  }
  
  function shareView() {

      var url = /*window.location.protocol + "://" +*/ 
    	  "http://"+ window.location.host + 
                window.location.pathname;
      
      var sLayer    = this.getUrlVars()["layer"];
      var sBnd      = map.getExtent();
      
      var sCriteria = Ext.get('tcriteria').dom.value;
      var sTypes    = Ext.get('types'    ).dom.value;
      var sColors   = Ext.get('colors'   ).dom.value;
      var sRanges   = Ext.get('ranges'   ).dom.value;
      var sFromDate = Ext.get('fromDateId').dom.value;
      var sToDate   = Ext.get('toDateId'  ).dom.value;
    	  
      var sBaseLayer= map.baseLayer.id;
      
      url = url.concat("?layer=").concat(sLayer);
      url = url.concat("&bnd=").concat(sBnd);
      url = url.concat("&criteria=").concat(sCriteria);
      url = url.concat("&types=").concat(sTypes);
      url = url.concat("&ranges=").concat(sRanges);
      url = url.concat("&colors=").concat(sColors);
      url = url.concat("&baselayer=").concat(sBaseLayer);
      url = url.concat("&fromDate=").concat(sFromDate);
      url = url.concat("&toDate=").concat(sToDate);
      
      showShareWin( url );
  }

  function setFromParams() {

	  var pParams   = this.getUrlVars();
	  
	  var pRanges   = pParams["ranges"];
      var pCriteria = pParams["criteria"];
      var pTypes    = pParams["types"];
      var pColors   = pParams["colors"];
      var pBnd      = pParams["bnd"];
      var pBaseLayer= pParams["baselayer"];
      var pFromDate = pParams["fromDate"];
      var pToDate   = pParams["toDate"];
      
      if( pRanges != null )
    	  Ext.getCmp('rangesId').setValue(pRanges);
      if( pCriteria != null )
    	  Ext.getCmp('criteriaId').setValue(pCriteria);
      if( pTypes != null )
    	  Ext.getCmp('typesId').setValue(pTypes);
      if( pColors )
    	  Ext.getCmp('colorsId').setValue(pColors);
      
      if( pBnd != null ){
    	  var tBnd = pBnd.split(',');
    	  map.zoomToExtent(new OpenLayers.Bounds(
    			  tBnd[0],tBnd[1],tBnd[2],tBnd[3]) );
      }
      
      if( pBaseLayer != null ) {    	  
    	  var bLayer = map.getLayer( pBaseLayer );
    	  map.setBaseLayer( bLayer );
      }
      
      if(pFromDate != null && pToDate != null) {
    	  Ext.getCmp('fromDateId').setValue(pFromDate);
    	  Ext.getCmp('toDateId').setValue(pToDate);
      }
  }
  
  function setPDF(title,description) {

	  var date     = new Date();
	  var merc     = new OpenLayers.Projection("EPSG:900913");
	  var latlon   = new OpenLayers.Projection("EPSG:4326");
	  
	  var ext      = map.getExtent().transform(merc,latlon);
	  
	  var criteria =  Ext.get('tcriteria').dom.value;
	  var themetype=  Ext.get('types'   ).dom.value;
      var cql      = "";
      
      if( mFromDate != "" && mToDate != "" ) {
          cql = "intime >= '"+Ext.get('fromDateId').dom.value+
           "' and intime <= '"+Ext.get('toDateId').dom.value+"'";
      }
      
	  if( themetype == "Heatmap" ) {
		  var imgHtml  = wmsServer+
		    "?REQUEST=GetPDFGraphic"+
		    "&FORMAT="+mFormat+
		    "&LAYERS="+mLayers+
		    "&TRANSPARENT=TRUE"+
		    "&SRS=EPSG:4326"+            
		    "&BBOX="+ext.toBBOX()+
		    "&SERVICE=wms"+
		    "&STYLES="+criteria+
		    "&VIEWPARAMS="+mViewParams+
		    "&PDF_TITLE="+encodeURI(title)+
		    "&PDF_NOTE="+encodeURI(description)+
		    "&CQL_FILTER="+cql+
		    "&DATE="+date.getTime();
	  }
	  else {
		  var imgHtml  = wmsServer+
		    "?REQUEST=GetPDFGraphic"+
		    "&FORMAT="+mFormat+
		    "&LAYERS="+mLayers+
		    "&TRANSPARENT=TRUE"+
		    "&SRS=EPSG:4326"+            
		    "&BBOX="+ext.toBBOX()+
		    "&SERVICE=wms"+
		    "&VIEWPARAMS="+mViewParams+
		    "&PDF_TITLE="+encodeURI(title)+
		    "&PDF_NOTE="+encodeURI(description)+
		    "&CQL_FILTER="+cql+
		    "&DATE="+date.getTime();
	  }
	  
      //window.open( imgHtml );
      
      var iframe;
      iframe = document.getElementById("hiddenDownloader");
      
      if (iframe === null)
      {
          iframe = document.createElement('iframe');  
          iframe.id = "hiddenDownloader";
          iframe.style.visibility = 'hidden';
          document.body.appendChild(iframe);
      }
      
      iframe.src = imgHtml; 

      Ext.MessageBox.alert('<%= rb.getString("PDF.ALERT_TITLE") %>', 
    		  '<%= rb.getString("PDF.ALERT_MSSG") %> ' );
  }
  
  function setOpenLayers() {
      
      // pink tile avoidance
      OpenLayers.IMAGE_RELOAD_ATTEMPTS = 5;
      // make OL compute scale according to WMS spec
      OpenLayers.DOTS_PER_INCH = 25.4 / 0.28;
  
      var bounds = new OpenLayers.Bounds(
              15001225.42105, 4028842.88629, 15174890.34929, 4196698.60039);
              
      var boundsGeoserver = new OpenLayers.Bounds(
              <%= tb.getBounds() %>);
  
      var options = {
              projection: mSRS,
              units: 'm',
            //restrictedExtent: bounds,
              maxResolution: 156543.0339,
              maxExtent: new OpenLayers.Bounds(-20037508, -20037508,
                                               20037508, 20037508)           
       };
  
	  map = new OpenLayers.Map('mapPanel',options);

	  wmsLayer = new OpenLayers.Layer.WMS(
              "Geoserver Presentation", 
              wmsServer,        
              {
                  transparent: true,
                  layers     : mLayers,
                  format     : mFormat,
                  viewparams : mViewParams,
                //tiled      : "false",
                //styles     : "col1",
                  tilesorigin: [map.maxExtent.left,map.maxExtent.bottom]                        
              },
              { isBaseLayer: false,
                singleTile : false,
                tileSize   : new OpenLayers.Size(512,512), 
                buffer     : 0,
                displayInLayerSwitcher: false } 
          );
	  
	  wmsLayer_stile = new OpenLayers.Layer.WMS(
              "Geoserver Presentation", 
              wmsServer,        
              {
                  transparent: true,
                  layers     : mLayers,
                  format     : mFormat,
                  viewparams : mViewParams,
                //tiled      : "false",
                //styles     : "col1",
                  tilesorigin: [map.maxExtent.left,map.maxExtent.bottom]                        
              },
              { isBaseLayer: false,
                singleTile : true,
                tileSize   : new OpenLayers.Size(512,512), 
                buffer     : 0,
                displayInLayerSwitcher: false } 
          );
      
      var googleLayer = new OpenLayers.Layer.Google( 'Google',
              { 'minZoomLevel'     : 1,
                'maxZoomLevel'     : 20,
                'sphericalMercator': true
              },{isBaseLayer: true});
      
      var googlePhys  = new OpenLayers.Layer.Google( 'GooglePhysical',
              { 'minZoomLevel'     : 1,
                'maxZoomLevel'     : 20,
                'type'             : google.maps.MapTypeId.TERRAIN,
                'sphericalMercator': true
              },{isBaseLayer: true});
          
       var googleSat   = new OpenLayers.Layer.Google( 'GoogleSatellite',
              { 'minZoomLevel'     : 1,
                'maxZoomLevel'     : 20,
                'type'             : google.maps.MapTypeId.HYBRID,
                'sphericalMercator': true
              },{isBaseLayer: true});
       
      var osm   = new OpenLayers.Layer.OSM(); 
      var osmgr = new OpenLayers.Layer.OSM('OSM GrayMap', null, {
          eventListeners: {
              tileloaded: function(evt) {
                  var ctx = evt.tile.getCanvasContext();
                  if (ctx) {
                      var imgd = ctx.getImageData(0, 0, evt.tile.size.w, evt.tile.size.h);
                      var pix = imgd.data;
                      for (var i = 0, n = pix.length; i < n; i += 4) {
                          pix[i] = pix[i + 1] = pix[i + 2] = (3 * pix[i] + 
                        		  4 * pix[i + 1] + pix[i + 2]) / 8;
                      }
                      ctx.putImageData(imgd, 0, 0);
                      evt.tile.imgDiv.removeAttribute("crossorigin");
                      evt.tile.imgDiv.src = ctx.canvas.toDataURL();
                  }
              }
          }
      });

      var webtisMap = new webtis.Layer.BaseMap("電子国土Web");

      map.addLayers([googleSat,googleLayer,googlePhys,osm,osmgr,webtisMap,
                     wmsLayer,wmsLayer_stile]);

      var geographic = new OpenLayers.Projection("<%= tb.getSrs() %>");
      var mercator   = new OpenLayers.Projection(mSRS);
        
      map.zoomToExtent( boundsGeoserver.transform(geographic, mercator));
      
      map.addControl(new OpenLayers.Control.LayerSwitcher());
      map.addControl(new OpenLayers.Control.Attribution());

      wmsLayer.setVisibility( false );
      wmsLayer_stile.setVisibility( false );
  }
  
  Ext.onReady(function() {

      Ext.util.CSS.swapStyleSheet("theme",
    		  "resources/lib/extJS/resources/css/xtheme-gray.css");
	  Ext.BLANK_IMAGE_URL = 
		  'resources/lib/extJS/resources/images/default/s.gif';

	    var themeCriteria = new Ext.data.SimpleStore({
		    fields: ['idc','criteria'],
		    data  : [<%= tb.getPropList() %>] });
        
	    var themeRange = new Ext.data.SimpleStore({
		    fields: ['id','range'],
		    data  : [<%= tb.getThemeRanges() %>] });
	    
	    var themeType;
        
	    themeType = new Ext.data.SimpleStore({
            fields: ['id','type'],
            data: [['EQRange' ,'<%= rb.getString("TY.EQ_RANGE") %>'],
                   ['EQCount' ,'<%= rb.getString("TY.EQ_COUNT") %>'],
                   ['Natural' ,'<%= rb.getString("TY.NATURAL") %>'],
                   ['Standard','<%= rb.getString("TY.STANDARD") %>']]});
        
        /** For HeatMap enabled	    
	    if( mLayerType == "point" ) {
	        themeType = new Ext.data.SimpleStore({
                fields: ['id','type'],
                data: [['EQRange' ,'<%= rb.getString("TY.EQ_RANGE") %>'],
                       ['EQCount' ,'<%= rb.getString("TY.EQ_COUNT") %>'],
                       ['Natural' ,'<%= rb.getString("TY.NATURAL") %>'],
                       ['Standard','<%= rb.getString("TY.STANDARD") %>'],
                       ['Heatmap' ,'<%= rb.getString("TY.HEATMAP") %>']]});
	    }
	    else {
	    	themeType = new Ext.data.SimpleStore({
	    	    fields: ['id','type'],
	    	    data: [['EQRange' ,'<%= rb.getString("TY.EQ_RANGE") %>'],
                       ['EQCount' ,'<%= rb.getString("TY.EQ_COUNT") %>'],
                       ['Natural' ,'<%= rb.getString("TY.NATURAL") %>'],
                       ['Standard','<%= rb.getString("TY.STANDARD") %>']]});  
	    }
	    **/
	    
        var themeColor = new Ext.data.SimpleStore({
            fields: ['id','color'],
            data: [<%= tb.getColorNames() %>]});
     
	    var dispDates = false;
	    
	    if(mFromDate == "" && mToDate == "") {
	    	dispDates = true;
	    }
	    
	    var viewport = new Ext.Viewport({
	        layout: 'border',
	        id: 'mainpanel',
	        renderTo: Ext.get('tabs1'),
	        items: [	    	    
	            {region: 'east',xtype: 'panel',id: 'east',
		                        split: true, width:250,minSize:250,
		                        maxSize:250,collapsible: true,
		                        collapseMode: 'mini',
		                        title: '<%= rb.getString("MW.DATA_TITLE") %>',
		                        layout: 'border',		             		                                 
		                        items: [
		                             {region:'north',xtype:'form',	
		                              labelWidth: 55,
                                      bodyStyle: 'padding:15px 15px',   
			                          title: '<%= rb.getString("MW.PARAMETERS") %>', 
			                          height:255,
			                          items: [
                                        { xtype: 'combo',    
                                            fieldLabel: '<%= rb.getString("PW.CRITERIA") %>',                                           
                                            name: 'criteria',
                                            id: 'criteriaId',
                                            mode: 'local',
                                            store: themeCriteria,
                                            hiddenName: 'tcriteria',
                                            valueField: 'idc',
                                            displayField: 'criteria',
                                            forceSelection:true,
                                            allowBlank: false,
                                            editable: false,
                                            triggerAction: 'all',
                                            value: 'col1', 
                                            //value: <%= tb.getFirstProp() %>',
                                            width: 150
                                          },
			                              { xtype: 'combo',    
	                                        fieldLabel: '<%= rb.getString("PW.RANGES") %>',	                                        
	                                        name: 'ranges',
	                                        id: 'rangesId',
	                                        hiddenName: 'ranges',
	                                        mode: 'local',
	                                        store: themeRange,
	                                        valueField: 'id',
	                                        displayField: 'range',
	                                        forceSelection:true,
	                                        allowBlank: false,
	                                        editable: false,
	                                        triggerAction: 'all',
	                                        value: <%= tb.getFirstRange() %>,
	                                        width: 150
	                                      },
	                                      { xtype: 'combo',
	                                        fieldLabel: '<%= rb.getString("PW.TYPE") %>',
	                                        name: 'types',
	                                        id: 'typesId',
	                                        hiddenName: 'types',
	                                        mode: 'local',
	                                        store: themeType,
	                                        valueField: 'id',
	                                        displayField: 'type',
	                                        forceSelection:true,
	                                        allowBlank: false,	  
	                                        editable: false,     
	                                        triggerAction: 'all',    
	                                        value: 'EQRange',                                 
	                                        width: 150
	                                      },
	                                      { xtype: 'combo',
                                            fieldLabel: '<%= rb.getString("PW.COLOR") %>',
                                            name: 'colors',
                                            id: 'colorsId',
                                            hiddenName: 'colors',
                                            mode: 'local',
                                            store: themeColor,
                                            valueField: 'id',
                                            displayField: 'color',
                                            forceSelection:true,
                                            allowBlank: false,    
                                            editable: false,     
                                            triggerAction: 'all',    
                                            value: '<%= tb.getFirstColor() %>',                                 
                                            width: 150
	                                      },
	                                      {
	                                    	  xtype: 'datefield',
	                                    	  id: 'fromDateId',
	                                    	  fieldLabel:'<%= rb.getString("PW.FROMDATE") %>',
	                                    	  width: 150,
	                                    	  format: 'Y/m/d',
	                                    	  editable: false,
	                                    	  disabled: dispDates,
	                                    	  value: mFromDate.substring(0,10),
	                                    	  minValue: mFromDate.substring(0,10),
	                                    	  maxValue: mToDate.substring(0,10)
	                                      },
	                                      {
                                              xtype: 'datefield',
                                              id: 'toDateId',
                                              fieldLabel:'<%= rb.getString("PW.TODATE") %>',
                                              width: 150,
                                              format: 'Y/m/d',
                                              editable: false,
                                              disabled: dispDates,
                                              value: mToDate.substring(0,10),
                                              minValue: mFromDate.substring(0,10),
                                              maxValue: mToDate.substring(0,10)
                                          }],
	                                      buttons: [{text:'<%= rb.getString("PB.SUBMIT") %>',
	                                    	         id:'mapSub', 
	                                    	         width:'52'},
	      	                                        {text: '<%= rb.getString("PB.RESET") %>',
	                                    	    	 id:'mapRes', 
	                                    	    	 width:'52'},
	      	                                        {text: '<%= rb.getString("PB.PDF") %>',
	                                    	         id:'mapPrn', 
	                                    	         width:'52',
	      	                                         disabled: false,
	      	                                         handler: function()
	      	                                             { showPDFWin(); }
	      	                                        },
	      	                                        {text: "<%= rb.getString("PB.SHARE") %>",
	      	                                         id: 'mapShare',
	      	                                         width:'52',
	      	                                         handler: function() 
	      	                                           {shareView();} }
                                                   ] 
	      	                              },
	      	                            	                             	      		      	                             
	      	                              {region:'center',xtype:'panel',
	      	                               bodyStyle: 'padding:15px 15px',        
	      		      	                   title:'<%= rb.getString("MW.LEGEND") %>', 
	      		      	                   autoScroll: true,
	      		      	                   html:'<div id=\'mapLegend\'></div>'}
	      		      	               ]},

		        {region: 'center',xtype: 'panel', 
	      		 title:'<%= rb.getString("MW.MAP_TITLE") %>',
	      		 html:'<div id="mapPanel" style="width:100%;height:100%"></div>'}
			     
			]
	    })

        Ext.get('mapSub').on('click', function(){
            setThematics();
            Ext.getCmp('mapPrn'  ).setDisabled(false);
            Ext.getCmp('mapShare').setDisabled(false);
        });
	    
        Ext.get('mapRes').on('click', function() {
            wmsLayer.setVisibility( false );
            wmsLayer_stile.setVisibility( false );
            Ext.get('mapLegend').update('&nbsp;',true);
            Ext.getCmp('mapPrn'  ).setDisabled(true);
            Ext.getCmp('mapShare').setDisabled(true);
        });
        this.setOpenLayers(); 
        this.setFromParams();
        this.setThematics();
  })
</SCRIPT>
</HEAD>
<BODY>

<div id="tabs1"></div>

</BODY>
</HTML>