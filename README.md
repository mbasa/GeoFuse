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

``createdb licloud``
``pgsql -f /usr/share/postgresql/8.4/contrib/postgis-1.5/postgis.sql licloud``
``psql -f /usr/share/postgresql/8.4/contrib/postgis-1.5/postgis.sql licloud``
``psql -f /usr/share/postgresql/8.4/contrib/postgis-1.5/spatial_ref_sys.sql licloud``

* create the dummy and metadata tables found in createtabs.sql
``psql -f createtabs.sql licloud``
``psql -f deltemp.sql licloud``

* add map layers (polygons/lines/points) which will later be used for thematics. 
* Edit the ``tomcat_dir/webapps/GeoFuse/classes/properties/database.properties`` file to use your newly created postgis database

Configure GeoServer
-------------------
* Create a new postgis datastore
* Create a new layer.  
* Click the Configure new SQL view...
* View name ``linker1``
* SQL statement ``select a.*,the_geom from %linktab% a,%maptab% b where a.col0 = b.%mapcol%``
* Click the ``Guess parameters from SQL``.  In the name {explain more the Deafult value parameters).
* Create another layer for linker2.

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
dynamic_linker.properties
thematic.properties``


Adding new postgis table

Adding new background layer for printing

Adding new colorscheme


<tomcat dir>/webapps/geothematics/WEB-INF/classes/properties/thematic.properties

NOTE: the ColorNames and Colors should have equal number of items, otherwise
      no color choices will appear in the Colors list of the web page.

Deleting data


License
-------
Released under GPL.