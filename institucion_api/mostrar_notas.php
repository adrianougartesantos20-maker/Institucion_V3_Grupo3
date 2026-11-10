<?php
// HU-08 / HU-10
require "conexion.php";
$yo = verificar_sesion($conexion, array("Docente", "Alumno"));

$sql = "SELECT n.id, n.id_asignacion, c.nombre AS curso, n.id_alumno,
               CONCAT(u.apellidos, ', ', u.nombres) AS alumno, n.evaluacion, n.nota,
               DATE_FORMAT(n.fecha, '%d/%m/%Y') AS fecha
          FROM notas n
          INNER JOIN asignaciones s ON s.id = n.id_asignacion
          INNER JOIN cursos c       ON c.id = s.id_curso
          INNER JOIN usuarios u     ON u.id = n.id_alumno";
if ($yo["rol"] == "Alumno") {
    $datos = consultar($conexion, $sql . " WHERE n.id_alumno = ? ORDER BY curso, n.fecha", "i", array($yo["id"]));
} else {
    $datos = consultar($conexion, $sql . " WHERE n.id_asignacion = ? AND s.id_docente = ? ORDER BY alumno, n.evaluacion",
                       "ii", array(intval(dato("id_asignacion")), $yo["id"]));
}
responder("1", "Lista de notas", $datos);
?>
