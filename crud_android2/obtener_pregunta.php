<?php
// obtener_pregunta.php
include 'conexion.php';

$nombre_usuario = isset($_POST['nombre_usuario']) ? $_POST['nombre_usuario'] : '';
$nombre_seguro = mysqli_real_escape_string($conexion, $nombre_usuario);

// 1. Buscar usuario y su pregunta de seguridad
$query = "SELECT pregunta_seguridad FROM usuario2 WHERE nombre = '$nombre_seguro'";
$resultado = mysqli_query($conexion, $query);

if (mysqli_num_rows($resultado) > 0) {
    $fila = mysqli_fetch_assoc($resultado);
    $pregunta = $fila['pregunta_seguridad'];

    if (!empty($pregunta)) {
        echo json_encode(array(
            "estado" => "ok",
            "pregunta_seguridad" => $pregunta
        ));
    } else {
        echo json_encode(array("estado" => "error", "mensaje" => "Usuario encontrado, pero no tiene pregunta de seguridad configurada."));
    }
} else {
    echo json_encode(array("estado" => "error", "mensaje" => "Nombre de usuario no encontrado."));
}

mysqli_close($conexion);
?>