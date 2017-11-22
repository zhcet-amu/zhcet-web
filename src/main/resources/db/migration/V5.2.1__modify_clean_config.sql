DROP EVENT IF EXISTS CleanConfig;
DELIMITER $
CREATE EVENT CleanConfig
  ON SCHEDULE EVERY 1 DAY
  STARTS CURRENT_TIMESTAMP
DO
  BEGIN
    SELECT COUNT(*) INTO @cnt FROM configuration;
    SET @max_config = 50;
    SET @extra = @cnt - @max_config;
    CALL delete_config(@extra);
  END $
DELIMITER ;

DROP PROCEDURE IF EXISTS delete_config;
DELIMITER $
create PROCEDURE delete_config(extra INT)
  begin
    SET @page_from = extra;
    IF (@page_from > 0) THEN
      PREPARE stmt FROM "DELETE FROM configuration ORDER BY id ASC limit ?;";
      EXECUTE stmt USING @page_from;
      DEALLOCATE PREPARE stmt;
    END IF;
  end $
DELIMITER ;
