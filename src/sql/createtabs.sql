--
-- Creating the Schemas 
--
DROP SCHEMA geofuse cascade;
DROP SCHEMA geodata cascade;
DROP SCHEMA csvdata cascade;

CREATE SCHEMA geofuse ;
CREATE SCHEMA geodata ;
CREATE SCHEMA csvdata ;

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
  col10 double precision
);

--
-- Table: mapdummy
-- only used to create a Parametric SQL view in Geoserver
--

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
