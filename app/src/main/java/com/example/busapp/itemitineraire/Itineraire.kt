// Corriger la déclaration de la classe (supprimer la duplication)
package com.example.busapp.itemitineraire

data class Itineraire(
    val source: String = "",
    val destination: String = "",
    val horaireDepart: String = "",
    val horaireArrivee: String = "",
    val arrets: String = "",
    val numbus: String = ""
)