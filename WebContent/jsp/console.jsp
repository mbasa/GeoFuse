<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>GeoFUSE</title>
<link rel="stylesheet" type="text/css" href="resources/css/console.css">

<script type="text/javascript" src="resources/lib/extJS/ext-base.js"></script>
<script type="text/javascript" src="resources/lib/extJS/ext-all.js"></script>
<script type="text/javascript" src="resources/lib/elastic-textarea.js"></script>

<style type="text/css">
body {
	background-image: url("resources/graphics/PhotoGray_bg_c-1.jpg");
	background-repeat: repeat;
}

textarea {
	width: 700px;
	height: 30px;
	border: 3px solid #464646;
	font-family: arial;
	font-size: 14px;
	padding: 5px;
	color: #464646;
	background-color: #eeffff;
}
</style>
<script type="text/javascript">
	var mapOL = "http://";
	var mapZD = "http://";

	function clearText() {
		Ext.get('tdata').dom.value = '';
		Ext.get('turl').dom.value = 'http://';
		Ext.get('b_in').dom.disabled = null;

		mapOL = "http://";
		mapZD = "http://";
	};

	function switchMap(mtype) {

		var url = Ext.get('turl').dom.value;

		if (url.indexOf("Error") < 0) {
			if (mtype == "ol")
				Ext.get('turl').dom.value = mapOL;
			else
				Ext.get('turl').dom.value = mapZD;
		}
	};

	function openWin() {
		var url = Ext.get('turl').dom.value;

		if (url.indexOf("Error") < 0 && url != 'http://') {
			window.open(url, "_newtab");
		}
	};

	function sendText() {

		var txtData = Ext.get('tdata').dom.value;
		var mapType = 'ol';

		Ext.Ajax.request({
			url : 'indata',
			method : 'POST',
			params : {
				data : txtData,
				type : mapType
			},
			success : function(result, request) {
				Ext.get('turl').dom.value = result.responseText;

				var url = result.responseText;

				if (url.indexOf("Error") < 0) {
					mapOL = url;
					mapZD = url.replace('showtheme', 'zshowtheme');
				}
			},
			failure : function() {
				Ext.get('turl').dom.value = 'Error: Server Request';
			}
		});

		Ext.get('b_in').dom.disabled = 'true';
	};
</script>
</head>
<body>
	<img src="resources/graphics/shapeimage_1.png" />&nbsp;
	<img src="resources/graphics/shapeimage_2.png" />
	<div class="Header">Geo-Fuse</div>
	<div class="Title" >a Location Intelligence Tool</div>
	<br />
	<div class="Body" style="width:700px;">
		<p>Place data at the textbox below.</p>
		<p>
			■ <u>Linking Data with Maps</u>: 
			&nbsp;The first column name must contain the pre-defined column
			that will link the entered data with a map (i.e. city,province,etc).
			Maximum number of data columns is 10.
		</p>
		<p>
			■ <u>Displaying Longitude/Latitude points</u>: 
			&nbsp;The first column must be an identifier, and the 
			last 2 column name should be "lon,lat" that contains the 
			WGS84 points. Maximum number of data columns is 10 exlcluding
			the "lon,lat" columns.
		</p>
	</div>
	
	<textarea id="tdata"></textarea>
	
	<br />
	<br />
	<input type="button" id="b_in" value="Submit" onclick="sendText();">
	&nbsp;
	<input type="button" value="Reset" onclick="clearText();">

	<script type="text/javascript">
		elasticTextArea("tdata");
	</script>

	<div class="Body" style="width:700px;">
	   <p>Map URL</p>
	</div>

	<textarea id="turl">http://</textarea>

	<br />
	<br />
	<input type="button" value="Display Map" id="b_view" onclick="openWin();">
</body>
</html>