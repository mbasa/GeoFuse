--
-- Creating the Schemas 
--
DROP SCHEMA geofuse      cascade;
DROP SCHEMA geodata      cascade;
DROP SCHEMA csvdata      cascade;
DROP SCHEMA markerlayers cascade;

CREATE SCHEMA geofuse ;
CREATE SCHEMA geodata ;
CREATE SCHEMA csvdata ;
CREATE SCHEMA markerlayers;

--
-- Table: dummy
-- only used to create a Parametric SQL view in Geoserver
--

DROP TABLE geodata.dummy;

CREATE TABLE geodata.dummy
(
  col0  character varying(100) PRIMARY KEY,
  col1  double precision,
  col2  double precision,
  col3  double precision,
  col4  double precision,
  col5  double precision,
  col6  double precision,
  col7  double precision,
  col8  double precision,
  col9  double precision,
  col10 double precision,
  intime timestamp
);

--
-- Table: mapdummy
-- only used to create a Parametric SQL view in Geoserver
--

DROP TABLE geodata.mapdummy;

CREATE TABLE geodata.mapdummy
(
  mapcol character varying(100),
  the_geom geometry
);

--
-- Table: dummy2
-- only used to create a Parametric SQL view in Geoserver
-- this is used for uploaded data with lon,lat information
--

DROP TABLE geodata.dummy_pt;

CREATE TABLE geodata.dummy_pt
(
  col0 character varying(100) PRIMARY KEY,
  col1 double  precision,
  col2 double  precision,
  col3 double  precision,
  col4 double  precision,
  col5 double  precision,
  col6 double  precision,
  col7 double  precision,
  col8 double  precision,
  col9 double  precision,
  col10 double precision,
  intime timestamp,
  the_geom geometry  -- lon,lat data will be placed here
);

--
-- Table: metadata
-- this table will contain the Link information bet Map & Attribute Tables.
-- GeoFuse will populate and read from this table.
--

DROP TABLE geofuse.metadata;

CREATE TABLE geofuse.metadata
(
  tabid      TEXT NOT NULL,
  linklayer  TEXT,
  maptable   TEXT,
  linkcolumn TEXT,
  colnames   TEXT,
  ddate      timestamp without time zone,
  layertype  TEXT,
  layername  TEXT,
  CONSTRAINT metadata_pkey PRIMARY KEY (tabid)
);

--
-- Table: maplinker
-- this table will contain the Maps information that will be used by
-- GeoFuse to link with the Attribute Tables
--

DROP TABLE geofuse.maplinker;

CREATE TABLE geofuse.maplinker
(
  colname   TEXT NOT NULL, -- Link column name
  mapname   TEXT NOT NULL, -- Name of the Map. Schema should be included
  layername TEXT NOT NULL, -- Geoserver layer name.Schema should be included
  maptype   TEXT NOT NULL, -- Type of Map: i.e. polygon,point,line
  CONSTRAINT dynlinker_pkey PRIMARY KEY (colname)
);

--
-- Table: baselayer
-- this table will hold the baselayers that will be displayed
-- in GeoFuse
--

DROP TABLE geofuse.baselayer;

CREATE TABLE geofuse.baselayer
(
    rowid       serial PRIMARY KEY,
	rank        integer, -- Display Rank. Lower Numbers have higher rank
	url         TEXT,    -- URL of the base layer
	attribution TEXT,    -- Attribution of the base layer
	subdomain   TEXT,    -- SubDomains. Should be in CSV format i.e. "a,b,c,d"
	name        TEXT,    -- Display Name in the Layer Control
	display     BOOLEAN  -- Display Flag
);

--
-- Table: markerlayer
-- this table will hold the marker overlay layers 
-- that will be displayed in GeoFuse
--

DROP TABLE geofuse.markerlayer;

CREATE TABLE geofuse.markerlayer
(
    id serial primary key,
    layername text,   -- Display name in Layer Control
    tablename text    -- Table of the marker layer which has lon/lat info
);

--
-- Table: overlaylayer
-- this table will hold the additional WMS overlay layers 
-- that will be displayed in GeoFuse
--

DROP TABLE geofuse.overlaylayer;

