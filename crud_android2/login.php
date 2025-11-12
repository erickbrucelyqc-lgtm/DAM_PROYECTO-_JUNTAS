<?php
// login.php

include 'conexion.php';

$nombre = isset($_POST['nombre']) ? $_POST['nombre'] : '';
$contrasena = isset($_POST['contrasena']) ? $_POST['contrasena'] : '';

$nombre_seguro = mysqli_real_escape_string($conexion, $nombre);
$contrasena_segura = mysqli_real_escape_string($conexion, $contrasena);

// 🔑 MODIFICACIÓN CLAVE: Incluir 'url_perfil' en la consulta SELECT
// ------------------------------------------------------------------------
$query = "SELECT id, nombre, url_perfil FROM usuario2 WHERE nombre = '$nombre_seguro' AND contrasena = '$contrasena_segura'";
// ------------------------------------------------------------------------

$resultado = mysqli_query($conexion, $query);

if (mysqli_num_rows($resultado) > 0) {
    // Éxito: Se encontró al usuario
    $fila = mysqli_fetch_assoc($resultado);
    
    // 🔑 MODIFICACIÓN CLAVE: Devolver 'url_perfil' en el JSON
    // ------------------------------------------------------------------------
    $url_perfil = $fila['url_perfil'];
    
    // Si la URL es NULL en la BD, se envía un string vacío o NULL (Android lo maneja bien)
    if ($url_perfil === null || $url_perfil === '') {
        $url_perfil = ""; // Enviamos un string vacío si es nulo
    }
    
    echo json_encode(array(
        "estado" => "ok", 
        "mensaje" => "¡Bienvenido/a! Acceso concedido.", 
        "nombre_usuario" => $fila['nombre'],
        "id_usuario" => $fila['id'],
        "url_perfil" => $url_perfil // ⬅️ Nuevo dato enviado
    ));
    // ------------------------------------------------------------------------
    
} else {
    // Fallo: Usuario o contraseña incorrectos
    echo json_encode(array("estado" => "error", "mensaje" => "Usuario o contraseña incorrectos."));
}

mysqli_close($conexion);
?>