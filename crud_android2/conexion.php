<?php
// conexion.php
$conexion =mysqli_connect('localhost','root','','bd_upn');
if(!$conexion){
    // Si la conexión a la base de datos falla, devolvemos un error JSON
    die(json_encode(array("estado" => "error", "mensaje" => "Error de conexión a la BD: " . mysqli_connect_error())));
}
// mysqli_set_charset($conexion, "utf8"); // Opcional
?>