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
    IF @extra > 0 THEN
      CALL delete_config(@extra);
    END IF;
  END $
DELIMITER ;

DROP PROCEDURE IF EXISTS delete_config;
DELIMITER $
create PROCEDURE delete_config(extra INT)
  begin
    SET @page_from = extra;
    PREPARE stmt FROM "DELETE FROM configuration ORDER BY id ASC limit ?;";
    EXECUTE stmt USING @page_from;
    DEALLOCATE PREPARE stmt;
  end $
DELIMITER ;
