Prerequisites
-------------
* [Tomcat](http://tomcat.apache.org)
* [GeoServer](http://www.geoserver.org)
* [PostGIS](http://www.postgis.org)

To Install
----------
* download the source code and build a WAR file using Maven

```
  mvn clean install
```

* copy the created WAR file into tomcat_dir/webapps directory
* start tomcat

Configure PostgreSQL
--------------------
* create a database and install PostGIS

```  
  createdb geofuse
  psql -c "CREATE EXTENSION postgis" geofuse
  psql -c "CREATE EXTENSION postgis_topology" geofuse
```

* create the schemas that will contain the dummy and metadata tables found in createtabs.sql

```
  psql -f createtabs.sql geofuse
```

* create the deltemp function that will delete the temporary files

```
  psql -f deltemp.sql geofuse
```

* add map layers (polygons/lines/points) which will later be used for thematics. it will be best to place the table into the created "geodata" schema. 

Configure GeoServer
-------------------
* Create a Workspace named `` geofuse ``
![alt text](https://raw.githubusercontent.com/mbasa/GeoFuse-Admin/master/WebContent/VAADIN/themes/geofuse_admin/layouts/workspace.png "" )

* Create a new postgis DataStore
![alt text](https://raw.githubusercontent.com/mbasa/GeoFuse-Admin/master/WebContent/VAADIN/themes/geofuse_admin/layouts/postgis_store.png "" )

* Name the new Store as `` geofuse `` and set the database parameter to the `` geofuse `` database created. Set the schema parameter to `` geodata ``
![alt text](https://raw.githubusercontent.com/mbasa/GeoFuse-Admin/master/WebContent/VAADIN/themes/geofuse_admin/layouts/store.png "" )

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