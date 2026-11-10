<?php
// HU-07
require "conexion.php";
verificar_sesion($conexion, array("Administrador"));
$id_docente = intval(dato("id_docente"));
$id_curso   = intval(dato("id_curso"));
$grado      = dato("grado");
$seccion    = dato("seccion");
$periodo    = dato("periodo");

if ($grado == "" || $seccion == "" || $periodo == "") responder("0", "Complete grado, sección y periodo");
if (rol_de($conexion, $id_docente) != "Docente") responder("0", "El usuario seleccionado no es docente");

$error = ejecutar($conexion,
    "INSERT INTO asignaciones (id_docente, id_curso, grado, seccion, periodo) VALUES (?, ?, ?, ?, ?)",
    "iisss", array($id_docente, $id_curso, $grado, $seccion, $periodo));
if ($error == 1062) responder("0", "Ese curso ya tiene docente en " . $grado . " " . $seccion . " (" . $periodo . ")");
if ($error != 0)    responder("0", "No se pudo asignar el curso");
responder("1", "Curso asignado correctamente");
?>
