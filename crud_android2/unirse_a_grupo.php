<?php
// unirse_a_grupo.php (MODIFICADO)

include 'conexion.php';

// Verificar que los datos POST hayan sido enviados
if (isset($_POST['codigo']) && isset($_POST['usuario_id'])) {
    
    $codigo = $conexion->real_escape_string($_POST['codigo']);
    $usuario_id = (int)$_POST['usuario_id'];
    
    // 1. Buscar el ID y NOMBRE del grupo usando el código de invitación
    $query_grupo = "SELECT id, nombre_grupo FROM grupo WHERE codigo_invitacion = '$codigo'";
    $resultado_grupo = $conexion->query($query_grupo);

    if ($resultado_grupo->num_rows === 0) {
        echo json_encode(array("estado" => "error", "mensaje" => "Código de invitación no válido."));
        $conexion->close();
        exit();
    }
    
    $fila_grupo = $resultado_grupo->fetch_assoc();
    $grupo_id = $fila_grupo['id'];
    $nombre_grupo = $fila_grupo['nombre_grupo']; // ⬅️ Nuevo: Obtenemos el nombre
    
    // 2. Insertar el registro en la tabla de miembros (asocia usuario y grupo)
    $query_insert = "INSERT INTO miembro_grupo (usuario_id, grupo_id) VALUES ($usuario_id, $grupo_id)";

    if ($conexion->query($query_insert) === TRUE) {
        // ⬅️ Devolvemos el nombre del grupo en caso de éxito
        echo json_encode(array("estado" => "ok", "mensaje" => "Te has unido al grupo con éxito.", "nombre_grupo" => $nombre_grupo)); 
    } else {
        if ($conexion->errno == 1062) {
             echo json_encode(array("estado" => "error", "mensaje" => "Ya eres miembro de este grupo."));
        } else {
            echo json_encode(array("estado" => "error", "mensaje" => "Error de SQL: " . $conexion->error));
        }
    }

} else {
    echo json_encode(array("estado" => "error", "mensaje" => "Faltan parámetros requeridos (código o usuario_id)."));
}

$conexion->close();
?>