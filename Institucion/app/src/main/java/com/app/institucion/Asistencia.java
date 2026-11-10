package com.app.institucion;

import java.io.Serializable;

public class Asistencia implements Serializable {
    private String id, idAlumno, alumno, fecha, hora, estado;

    public Asistencia() {
    }

    public Asistencia(String id, String idAlumno, String alumno, String fecha, String hora, String estado) {
        this.id = id;
        this.idAlumno = idAlumno;
        this.alumno = alumno;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(String idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getAlumno() {
        return alumno;
    }

    public void setAlumno(String alumno) {
        this.alumno = alumno;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
