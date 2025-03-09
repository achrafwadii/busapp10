package com.example.busapp.itemitineraire

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.busapp.R

class ItineraireAdapter(private val itineraires: List<Itineraire>) :
    RecyclerView.Adapter<ItineraireAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textSource: TextView = view.findViewById(R.id.textViewSource)
        val textDestination: TextView = view.findViewById(R.id.textViewDestination)
        val textHoraireDepart: TextView = view.findViewById(R.id.texthoraire)
        val textHoraireArrivee: TextView = view.findViewById(R.id.textViewHoraires)
        val textArrets: TextView = view.findViewById(R.id.textViewArrets)
        val textNumbus: TextView = view.findViewById(R.id.textnumbus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_itineraire, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itineraire = itineraires[position]
        holder.textSource.text = itineraire.source
        holder.textDestination.text = itineraire.destination
        holder.textHoraireDepart.text = "Départ : ${itineraire.horaireDepart}"
        holder.textHoraireArrivee.text = "Arrivée : ${itineraire.horaireArrivee}"
        holder.textArrets.text = "Arrêts : ${itineraire.arrets}"
        holder.textNumbus.text = "NumBus : ${itineraire.numbus}"
    }

    override fun getItemCount() = itineraires.size
}
