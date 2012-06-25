-- 
-- Geoserver Parametric SQL for linker1
--   values: linktab = 'dummy', maptab = 'mapdummy', mapcol = 'mapcol'
--
select a.*,the_geom from %linktab% a,%maptab% b where a.col0 = b.%mapcol%

-- 
-- Geoserver Parametric SQL for linker2
--    values: linktab = 'dummy2';
--
select * from %linktab%