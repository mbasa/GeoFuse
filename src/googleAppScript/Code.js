/**
 *
 * A special function that runs when the spreadsheet is open, used to add a
 * custom menu to the spreadsheet.
 *
 */
function onOpen() {
  var spreadsheet = SpreadsheetApp.getActive();
  var menuItems = [
    {name: 'Send Selected Data', functionName: 'sendSelectedData_'},
    {name: 'About', functionName: 'about_'}
  ];
  spreadsheet.addMenu('GeoFuse', menuItems);
}

/**
 *
 * Display an About Dialog Box
 *
 */
function about_() {
  var ui   = SpreadsheetApp.getUi();
  var html = HtmlService.createHtmlOutputFromFile('About')
    .setWidth(400)  
    .setHeight(300);
  
  ui.showModalDialog( html,"About" );
}

/**
 *
 * Function to Send Selected Range to Geofuse.
 *
 */
function sendSelectedData_() {
  var ui = SpreadsheetApp.getUi();
  
  var t_range = SpreadsheetApp.getActive().getActiveRange();
  var t_data  = t_range.getValues();

  if( !t_data || t_data.length < 2 ) {
    ui.alert("Selected Range is empty or too few data");  
    return;
  }
  
  var layerName = "NoName";
  var response  = ui.prompt("Enter Layer Name",ui.ButtonSet.OK_CANCEL);
  
  if(  response.getSelectedButton()  == ui.Button.CANCEL ) {
    return;
  }
  
  if( response.getResponseText().length > 0  ) {
    layerName = response.getResponseText();
  }
  
  var csv   = getCsvActiveRange_( t_data );
  var reply = sendCsv_( layerName,csv ); 
  
  if( reply.getContentText().indexOf("E") == 0 ) {
    Browser.msgBox( reply );
  }
  else {
    var html = '<html><body>Display Layer: <a href="'+ reply +
      '" target="_geotab" onclick="google.script.host.close()">' +
      layerName + '</a></body></html>';
    
    var html_srv = HtmlService.createHtmlOutput(html)
      .setWidth(300)
      .setHeight(120);
    
    ui.showModalDialog(html_srv,"GeoFuse Map");
  }
  
}

/**
 *
 * Function to convert the Selected Range into CSV
 *
 */
function getCsvActiveRange_( t_data ) {
  
  var csv = "";
  
  for( var row=0; row<t_data.length; row++ ) {
    for( var col=0; col<t_data[row].length-1; col++ ) {
      csv += t_data[row][col] + ",";
    }
    csv += t_data[row][col] + "\r\n";
  }

  return csv;
}

/**
 *
 * Sending the Layer Name  and CSV into GeoFuse
 *
 */
function sendCsv_( layerName,csv ) {

  var url = "http://geofuse.georepublic.net/geofuseV2/indata";
  var payload = { 
    "data"      : csv,//encodeURIComponent(csv),
    "layername" : layerName
  };
  var options = {
    "method"  : "POST",
    "payload" : payload
  };
  
  var reply = UrlFetchApp.fetch( url, options );
  return reply; 
}
