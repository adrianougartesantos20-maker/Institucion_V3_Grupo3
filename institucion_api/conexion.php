<?php
$servidor   = "localhost";
$usuario_bd = "root";
$clave_bd   = "";
$base_datos = "BD_INSTITUCION";

header("Content-Type: application/json; charset=utf-8");
mysqli_report(MYSQLI_REPORT_OFF);

$conexion = @new mysqli($servidor, $usuario_bd, $clave_bd, $base_datos);
if ($conexion->connect_error) {
    responder("0", "No se pudo conectar con la base de datos");
}
$conexion->set_charset("utf8mb4");

function responder($exito, $mensaje, $datos = array(), $extra = array()) {
    $respuesta = array("exito" => $exito, "mensaje" => $mensaje, "datos" => $datos);
    echo json_encode(array_merge($respuesta, $extra), JSON_UNESCAPED_UNICODE);
    exit;
}

function dato($nombre) {
    return isset($_POST[$nombre]) ? trim($_POST[$nombre]) : "";
}

function consultar($conexion, $sql, $tipos = "", $valores = array()) {
    $stmt = $conexion->prepare($sql);
    if (!$stmt) responder("0", "Error en la consulta");
    if ($tipos != "") $stmt->bind_param($tipos, ...$valores);
    $stmt->execute();
    $resultado = $stmt->get_result();
    $filas = array();
    while ($fila = $resultado->fetch_assoc()) $filas[] = $fila;
    $stmt->close();
    return $filas;
}

function ejecutar($conexion, $sql, $tipos, $valores) {
    $stmt = $conexion->prepare($sql);
    if (!$stmt) responder("0", "Error en la consulta");
    $stmt->bind_param($tipos, ...$valores);
    $stmt->execute();
    $error = $stmt->errno;
    $stmt->close();
    return $error;
}

function verificar_sesion($conexion, $roles_permitidos) {
    $token = dato("token");
    if ($token == "") responder("0", "Sesión no válida. Inicie sesión nuevamente");
    $filas = consultar($conexion,
        "SELECT u.id, u.usuario, u.nombres, u.apellidos, r.nombre AS rol
           FROM usuarios u INNER JOIN roles r ON r.id = u.id_rol
          WHERE u.token = ?", "s", array($token));
    if (count($filas) == 0) responder("0", "Sesión no válida. Inicie sesión nuevamente");
    if (!in_array($filas[0]["rol"], $roles_permitidos)) responder("0", "No tiene permiso para esta opción");
    return $filas[0];
}

function rol_de($conexion, $id_usuario) {
    $filas = consultar($conexion,
        "SELECT r.nombre FROM usuarios u INNER JOIN roles r ON r.id = u.id_rol WHERE u.id = ?",
        "i", array($id_usuario));
    return count($filas) == 0 ? "" : $filas[0]["nombre"];
}
?>
