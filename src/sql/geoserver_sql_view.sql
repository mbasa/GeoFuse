-- 
--
select a.*,the_geom from %linktab% a,%maptab% b where a.col0 = b.%mapcol%

select * from %linktab%