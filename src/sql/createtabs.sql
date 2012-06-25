--
-- Table: dummy
-- only used to create a Parametric SQL view in Geoserver
--

DROP TABLE dummy;

CREATE TABLE dummy
(
  col0 character varying(100),
  col1 double precision,
  col2 double precision,
  col3 double precision,
  col4 double precision,
  col5 double precision,
  col6 double precision,
  col7 double precision,
  col8 double precision,
  col9 double precision,
  col10 double precision
);

--
-- Table: mapdummy
-- only used to create a Parametric SQL view in Geoserver
--

CREATE TABLE mapdummy
(
  mapcol character varying(100),
  the_geom geometry
);

--
-- Table: dummy2
-- only used to create a Parametric SQL view in Geoserver
--

DROP TABLE dummy2;

CREATE TABLE dummy2
(
  col0 character varying(100),
  col1 double precision,
  col2 double precision,
  col3 double precision,
  col4 double precision,
  col5 double precision,
  col6 double precision,
  col7 double precision,
  col8 double precision,
  col9 double precision,
  col10 double precision,
  the_geom geometry
);

--
-- Table: metadata
-- this table will contain the Link information bet Map & Attribute Tables
--

DROP TABLE metadata;

CREATE TABLE metadata
(
  tabid character varying(150) NOT NULL,
  linklayer character varying(80),
  maptable character varying(80),
  linkcolumn character varying(30),
  colnames character varying(3000),
  ddate timestamp without time zone,
  layertype character varying(25),
  CONSTRAINT metadata_pkey PRIMARY KEY (tabid)
);