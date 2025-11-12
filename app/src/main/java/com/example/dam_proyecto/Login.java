package com.example.dam_proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText; // Añadir import para EditText
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

// Importaciones de Volley
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


public class Login extends AppCompatActivity {

    // Declaración de componentes de la interfaz
    EditText editUsuario; // ID: editUsuario
    EditText editContrasena; // ID: editContraseña
    Button btnIngresar;
    Button btnCrearCuenta;
    Button btnOlvidasteContraseña;

    // Ajusta esta URL con tu IP local y puerto
    private static final String URL_LOGIN = "http://10.0.2.2/crud_android2/login.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Vinculación de EditText
        editUsuario = findViewById(R.id.editUsuario);
        editContrasena = findViewById(R.id.editContraseña);

        // 2. Vinculación de Botones
        btnIngresar = findViewById(R.id.btnIngresar);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnOlvidasteContraseña = findViewById(R.id.btnOlvidasteContraseña);

        // Evento al hacer clic en INGRESAR
        btnIngresar.setOnClickListener(view -> {
            // Ya no hay redirección directa, ahora llamamos a la función de autenticación.
            autenticarUsuario();
        });

        // Evento CREAR CUENTA (mantienes tu lógica original)
        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registrarse.class);
                startActivity(intent);
            }
        });

        // Evento OLVIDASTE CONTRASEÑA (mantienes tu lógica original)
        btnOlvidasteContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Olvidaste.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Función para enviar el usuario y contraseña al servidor para verificación.
     */
    private void autenticarUsuario() {
        // Obtenemos los valores de los EditText
        final String usuario = editUsuario.getText().toString().trim();
        final String contrasena = editContrasena.getText().toString().trim();

        // Validación simple de campos vacíos
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Debe ingresar usuario y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String estado = jsonResponse.getString("estado");
                            String mensaje = jsonResponse.getString("mensaje");

                            Toast.makeText(Login.this, mensaje, Toast.LENGTH_LONG).show();

                            if (estado.equals("ok")) {
                                String nombreUsuario = jsonResponse.getString("nombre_usuario");
                                String urlPerfil  = jsonResponse.getString("url_perfil");
                                int idUsuario = jsonResponse.getInt("id_usuario");
                                // *** LOGIN EXITOSO: REDIRIGIR A MainActivity ***
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("NOMBRE_USUARIO", nombreUsuario);
                                intent.putExtra("ID_USUARIO", idUsuario);
                                intent.putExtra("URL_PERFIL", urlPerfil);
                                startActivity(intent);
                                finish(); // Opcional: Cierra la ventana de Login
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Login.this, "Error de respuesta del servidor: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "Error de red o servidor: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {

            // Definir los parámetros POST que el login.php espera
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", usuario);
                params.put("contrasena", contrasena); // Clave sin 'ñ' ni tilde, según la BD
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}