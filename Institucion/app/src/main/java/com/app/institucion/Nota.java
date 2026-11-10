package com.app.institucion;

import java.io.Serializable;

public class Nota implements Serializable {
    private String id, idAsignacion, curso, idAlumno, alumno, evaluacion, nota, fecha;

    public Nota() {
    }

    public Nota(String id, String idAsignacion, String curso, String idAlumno, String alumno, String evaluacion, String nota, String fecha) {
        this.id = id;
        this.idAsignacion = idAsignacion;
        this.curso = curso;
        this.idAlumno = idAlumno;
        this.alumno = alumno;
        this.evaluacion = evaluacion;
        this.nota = nota;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(String idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
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

    public String getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(String evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
