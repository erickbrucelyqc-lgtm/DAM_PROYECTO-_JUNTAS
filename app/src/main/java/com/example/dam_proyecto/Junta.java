package com.example.dam_proyecto;

import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageRequest;
import android.graphics.Bitmap;
import android.widget.ImageView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Junta extends AppCompatActivity {

    // Variables globales para los datos pasados y la lista
    private int grupoId, userId, creadorId;
    private String grupoNombre, grupoDescri, grupoUrlImagen;
    private String userName, urlUser;
    private ImageButton btnVolver;
    private ImageButton btnPagar;
    private Button btnEditar;
    private TextView tvNombreGrupo;
    private TextView tvUserDashboard;
    private RecyclerView rvListaMiembros;

    private ImageView iv_imagen_grupo;
    private RequestQueue requestQueue;
    private ImageView imageViewUser;

    private MiembroAdapter miembroAdapter;
    private List<Miembro> listaMiembros;

    // URL para el script PHP (AJUSTAR ESTA URL)
    private static final String URL_MIEMBROS = "http://10.0.2.2/crud_android2/obtener_miembros.php";

    private static final String URL_ACTUALIZAR_GRUPO = "http://10.0.2.2/crud_android2/actualizar_grupo.php";
    private static final String URL_ELIMINAR_GRUPO = "http://10.0.2.2/crud_android2/eliminar_grupo.php";
    private static final String URL_ACTUALIZAR_PAGO = "http://10.0.2.2/crud_android2/actualizar_estado_pago.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_junta);

        // 1. Vincular vistas
        tvNombreGrupo = findViewById(R.id.tv_nombre_grupo_junta);
        tvUserDashboard = findViewById(R.id.tv_user_dashboard_junta);
        rvListaMiembros = findViewById(R.id.rvListaMiembros);
        btnVolver = findViewById(R.id.imageButton);
        btnPagar = findViewById(R.id.btnPagar);
        btnEditar = findViewById(R.id.btnEditar);
        iv_imagen_grupo = findViewById(R.id.iv_imagen_grupo);
        imageViewUser = findViewById(R.id.imageViewUser);
        requestQueue = Volley.newRequestQueue(this);

        // 2. Recuperar datos del Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            grupoId = extras.getInt("GRUPO_ID", 0);
            grupoNombre = extras.getString("NOMBRE_GRUPO", "Grupo");
            userName = extras.getString("NOMBRE_USUARIO", "Error de Traspaso");
            grupoDescri = extras.getString("DESCRI_GRUPO","Descripcion");
            grupoUrlImagen = extras.getString("URL_IMAGEN_GRUPO", "");
            urlUser = extras.getString("URL_IMAGEN_USUARIO", "");
            userId = extras.getInt("USER_ID",0);
            creadorId = extras.getInt("CREADOR_ID", 0);

        }
        if (getIntent().getExtras() != null) {


            // Llamar a la función para cargar la imagen en el ImageView del perfil
            cargarImagenUsuario(urlUser);
        }

        // 3. Inicializar RecyclerView y Listas
        if (tvUserDashboard != null && userName != null) {
            tvUserDashboard.setText(userName);
        }
        tvNombreGrupo.setText(grupoNombre);
        cargarImagenGrupo(grupoUrlImagen);

        listaMiembros = new ArrayList<>();
        rvListaMiembros.setLayoutManager(new LinearLayoutManager(this));

        // 4. Cargar los miembros si tenemos el ID
        if (grupoId != 0) {
            cargarMiembrosDelGrupo();
        } else {
            Toast.makeText(this, "Error: No se encontró el ID del grupo.", Toast.LENGTH_LONG).show();
        }

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creamos el Intent para abrir la primera ventana emergente
                mostrarDialogoRealizarPago();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoEditarGrupo(); // Llama al método que crea la ventana emergente
            }
        });

    }

    // En JuntaActivity.java

    // En JuntaActivity.java

    private void mostrarDialogoPagoFinalizado() {
        // 1. Crear el objeto Dialog
        final Dialog dialog = new Dialog(this);

        // Evita que se pueda cerrar al tocar fuera del diálogo
        dialog.setCancelable(true);

        // 2. Establecer el diseño XML personalizado
        dialog.setContentView(R.layout.activity_sub_realizar_pago2);

        // 3. Configurar dimensiones y fondo
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // 4. Vincular componentes del diálogo
        Button btnSubirEvidencias = dialog.findViewById(R.id.btnSubirEvidencias);
        ImageButton btnCerrar = dialog.findViewById(R.id.cerrar);

        // 5. Lógica de Clics

        // Clic en Subir Evidencias: Finaliza la secuencia de pago
        btnSubirEvidencias.setOnClickListener(v -> {
            actualizarEstadoPago(grupoId, userId);
            // Aquí iría la lógica real para subir la evidencia (en el futuro)
            Toast.makeText(this, "Evidencias enviadas. ¡Pago Registrado!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // Clic en Cerrar
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        // 6. Mostrar el diálogo
        dialog.show();
    }

    /**
     * Muestra el segundo diálogo, confirmando que el pago ha finalizado.
     */
    //
    // En JuntaActivity.java

    /**
     * Muestra una alerta para confirmar la eliminación del grupo.
     */
    private void mostrarDialogoConfirmacionEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar permanentemente este grupo? Esta acción no se puede deshacer.")
                // Botón Positivo (Sí)
                .setPositiveButton("Sí, Eliminar", (dialog, which) -> {
                    // Llama a la función de Volley para el borrado
                    eliminarGrupo(grupoId); // Asumiendo que 'grupoId' es una variable global
                })
                // Botón Negativo (No)
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert) // Icono de advertencia

                .show();
    }

    private void mostrarDialogoRealizarPago() {
        // 1. Crear el objeto Dialog
        final Dialog dialog = new Dialog(this);

        // Evita que se pueda cerrar al tocar fuera del diálogo
        dialog.setCancelable(true); // Permite cerrar con la tecla Atrás

        // 2. Establecer el diseño XML personalizado
        dialog.setContentView(R.layout.activity_sub_realizar_pago);

        // 3. Configurar dimensiones y fondo (para que parezca flotante y transparente)
        if (dialog.getWindow() != null) {
            // Hace el fondo transparente (necesario si el layout XML tiene esquinas redondeadas)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Define que el ancho sea WRAP_CONTENT para usar el 320dp que definiste en el XML
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // 4. Vincular componentes del diálogo
        Button btnFinalizarPago = dialog.findViewById(R.id.btnFinalizarPago);
        ImageButton btnCerrar = dialog.findViewById(R.id.cerrar);

        // 5. Lógica de Clics

        // Clic en Finalizar Pago: Cierra la ventana actual y abre la siguiente
        btnFinalizarPago.setOnClickListener(v -> {
            // Cierra la ventana actual
            dialog.dismiss();

            // Abre el segundo diálogo (Pago Finalizado)
            mostrarDialogoPagoFinalizado();
        });

        // Clic en Cerrar
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        // 6. Mostrar el diálogo
        dialog.show();
    }

    // En JuntaActivity.java

    private void mostrarDialogoEditarGrupo() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);

        // Establecer el nuevo diseño XML
        dialog.setContentView(R.layout.activity_editar_grupo);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // 1. Vincular componentes del diálogo
        ImageButton btnCerrar = dialog.findViewById(R.id.btn_cerrar_dialogo_editar);
        EditText etNombre = dialog.findViewById(R.id.et_nombre_grupo_editar);
        EditText etURLImagen = dialog.findViewById(R.id.et_url_imagen_editar); // Nuevo campo
        EditText etDescripcion = dialog.findViewById(R.id.et_descripcion_grupo_editar);
        Button btnGuardarCambios = dialog.findViewById(R.id.btn_editar);
        Button btnEliminar = dialog.findViewById(R.id.btn_eliminar_grupo); // Botón de eliminar (inactivo por ahora)

        // 2. Precargar datos (Necesitas tener las variables grupoNombre, grupoDescripcion, y grupoURL)
        // Usamos las variables que pasaste al iniciar JuntaActivity (si las guardaste globalmente)
        etNombre.setText(grupoNombre);
        etURLImagen.setText(grupoUrlImagen); // Necesitas cargar el URL en tu JuntaActivity
        // etDescripcion.setText(grupoDescripcion); // Necesitas cargar la descripción en tu JuntaActivity
        etDescripcion.setText(grupoDescri); // Placeholder si no tienes la variable

        // 3. Lógica del Botón Cerrar (X)
        btnCerrar.setOnClickListener(v -> dialog.dismiss());


        btnGuardarCambios.setOnClickListener(v -> {
            if (creadorId == userId)
            {
                String nuevoNombre = etNombre.getText().toString().trim();
                String nuevaDescripcion = etDescripcion.getText().toString().trim();
                String nuevaURL = etURLImagen.getText().toString().trim();

                if (!nuevoNombre.isEmpty()) {
                // Llamamos al método de Volley
                actualizarGrupo(grupoId, nuevoNombre, nuevaDescripcion, nuevaURL);
                dialog.dismiss(); // Cerrar el diálogo después de enviar
                } else {
                Toast.makeText(this, "Los campos no pueden estar vacios.", Toast.LENGTH_SHORT).show();
                }
            }else {
                    Toast.makeText(this, "Usted no puede editar el grupo", Toast.LENGTH_SHORT).show();
            }
        });





        // 5. Lógica del Botón Eliminar (INACTIVO)
        btnEliminar.setOnClickListener(v -> {
            if (creadorId == userId){
                dialog.dismiss();
                mostrarDialogoConfirmacionEliminar();
            }else{
                Toast.makeText(this, "Usted no puede eliminar el grupo", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    // Dentro de JuntaActivity.java

    /**
     * Muestra una alerta para confirmar la eliminación del grupo.
     */
    private void eliminarGrupo(int grupoId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ELIMINAR_GRUPO,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String estado = obj.getString("estado");

                        Toast.makeText(Junta.this, obj.getString("mensaje"), Toast.LENGTH_LONG).show();

                        if (estado.equals("ok")) {
                            // Si la eliminación fue exitosa, cerramos esta Activity.
                            // Esto hará que MainActivity se ejecute y recargue la lista de grupos actualizada.
                            finish();
                        }

                    } catch (JSONException e) {
                        // El error que viste: "Error al parsear JSON"
                        Toast.makeText(Junta.this, "Error: Problema al procesar la respuesta del servidor.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(Junta.this, "Error de red: No se pudo conectar con el servidor.", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Solo necesitamos el ID del grupo para borrarlo
                params.put("grupo_id", String.valueOf(grupoId));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    // En JuntaActivity.java

    /**
     * Envía los nuevos datos del grupo al servidor para su actualización.
     */
    private void actualizarGrupo(int grupoId, String nombre, String descripcion, String urlImagen) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ACTUALIZAR_GRUPO,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String estado = obj.getString("estado");

                        Toast.makeText(Junta.this, obj.getString("mensaje"), Toast.LENGTH_LONG).show();

                        if (estado.equals("ok")) {
                            // Si la actualización fue exitosa:
                            // 1. Cerrar el diálogo.
                            // 2. Actualizar las vistas de JuntaActivity (nombre y posible imagen).

                            // Actualizar las variables globales de JuntaActivity
                            grupoNombre = nombre;
                            // Necesitas una variable global para la descripción si la muestras.
                            // Si no usas Glide/Picasso, aquí tendrías que actualizar la imagen con la nueva URL

                            // Actualizar TextView del nombre del grupo
                            tvNombreGrupo.setText(grupoNombre);

                            // Cargar la nueva imagen si se proporcionó una URL
                            cargarImagenGrupo(urlImagen);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Junta.this, "Error al procesar la respuesta del servidor.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(Junta.this, "Error de red al actualizar el grupo.", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("grupo_id", String.valueOf(grupoId));
                params.put("nombre_grupo", nombre);
                params.put("descripcion", descripcion);
                params.put("url_imagen", urlImagen); // Nuevo parámetro
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    /**
     * Llama al script PHP para obtener la lista de integrantes del grupo.
     */
    private void cargarImagenUsuario(String urlImagen) {
        // URL por defecto si es nula o vacía
        if (urlImagen == null || urlImagen.isEmpty()) {
            // Carga la imagen de referencia local para el perfil del usuario
            imageViewUser.setImageResource(R.drawable.images); // 🔑 Usa tu Drawable por defecto
            return;
        }

        // 1. Crear la solicitud de imagen
        ImageRequest imageRequest = new ImageRequest(
                urlImagen,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // 2. Éxito: Asigna el Bitmap al ImageView
                        imageViewUser.setImageBitmap(bitmap);
                    }
                },
                0, // Ancho máximo
                0, // Alto máximo
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 3. Error: Si falla la descarga, usa la imagen por defecto
                        imageViewUser.setImageResource(R.drawable.images);
                        Toast.makeText(Junta.this, "Error al cargar la foto de perfil.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        // 4. Agregar la solicitud a la cola de Volley
        requestQueue.add(imageRequest);
    }
    private void cargarImagenGrupo(String urlImagen) {
        if (urlImagen == null || urlImagen.isEmpty()) {
            // Si no hay URL, carga la imagen de referencia local
            iv_imagen_grupo.setImageResource(R.drawable.refjunta);
            return;
        }

        // 1. Crear la solicitud de imagen
        ImageRequest imageRequest = new ImageRequest(
                urlImagen,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // 2. Éxito: Cuando la imagen se descarga, se asigna al ImageView
                        iv_imagen_grupo.setImageBitmap(bitmap);
                    }
                },
                0, // Ancho máximo (0 = sin límite)
                0, // Alto máximo (0 = sin límite)
                ImageView.ScaleType.CENTER_CROP, // Escala de la imagen
                Bitmap.Config.RGB_565, // Configuración del Bitmap
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 3. Error: Si falla la descarga, usa la imagen por defecto
                        iv_imagen_grupo.setImageResource(R.drawable.refjunta);
                        // Opcional: Mostrar un Toast de error de carga
                        Toast.makeText(Junta.this, "Error al cargar la imagen del grupo.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        // 4. Agregar la solicitud a la cola de Volley
        // Asegúrate de usar la RequestQueue que ya estás usando para las otras solicitudes
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(imageRequest);
    }
    // En Junta.java (Nueva función)
    private void actualizarEstadoPago(int gId, int uId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ACTUALIZAR_PAGO,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(Junta.this, obj.getString("mensaje"), Toast.LENGTH_LONG).show();

                        if (obj.getString("estado").equals("ok")) {
                            // Si el pago es exitoso, RECARGAMOS la lista para que el Check/X se actualice
                            cargarMiembrosDelGrupo();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Junta.this, "Error al procesar la respuesta del pago.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(Junta.this, "Error de red al actualizar pago.", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("grupo_id", String.valueOf(gId));
                params.put("user_id", String.valueOf(uId)); // El usuario actual es quien paga
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void cargarMiembrosDelGrupo() {
        // Limpiar la lista antes de cargar
        listaMiembros.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MIEMBROS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String estado = obj.getString("estado");

                            if (estado.equals("ok")) {
                                JSONArray miembrosArray = obj.getJSONArray("miembros");

                                for (int i = 0; i < miembrosArray.length(); i++) {
                                    JSONObject miembroJson = miembrosArray.getJSONObject(i);
                                    Miembro miembro = new Miembro(
                                            miembroJson.getInt("id"),
                                            miembroJson.getString("nombre"),
                                            // 🔑 NUEVOS CAMPOS:
                                            miembroJson.getString("url_imagen"),
                                            miembroJson.getString("estado_pago")
                                    );
                                    listaMiembros.add(miembro);
                                }

                                // Inicializar/Notificar al Adaptador
                                if (miembroAdapter == null) {
                                    // 🔑 CORRECCIÓN: Usar Junta.this para pasar el Context
                                    miembroAdapter = new MiembroAdapter(listaMiembros, Junta.this);
                                    rvListaMiembros.setAdapter(miembroAdapter);
                                } else {
                                    miembroAdapter.notifyDataSetChanged();
                                }

                            } else {
                                Toast.makeText(Junta.this, "Grupo sin miembros: " + obj.getString("mensaje"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Junta.this, "Error al parsear JSON de miembros.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Junta.this, "Error de red al obtener miembros.", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }) {

            // Método para ENVIAR el GRUPO_ID por POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("grupo_id", String.valueOf(grupoId));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}