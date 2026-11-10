package com.app.institucion;

import java.io.Serializable;

public class Asignacion implements Serializable {
    private String id, idDocente, docente, idCurso, curso, grado, seccion, periodo;

    public Asignacion() {
    }

    public Asignacion(String id, String idDocente, String docente, String idCurso, String curso, String grado, String seccion, String periodo) {
        this.id = id;
        this.idDocente = idDocente;
        this.docente = docente;
        this.idCurso = idCurso;
        this.curso = curso;
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

    public String getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(String idDocente) {
        this.idDocente = idDocente;
    }

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public String getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(String idCurso) {
        this.idCurso = idCurso;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
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
        return curso + " - " + grado + " " + seccion + " (" + periodo + ")";
    }
}
