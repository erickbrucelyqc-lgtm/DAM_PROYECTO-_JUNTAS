<?php
// actualizar_estado_pago.php (NUEVO)
include 'conexion.php'; 

if (isset($_POST['grupo_id']) && isset($_POST['user_id'])) {
    
    $grupo_id = (int)$_POST['grupo_id'];
    $user_id = (int)$_POST['user_id'];
    
    // Actualiza el estado_pago a 'SI' para el usuario y grupo específicos
    $sql = "UPDATE miembro_grupo 
            SET estado_pago = 'SI' 
            WHERE grupo_id = $grupo_id AND usuario_id = $user_id";

    if ($conexion->query($sql) === TRUE) {
        if ($conexion->affected_rows > 0) {
            echo json_encode(array("estado" => "ok", "mensaje" => "Estado de pago actualizado a SI."));
        } else {
            echo json_encode(array("estado" => "ok", "mensaje" => "El pago ya estaba registrado (o no eres miembro)."));
        }
    } else {
        echo json_encode(array("estado" => "error", "mensaje" => "Error al actualizar estado: " . $conexion->error));
    }

} else {
    echo json_encode(array("estado" => "error", "mensaje" => "Faltan parámetros requeridos (grupo_id, user_id)."));
}

$conexion->close();
?>