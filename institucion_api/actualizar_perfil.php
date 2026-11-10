<?php
// HU-09
require "conexion.php";
$yo = verificar_sesion($conexion, array("Administrador", "Auxiliar", "Docente", "Alumno"));
$correo   = dato("correo");
$telefono = dato("telefono");
$clave    = dato("clave");

if ($correo != "" && !filter_var($correo, FILTER_VALIDATE_EMAIL)) responder("0", "Correo no válido");
if ($telefono != "" && !preg_match('/^[0-9]{6,15}$/', $telefono)) responder("0", "Teléfono no válido (solo números)");

if ($clave != "") {
    if (strlen($clave) < 6) responder("0", "La contraseña debe tener al menos 6 caracteres");
    ejecutar($conexion, "UPDATE usuarios SET correo = ?, telefono = ?, clave = ? WHERE id = ?", "sssi",
             array($correo, $telefono, password_hash($clave, PASSWORD_DEFAULT), $yo["id"]));
} else {
    ejecutar($conexion, "UPDATE usuarios SET correo = ?, telefono = ? WHERE id = ?", "ssi",
             array($correo, $telefono, $yo["id"]));
}
responder("1", "Perfil actualizado");
?>
