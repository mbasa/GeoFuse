--
-- This function cleans up the temporary files by name.
--
-- The function an be run through a crontab entry. Example:
-- 0 * * * * /usr/bin/psql -U postgres -c "select deltemp_layername('flutter_mb');" licloud > /dev/null 2>&1
--
-- which will clean up tables with specified layername in licloud databes
--
--

CREATE OR REPLACE FUNCTION deltemp_layername( character varying ) 
  RETURNS varchar AS $$
DECLARE
    mLayerName   alias for $1;
    mRecord record;
    mTab    record;
    mString varchar; 
    mCount  int;
BEGIN
	
  mCount := 0;
  mString:= '';
  
  FOR mRecord IN SELECT tabid FROM geofuse.metadata
     WHERE layername = mLayerName  LOOP   
        SELECT into mTab * from to_regclass(mRecord.tabid);
        --
        -- No CSV Table
        -- 
        CONTINUE WHEN mTab IS NULL;
        
        mCount := mCount + 1;
        mString:= 'DROP TABLE ' || mRecord.tabid;
        EXECUTE mString;
  END LOOP;        
  
  mString := 'DELETE FROM geofuse.metadata WHERE layername = '
       || quote_literal(mLayerName) ;
       
  EXECUTE mString;
  
  RETURN 'Processed '||mCount||' records';

END;
$$ LANGUAGE plpgsql;
