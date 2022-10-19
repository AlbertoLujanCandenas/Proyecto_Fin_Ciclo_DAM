package net.devalbert.alcloud

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    private lateinit var authLayout: LinearLayout
    private lateinit var btnLogin: Button
    private lateinit var btnRegistrar: Button
    private lateinit var googleBtn: Button
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnLogin = findViewById(R.id.btnAcceder)
        authLayout = findViewById(R.id.authLayout)
        googleBtn = findViewById(R.id.googleButtom)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        session()
    }

    private fun session() {
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email: String? = prefs.getString("email", null)
        val username: String? = prefs.getString("username", null)
        val provider: String? = prefs.getString("provider", null)

        //En caso de que exista una sesion ya iniciada entrara a la home automaticamente escondiendo el formulario de registro
        if(email != null && provider != null && username != null){
            authLayout.visibility = View.INVISIBLE
            showMain(email, username, ProviderType.valueOf(provider))
        }
        else{
            setup()
        }
    }

    private fun setup() {
        title = "Autenticación"
        //LOGICA DEL BOTON REGISTAR
        btnRegistrar.setOnClickListener {
            if (etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    etEmail.text.toString(),
                    //addOnCompleteListener nos devuelve si la operacion ha sido exitosa o no
                    etPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showMain(it.result?.user?.email ?: "", it.result?.user?.displayName?: "", ProviderType.BASIC)
                        //en caso de no existir el email devolvera un string vacio
                    } else {
                        showAlert()
                    }
                }
            }
        }
        //LOGICA DEL BOTON ACCEDER
        btnLogin.setOnClickListener{
            if (etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    etEmail.text.toString(), etPassword.text.toString()).addOnCompleteListener{
                    //addOnCompleteListener nos devuelve si la operacion ha sido exitosa o no
                    if(it.isSuccessful){
                        showMain(it.result?.user?.email?: "", it.result?.user?.displayName?:"", ProviderType.BASIC)
                        //en caso de no existir el email devolvera un string vacio
                    }else{
                        showAlert()
                    }
                }
            }
        }
        //LGOICA DEL BOTON GOOGLE
        googleBtn.setOnClickListener{
            //Configuracion
            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //No es un error, el string esta dentro de res(generated) que se hace automaticente por Google
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient:GoogleSignInClient = GoogleSignIn.getClient(this, gso)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    //LOGICA DE LA ACCION AL ACCEDER CON GOOGLE
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                if (account != null){

                    val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)

                        .addOnCompleteListener{
                            if(it.isSuccessful){
                                showMain(account.email?: "", account.displayName?:"", ProviderType.GOOGLE)
                                //en caso de no existir el email devolvera un string vacio
                            }else{
                                showAlert()
                            }
                        }
                }
            }
            catch (e: ApiException){
                showAlert()
            }
        }
    }


    private fun showMain(email: String, username: String, provider: ProviderType) {
        //el .apply es lo mismo que si llamamos a la variable intent para despues hacerle intent.putExtra...
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
            putExtra("username", username)
        }
        startActivity(intent)
        finish()
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error a la hora de autenticar el usuario" +
                " si el problema persiste y crees que los datos son correctos," +
                " contacta con el adminsitrador para que te ofrezca una nueva contraseña")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}