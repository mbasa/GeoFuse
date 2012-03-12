CREATE OR REPLACE FUNCTION deltemp( character varying ) 
  RETURNS varchar AS $$
DECLARE
    mTime   alias for $1;
    mRecord record;
    mString varchar; 
    mCount  int;
BEGIN
	
  mCount := 0;
  mString:= '';
  
  FOR mRecord IN SELECT tabid FROM metadata
     WHERE ddate < now() - mTime::interval  LOOP       
        mCount := mCount + 1;
        mString:= 'DROP TABLE ' || mRecord.tabid;
        EXECUTE mString;
  END LOOP;        
  
  mString := 'DELETE FROM metadata WHERE ddate < now() - '
       || quote_literal(mTime) ||'::interval';
       
  EXECUTE mString;
  
  RETURN 'Processed '||mCount||' records';

END;
$$ LANGUAGE plpgsql;
