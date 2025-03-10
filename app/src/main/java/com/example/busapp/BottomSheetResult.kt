import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.busapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetResult(private val source: String, private val destination: String) : BottomSheetDialogFragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_result, container, false)

        // Find views
        val textViewSource = view.findViewById<TextView>(R.id.textViewSource)
        val textViewDestination = view.findViewById<TextView>(R.id.textViewDestination)

        // Set source and destination
        textViewSource.text = source
        textViewDestination.text = destination

        return view
    }
}