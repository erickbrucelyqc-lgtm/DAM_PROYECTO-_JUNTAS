<?php
// guardar_perfil_usuario.php

include 'conexion.php'; 

if (!isset($_POST['id_usuario']) || !isset($_POST['url_perfil'])) {
    echo json_encode(array("estado" => "error", "mensaje" => "Faltan parámetros requeridos."));
    exit();
}

$id_usuario = (int)$_POST['id_usuario'];
$url_perfil = $_POST['url_perfil'];

// Sentencia SQL UPDATE
// La tabla es 'usuario2'
$query = "UPDATE usuario2 SET url_perfil = ? WHERE id = ?";

// Usar prepared statements para seguridad (obligatorio cuando manejas entradas de usuario)
$stmt = $conexion->prepare($query);
$stmt->bind_param("si", $url_perfil, $id_usuario); // "s" por string (URL), "i" por integer (ID)

if ($stmt->execute()) {
    echo json_encode(array("estado" => "ok", "mensaje" => "Perfil actualizado."));
} else {
    echo json_encode(array("estado" => "error", "mensaje" => "Error al actualizar: " . $stmt->error));
}

$stmt->close();
mysqli_close($conexion);
?>