<?php
// resetear_contrasena_seguridad.php
include 'conexion.php';

$accion = isset($_POST['accion']) ? $_POST['accion'] : '';
$nombre_usuario = isset($_POST['nombre_usuario']) ? $_POST['nombre_usuario'] : '';
$nombre_seguro = mysqli_real_escape_string($conexion, $nombre_usuario);

if ($accion == 'verificar') {
    $respuesta = isset($_POST['respuesta_secreta']) ? $_POST['respuesta_secreta'] : '';
    $respuesta_segura = mysqli_real_escape_string($conexion, $respuesta);

    // Verificar Respuesta Secreta
    $query_check = "SELECT id FROM usuario2 
                    WHERE nombre = '$nombre_seguro' 
                    AND respuesta_secreta = '$respuesta_segura'";

    $resultado_check = mysqli_query($conexion, $query_check);

    if (mysqli_num_rows($resultado_check) > 0) {
        echo json_encode(array("estado" => "verificado", "mensaje" => "Verificación exitosa."));
    } else {
        echo json_encode(array("estado" => "error", "mensaje" => "Respuesta secreta incorrecta."));
    }

} else if ($accion == 'resetear') {
    $nueva_contrasena = isset($_POST['nueva_contrasena']) ? $_POST['nueva_contrasena'] : '';
    // 🔑 IMPORTANTE: Encripta la contraseña si lo haces en tu Login/Registro.
    $nueva_contrasena_segura = mysqli_real_escape_string($conexion, $nueva_contrasena); 

    // Actualizar la contraseña
    $query_update = "UPDATE usuario2 SET contrasena = '$nueva_contrasena_segura' WHERE nombre = '$nombre_seguro'";
    
    if (mysqli_query($conexion, $query_update)) {
        echo json_encode(array("estado" => "ok", "mensaje" => "Contraseña restablecida con éxito."));
    } else {
        echo json_encode(array("estado" => "error", "mensaje" => "Error al actualizar la contraseña: " . mysqli_error($conexion)));
    }
} else {
    echo json_encode(array("estado" => "error", "mensaje" => "Acción no válida."));
}

mysqli_close($conexion);
?>