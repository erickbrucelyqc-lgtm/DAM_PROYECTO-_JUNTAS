package com.example.dam_proyecto; // Asegúrate de que este sea tu paquete correcto

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder> {

    // 1. Declaración de la Interfaz de Clic
    public interface GrupoSeleccionadoListener {
        void onGrupoClick(Grupo grupo);
    }

    private final Context context;
    private final List<Grupo> listaGrupos;
    // 2. Declaración del Listener con el nuevo nombre
    private final GrupoSeleccionadoListener listener;


    // 3. Constructor que recibe el Contexto, la lista de datos y el Listener
    public GrupoAdapter(Context context, List<Grupo> listaGrupos, GrupoSeleccionadoListener listener) {
        this.context = context;
        this.listaGrupos = listaGrupos;
        this.listener = listener;
    }

    // 1. Crea las vistas
    @NonNull
    @Override
    public GrupoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grupo, parent, false);
        return new GrupoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoViewHolder holder, int position) {
        Grupo grupoActual = listaGrupos.get(position);

        holder.tvNombreGrupo.setText(grupoActual.getNombre());
        holder.tvDescripcionGrupo.setText(grupoActual.getDescripcion());

        String urlImagen = grupoActual.getUrlImagen();

        // 1. Limpiar la vista y poner el placeholder/imagen por defecto inmediatamente
        // Debes tener una imagen llamada 'ref_default' (o similar) en res/drawable.
        holder.tvImagenGrupo.setImageResource(R.drawable.images);

        // 2. Verificar si hay URL válida para descargar
        if (urlImagen != null && !urlImagen.isEmpty()) {

            // Creamos la cola de peticiones usando el contexto del ImageView
            RequestQueue requestQueue = Volley.newRequestQueue(holder.tvImagenGrupo.getContext());

            // 3. Crear la petición de imagen de Volley
            ImageRequest imageRequest = new ImageRequest(
                    urlImagen, // ⬅️ La URL de la imagen de tu servidor
                    bitmap -> {
                        // Éxito: Cuando la imagen se descarga, se asigna al ImageView.
                        holder.tvImagenGrupo.setImageBitmap(bitmap);
                    },
                    0, // Ancho máximo (0 = sin límite)
                    0, // Alto máximo (0 = sin límite)
                    ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.RGB_565, // Formato del Bitmap
                    error -> {
                        // Error: Si falla la descarga, deja la imagen por defecto
                        holder.tvImagenGrupo.setImageResource(R.drawable.images);
                        // Aquí puedes añadir un Toast o Log para depuración si es necesario
                    });

            // 4. Agregar la solicitud a la cola de Volley para su ejecución
            requestQueue.add(imageRequest);
        }

        // 5. Asignación del clic al contenedor
        holder.cardContainer.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGrupoClick(grupoActual);
            }
        });
    }

    // 3. Devuelve la cantidad total de ítems
    @Override
    public int getItemCount() {
        return listaGrupos.size();
    }

    // Clase interna que mantiene las referencias a los elementos de la vista
    public static class GrupoViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreGrupo;
        TextView tvDescripcionGrupo;
        ImageView tvImagenGrupo;
        CardView cardContainer;

        public GrupoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vinculación de los componentes del item_grupo.xml
            tvNombreGrupo = itemView.findViewById(R.id.tv_nombre_grupo);
            tvDescripcionGrupo = itemView.findViewById(R.id.tv_descripcion_grupo);
            tvImagenGrupo= itemView.findViewById(R.id.img_grupo);
            cardContainer = itemView.findViewById(R.id.main_container);
        }
    }
}