<?php
// actualizar_grupo.php

include 'conexion.php'; // Incluye tu archivo de conexión a la base de datos

if (isset($_POST['grupo_id']) && isset($_POST['nombre_grupo']) && isset($_POST['descripcion']) && isset($_POST['url_imagen'])) {
    
    // 1. Sanitizar y obtener los datos
    $grupo_id = (int)$_POST['grupo_id'];
    $nombre = $conexion->real_escape_string($_POST['nombre_grupo']);
    $descripcion = $conexion->real_escape_string($_POST['descripcion']);
    $url_imagen = $conexion->real_escape_string($_POST['url_imagen']);
    
    // 2. Sentencia SQL UPDATE
    $query = "UPDATE grupo SET 
              nombre_grupo = '$nombre', 
              descripcion = '$descripcion', 
              url_imagen = '$url_imagen' 
              WHERE id = $grupo_id";

    // 3. Ejecutar la consulta
    if ($conexion->query($query) === TRUE) {
        // Verificar si se afectó alguna fila (asegurar que el ID existía)
        if ($conexion->affected_rows > 0) {
            echo json_encode(array("estado" => "ok", "mensaje" => "Grupo actualizado exitosamente."));
        } else {
            echo json_encode(array("estado" => "error", "mensaje" => "No se realizó ninguna actualización (El ID del grupo podría ser incorrecto o los datos son los mismos)."));
        }
    } else {
        echo json_encode(array("estado" => "error", "mensaje" => "Error al ejecutar la consulta: " . $conexion->error));
    }

} else {
    echo json_encode(array("estado" => "error", "mensaje" => "Faltan parámetros requeridos (grupo_id, nombre, descripcion, url_imagen)."));
}

$conexion->close();
?>