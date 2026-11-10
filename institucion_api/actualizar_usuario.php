<?php
// HU-02 / HU-03
require "conexion.php";
verificar_sesion($conexion, array("Administrador"));

$id        = intval(dato("id"));
$usuario   = dato("usuario");
$clave     = dato("clave");
$nombres   = dato("nombres");
$apellidos = dato("apellidos");
$correo    = dato("correo");
$telefono  = dato("telefono");

if ($usuario == "" || $nombres == "" || $apellidos == "") responder("0", "Complete usuario, nombres y apellidos");
if ($correo != "" && !filter_var($correo, FILTER_VALIDATE_EMAIL)) responder("0", "Correo no válido");

if ($clave != "") {
    if (strlen($clave) < 6) responder("0", "La contraseña debe tener al menos 6 caracteres");
    $error = ejecutar($conexion,
        "UPDATE usuarios SET usuario=?, clave=?, nombres=?, apellidos=?, correo=?, telefono=? WHERE id=?",
        "ssssssi", array($usuario, password_hash($clave, PASSWORD_DEFAULT), $nombres, $apellidos, $correo, $telefono, $id));
} else {
    $error = ejecutar($conexion,
        "UPDATE usuarios SET usuario=?, nombres=?, apellidos=?, correo=?, telefono=? WHERE id=?",
        "sssssi", array($usuario, $nombres, $apellidos, $correo, $telefono, $id));
}
if ($error == 1062) responder("0", "El usuario " . $usuario . " ya está registrado");
if ($error != 0)    responder("0", "No se pudo actualizar");
responder("1", "Usuario actualizado correctamente");
?>
