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
import java.util.Locale;
import java.util.Map;

// HU-10
public class mis_notas extends AppCompatActivity {

    TextView tvPromedio;
    ListView lst1;
    ArrayList<String> notas_ = new ArrayList<String>();
    ArrayList<Nota> notas = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Sesion.activa(this)) return;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_notas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvPromedio = (TextView) findViewById(R.id.tvPromedio);
        lst1 = (ListView) findViewById(R.id.lst1);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_lista, notas_);
        lst1.setAdapter(arrayAdapter);
        ListarDatos();
    }

    public void ListarDatos() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Conexion.URL + "mostrar_notas.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        notas_.clear();
                        notas.clear();
                        double suma = 0;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("exito").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("datos");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Nota n = new Nota(
                                            object.getString("id"),
                                            object.getString("id_asignacion"),
                                            object.getString("curso"),
                                            object.getString("id_alumno"),
                                            object.getString("alumno"),
                                            object.getString("evaluacion"),
                                            object.getString("nota"),
                                            object.getString("fecha"));
                                    notas.add(n);
                                    suma = suma + Double.parseDouble(n.getNota());
                                    notas_.add(n.getCurso() + "\n" + n.getEvaluacion() + ":  " + n.getNota() + "   (" + n.getFecha() + ")");
                                }
                            } else {
                                Toast.makeText(mis_notas.this, jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (notas.isEmpty()) {
                            tvPromedio.setText("Aún no tiene notas registradas");
                        } else {
                            double promedio = suma / notas.size();
                            tvPromedio.setText("Promedio general: " + String.format(Locale.US, "%.2f", promedio));
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
        RequestQueue requestQueue = Volley.newRequestQueue(mis_notas.this);
        requestQueue.add(request);
    }
}
