<?php
// HU-07 / HU-08
require "conexion.php";
$yo = verificar_sesion($conexion, array("Administrador", "Docente"));

$sql = "SELECT s.id, s.id_docente, CONCAT(u.apellidos, ', ', u.nombres) AS docente,
               s.id_curso, c.nombre AS curso, s.grado, s.seccion, s.periodo
          FROM asignaciones s
          INNER JOIN usuarios u ON u.id = s.id_docente
          INNER JOIN cursos c   ON c.id = s.id_curso";
if ($yo["rol"] == "Docente") {
    $datos = consultar($conexion, $sql . " WHERE s.id_docente = ? ORDER BY s.periodo DESC, s.grado, s.seccion, curso",
                       "i", array($yo["id"]));
} else {
    $datos = consultar($conexion, $sql . " ORDER BY s.periodo DESC, s.grado, s.seccion, curso");
}
responder("1", "Lista de asignaciones", $datos);
?>
