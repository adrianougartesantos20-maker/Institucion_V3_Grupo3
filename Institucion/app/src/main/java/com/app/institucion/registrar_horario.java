package com.app.institucion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// HU-11
public class registrar_horario extends AppCompatActivity {

    Spinner spAsignacion, spDia;
    EditText edInicio, edFin;
    Button btnRegistrar;
    ListView lst1;

    ArrayList<Asignacion> asignaciones = new ArrayList<>();
    ArrayAdapter<Asignacion> adapterAsignaciones;
    ArrayList<String> horarios_ = new ArrayList<String>();
    ArrayList<Horario> horarios = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_horario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spAsignacion = (Spinner) findViewById(R.id.spAsignacion);
        spDia = (Spinner) findViewById(R.id.spDia);
        edInicio = (EditText) findViewById(R.id.edInicio);
        edFin = (EditText) findViewById(R.id.edFin);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        lst1 = (ListView) findViewById(R.id.lst1);

        adapterAsignaciones = new ArrayAdapter<>(this, R.layout.item_spinner, asignaciones);
        adapterAsignaciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAsignacion.setAdapter(adapterAsignaciones);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_lista, horarios_);
        lst1.setAdapter(arrayAdapter);

        cargarAsignaciones();
        ListarDatos();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();
            }
        });

        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (horarios.isEmpty()) return;
                final Horario h = horarios.get(position);
                new AlertDialog.Builder(registrar_horario.this)
                        .setTitle("Eliminar horario")
                        .setMessage("¿Eliminar " + h.getCurso() + " del " + h.getDia() + " " + h.getHoraInicio() + "?")
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Eliminar(h.getId());
                            }
                        })
                        .show();
            }
        });
    }

    public void cargarAsignaciones() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_asignaciones.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        asignaciones.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    asignaciones.add(new Asignacion(
                                            object.getString("id"),
                                            object.getString("id_docente"),
                                            object.getString("docente"),
                                            object.getString("id_curso"),
                                            object.getString("curso"),
                                            object.getString("grado"),
                                            object.getString("seccion"),
                                            object.getString("periodo")));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapterAsignaciones.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", Sesion.token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(registrar_horario.this);
        requestQueue.add(request);
    }

    public void insert() {
        if (spAsignacion.getSelectedItem() == null) {
            Toast.makeText(this, "Primero asigne cursos a los docentes", Toast.LENGTH_SHORT).show();
            return;
        }
        final Asignacion asignacion = (Asignacion) spAsignacion.getSelectedItem();
        final String dia = spDia.getSelectedItem().toString();
        final String inicio = edInicio.getText().toString().trim();
        final String fin = edFin.getText().toString().trim();

        if (!inicio.matches("([01][0-9]|2[0-3]):[0-5][0-9]") || !fin.matches("([01][0-9]|2[0-3]):[0-5][0-9]")) {
            Toast.makeText(this, "Use el formato HH:MM, ejemplo 08:00", Toast.LENGTH_SHORT).show();
            return;
        }
        if (inicio.compareTo(fin) >= 0) {
            Toast.makeText(this, "La hora de fin debe ser mayor a la de inicio", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "insertar_horario.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(registrar_horario.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("exito").equals("1")) {
                                edInicio.setText("");
                                edFin.setText("");
                                ListarDatos();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(registrar_horario.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", Sesion.token);
                params.put("id_asignacion", asignacion.getId());
                params.put("dia", dia);
                params.put("hora_inicio", inicio);
                params.put("hora_fin", fin);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(registrar_horario.this);
        requestQueue.add(request);
    }

    public void ListarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_horarios.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        horarios_.clear();
                        horarios.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Horario h = new Horario(
                                            object.getString("id"),
                                            object.getString("id_asignacion"),
                                            object.getString("curso"),
                                            object.getString("docente"),
                                            object.getString("grado"),
                                            object.getString("seccion"),
                                            object.getString("dia"),
                                            object.getString("hora_inicio"),
                                            object.getString("hora_fin"));
                                    horarios.add(h);
                                    horarios_.add(h.getGrado() + " " + h.getSeccion() + " - " + h.getDia() + " "
                                            + h.getHoraInicio() + " - " + h.getHoraFin() + "\n" + h.getCurso());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", Sesion.token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(registrar_horario.this);
        requestQueue.add(request);
    }

    public void Eliminar(final String id) {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "eliminar_horario.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                Toast.makeText(registrar_horario.this, "Horario eliminado", Toast.LENGTH_SHORT).show();
                                ListarDatos();
                            } else {
                                Toast.makeText(registrar_horario.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(registrar_horario.this, "Respuesta no válida del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", Sesion.token);
                params.put("id", id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(registrar_horario.this);
        requestQueue.add(request);
    }
}
