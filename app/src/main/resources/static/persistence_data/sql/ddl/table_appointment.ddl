-- Change DateTime nanoseconds precision from 6 digits per default to 0.
-- from '2025-05-01 09:00:00.000000', to '2025-05-01 09:00:00'
ALTER TABLE `smartcare_coursera`.`appointment`
    CHANGE COLUMN `appointment_time` `appointment_time` DATETIME NOT NULL ;