<?php
// eliminar_grupo.php

include 'conexion.php'; // Asegúrate de que la conexión sea correcta

if (isset($_POST['grupo_id'])) {
    
    // Obtener y sanitizar el ID del grupo
    $grupo_id = (int)$_POST['grupo_id'];
    
    // Importante: La tabla en la BD es 'grupo'
    $query = "DELETE FROM grupo WHERE id = $grupo_id";

    if (mysqli_query($conexion, $query)) {
        if (mysqli_affected_rows($conexion) > 0) {
            echo json_encode(array("estado" => "ok", "mensaje" => "Grupo eliminado exitosamente."));
        } else {
            echo json_encode(array("estado" => "error", "mensaje" => "Error: No se encontró el grupo con ese ID para eliminar."));
        }
    } else {
        echo json_encode(array("estado" => "error", "mensaje" => "Error al ejecutar la consulta: " . mysqli_error($conexion)));
    }

} else {
    echo json_encode(array("estado" => "error", "mensaje" => "Falta el parámetro requerido (grupo_id)."));
}

mysqli_close($conexion);
?>