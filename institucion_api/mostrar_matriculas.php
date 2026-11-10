<?php
// HU-04 / HU-08
require "conexion.php";
verificar_sesion($conexion, array("Administrador", "Auxiliar", "Docente"));
$grado   = dato("grado");
$seccion = dato("seccion");
$periodo = dato("periodo");

$sql = "SELECT m.id, m.id_alumno, CONCAT(u.apellidos, ', ', u.nombres) AS alumno, u.usuario,
               m.grado, m.seccion, m.periodo
          FROM matriculas m INNER JOIN usuarios u ON u.id = m.id_alumno";
if ($grado != "" && $seccion != "" && $periodo != "") {
    $datos = consultar($conexion, $sql . " WHERE m.grado = ? AND m.seccion = ? AND m.periodo = ?
                                            ORDER BY alumno", "sss", array($grado, $seccion, $periodo));
} else {
    $datos = consultar($conexion, $sql . " ORDER BY m.periodo DESC, m.grado, m.seccion, alumno");
}
responder("1", "Lista de matrículas", $datos);
?>
