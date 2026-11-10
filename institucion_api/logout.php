<?php
// HU-13
require "conexion.php";
$token = dato("token");
if ($token != "") {
    ejecutar($conexion, "UPDATE usuarios SET token = NULL WHERE token = ?", "s", array($token));
}
responder("1", "Sesión cerrada");
?>
