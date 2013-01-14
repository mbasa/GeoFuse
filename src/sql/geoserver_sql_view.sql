-- 
-- Geoserver Parametric SQL for linker1
--   values: linktab = 'geodata.dummy', 
--           maptab  = 'goedata.mapdummy', 
--           mapcol  = 'mapcol'
--
select a.*,the_geom from %linktab% a,%maptab% b where a.col0 = b.%mapcol%

-- 
-- Geoserver Parametric SQL for linker2
-- (for uploaded data with lon,lat information)
--    values: linktab = 'geodata.dummy_pt';
--
select * from %linktab%