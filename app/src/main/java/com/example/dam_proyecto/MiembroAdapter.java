package com.example.dam_proyecto; // Asegúrate de que el paquete es correcto

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat; // Útil si quieres usar colores en el check/cross

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import java.util.List;

public class MiembroAdapter extends RecyclerView.Adapter<MiembroAdapter.MiembroViewHolder> {

    private List<Miembro> listaMiembros;
    private RequestQueue requestQueue;

    // 🔑 CONSTRUCTOR CORREGIDO: Recibe el Context (Junta.this)
    public MiembroAdapter(List<Miembro> listaMiembros, Context context) {
        this.listaMiembros = listaMiembros;
        // Se inicializa el RequestQueue con el context recibido
        this.requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public MiembroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_miembro, parent, false);
        return new MiembroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiembroViewHolder holder, int position) {
        Miembro miembro = listaMiembros.get(position);
        holder.tvNombre.setText(miembro.getNombre());

        // 1. Cargar Imagen de Perfil
        cargarImagen(miembro.getUrlImagen(), holder.ivPerfil);

        // 2. Mostrar Estado de Pago (Check/X)
        if ("SI".equalsIgnoreCase(miembro.getEstadoPago())) {
            // Asegúrate de tener un drawable llamado 'ic_check' y 'tu_color_verde' definido
            holder.ivEstadoPago.setImageResource(R.drawable.ic_check);
            // Opcional: holder.ivEstadoPago.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        } else {
            // Asegúrate de tener un drawable llamado 'ic_cross' (una 'X')
            holder.ivEstadoPago.setImageResource(R.drawable.ic_cross);
            // Opcional: holder.ivEstadoPago.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return listaMiembros.size();
    }

    // ViewHolder
    public static class MiembroViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ImageView ivPerfil;
        ImageView ivEstadoPago;

        public MiembroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_miembro);
            ivPerfil = itemView.findViewById(R.id.iv_perfil_miembro);
            ivEstadoPago = itemView.findViewById(R.id.iv_estado_pago);
        }
    }

    // Función auxiliar para cargar imagen de perfil usando Volley
    private void cargarImagen(String urlImagen, ImageView imageView) {
        if (urlImagen == null || urlImagen.isEmpty()) {
            imageView.setImageResource(R.drawable.images);
            return;
        }

        ImageRequest imageRequest = new ImageRequest(
                urlImagen,
                imageView::setImageBitmap,
                0, 0,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                error -> imageView.setImageResource(R.drawable.images) // Imagen por defecto en caso de error
        );
        requestQueue.add(imageRequest);
    }
}