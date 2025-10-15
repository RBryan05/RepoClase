package com.example.chats_kotlin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chats_kotlin.databinding.ActivityRegistroEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    //CREAR 4 VARIABLES PARA EL REGISTRO
    private var nombres = " "
    private var email = ""
    private var password = " "
    private var r_password = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistroEmailBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        // Guardamos el padding original definido en XML
        val mainLayout = binding.main
        val originalPadding = intArrayOf(
            mainLayout.paddingLeft,
            mainLayout.paddingTop,
            mainLayout.paddingRight,
            mainLayout.paddingBottom
        )

        // Ajustamos solo los insets del sistema sumándolos al padding original
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                originalPadding[0] + systemBars.left,
                originalPadding[1] + systemBars.top,
                originalPadding[2] + systemBars.right,
                originalPadding[3] + systemBars.bottom
            )
            insets
        }

        // CREAR INSTANCIA DE FIREBASE
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        // Click en "Registrarme"
        binding.btnRegistrar.setOnClickListener {
            // Validar datos
            validarInformacion()

        }
    }

    private fun validarInformacion() {
        nombres = binding.etNombres.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        r_password = binding.etRPassword.text.toString().trim()

        //VALIDAR CAMPOS
        if (nombres.isEmpty()) {
            binding.etNombres.error = "Ingrese sus nombres"
            binding.etNombres.requestFocus()
            return
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Ingrese un email valido"
            binding.etEmail.requestFocus()
            return
        }
        else if (password.isEmpty()) {
            binding.etPassword.error = "Ingrese una contraseña"
            binding.etPassword.requestFocus()
            return
        }
        else if (r_password.isEmpty()) {
            binding.etRPassword.error = "Repita la contraseña"
            binding.etRPassword.requestFocus()
            return
        }
        else if (password != r_password) {
            binding.etRPassword.error = "Las contraseñas no coinciden"
            binding.etRPassword.requestFocus()
            return
        }
        else {
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        progressDialog.setMessage("Creando cuenta...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                actualizarInformacion()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Fallo en el registro debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarInformacion() {
        progressDialog.setMessage("Guardando información...")

        val uidU = firebaseAuth.uid
        val nombreU = nombres
        val emailU = firebaseAuth.currentUser!!.email
        val tiempoR = Constantes.obtenerTiempoDelD()

        //ENVIAR INFORMACION A FIREBASE
        val datosUsuarios = HashMap<String, Any>()
        datosUsuarios["uid"] = "$uidU"
        datosUsuarios["nombres"] = "$nombreU"
        datosUsuarios["email"] = "$emailU"
        datosUsuarios["tiempoR"] = "$tiempoR"
        datosUsuarios["proveedor"] = "email"
        datosUsuarios["estado"] = "online"

        //GUARDAMOS LA INFORMACIÓN EN FIREBASE
        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidU!!)
            .setValue(datosUsuarios)
            .addOnCompleteListener {
                progressDialog.dismiss()
                startActivity(Intent(application, MainActivity::class.java))
            }
            .addOnFailureListener{ e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Fallo en el registro debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}