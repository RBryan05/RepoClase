package com.example.chats_kotlin.Fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.chats_kotlin.Constantes
import com.example.chats_kotlin.OpcionesLoginActivity
import com.example.chats_kotlin.R
import com.example.chats_kotlin.databinding.FragmentPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentPerfil : Fragment() {

    private lateinit var binding: FragmentPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPerfilBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()

        // Configurar cliente de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.example.chats_kotlin.R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        // Botón cerrar sesión
        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesionCompleta()
        }

        cargarInformacion()
        return binding.root
    }

    private fun cerrarSesionCompleta() {
        // Cierra sesión en Firebase
        firebaseAuth.signOut()

        // Cierra sesión en Google (si aplica)
        googleSignInClient.signOut().addOnCompleteListener {
            // Redirige al login
            startActivity(Intent(requireContext(), OpcionesLoginActivity::class.java))
            activity?.finishAffinity()
        }
    }

    private fun cargarInformacion() {
        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid ?: return // No hay usuario logueado

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return

                if (snapshot.exists()) {
                    val nombres = snapshot.child("nombres").getValue(String::class.java) ?: "Sin nombre"
                    val email = snapshot.child("email").getValue(String::class.java) ?: "Sin email"
                    val proveedor = snapshot.child("proveedor").getValue(String::class.java) ?: "Desconocido"
                    val t_registro = snapshot.child("tiempoR").getValue(String::class.java) ?: "Desconocido"
                    val fecha = obtenerFechaLegible(t_registro)

                    // Actualizar UI
                    binding.tvNombres.text = nombres
                    binding.tvEmail.text = email
                    binding.tvProveedor.text = proveedor
                    binding.tvTRegistro.text = fecha

                    // Cargar foto de perfil de Google
                    val photoUrl = currentUser.photoUrl
                    if (photoUrl != null) {
                        Glide.with(binding.root)
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.ivPerfil)
                    } else {
                        binding.ivPerfil.setImageResource(R.drawable.ic_img_perfil)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun obtenerFechaLegible(timestamp: String): String {
        return try {
            val tiempo = timestamp.toLong()
            val fecha = java.util.Date(tiempo)
            val formato = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            formato.format(fecha)
        } catch (e: Exception) {
            "Desconocido"
        }
    }
}
