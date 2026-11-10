<?php
// HU-03
require "conexion.php";
$yo = verificar_sesion($conexion, array("Administrador"));
$id_usuario = intval(dato("id_usuario"));
$id_rol     = intval(dato("id_rol"));

if ($id_rol < 1 || $id_rol > 4) responder("0", "Seleccione un rol válido");
if ($id_usuario == $yo["id"] && $id_rol != 1) responder("0", "No puede quitarse el rol de administrador");
if (rol_de($conexion, $id_usuario) == "Administrador" && $id_rol != 1) {
    $admins = consultar($conexion, "SELECT COUNT(*) AS total FROM usuarios WHERE id_rol = 1");
    if ($admins[0]["total"] <= 1) responder("0", "Debe quedar al menos un administrador");
}
ejecutar($conexion, "UPDATE usuarios SET id_rol = ? WHERE id = ?", "ii", array($id_rol, $id_usuario));
responder("1", "Rol asignado correctamente");
?>
