<?php
// HU-06
require "conexion.php";
verificar_sesion($conexion, array("Auxiliar", "Administrador"));
$id_alumno = intval(dato("id_alumno"));

$sql = "SELECT a.id, a.id_alumno, CONCAT(u.apellidos, ', ', u.nombres) AS alumno,
               DATE_FORMAT(a.fecha, '%d/%m/%Y') AS fecha, TIME_FORMAT(a.hora, '%H:%i') AS hora, a.estado
          FROM asistencias a INNER JOIN usuarios u ON u.id = a.id_alumno";
if ($id_alumno > 0) {
    $datos = consultar($conexion, $sql . " WHERE a.id_alumno = ? ORDER BY a.fecha DESC, a.hora DESC LIMIT 200",
                       "i", array($id_alumno));
} else {
    $datos = consultar($conexion, $sql . " ORDER BY a.fecha DESC, a.hora DESC LIMIT 200");
}
responder("1", "Historial de asistencia", $datos);
?>
