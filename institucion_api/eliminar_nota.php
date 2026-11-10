<?php
// HU-08
require "conexion.php";
$yo = verificar_sesion($conexion, array("Docente"));
ejecutar($conexion,
    "DELETE n FROM notas n INNER JOIN asignaciones s ON s.id = n.id_asignacion
      WHERE n.id = ? AND s.id_docente = ?", "ii", array(intval(dato("id")), $yo["id"]));
responder("1", "datos eliminados");
?>
