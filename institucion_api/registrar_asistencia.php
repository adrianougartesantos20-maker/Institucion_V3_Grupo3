<?php
// HU-05
require "conexion.php";
$yo     = verificar_sesion($conexion, array("Auxiliar", "Administrador"));
$codigo = dato("codigo");
$estado = dato("estado");

if (!in_array($estado, array("Presente", "Tardanza", "Falta"))) $estado = "Presente";
$alumno = consultar($conexion,
    "SELECT u.id, u.nombres, u.apellidos FROM usuarios u
      WHERE u.usuario = ? AND u.id_rol = 4", "s", array($codigo));
if (count($alumno) == 0) responder("0", "El código " . $codigo . " no pertenece a ningún alumno");

$a = $alumno[0];
$error = ejecutar($conexion,
    "INSERT INTO asistencias (id_alumno, fecha, hora, estado, id_auxiliar)
     VALUES (?, CURDATE(), CURTIME(), ?, ?)", "isi", array($a["id"], $estado, $yo["id"]));

if ($error == 1062) responder("0", $a["nombres"] . " " . $a["apellidos"] . " ya tiene asistencia registrada hoy");
if ($error != 0)    responder("0", "No se pudo registrar la asistencia");
responder("1", $estado . ": " . $a["nombres"] . " " . $a["apellidos"]);
?>
