<?php
// HU-07
require "conexion.php";
verificar_sesion($conexion, array("Administrador"));
responder("1", "Lista de cursos", consultar($conexion, "SELECT id, nombre FROM cursos ORDER BY nombre"));
?>
