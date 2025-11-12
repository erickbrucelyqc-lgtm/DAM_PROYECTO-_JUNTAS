package com.example.dam_proyecto;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Importaciones de Volley y JSON
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response.Listener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements GrupoAdapter.GrupoSeleccionadoListener {

    // Componentes existentes
    ImageButton imageButton;
    Button btnIngresar2;
    Button btnAgregar;
    TextView txtUsuarioHeader;
    TextView txtUsuarioBienvenida;

    CircleImageView imageViewUser;

    // 1. Declaración de RecyclerView y componentes relacionados
    RecyclerView rvListaGrupos;
    GrupoAdapter grupoAdapter;
    List<Grupo> listaGrupos; // Usaremos esta lista para almacenar los objetos Grupo
    private String userName;
    private String urlPerfil = null;
    private int userId = 0;
    private RequestQueue requestQueue;

    // URL para el script PHP (AJUSTA TU IP Y PUERTO)
    private static final String URL_GRUPOS = "http://10.0.2.2/crud_android2/obtener_grupos.php";
    private static final String URL_CREAR_GRUPO = "http://10.0.2.2/crud_android2/crear_grupo.php";
    private static final String URL_UNIRSE_GRUPO = "http://10.0.2.2/crud_android2/unirse_a_grupo.php";
    private static final String URL_GUARDAR_PERFIL = "http://10.0.2.2/crud_android2/guardar_perfil_usuario.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        // --- Personalización de Usuario (Mantienes esta lógica) ---
        imageButton = findViewById(R.id.imageButton);
        txtUsuarioHeader = findViewById(R.id.textView4);
        txtUsuarioBienvenida = findViewById(R.id.textView6);
        requestQueue = Volley.newRequestQueue(this);
        imageViewUser = findViewById(R.id.imageViewUser);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String nombreUsuario = extras.getString("NOMBRE_USUARIO");
            userId = extras.getInt("ID_USUARIO", 0);
            urlPerfil = extras.getString("URL_PERFIL");
            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                userName = nombreUsuario;
                txtUsuarioHeader.setText(userName);
                txtUsuarioBienvenida.setText(userName);
            } else {
                txtUsuarioHeader.setText("Invitado");
                txtUsuarioBienvenida.setText("Invitado");
            }
        }

        // --- 2. Inicialización del RecyclerView ---
        rvListaGrupos = findViewById(R.id.rvListaGrupos);
        rvListaGrupos.setHasFixedSize(true);
        // Usa LinearLayoutManager para que la lista sea vertical (la forma más común)
        rvListaGrupos.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de Grupos
        listaGrupos = new ArrayList<>();
        cargarImagenPerfil(urlPerfil);

        // Llamar a la función para cargar los datos desde PHP
        cargarGrupos();

        // --- Listeners Existentes ---

        // Cerrar Sesión (Logout)
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish(); // Opcional: Cierra MainActivity para que no pueda volver atrás
            }
        });

        // Unirse a Grupo
        btnIngresar2 = findViewById(R.id.btnEditar);
        btnIngresar2.setOnClickListener(view -> {
            mostrarDialogoUnirseGrupo();
        });

        // Crear Grupo
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(view -> {
            mostrarDialogoCrearGrupo();
        });

        imageViewUser.setOnClickListener(v -> {
            // 2. Llama al nuevo diálogo
            mostrarDialogoCambiarPerfil();
        });

    }

    /**
     * Usa Volley para conectar con obtener_grupos.php y llenar el RecyclerView.
     */
    private void cargarGrupos() {
        // 1. Limpiar la lista existente antes de cargar nuevos datos
        listaGrupos.clear();
        if(grupoAdapter != null) {
            grupoAdapter.notifyDataSetChanged();
        }

        // Cambiar a POST para enviar el ID del usuario
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GRUPOS,
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Lógica de parsing JSON
                        try {
                            JSONObject obj = new JSONObject(response);
                            String estado = obj.getString("estado");

                            if (estado.equals("ok")) {
                                JSONArray gruposArray = obj.getJSONArray("grupos");

                                // Iterar sobre todos los grupos en el JSON
                                for (int i = 0; i < gruposArray.length(); i++) {

                                    JSONObject grupoJson = gruposArray.getJSONObject(i);
                                    // Asegúrate de que tu constructor de Grupo reciba los 4 parámetros
                                    Grupo grupo = new Grupo(
                                            grupoJson.getInt("id"),
                                            grupoJson.getString("nombre"),
                                            grupoJson.getString("descripcion"),
                                            grupoJson.getInt("creador_id"),
                                            grupoJson.getString("url_imagen")
                                    );
                                    listaGrupos.add(grupo);
                                }

                                if (grupoAdapter == null) {
                                    // ⚠️ SOLUCIÓN FINAL AL PROBLEMA DE AMBIGÜEDAD:
                                    // Usamos MainActivity.this para apuntar a la Activity.
                                    // Usamos el cast explícito a GrupoSeleccionadoListener
                                    // para evitar el conflicto con Response.Listener<String>.
                                    grupoAdapter = new GrupoAdapter(
                                            MainActivity.this,
                                            listaGrupos,
                                            (GrupoAdapter.GrupoSeleccionadoListener) MainActivity.this
                                    );
                                    rvListaGrupos.setAdapter(grupoAdapter);
                                } else {
                                    grupoAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Error al cargar grupos: " + obj.getString("mensaje"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error al parsear JSON. Respuesta: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error de red: No se pudo conectar al servidor PHP.", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }) {
            // Método para ENVIAR los parámetros POST (el ID del usuario)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Enviar el ID del usuario logueado
                params.put("id_usuario", String.valueOf(userId));
                return params;
            }
        };

        // Añadir la solicitud a la cola de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // Archivo: MainActivity.java

    /**
     * Muestra el diálogo para cambiar la imagen de perfil del usuario.
     */
    private void mostrarDialogoCambiarPerfil() {
        // 1. Crear el objeto Diálogo
        final Dialog dialog = new Dialog(this);
        // 2. Usar la vista XML que creamos
        dialog.setContentView(R.layout.activity_editar_perfil);

        // 3. Vincular componentes del diálogo
        final EditText etNuevaUrl = dialog.findViewById(R.id.et_nueva_url_perfil);
        final CircleImageView imgPreview = dialog.findViewById(R.id.img_perfil_preview);
        Button btnGuardar = dialog.findViewById(R.id.btn_guardar_perfil);
        ImageButton btnCerrar = dialog.findViewById(R.id.btn_cerrar_dialogo_editar);

        // [Opcional] Cargar la URL actual del perfil en el preview y el EditText (si ya la tienes guardada)
        // String urlActual = // ... obtener la URL actual (ej: de SharedPreferences) ...
        etNuevaUrl.setText(urlPerfil);
        if (urlPerfil != null && !urlPerfil.isEmpty()) {

            // Opcional: setear la URL en el EditText
            etNuevaUrl.setText(urlPerfil);

            ImageRequest imageRequest = new ImageRequest(
                    urlPerfil,
                    bitmap -> {
                        // Éxito: Asignar el Bitmap al preview del diálogo
                        imgPreview.setImageBitmap(bitmap);
                    },
                    0, 0, // 0, 0 permite que Volley determine el tamaño (o usa 80, 80 para el tamaño exacto del preview)
                    android.widget.ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.RGB_565,
                    error -> {
                        // Error: Usar imagen por defecto si la URL actual falla
                        imgPreview.setImageResource(R.drawable.images);
                    }
            );
            // Usamos la cola de peticiones de clase
            requestQueue.add(imageRequest);

        } else {
            // Si no hay URL, carga el drawable por defecto.
            imgPreview.setImageResource(R.drawable.images);
        }
        // Cargar la imagen actual en imgPreview usando Volley/Glide

        // 4. Listener del botón Guardar
        btnGuardar.setOnClickListener(v -> {
            String nuevaUrl = etNuevaUrl.getText().toString();
            if (!nuevaUrl.isEmpty()) {
                // Llama a la función que guarda la URL en el servidor
                guardarUrlPerfil(nuevaUrl);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "La URL no puede estar vacía.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void mostrarDialogoUnirseGrupo() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_sub_unirse);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            // Aseguramos que sea 'wrap_content' en alto para que se vea como emergente
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // 1. Vincular componentes
        // Nota: El campo de texto que era 'descripcion' en el XML original ahora tiene el ID 'et_codigo_invitacion' en el V2
        EditText etCodigo = dialog.findViewById(R.id.et_codigo_invitacion);

        // Nota: El botón de cerrar del XML original tenía ID 'cerrar', y el botón de acción era 'btncerrar2'
        ImageButton btnCerrar = dialog.findViewById(R.id.cerrar); // Botón (X) de cerrar
        Button btnUnirse = dialog.findViewById(R.id.btn_unirse_final); // Botón de acción 'UNIRSE'

        // 2. Listeners
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        // El botón del XML original que se llamaba 'btncerrar2' lo usaremos ahora para cerrar si es necesario
        Button btnCerrarInferior = dialog.findViewById(R.id.btncerrar2);
        if (btnCerrarInferior != null) { // Si mantuviste el botón 'Cerrar' de abajo
            btnCerrarInferior.setOnClickListener(v -> dialog.dismiss());
        }

        btnUnirse.setOnClickListener(v -> {
            String codigo = etCodigo.getText().toString().trim();

            if (codigo.length() != 6) {
                Toast.makeText(this, "El código debe tener 6 caracteres.", Toast.LENGTH_SHORT).show();
            } else {
                // Lógica para enviar el código y el ID del usuario al servidor
                unirseAGrupo(codigo.toUpperCase(), userId, dialog);
            }
        });

        dialog.show();
    }
    private void mostrarDialogoCrearGrupo() {
        // 1. Crear el objeto Dialog
        final Dialog dialog = new Dialog(this);

        // Evita que se pueda cerrar al tocar fuera del diálogo
        dialog.setCancelable(true);

        // 2. Establecer el diseño XML personalizado
        dialog.setContentView(R.layout.activity_sub_crear_grupo);

        // Asegurar que el diálogo tenga esquinas redondeadas y ocupe el ancho completo
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // 3. Vincular componentes del diálogo
        EditText etNombre = dialog.findViewById(R.id.et_nombre_grupo);
        EditText etDescripcion = dialog.findViewById(R.id.et_descripcion_grupo);
        Button btnCrear = dialog.findViewById(R.id.btn_crear_grupo_final);
        ImageButton btnCerrar = dialog.findViewById(R.id.btn_cerrar_dialogo);

        // 4. Listeners
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        btnCrear.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre del grupo es obligatorio.", Toast.LENGTH_SHORT).show();
            } else {
                // Lógica para enviar los datos al servidor
                crearNuevoGrupo(nombre, descripcion, userId, dialog);
            }
        });

        dialog.show();
    }

    // ... dentro de la clase MainActivity ...

    /**
     * Muestra el diálogo de éxito de creación de grupo.
     */
    private void mostrarDialogoExito(String codigo) {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false); // No se puede cerrar al tocar fuera
        dialog.setContentView(R.layout.activity_sub_creado);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Vincular componentes
        TextView tvCodigo = dialog.findViewById(R.id.tv_codigo_invitacion);
        Button btnCerrar = dialog.findViewById(R.id.btn_cerrar_exito);

        // Asignar el código (este dato vendrá del servidor después)
        tvCodigo.setText(codigo);

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // ... dentro de la clase MainActivity ...

    /**
     * Muestra el diálogo de éxito al unirse a un grupo.
     */
    private void mostrarDialogoUnirseExito(String nombreGrupo) {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false); // No se puede cancelar fuera del botón
        dialog.setContentView(R.layout.activity_sub_unirse2);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Vincular componentes
        TextView tvNombreGrupo = dialog.findViewById(R.id.nombreGrupo);
        Button btnCerrar = dialog.findViewById(R.id.btncerrar2);

        // Asignar el nombre dinámico
        tvNombreGrupo.setText(nombreGrupo);

        // Listener para cerrar
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
// ...

    /**
     * Función que usa Volley para enviar los datos del nuevo grupo al servidor PHP.
     */
    private void crearNuevoGrupo(String nombre, String descripcion, int creadorId, final Dialog dialog) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CREAR_GRUPO,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String estado = obj.getString("estado");

                        if (estado.equals("ok")) {
                            Toast.makeText(MainActivity.this, "¡Grupo creado con éxito!", Toast.LENGTH_LONG).show();
                            dialog.dismiss(); // Cerrar el diálogo al éxito
                            // ** AÑADIR ESTO: Mostrar el segundo diálogo **
                            // Usamos un código de prueba "XYZ123" por ahora.
                            String codigoInvitacion = obj.getString("codigo");
                            mostrarDialogoExito(codigoInvitacion);
                            cargarGrupos(); // **IMPORTANTE**: Refrescar la lista de grupos
                        } else {
                            Toast.makeText(MainActivity.this, "Error: " + obj.getString("mensaje"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error al procesar respuesta del servidor.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(MainActivity.this, "Error de red al crear grupo.", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("descripcion", descripcion);
                params.put("creador_id", String.valueOf(creadorId));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    /**
     * Función que usa Volley para unirse a un grupo mediante código.
     */
    private void cargarImagenPerfil(String urlPerfil) {
        if (urlPerfil != null && !urlPerfil.isEmpty()) {

            // 1. Crear la petición de imagen
            ImageRequest imageRequest = new ImageRequest(
                    urlPerfil, // La URL obtenida del servidor/SharedPreferences
                    bitmap -> {
                        // Éxito: Asignar el Bitmap al CircleImageView
                        imageViewUser.setImageBitmap(bitmap);
                    },
                    0, // Ancho máximo
                    0, // Alto máximo
                    android.widget.ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.RGB_565,
                    error -> {
                        // Error: Si falla la descarga, usa la imagen por defecto
                        imageViewUser.setImageResource(R.drawable.images); // Tu imagen por defecto
                        Toast.makeText(this, "Error al cargar la imagen de perfil.", Toast.LENGTH_SHORT).show();
                    }
            );

            // 2. Añadir la solicitud a la cola
            requestQueue.add(imageRequest);
        } else {
            // Si no hay URL, usa la imagen por defecto
            imageViewUser.setImageResource(R.drawable.images);
        }
    }
    private void guardarUrlPerfil(String nuevaUrl) {
        // Necesitas el ID del usuario. Asegúrate de obtenerlo (ej: de SharedPreferences)
        // int usuarioId = // ... obtener ID del usuario logueado ...
        final String sUsuarioId = String.valueOf(userId);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GUARDAR_PERFIL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String estado = obj.getString("estado");

                        Toast.makeText(MainActivity.this, obj.getString("mensaje"), Toast.LENGTH_SHORT).show();

                        if (estado.equals("ok")) {
                            urlPerfil = nuevaUrl;
                            cargarImagenPerfil(nuevaUrl);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Error: Problema al procesar la respuesta.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(MainActivity.this, "Error de red: No se pudo actualizar el perfil.", Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", sUsuarioId);
                params.put("url_perfil", nuevaUrl);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void unirseAGrupo(String codigo, int usuarioId, final Dialog dialog) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UNIRSE_GRUPO,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String estado = obj.getString("estado");
                        String mensaje = obj.getString("mensaje");

                        Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_LONG).show();

                        if (estado.equals("ok")) {
                            dialog.dismiss(); // Cerrar el diálogo al éxito
                            String nombreGrupo = obj.getString("nombre_grupo");
                            mostrarDialogoUnirseExito(nombreGrupo);
                            cargarGrupos(); // Refrescar la lista para incluir el nuevo grupo
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error al procesar respuesta del servidor.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(MainActivity.this, "Error de red al intentar unirse al grupo.", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("codigo", codigo);
                params.put("usuario_id", String.valueOf(usuarioId));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public void onGrupoClick(Grupo grupo) {
        // Muestra el Toast de confirmación que tenías antes:
        Toast.makeText(this, "Redirigiendo a: " + grupo.getNombre(), Toast.LENGTH_SHORT).show();

        // Crear el Intent para ir a JuntaActivity
        Intent intent = new Intent(MainActivity.this, Junta.class);

        // Adjuntar los datos del grupo (ID necesario para la siguiente Activity)
        intent.putExtra("GRUPO_ID", grupo.getId());
        intent.putExtra("NOMBRE_GRUPO", grupo.getNombre());
        intent.putExtra("DESCRI_GRUPO", grupo.getDescripcion());
        intent.putExtra("URL_IMAGEN_GRUPO", grupo.getUrlImagen());
        intent.putExtra("CREADOR_ID", grupo.getCreadorId());
        intent.putExtra("USER_ID", userId);
        // Adjuntar el Nombre del Usuario (para el dashboard en JuntaActivity)
        intent.putExtra("NOMBRE_USUARIO", userName);
        intent.putExtra("URL_IMAGEN_USUARIO", urlPerfil);

        // Iniciar la nueva Activity
        startActivity(intent);
        Toast.makeText(this, "Redirigiendo a: " + grupo.getNombre(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 🔑 Llama a la función que recupera la lista de grupos actualizada de la BD
        // Esto asegura que, después de volver de JuntaActivity, se obtenga la lista fresca.
        cargarGrupos();
    }
}