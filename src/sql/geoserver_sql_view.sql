--
-- Create a Workspace named 'geofuse' and then a 
-- PostGIS Store named 'geofuse' first in Geoserver.
-- Then create the following ParametricSQL Layers 
-- using the parameters below:
--

-- 
-- Geoserver Parametric SQL for geofuse.geolink Layer
--   values: linktab = 'geodata.dummy', 
--           maptab  = 'geodata.mapdummy', 
--           mapcol  = 'mapcol'
--
select a.*,the_geom from %linktab% a,%maptab% b where a.col0 = b.%mapcol%

-- 
-- Geoserver Parametric SQL for geofuse.geolink_pt Layer
-- (for uploaded data with lon,lat information)
--    values: linktab = 'geodata.dummy_pt';
--
select * from %linktab%