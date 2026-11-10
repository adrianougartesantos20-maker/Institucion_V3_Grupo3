<?php
// HU-01
require "conexion.php";

$usuario = dato("usuario");
$clave   = dato("clave");
if ($usuario == "" || $clave == "") responder("0", "Ingrese usuario y contraseña");

$filas = consultar($conexion,
    "SELECT u.id, u.usuario, u.clave, u.nombres, u.apellidos, u.correo, u.telefono,
            u.id_rol, r.nombre AS rol
       FROM usuarios u INNER JOIN roles r ON r.id = u.id_rol
      WHERE u.usuario = ?", "s", array($usuario));

if (count($filas) == 0 || !password_verify($clave, $filas[0]["clave"])) {
    responder("0", "Usuario o contraseña incorrectos");
}

$fila = $filas[0];
unset($fila["clave"]);
$token = bin2hex(random_bytes(32));
ejecutar($conexion, "UPDATE usuarios SET token = ? WHERE id = ?", "si", array($token, $fila["id"]));

responder("1", "Bienvenido " . $fila["nombres"], array($fila), array("token" => $token));
?>
