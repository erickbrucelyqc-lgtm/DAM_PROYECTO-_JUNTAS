package com.example.dam_proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Olvidaste extends AppCompatActivity {

    private EditText etNombreUsuario;
    // Usaremos etCodigoVerificacion como el campo de la RESPUESTA SECRETA
    private EditText etRespuestaSecreta;
    private EditText etNuevaContrasena;
    private EditText etConfirmarContrasena;
    private Button btnAccion;

    // Usaremos el TextView de título para mostrar la pregunta (o puedes agregar un TextView nuevo)
    private TextView tvTituloPregunta;

    // URL para los scripts PHP
    private static final String URL_OBTENER_PREGUNTA = "http://10.0.2.2/crud_android2/obtener_pregunta.php";
    private static final String URL_RESETEAR_CONTRASENA = "http://10.0.2.2/crud_android2/resetear_contrasena_seguridad.php";

    private RequestQueue requestQueue;

    // Control de Flujo
    private int pasoActual = 1; // 1: Ingresar Usuario, 2: Responder Pregunta, 3: Nueva Contraseña
    private String nombreUsuarioVerificado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvidaste);

        // Inicializar componentes (usando tus IDs del XML)
        etNombreUsuario = findViewById(R.id.editUsuario); // Correo/Usuario
        etRespuestaSecreta = findViewById(R.id.editUsuario2); // Respuesta Secreta
        etNuevaContrasena = findViewById(R.id.editUsuario3); // Nueva Contraseña
        etConfirmarContrasena = findViewById(R.id.editUsuario4); // Confirmar Contraseña
        btnAccion = findViewById(R.id.btnIngresar);
        tvTituloPregunta = findViewById(R.id.textView); // Usaremos el título para la pregunta

        requestQueue = Volley.newRequestQueue(this);

        // 🔑 Inicialización: Paso 1
        configurarPaso1();

        // Listener del botón principal
        btnAccion.setOnClickListener(v -> {
            if (pasoActual == 1) {
                obtenerPregunta();
            } else if (pasoActual == 2) {
                verificarRespuesta();
            } else if (pasoActual == 3) {
                restablecerContrasena();
            }
        });
    }

    // --- CONFIGURACIÓN DE VISTAS POR PASO ---

    private void configurarPaso1() { // Ingresar Nombre de Usuario
        pasoActual = 1;
        tvTituloPregunta.setText("VERIFICAR CUENTA");
        etNombreUsuario.setHint("Nombre de Usuario");
        etNombreUsuario.setVisibility(View.VISIBLE);
        etNombreUsuario.setEnabled(true);
        etRespuestaSecreta.setVisibility(View.GONE);
        etNuevaContrasena.setVisibility(View.GONE);
        etConfirmarContrasena.setVisibility(View.GONE);
        btnAccion.setText("CONTINUAR");
    }

    private void configurarPaso2(String pregunta) { // Responder Pregunta
        pasoActual = 2;
        tvTituloPregunta.setText(pregunta); // Muestra la pregunta de seguridad
        etNombreUsuario.setEnabled(false); // Bloquear edición del usuario
        etRespuestaSecreta.setHint("Respuesta Secreta");
        etRespuestaSecreta.setVisibility(View.VISIBLE);
        btnAccion.setText("VERIFICAR RESPUESTA");
    }

    private void configurarPaso3() { // Ingresar Nueva Contraseña
        pasoActual = 3;
        tvTituloPregunta.setText("CREAR NUEVA CONTRASEÑA");
        etRespuestaSecreta.setVisibility(View.GONE);
        etNuevaContrasena.setVisibility(View.VISIBLE);
        etConfirmarContrasena.setVisibility(View.VISIBLE);
        btnAccion.setText("REESTABLECER");
    }

    // --- LÓGICA DE PETICIONES VOLLEY ---

    /**
     * Paso 1: Envía el nombre de usuario y espera la Pregunta de Seguridad.
     */
    private void obtenerPregunta() {
        final String nombreUsuario = etNombreUsuario.getText().toString().trim();
        if (nombreUsuario.isEmpty()) {
            Toast.makeText(this, "Ingresa tu nombre de usuario.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_OBTENER_PREGUNTA,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String estado = obj.getString("estado");

                        if (estado.equals("ok")) {
                            String pregunta = obj.getString("pregunta_seguridad");
                            // 🔑 ÉXITO: Pasar al Paso 2
                            nombreUsuarioVerificado = nombreUsuario; // Guardar el usuario para el paso final
                            configurarPaso2(pregunta);
                        } else {
                            Toast.makeText(Olvidaste.this, obj.getString("mensaje"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(Olvidaste.this, "Error de respuesta del servidor.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(Olvidaste.this, "Error de red al buscar usuario.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre_usuario", nombreUsuario);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * Paso 2: Envía la respuesta secreta y espera la verificación.
     */
    private void verificarRespuesta() {
        final String respuesta = etRespuestaSecreta.getText().toString().trim();

        if (respuesta.isEmpty()) {
            Toast.makeText(this, "Ingresa tu respuesta secreta.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RESETEAR_CONTRASENA,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String estado = obj.getString("estado");

                        if (estado.equals("verificado")) {
                            // 🔑 ÉXITO: Pasar al Paso 3 para ingresar nueva contraseña
                            Toast.makeText(Olvidaste.this, "Verificación exitosa. Ingresa tu nueva contraseña.", Toast.LENGTH_SHORT).show();
                            configurarPaso3();
                        } else {
                            Toast.makeText(Olvidaste.this, obj.getString("mensaje"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(Olvidaste.this, "Error de respuesta del servidor.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(Olvidaste.this, "Error de red al verificar respuesta.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("accion", "verificar");
                params.put("nombre_usuario", nombreUsuarioVerificado);
                params.put("respuesta_secreta", respuesta);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * Paso 3: Envía la nueva contraseña para actualizar la BD.
     */
    private void restablecerContrasena() {
        final String nuevaContrasena = etNuevaContrasena.getText().toString().trim();
        final String confirmarContrasena = etConfirmarContrasena.getText().toString().trim();

        if (nuevaContrasena.isEmpty() || !nuevaContrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden o están vacías.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RESETEAR_CONTRASENA,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String estado = obj.getString("estado");
                        String mensaje = obj.getString("mensaje");

                        Toast.makeText(Olvidaste.this, mensaje, Toast.LENGTH_LONG).show();

                        if (estado.equals("ok")) {
                            // ÉXITO FINAL: Redirigir al Login
                            Intent intent = new Intent(Olvidaste.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(Olvidaste.this, "Error de respuesta del servidor.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(Olvidaste.this, "Error de red al restablecer contraseña.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("accion", "resetear");
                params.put("nombre_usuario", nombreUsuarioVerificado);
                params.put("nueva_contrasena", nuevaContrasena);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}