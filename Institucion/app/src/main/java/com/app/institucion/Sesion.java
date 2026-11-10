package com.app.institucion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Sesion {

    public static Usuario usuario;
    public static String token = "";

    public static boolean activa(AppCompatActivity activity) {
        if (usuario != null && !token.isEmpty()) return true;
        Toast.makeText(activity, "Su sesión terminó, ingrese nuevamente", Toast.LENGTH_SHORT).show();
        irAlLogin(activity);
        return false;
    }

   
    public static void cerrar(final AppCompatActivity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Cerrar sesión")
                .setMessage("¿Desea salir de su cuenta?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String tokenAnterior = token;
                        StringRequest request = new StringRequest(Request.Method.POST,
                                Conexion.URL + "logout.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("token", tokenAnterior);
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(activity);
                        requestQueue.add(request);

                        usuario = null;
                        token = "";
                        Toast.makeText(activity, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                        irAlLogin(activity);
                    }
                })
                .show();
    }
    private static void irAlLogin(AppCompatActivity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
