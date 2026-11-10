<?php
// HU-02
require "conexion.php";
verificar_sesion($conexion, array("Administrador"));

$usuario   = dato("usuario");
$clave     = dato("clave");
$nombres   = dato("nombres");
$apellidos = dato("apellidos");
$correo    = dato("correo");
$telefono  = dato("telefono");
$id_rol    = intval(dato("id_rol"));

if ($usuario == "" || $nombres == "" || $apellidos == "") responder("0", "Complete usuario, nombres y apellidos");
if (strlen($clave) < 6) responder("0", "La contraseña debe tener al menos 6 caracteres");
if ($id_rol < 1 || $id_rol > 4) responder("0", "Seleccione un rol válido");
if ($correo != "" && !filter_var($correo, FILTER_VALIDATE_EMAIL)) responder("0", "Correo no válido");

$hash  = password_hash($clave, PASSWORD_DEFAULT);
$error = ejecutar($conexion,
    "INSERT INTO usuarios (usuario, clave, nombres, apellidos, correo, telefono, id_rol)
     VALUES (?, ?, ?, ?, ?, ?, ?)", "ssssssi",
    array($usuario, $hash, $nombres, $apellidos, $correo, $telefono, $id_rol));

if ($error == 1062) responder("0", "El usuario " . $usuario . " ya está registrado");
if ($error != 0)    responder("0", "No se pudo registrar");
responder("1", "Usuario registrado correctamente");
?>
