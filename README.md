Prerequisites
=============
* GeoServer (with Tomcat)
* PostgreSQL/PostGIS

To Install
----------
* download the war file found here
* copy the war file to <tomcat dir>/webapps
* start tomcat

Configure PostgreSQL
--------------------
* create a database and install PostGIS
* create the dummy and metadata tables found in createtabs.sql
* add map layers (polygons/lines/points) which will later be used for thematics 

Configure GeoServer
===================

To View Thematic Maps:
----------------------
1. Register polygon layers into the Geoserver Repository
2. View the polygon layers via the url:

   http://<host>:<port>/geothematics/showtheme?layer=<namespace>:<layer name>

example:

   http://localhost:8080/geothematics/showtheme?layer=topp:states

NOTE: This application will try to get only metric (numeric) fields to
      display in the Thematic attribute list. For numeric ID fileds 
      (i.e. Prefecture-ID,Country-ID,etc.), add a suffix "-ID" to the 
      fieldname so that it will not show in the attribute list.

To Customize:
-------------

To change the Google Key, add or delete colors and ranges, etc., edit
the properties file at:

<tomcat dir>/webapps/geothematics/WEB-INF/classes/properties/thematic.properties

NOTE: the ColorNames and Colors should have equal number of items, otherwise
      no color choices will appear in the Colors list of the web page.