CREATE TABLE geofuse.overlaylayer
(
    rowid       serial PRIMARY KEY,
	rank        integer, -- Display Rank. Lower Numbers have higher rank
	url         TEXT,    -- URL of the WMS overlay layer
	layers      TEXT,    -- layers. Should be in CSV format 
	name        TEXT,    -- Display Name in the Layer Control
	active      BOOLEAN, -- Display Flag in the Layer Control
	display     BOOLEAN, -- Display Flag
	minzoom     integer  -- Minimum Zoom
);

--
-- User information for Layer authentication
--

DROP TABLE geofuse.userinf;

CREATE TABLE geofuse.userinf
(
	username TEXT PRIMARY KEY,
	password TEXT,
	role     TEXT
);

--
-- Initial Data for Name: baselayer; Type: TABLE DATA; Schema: geofuse;
--
INSERT INTO geofuse.baselayer (rank,url,attribution,subdomain,name,display) VALUES (9, 'http://cyberjapandata.gsi.go.jp/xyz/pale/{z}/{x}/{y}.png', '電子国土基本図', '', '地理院タイル　(淡色地図)', true);
INSERT INTO geofuse.baselayer (rank,url,attribution,subdomain,name,display) VALUES (3, 'http://stamen-tiles-{s}.a.ssl.fastly.net/toner/{z}/{x}/{y}.png', 'Map tiles by <a href="http://stamen.com">Stamen Design</a>, <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a> &mdash; Map data &copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>', 'a,b,c,d', 'Stamen-Toner', true);
INSERT INTO geofuse.baselayer (rank,url,attribution,subdomain,name,display) VALUES (4, 'http://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', 'Tiles &copy; Esri &mdash; Source: Esri, DeLorme, NAVTEQ, USGS, Intermap, iPC, NRCAN, Esri Japan, METI, Esri China (Hong Kong), Esri (Thailand), TomTom, 2012', '', 'ESRI StreetMap', true);
INSERT INTO geofuse.baselayer (rank,url,attribution,subdomain,name,display) VALUES (6, 'http://{s}.tile.thunderforest.com/transport/{z}/{x}/{y}.png', 'Tiles Courtesy of <a href="http://www.thunderforest.com/" arget="_blank">Thunderforest</a>&nbspand OpenStreetMap contributors', 'a,b,c', 'ThunderForest', true);
INSERT INTO geofuse.baselayer (rank,url,attribution,subdomain,name,display) VALUES (5, 'http://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community', '', 'ESRI World', true);
INSERT INTO geofuse.baselayer (rank,url,attribution,subdomain,name,display) VALUES (7, 'http://otile{s}.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png', 'Tiles by <a href="http://www.mapquest.com/">MapQuest</a> &mdash; Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>', '1,2,3,4', 'MapQuest', true);
INSERT INTO geofuse.baselayer (rank,url,attribution,subdomain,name,display) VALUES (8, 'http://cyberjapandata.gsi.go.jp/xyz/std/{z}/{x}/{y}.png', '地理院タイル（標準地図）', '', '地理院タイル（標準地図）', true);
INSERT INTO geofuse.baselayer (rank,url,attribution,subdomain,name,display) VALUES (1, 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', '© OpenStreetMap Contributors', '', 'OSM', true);
INSERT INTO geofuse.baselayer (rank,url,attribution,subdomain,name,display) VALUES (10, 'http://cyberjapandata.gsi.go.jp/xyz/ort/{z}/{x}/{y}.jpg', '電子国土基本図', '', '地理院タイル（オルソ画像）', true);
INSERT INTO geofuse.baselayer (rank,url,attribution,subdomain,name,display) VALUES (2, 'http://openmapsurfer.uni-hd.de/tiles/roadsg/x={x}&y={y}&z={z}', 'Imagery from <a href="http://giscience.uni-hd.de/">GIScience Research Group @ University of Heidelberg</a> &mdash; Map data &copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>''', '', 'OSM-BW', true);

--
-- Initial Data for Name: userinf; Type: TABLE DATA; Schema: geofuse;
--
INSERT INTO geofuse.userinf (username,password,role) VALUES ('admin','admin','admin');

--
-- Initial Data for Name: maplinker; Type: TABLE DATA; Schema: geofuse;
--
INSERT INTO geofuse.maplinker VALUES ( 'latlon','latlon','geofuse:geolink_pt','point');