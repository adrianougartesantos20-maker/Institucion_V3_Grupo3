<?php
// HU-11
require "conexion.php";
$yo = verificar_sesion($conexion, array("Administrador", "Docente", "Alumno"));

$sql = "SELECT h.id, h.id_asignacion, c.nombre AS curso, CONCAT(u.nombres, ' ', u.apellidos) AS docente,
               s.grado, s.seccion, h.dia, h.hora_inicio, h.hora_fin
          FROM horarios h
          INNER JOIN asignaciones s ON s.id = h.id_asignacion
          INNER JOIN cursos c       ON c.id = s.id_curso
          INNER JOIN usuarios u     ON u.id = s.id_docente";
$orden = " ORDER BY FIELD(h.dia, 'Lunes','Martes','Miércoles','Jueves','Viernes','Sábado'), h.hora_inicio";

if ($yo["rol"] == "Alumno") {
    $datos = consultar($conexion, $sql . " INNER JOIN matriculas m
                 ON m.grado = s.grado AND m.seccion = s.seccion AND m.periodo = s.periodo
                 WHERE m.id_alumno = ?" . $orden, "i", array($yo["id"]));
} else if ($yo["rol"] == "Docente") {
    $datos = consultar($conexion, $sql . " WHERE s.id_docente = ?" . $orden, "i", array($yo["id"]));
} else {
    $datos = consultar($conexion, $sql . $orden);
}
responder("1", "Lista de horarios", $datos);
?>
