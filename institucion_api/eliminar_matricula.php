<?php
// HU-04
require "conexion.php";
verificar_sesion($conexion, array("Administrador"));
ejecutar($conexion, "DELETE FROM matriculas WHERE id = ?", "i", array(intval(dato("id"))));
responder("1", "datos eliminados");
?>
