package com.example.dam_proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class Registrarse extends AppCompatActivity {

    EditText editUsuario;
    EditText editCorreo;
    EditText editContrasena;
    EditText editConfirmarContrasena;

    // 🔑 Nuevas variables para Seguridad
    EditText editPreguntaSeguridad;
    EditText editRespuestaSecreta;

    Button btnRegistrarse;

    // Asegúrate de que esta URL apunte al script PHP que maneja la inserción de 6 campos
    private static final String URL_REGISTRO = "http://10.0.2.2/crud_android2/insertarUsuario.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        // 1. Inicializar los componentes existentes
        editUsuario = findViewById(R.id.editUsuario);
        editCorreo = findViewById(R.id.editUsuario2);
        editContrasena = findViewById(R.id.editUsuario3);
        editConfirmarContrasena = findViewById(R.id.editUsuario4);

        // 🔑 Inicializar los nuevos componentes
        editPreguntaSeguridad = findViewById(R.id.editUsuario5);
        editRespuestaSecreta = findViewById(R.id.editUsuario6);

        btnRegistrarse = findViewById(R.id.btnIngresar);

        // 2. Evento al hacer clic en el botón
        btnRegistrarse.setOnClickListener(view -> {
            registrarUsuario();
        });
    }

    private void registrarUsuario() {
        final String nombre = editUsuario.getText().toString().trim();
        final String correo = editCorreo.getText().toString().trim();
        final String contrasena = editContrasena.getText().toString().trim();
        final String confirmar = editConfirmarContrasena.getText().toString().trim();

        // 🔑 Obtener valores de seguridad
        final String pregunta = editPreguntaSeguridad.getText().toString().trim();
        final String respuesta = editRespuestaSecreta.getText().toString().trim();

        // 1. Validaciones locales
        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty() ||
                pregunta.isEmpty() || respuesta.isEmpty()) { // 🔑 Agregar validación de campos de seguridad
            Toast.makeText(this, "Completa todos los campos, incluyendo la Pregunta de Seguridad.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmar)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Configurar la petición HTTP (POST)
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTRO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String estado = jsonResponse.getString("estado");
                            String mensaje = jsonResponse.getString("mensaje");

                            Toast.makeText(Registrarse.this, mensaje, Toast.LENGTH_LONG).show();

                            if (estado.equals("ok")) {
                                Intent intent = new Intent(Registrarse.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Registrarse.this, "Error de respuesta: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Registrarse.this, "Error de red: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {

            // 3. Definir los parámetros POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();

                // Parámetros existentes
                parametros.put("nombre", nombre);
                parametros.put("correo", correo);
                parametros.put("contrasena", contrasena);

                // 🔑 Nuevos parámetros de seguridad
                parametros.put("pregunta_seguridad", pregunta);
                parametros.put("respuesta_secreta", respuesta);

                return parametros;
            }
        };

        // 4. Enviar la petición
        requestQueue.add(stringRequest);
    }
}