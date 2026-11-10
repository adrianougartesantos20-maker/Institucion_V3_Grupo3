<?php
// HU-14
require "conexion.php";
verificar_sesion($conexion, array("Administrador"));
$tipo = intval(dato("tipo"));

if ($tipo == 1) {
    $datos = consultar($conexion,
        "SELECT r.nombre AS descripcion, COUNT(u.id) AS valor
           FROM roles r LEFT JOIN usuarios u ON u.id_rol = r.id
          GROUP BY r.id, r.nombre ORDER BY r.id");
} else if ($tipo == 2) {
    $datos = consultar($conexion,
        "SELECT CONCAT(grado, ' ', seccion, ' - ', periodo) AS descripcion, COUNT(*) AS valor
           FROM matriculas GROUP BY periodo, grado, seccion ORDER BY periodo DESC, grado, seccion");
} else if ($tipo == 3) {
    $datos = consultar($conexion,
        "SELECT estado AS descripcion, COUNT(*) AS valor
           FROM asistencias WHERE fecha = CURDATE() GROUP BY estado ORDER BY estado");
    $total = consultar($conexion, "SELECT COUNT(*) AS t FROM usuarios WHERE id_rol = 4");
    $marcados = 0;
    foreach ($datos as $d) $marcados += $d["valor"];
    $datos[] = array("descripcion" => "Sin registro hoy", "valor" => max(0, $total[0]["t"] - $marcados));
} else if ($tipo == 4) {
    $datos = consultar($conexion,
        "SELECT CONCAT(c.nombre, ' (', s.grado, ' ', s.seccion, ')') AS descripcion,
                ROUND(AVG(n.nota), 2) AS valor
           FROM notas n
           INNER JOIN asignaciones s ON s.id = n.id_asignacion
           INNER JOIN cursos c       ON c.id = s.id_curso
          GROUP BY s.id, c.nombre, s.grado, s.seccion ORDER BY c.nombre");
} else {
    responder("0", "Tipo de reporte no válido");
}
responder("1", "Reporte generado", $datos);
?>
