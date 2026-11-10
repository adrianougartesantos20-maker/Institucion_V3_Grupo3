<?php
// HU-04
require "conexion.php";
verificar_sesion($conexion, array("Administrador"));
$id_alumno = intval(dato("id_alumno"));
$grado     = dato("grado");
$seccion   = dato("seccion");
$periodo   = dato("periodo");

if ($grado == "" || $seccion == "" || $periodo == "") responder("0", "Complete grado, sección y periodo");
if (rol_de($conexion, $id_alumno) != "Alumno") responder("0", "El usuario seleccionado no es alumno");

ejecutar($conexion,
    "INSERT INTO matriculas (id_alumno, grado, seccion, periodo) VALUES (?, ?, ?, ?)
     ON DUPLICATE KEY UPDATE grado = VALUES(grado), seccion = VALUES(seccion)",
    "isss", array($id_alumno, $grado, $seccion, $periodo));
responder("1", "Alumno asignado a " . $grado . " " . $seccion);
?>
