package com.example.precocerto

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class CarouselAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    // ViewHolder para gerenciar cada item
    class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageCarousel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carousel_item, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val image = images[position]

        // Definir o raio das bordas
        val radius = 20  // Ajuste o valor do raio conforme necessário

        // Usar Glide para carregar a imagem com bordas arredondadas
        Glide.with(holder.imageView.context)
            .load(image)  // Carrega a imagem
            .apply(RequestOptions()
                .centerCrop()  // Ajusta a imagem proporcionalmente, cortando se necessário
                .transform(RoundedCorners(radius))  // Aplica bordas arredondadas
            )
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = images.size
}
