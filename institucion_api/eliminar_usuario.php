<?php
// HU-02
require "conexion.php";
$yo = verificar_sesion($conexion, array("Administrador"));
$id = intval(dato("id"));

if ($id == $yo["id"]) responder("0", "No puede eliminar la cuenta con la que inició sesión");
if (rol_de($conexion, $id) == "Administrador") {
    $admins = consultar($conexion, "SELECT COUNT(*) AS total FROM usuarios WHERE id_rol = 1");
    if ($admins[0]["total"] <= 1) responder("0", "Debe quedar al menos un administrador");
}
ejecutar($conexion, "DELETE FROM usuarios WHERE id = ?", "i", array($id));
responder("1", "datos eliminados");
?>
