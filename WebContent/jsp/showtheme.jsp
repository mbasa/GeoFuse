<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="geotheme.bean.*" %>
<% themeBean tb = (themeBean)request.getAttribute("themeBean"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<TITLE>Geoserver Thematics</TITLE>
<LINK rel="stylesheet" type="text/css" href="resources/lib/extJS/resources/css/ext-all.css" />

<SCRIPT type="text/javascript" src="resources/lib/OpenLayers/OpenLayers.js"></SCRIPT>
<SCRIPT type="text/javascript" src="resources/lib/extJS/ext-base.js"></SCRIPT>
<SCRIPT type="text/javascript" src="resources/lib/extJS/ext-all.js"></SCRIPT>

<script src="http://maps.googleapis.com/maps/api/js?sensor=false&v=3.6"></script>
    
<SCRIPT type="text/javascript">

  var map;
  var wmsLayer;
  
  var wmsServer    = "<%= tb.getWmsUrl() %>";
  var sldServer    = "<%= tb.getGsldUrl()%>";  
  var mLayers      = "<%= tb.getLayerName() %>";
  var mLayerType   = "<%= tb.getLayerType() %>";
  var mViewParams  = "<%= tb.getViewParams() %>";
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
      Ext.get('mapLegend').update('&nbsp;',true);
      
      var criteria =  Ext.get('tcriteria').dom.value;
      var ranges   =  Ext.get('ranges'  ).dom.value;
      var themetype=  Ext.get('types'   ).dom.value;
      var color    =  Ext.get('colors'  ).dom.value;
      
      var params = "?typename=" +mLayers     +
                   "&geotype="  +mLayerType  +
                   "&propname=" +criteria    +
                   "&numrange=" +ranges      +
                   "&typerange="+themetype   +
                   "&viewparams="+mViewParams+
                   "&labscale=<%= tb.getLabelScale() %>"+
                   "&color="    +color;

      var m_url = sldServer+encodeURI(params);                      

      Ext.Ajax.on('beforerequest'   , showMessage, this);
      Ext.Ajax.on('requestcomplete' , hideMessage, this);
      Ext.Ajax.on('requestexception', hideMessage, this);

      Ext.Ajax.request({
    	   url: m_url,
    	   type:'POST',
    	   success: function() {
               this.setLegend();
               wmsLayer.redraw( true );
               map.setCenter( map.getCenter(),
                              map.getZoom(), 
                              false, true );
               wmsLayer.setVisibility( true );
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
      "&BBOX="+map.getExtent().toBBOX()+
      "&SERVICE=wms"+
      "&DATE="+date.getTime()+" id=\"legend\" /></center>";

      Ext.get("mapLegend").update(imgHtml,true);

  }
  
  function showPDFWin(){
	var formItems = [
                     {
                         fieldLabel: 'Title',
                         id        : 'pdf_title',
                         allowBlank: false,
                         maxLength : 42,
                         minLength : 3,
                         width     : 230,
                         anchor    : '90%',
                         msgTarget : 'under'
                     },
                     {
                         fieldLabel: 'Description',
                         id        : 'pdf_description',
                         allowBlank: false,
                         maxLength : 62,
                         minLength : 3,
                         width     : 230,
                         anchor    : '90%',
                         msgTarget : 'under'
                      }];  

	var formButtons = [
	                   {   text    : "Create PDF",
	                       formBind: true,
	                       disabled: false,
	                       handler : function() {
	                    	   setPDF(Ext.get("pdf_title").dom.value,
	                    			  Ext.get("pdf_description").dom.value);
	                    	   pdfWindow.close();
	                       }
	                   },
	                   { text    : "Cancel",
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
	      title      : 'PDF Report',
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
  
  function setPDF(title,description) {

	  var date     = new Date();
	  var merc     = new OpenLayers.Projection("EPSG:900913");
	  var latlon   = new OpenLayers.Projection("EPSG:4326");
	  
	  var ext      = map.getExtent().transform(merc,latlon);
	  
      var imgHtml  = wmsServer+
      "?REQUEST=GetPDFGraphic"+
      "&FORMAT="+mFormat+
      "&LAYER="+mLayers+
      "&TRANSPARENT=TRUE"+
      "&SRS=EPSG:4326"+            
      "&BBOX="+ext.toBBOX()+
      "&SERVICE=wms"+
      "&VIEWPARAMS="+mViewParams+
      "&PDF_TITLE="+encodeURI(title)+
      "&PDF_NOTE="+encodeURI(description)+
      "&DATE="+date.getTime();

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

      Ext.MessageBox.alert('Status', 'Creating PDF. Depending on your '+
    		  'network connection, this might take some time.');
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
                  tiled      : "TRUE",
                  tilesorigin: [map.maxExtent.left,map.maxExtent.bottom]                        
              },
              { isBaseLayer: false,
              //singleTile: true,
                tileSize: new OpenLayers.Size(512,512), 
                buffer: 0,
                displayInLayerSwitcher: false } 
          );
      
      var googleLayer = new OpenLayers.Layer.Google( 'Google',
              { 'minZoomLevel'     : 1,
                'maxZoomLevel'     : 20,
                'sphericalMercator': true
              },{isBaseLayer: true});
      var googlePhys  = new OpenLayers.Layer.Google( 'Google Physical',
              { 'minZoomLevel'     : 1,
                'maxZoomLevel'     : 20,
                'type'             : google.maps.MapTypeId.TERRAIN,
                'sphericalMercator': true
              },{isBaseLayer: true});
          
       var googleSat   = new OpenLayers.Layer.Google( 'Google Satellite',
              { 'minZoomLevel'     : 1,
                'maxZoomLevel'     : 20,
                'type'             : google.maps.MapTypeId.HYBRID,
                'sphericalMercator': true
              },{isBaseLayer: true});
       
      var osm = new OpenLayers.Layer.OSM(); 
      
      map.addLayers([googleSat,googleLayer,googlePhys,osm,wmsLayer]);

      var geographic = new OpenLayers.Projection("<%= tb.getSrs() %>");
      var mercator   = new OpenLayers.Projection(mSRS);
        
      map.zoomToExtent( boundsGeoserver.transform(geographic, mercator));
      map.addControl(new OpenLayers.Control.LayerSwitcher());

      wmsLayer.setVisibility( false );
  }
  
  Ext.onReady(function() {

      Ext.util.CSS.swapStyleSheet("theme","resources/lib/extJS/resources/css/xtheme-gray.css");
	  Ext.BLANK_IMAGE_URL = 'resources/lib/extJS/resources/images/default/s.gif';

	    var themeCriteria = new Ext.data.SimpleStore({
		    fields: ['idc','criteria'],
		    data  : [<%= tb.getPropList() %>] });
        
	    var themeRange = new Ext.data.SimpleStore({
		    fields: ['id','range'],
		    data  : [<%= tb.getThemeRanges() %>] });
	    
	    var themeType = new Ext.data.SimpleStore({
            fields: ['id','type'],
            data: [['EQRange','Equal Range'],
                   ['EQCount','Equal Count'],
                   ['Natural','Natural Breaks'],
                   ['Standard','Standard Deviation']]});  

        var themeColor = new Ext.data.SimpleStore({
            fields: ['id','color'],
            data: [<%= tb.getColorNames() %>]});
        
	    var viewport = new Ext.Viewport({
	        layout: 'border',
	        id: 'mainpanel',
	        renderTo: Ext.get('tabs1'),
	        items: [	    	    
	            {region: 'east',xtype: 'panel',id: 'east',
		                        split: true, width:250,minSize:250,
		                        maxSize:250,collapsible: true,
		                        collapseMode: 'mini',title: 'Data Thematics',
		                        layout: 'border',		             		                                 
		                        items: [
		                             {region:'north',xtype:'form',	
		                              labelWidth: 55,
                                      bodyStyle: 'padding:15px 15px',   
			                          title: 'Parameters', height:220,
			                          items: [
                                        { xtype: 'combo',    
                                            fieldLabel: 'Criteria',                                           
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
	                                        fieldLabel: 'Ranges',	                                        
	                                        name: 'ranges',
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
	                                        fieldLabel: 'Type',
	                                        name: 'types',
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
                                            fieldLabel: 'Color',
                                            name: 'colors',
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
	                                          }],
	                                      buttons: [{text:'Submit',id:'mapSub'},
	      	                                        {text: 'Reset',id:'mapRes'},
	      	                                        {text: 'Print',id:'mapPrn',
	      	                                         disabled: true,
	      	                                         handler: function()
	      	                                             { showPDFWin(); }
	      	                                        }] 
	      	                              },
	      	                            	                             	      		      	                             
	      	                              {region:'center',xtype:'panel',
	      	                               bodyStyle: 'padding:15px 15px',        
	      		      	                   title:'Legend', autoScroll: true,
	      		      	                   html:'<div id=\'mapLegend\'></div>'}
	      		      	               ]},

		        {region: 'center',xtype: 'panel', title:'Map',html: 
			       '<div id="mapPanel" style="width:100%;height:100%"></div>'}
			     
			]
	    })

        Ext.get('mapSub').on('click', function(){
            setThematics();
            Ext.getCmp('mapPrn').setDisabled(false);
        });
	    
        Ext.get('mapRes').on('click', function() {
            wmsLayer.setVisibility( false );
            Ext.get('mapLegend').update('&nbsp;',true);
            Ext.getCmp('mapPrn').setDisabled(true);
        });
        this.setOpenLayers();        
  })
</SCRIPT>
</HEAD>
<BODY>

<div id="tabs1"></div>

</BODY>
</HTML>