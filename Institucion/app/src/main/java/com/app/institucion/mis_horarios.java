package com.app.institucion;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
public class mis_horarios extends AppCompatActivity {

    TextView tvResumen;
    ListView lst1;
    ArrayList<String> horarios_ = new ArrayList<String>();
    ArrayList<Horario> horarios = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_horarios);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvResumen = (TextView) findViewById(R.id.tvResumen);
        lst1 = (ListView) findViewById(R.id.lst1);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_lista, horarios_);
        lst1.setAdapter(arrayAdapter);
        ListarDatos();
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
                                    horarios_.add(h.getDia() + "   " + h.getHoraInicio() + " - " + h.getHoraFin()
                                            + "\n" + h.getCurso() + " - Prof. " + h.getDocente()
                                            + "  (" + h.getGrado() + " " + h.getSeccion() + ")");
                                }
                            } else {
                                Toast.makeText(mis_horarios.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tvResumen.setText(horarios.isEmpty() ? "Aún no hay horarios registrados" : horarios.size() + " clases a la semana");
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
        RequestQueue requestQueue = Volley.newRequestQueue(mis_horarios.this);
        requestQueue.add(request);
    }
}
