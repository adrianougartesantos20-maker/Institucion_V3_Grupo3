<?php
// HU-08
require "conexion.php";
$yo = verificar_sesion($conexion, array("Docente"));
$id_asignacion = intval(dato("id_asignacion"));
$id_alumno     = intval(dato("id_alumno"));
$evaluacion    = dato("evaluacion");
$nota          = dato("nota");

if ($evaluacion == "") responder("0", "Ingrese el nombre de la evaluación");
if (!is_numeric($nota) || $nota < 0 || $nota > 20) responder("0", "La nota debe estar entre 0 y 20");

$asig = consultar($conexion, "SELECT grado, seccion, periodo FROM asignaciones WHERE id = ? AND id_docente = ?",
                  "ii", array($id_asignacion, $yo["id"]));
if (count($asig) == 0) responder("0", "Ese curso no está asignado a usted");
$mat = consultar($conexion, "SELECT id FROM matriculas WHERE id_alumno = ? AND grado = ? AND seccion = ? AND periodo = ?",
                 "isss", array($id_alumno, $asig[0]["grado"], $asig[0]["seccion"], $asig[0]["periodo"]));
if (count($mat) == 0) responder("0", "El alumno no pertenece a esa sección");

$valor = floatval($nota);
ejecutar($conexion,
    "INSERT INTO notas (id_asignacion, id_alumno, evaluacion, nota, fecha) VALUES (?, ?, ?, ?, CURDATE())
     ON DUPLICATE KEY UPDATE nota = VALUES(nota), fecha = CURDATE()",
    "iisd", array($id_asignacion, $id_alumno, $evaluacion, $valor));
responder("1", "Nota guardada");
?>
