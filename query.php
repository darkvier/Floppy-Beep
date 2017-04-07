<?php
// LocalHost
//$mysqli = new mysqli("localhost", "root", "", "db");

//http://darkvier.site40.net/
$mysqli = new mysqli("localhost", "id1230894_darkvier", "dark1234", "id1230894_test");

// Comprobar la conexión
if ($mysqli->connect_errno) {
    printf("false\nFalló la conexión: %s\n", $mysqli->connect_error);
    exit();
}


// Si queremos que nos de el ranking
if($_GET['accion'] === "listar"){
	
	$sql = "SELECT Nickname, Puntuacion, DATE_FORMAT(Fecha, '%d/%m/%Y') AS Fecha
					FROM Ranking WHERE Dificultad = 'Facil' 
					ORDER By Puntuacion DESC LIMIT 10";
					
	if ($resultado = $mysqli->query($sql, MYSQLI_USE_RESULT)) {
		echo "true".PHP_EOL;
		while($fila = $resultado->fetch_assoc()){
			echo $fila['Nickname']."\t".$fila['Puntuacion']."\t".$fila['Fecha']."\n";
		}
		$resultado->close();
	}else{
		echo "false".PHP_EOL;
		echo "Error al listar el ranking". PHP_EOL;
		echo "Error: " . $sql . "<br>" . PHP_EOL . $mysqli->error . PHP_EOL;
	}
	echo "\t\t\n";
	
	$sql = "SELECT Nickname, Puntuacion, DATE_FORMAT(Fecha, '%d/%m/%Y') AS Fecha
					FROM Ranking WHERE Dificultad = 'Normal' 
					ORDER By Puntuacion DESC LIMIT 10";
					
	if ($resultado = $mysqli->query($sql, MYSQLI_USE_RESULT)) {
		while($fila = $resultado->fetch_assoc()){
			echo $fila['Nickname']."\t".$fila['Puntuacion']."\t".$fila['Fecha']."\n";
		}
		$resultado->close();
	}
	echo "\t\t\n";
	
	$sql = "SELECT Nickname, Puntuacion, DATE_FORMAT(Fecha, '%d/%m/%Y') AS Fecha
					FROM Ranking WHERE Dificultad = 'Dificil' 
					ORDER By Puntuacion DESC LIMIT 10";
					
	if ($resultado = $mysqli->query($sql, MYSQLI_USE_RESULT)) {
		while($fila = $resultado->fetch_assoc()){
			echo $fila['Nickname']."\t".$fila['Puntuacion']."\t".$fila['Fecha']."\n";
		}
		$resultado->close();
	}
}elseif($_GET['accion'] === "listarRecords" AND $_GET['Nickname'] != ""){
	
	$sql = "SELECT DISTINCT(Dificultad) AS Dif, 
				(SELECT Puntuacion FROM Ranking
				WHERE Nickname = '".$_GET['Nickname']."' AND Dificultad = Dif) AS Punt
			FROM Ranking ORDER BY Dif ASC LIMIT 10";
					
	if ($resultado = $mysqli->query($sql, MYSQLI_USE_RESULT)) {
		echo "true".PHP_EOL;
		while($fila = $resultado->fetch_assoc()){
			echo $fila['Dif']."\t".$fila['Punt']."\n";
		}
		$resultado->close();
	}else{
		echo "false".PHP_EOL;
		echo "Error al listar los recors". PHP_EOL;
		echo "Error: " . $sql . "<br>" . PHP_EOL . $mysqli->error . PHP_EOL;
	}
	
}else if($_GET['accion'] === "insertar"){
	
	if ($_GET['Nickname'] != "" AND $_GET['Puntuacion'] > -1 AND $_GET['Dificultad'] != "" ){
		
		// Comprobar si existe una puntuacion
		$sql = "SELECT Puntuacion FROM Ranking WHERE Nickname = '".$_GET['Nickname']."'
					AND Dificultad = '".$_GET['Dificultad']."'";
		if ($result = $mysqli->query($sql)) {
			$row_cnt = $result->num_rows;
			$fila = $result->fetch_assoc();
			
			// Insertar si no hay puntuacion
			if($row_cnt == 0){
				$sql = "INSERT INTO Ranking (Nickname, Puntuacion, Fecha, Dificultad)
					VALUES ('".$_GET['Nickname']."', '".$_GET['Puntuacion']."',
					now(), '".$_GET['Dificultad']."')";

				if ($mysqli->query($sql) === TRUE) {
					echo "true".PHP_EOL;
				} else {
					echo "false".PHP_EOL;
					echo "Error insertando puntuacion". PHP_EOL;
					echo "Error: " . $sql . "<br>" . PHP_EOL . $mysqli->error . PHP_EOL;
				}
				
			// Actualizar puntuacion si existe una menor
			}elseif($row_cnt == 1 && $fila['Puntuacion'] < $_GET['Puntuacion']){
				$sql = "UPDATE Ranking SET Puntuacion = '".$_GET['Puntuacion']."', Fecha = now() 
				WHERE Nickname = '".$_GET['Nickname']."' AND Dificultad = '".$_GET['Dificultad']."'";

				if ($mysqli->query($sql) === TRUE) {
					echo "true".PHP_EOL;
				} else {
					echo "false".PHP_EOL;
					echo "Error actualizando puntuacion". PHP_EOL;
					echo "Error: " . $sql . "<br>" . PHP_EOL . $mysqli->error . PHP_EOL;
				}
			}else{
				echo "true".PHP_EOL;
				echo "Puntuacion no superada". PHP_EOL;
			}
		}else{
			echo "false".PHP_EOL;
			echo "Error al comprobar puntuacion anterior". PHP_EOL;
			echo "Error: " . $sql . "<br>" . PHP_EOL . $mysqli->error . PHP_EOL;
		}
	}else{
		echo "false".PHP_EOL;
		echo "Insercion sin datos". PHP_EOL;
	}
}else{
	echo "false".PHP_EOL;
	echo "Accion no definida". PHP_EOL;
}
$mysqli->close();
?>
