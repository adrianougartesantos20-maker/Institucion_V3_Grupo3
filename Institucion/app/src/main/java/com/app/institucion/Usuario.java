package com.app.institucion;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String id, usuario, nombres, apellidos, correo, telefono, idRol, rol;

    public Usuario() {
    }

    public Usuario(String id, String usuario, String nombres, String apellidos, String correo, String telefono, String idRol, String rol) {
        this.id = id;
        this.usuario = usuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.idRol = idRol;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getIdRol() {
        return idRol;
    }

    public void setIdRol(String idRol) {
        this.idRol = idRol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    @Override
    public String toString() {
        if (apellidos == null || apellidos.isEmpty()) return nombres;
        return apellidos + ", " + nombres;
    }
}
