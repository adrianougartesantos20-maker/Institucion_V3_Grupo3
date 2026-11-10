<?php
// HU-11
require "conexion.php";
verificar_sesion($conexion, array("Administrador"));
$id_asignacion = intval(dato("id_asignacion"));
$dia           = dato("dia");
$hora_inicio   = dato("hora_inicio");
$hora_fin      = dato("hora_fin");

$dias = array("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado");
if (!in_array($dia, $dias)) responder("0", "Seleccione un día válido");
$formato = '/^([01][0-9]|2[0-3]):[0-5][0-9]$/';
if (!preg_match($formato, $hora_inicio) || !preg_match($formato, $hora_fin)) responder("0", "Use el formato de hora HH:MM, ejemplo 08:00");
if ($hora_inicio >= $hora_fin) responder("0", "La hora de fin debe ser mayor a la de inicio");

$error = ejecutar($conexion, "INSERT INTO horarios (id_asignacion, dia, hora_inicio, hora_fin) VALUES (?, ?, ?, ?)",
                  "isss", array($id_asignacion, $dia, $hora_inicio, $hora_fin));
if ($error != 0) responder("0", "No se pudo registrar el horario");
responder("1", "Horario registrado");
?>
