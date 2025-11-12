<?php
// obtener_miembros.php (MODIFICADO con tabla usuario2 y columna url_perfil)
include 'conexion.php'; // Asegúrate de que tu conexión esté incluida

if (isset($_POST['grupo_id'])) {
    $grupo_id = (int)$_POST['grupo_id'];

    // Consulta con JOIN para obtener nombre, URL de imagen y estado de pago
    $query = "SELECT 
                u.id, 
                u.nombre, 
                u.url_perfil AS url_imagen,  /* 🔑 USANDO usuario2.url_perfil */
                mg.estado_pago
              FROM miembro_grupo mg
              JOIN usuario2 u ON mg.usuario_id = u.id /* 🔑 USANDO usuario2 */
              WHERE mg.grupo_id = $grupo_id";

    $resultado = $conexion->query($query);
    $miembros = array();

    if ($resultado && $resultado->num_rows > 0) {
        while ($fila = $resultado->fetch_assoc()) {
            // Se usa 'url_imagen' en el array JSON para mantener la compatibilidad en Android
            $miembros[] = $fila; 
        }
        echo json_encode(array("estado" => "ok", "miembros" => $miembros));
    } else {
        echo json_encode(array("estado" => "error", "mensaje" => "No se encontraron miembros para este grupo o la consulta falló."));
    }

} else {
    echo json_encode(array("estado" => "error", "mensaje" => "Faltan parámetros (grupo_id)."));
}

$conexion->close();
?>