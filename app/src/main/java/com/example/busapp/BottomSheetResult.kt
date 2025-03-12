import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busapp.R
import com.example.busapp.itemitineraire.Itineraire
import com.example.busapp.itemitineraire.ItineraireAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore

class BottomSheetResult(private val source: String, private val destination: String) : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItineraireAdapter
    private val itineraireList = mutableListOf<Itineraire>()
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_result, container, false)

        // Initialiser les vues
        val textViewSource = view.findViewById<TextView>(R.id.textViewSource)
        val textViewDestination = view.findViewById<TextView>(R.id.textViewDestination)
        recyclerView = view.findViewById(R.id.recyclerItineraires) // ID correspondant au XML
        
        // Configurer RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ItineraireAdapter(itineraireList)
        recyclerView.adapter = adapter

        // Charger les itinéraires depuis Firestore
        loadItineraires(source, destination)

        return view
    }

    private fun loadItineraires(source: String, destination: String) {
        db.collection("itinéraire")
            .whereEqualTo("dep", source)
            .whereEqualTo("des", destination)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "Aucune route trouvée", Toast.LENGTH_SHORT).show()
                } else {
                    itineraireList.clear()
                    for (document in documents) {
                        val itineraire = Itineraire(
                            source = document.getString("dep") ?: "",
                            destination = document.getString("des") ?: "",
                            horaireDepart = document.getString("heuredep") ?: "",
                            horaireArrivee = document.getString("heuredes") ?: "",
                            arrets = document.getString("pointarret") ?: "",
                            numbus = document.getString("Numbus") ?: ""
                        )
                        itineraireList.add(itineraire)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erreur de chargement: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}