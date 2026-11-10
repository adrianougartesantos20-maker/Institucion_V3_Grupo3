<?php
// HU-02 / HU-03
require "conexion.php";
$yo  = verificar_sesion($conexion, array("Administrador", "Auxiliar", "Docente"));
$rol = dato("rol");
if ($yo["rol"] != "Administrador") $rol = "Alumno";

$sql = "SELECT u.id, u.usuario, u.nombres, u.apellidos, u.correo, u.telefono,
               u.id_rol, r.nombre AS rol
          FROM usuarios u INNER JOIN roles r ON r.id = u.id_rol";
if ($rol != "") {
    $datos = consultar($conexion, $sql . " WHERE r.nombre = ? ORDER BY u.apellidos, u.nombres", "s", array($rol));
} else {
    $datos = consultar($conexion, $sql . " ORDER BY u.apellidos, u.nombres");
}
responder("1", "Lista de usuarios", $datos);
?>
