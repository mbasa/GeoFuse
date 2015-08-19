Prerequisites
-------------
* [Tomcat](http://tomcat.apache.org)
* [GeoServer](http://www.geoserver.org)
* [PostGIS](http://www.postgis.org)

To Install
----------
* download the war file found here
* copy the war file to tomcat_dir/webapps
* start tomcat

Configure PostgreSQL
--------------------
* create a database and install PostGIS

``createdb geofuse``

``psql -f /usr/share/postgresql/8.4/contrib/postgis-1.5/postgis.sql geofuse``

``psql -f /usr/share/postgresql/8.4/contrib/postgis-1.5/spatial_ref_sys.sql geofuse``


* create the schemas that will contain the dummy and metadata tables found in createtabs.sql
``psql -f createtabs.sql geofuse``

* create the deltemp function that will delete the temporary files
``psql -f deltemp.sql geofuse``

* add map layers (polygons/lines/points) which will later be used for thematics. it will be best to place the table into the created "geodata" schema. 

Configure GeoServer
-------------------
* Create a new postgis datastore
* Create a new layer.  
* Click the Configure new SQL view...
* View name ``linker1``
* See *``geoserver_sql_view.sql``* for reference
* SQL statement ``select a.*,the_geom from %linktab% a,%maptab% b where a.col0 = b.%mapcol%``
* Click the ``Guess parameters from SQL``.  In the name {explain more the Deafult value parameters).
* Create another layer for ``linker2``.

Populate the MapLinker table in PostgreSQL
----------------------------------------
* insert the following for each map layer that will be used to create thematic
  1. link colunm (i.e. 'prefocode)
  2. table name with schema (i.e. 'geodata.prefectures')
  3. created geoserver view name (i.e. 'topp:linker1')
  4. type of layer (i.e. 'polygon')
  
  ``insert into geofuse.maplinker values ('prefcode','geodata.prefecture','topp.linker1','polygon');``

* insert the following for the layer that will contain lat,lon uploaded data

  ``insert into geofuse.maplinker values ('latlon','latlon','topp.linker2','point');``

To View Geofuse
----------------------
* In your browser, go to ``http://localhost:8080/geofuse/``

   NOTE: This application will try to get only metric (numeric) fields to
      display in the Thematic attribute list. For numeric ID fileds 
      (i.e. Prefecture-ID,Country-ID,etc.), add a suffix "-ID" to the 
      fieldname so that it will not show in the attribute list.

To Customize:
-------------

``tomcat_dir/webapps/GeoFuse/classes/properties/
database.properties
thematic.properties``


Adding new postgis table

Adding new background layer for printing

Adding new colorscheme


<tomcat dir>/webapps/geothematics/WEB-INF/classes/properties/thematic.properties

NOTE: the ColorNames and Colors should have equal number of items, otherwise
      no color choices will appear in the Colors list of the web page.



License
-------
Released under GPL.

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/Georepublic/geofuse/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

