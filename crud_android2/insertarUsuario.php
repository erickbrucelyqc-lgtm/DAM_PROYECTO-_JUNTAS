<?php 

include 'conexion.php';

// 1. Recibir todos los datos enviados desde Android
$nombre = isset($_POST['nombre']) ? $_POST['nombre'] : '';
$correo = isset($_POST['correo']) ? $_POST['correo'] : '';
$contrasena = isset($_POST['contrasena']) ? $_POST['contrasena'] : '';

// 🔑 Recibir los nuevos campos de seguridad
$pregunta = isset($_POST['pregunta_seguridad']) ? $_POST['pregunta_seguridad'] : '';
$respuesta = isset($_POST['respuesta_secreta']) ? $_POST['respuesta_secreta'] : '';


// 2. Escapar y asegurar los datos (¡IMPORTANTE para evitar SQL Injection!)
$nombre_seguro = mysqli_real_escape_string($conexion, $nombre);
$correo_seguro = mysqli_real_escape_string($conexion, $correo);
$contrasena_segura = mysqli_real_escape_string($conexion, $contrasena); 

$pregunta_segura = mysqli_real_escape_string($conexion, $pregunta);
$respuesta_segura = mysqli_real_escape_string($conexion, $respuesta); 


// 3. Crear la consulta SQL (incluyendo las nuevas columnas)
$query ="INSERT INTO usuario2(
            nombre, 
            correo, 
            contrasena, 
            pregunta_seguridad, 
            respuesta_secreta
        ) 
        values(
            '$nombre_seguro', 
            '$correo_seguro', 
            '$contrasena_segura', 
            '$pregunta_segura', 
            '$respuesta_segura'
        )";
        
$resultado = mysqli_query($conexion, $query);

// 4. Devolver la respuesta JSON
if($resultado){
    echo json_encode(array("estado" => "ok", "mensaje" => "Registrado correctamente"));
}else{
    // Devolvemos error en formato JSON para que Android lo maneje bien
    echo json_encode(array("estado" => "error", "mensaje" => "Error al registrar: " . mysqli_error($conexion)));
}

mysqli_close($conexion);

?>