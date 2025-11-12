<?php
// obtener_grupos.php

include 'conexion.php';

$grupos = array();

if (!isset($_POST['id_usuario'])) {
    echo json_encode(array("estado" => "error", "mensaje" => "Se requiere el ID del usuario."));
    exit();
}

$id_usuario = (int)$_POST['id_usuario'];

// Consulta que une los grupos creados por el usuario (creador_id) 
// Y los grupos a los que el usuario está asociado en la tabla miembro_grupo
$query = "
    SELECT 
        g.id, 
        g.nombre_grupo, 
        g.descripcion, 
        g.creador_id,
        g.url_imagen  -- 🔑 NUEVA COLUMNA A CONSULTAR
    FROM 
        grupo g
    WHERE 
        g.creador_id = $id_usuario 
    OR 
        g.id IN (SELECT grupo_id FROM miembro_grupo WHERE usuario_id = $id_usuario)
    GROUP BY 
        g.id
    ORDER BY 
        g.nombre_grupo ASC";

$resultado = mysqli_query($conexion, $query);

if ($resultado) {
    while ($fila = mysqli_fetch_assoc($resultado)) {
        $grupos[] = array(
            'id' => $fila['id'],
            'nombre' => $fila['nombre_grupo'], // Tu código Android espera 'nombre'
            'descripcion' => $fila['descripcion'],
            'creador_id' => $fila['creador_id'],
            'url_imagen' => $fila['url_imagen'] // 🔑 NUEVO CAMPO A DEVOLVER
        );
    }
    echo json_encode(array(
        "estado" => "ok",
        "grupos" => $grupos
    ));
} else {
   echo json_encode(array("estado" => "error", "mensaje" => "Error de SQL: " . mysqli_error($conexion)));
}

mysqli_close($conexion);
?>