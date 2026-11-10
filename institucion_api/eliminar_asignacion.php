<?php
// HU-07
require "conexion.php";
verificar_sesion($conexion, array("Administrador"));
ejecutar($conexion, "DELETE FROM asignaciones WHERE id = ?", "i", array(intval(dato("id"))));
responder("1", "datos eliminados");
?>
