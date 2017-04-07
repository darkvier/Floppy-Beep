SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+02:00";


CREATE DATABASE IF NOT EXISTS `db` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `db`;

CREATE TABLE `Ranking` (
  `ID` int(11) NOT NULL,
  `Nickname` varchar(20) NOT NULL,
  `Puntuacion` int(11) NOT NULL,
  `Dificultad` varchar(20) NOT NULL,
  `Fecha` date NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `Ranking` (`ID`, `Nickname`, `Puntuacion`, `Dificultad`, `Fecha`) VALUES
(1, 'Javi', 1, 'Facil', '2017-03-07'),
(2, 'Alex', 2, 'Normal', '2017-03-17'),
(3, 'Lolo', 3, 'Facil', '2017-05-17'),
(4, 'Juan', 4, 'Dificil', '2017-04-02'),
(5, 'Juan2', 5, 'Facil', '2017-04-22'),
(6, 'Juan4', 6, 'Facil', '2017-04-02'),
(7, 'Juan3', 8, 'Facil', '2017-04-02'),
(8, 'rfhrth', 9, 'Facil', '2017-04-02'),
(9, 'Juadfhfdghn3', 10, 'Facil', '2017-04-02'),
(10, 'fghfghfgh', 12, 'Facil', '2017-04-02'),
(11, 'Jufghfdghan3', 2, 'Facil', '2017-04-02'),
(12, 'ffghfgh', 3, 'Facil', '2017-04-02'),
(13, 'Jufghfghan3', 6, 'Facil', '2017-04-02'),
(14, 'fghfgh', 21, 'Facil', '2017-04-02');

ALTER TABLE `Ranking`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `Nickname` (`Nickname`,`Dificultad`);

ALTER TABLE `Ranking`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;
  
DELIMITER $$
CREATE TRIGGER `CheckEmptyInput` 
	BEFORE INSERT ON `Ranking` FOR EACH ROW
    	IF NEW.Nickname = ''
            THEN                
            SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'Blank value on Ranking.Nickname';
        ELSEIF NEW.Dificultad = ''
            THEN                
            SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = 'Blank value on Ranking.Dificultad';
      END IF
$$
DELIMITER ;