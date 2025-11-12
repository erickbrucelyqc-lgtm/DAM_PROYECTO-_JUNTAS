<?php
// crear_grupo.php

include 'conexion.php';

// Función para generar un código alfanumérico único de 6 caracteres (SIN CAMBIOS)
function generarCodigoUnico($conexion) {
    // ... (Tu código de función generarCodigoUnico) ...
    $caracteres = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $longitud = 6;
    do {
        $codigo = '';
        for ($i = 0; $i < $longitud; $i++) {
            $codigo .= $caracteres[rand(0, strlen($caracteres) - 1)];
        }
        $check_query = "SELECT id FROM grupo WHERE codigo_invitacion = '$codigo'";
        $resultado = $conexion->query($check_query);
    } while ($resultado->num_rows > 0);
    
    return $codigo;
}

if (isset($_POST['nombre']) && isset($_POST['descripcion']) && isset($_POST['creador_id'])) {
    
    $nombre = $conexion->real_escape_string($_POST['nombre']);
    $descripcion = $conexion->real_escape_string($_POST['descripcion']);
    $creador_id = (int)$_POST['creador_id']; 
    $codigo_invitacion = generarCodigoUnico($conexion); 

    // ACCIÓN 1: Insertar el Grupo en la tabla 'grupo'
    $sql_grupo = "INSERT INTO grupo (nombre_grupo, descripcion, codigo_invitacion, creador_id) 
                  VALUES ('$nombre', '$descripcion', '$codigo_invitacion', $creador_id)";

    if ($conexion->query($sql_grupo) === TRUE) {
        
        $id_nuevo_grupo = $conexion->insert_id; 
        
        // =======================================================
        // 🔑 ACCIÓN 2 CORREGIDA: Insertar SÓLO grupo_id y usuario_id
        // =======================================================
        
        // **Asegúrate de que no tienes columna 'rol' en miembro_grupo**
        $sql_miembro = "INSERT INTO miembro_grupo (grupo_id, usuario_id) 
                        VALUES ($id_nuevo_grupo, $creador_id)";
        
        if ($conexion->query($sql_miembro) === TRUE) {
            
            // Éxito en ambas inserciones
            echo json_encode(array(
                "estado" => "ok", 
                "mensaje" => "Grupo creado con éxito.",
                "codigo" => $codigo_invitacion
            ));
        } else {
            // Falla la inserción del miembro
            echo json_encode(array(
                "estado" => "error", 
                "mensaje" => "Grupo creado, pero falló al añadir al creador como miembro. Error SQL: " . $conexion->error
            ));
        }

    } else {
        // Falla la inserción inicial del grupo (e.g. Duplicate Entry)
        echo json_encode(array("estado" => "error", "mensaje" => "Error de SQL al crear grupo: " . $conexion->error));
    }

} else {
    // Faltan parámetros
    echo json_encode(array("estado" => "error", "mensaje" => "Faltan parámetros requeridos."));
}

$conexion->close();
?>