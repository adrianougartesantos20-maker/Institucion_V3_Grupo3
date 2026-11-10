package com.app.institucion;

import java.io.Serializable;

public class Matricula implements Serializable {
    private String id, idAlumno, alumno, usuario, grado, seccion, periodo;

    public Matricula() {
    }

    public Matricula(String id, String idAlumno, String alumno, String usuario, String grado, String seccion, String periodo) {
        this.id = id;
        this.idAlumno = idAlumno;
        this.alumno = alumno;
        this.usuario = usuario;
        this.grado = grado;
        this.seccion = seccion;
        this.periodo = periodo;
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    @Override
    public String toString() {
        return alumno;
    }
}
