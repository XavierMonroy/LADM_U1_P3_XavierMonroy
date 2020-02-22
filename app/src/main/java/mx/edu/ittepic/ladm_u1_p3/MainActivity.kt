package mx.edu.ittepic.ladm_u1_p3

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {
    var vector : Array<Int> = Array(10,{0})
    var posicion = 0
    var contenido = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Boton asignar
        asignar.setOnClickListener {
            if (txtValor.text.isEmpty()){
                mensaje("Escribe un valor")
                return@setOnClickListener
            } else if (txtPosicion.text.isEmpty()){
                mensaje("Escribe la posición en el vector")
                return@setOnClickListener
            }
            else{
                asignarVec()
            }
        }

        mostrar.setOnClickListener {
            mostrarVec()
        }

        // Boton guardar
        guardar.setOnClickListener {
            if (txtGuardar.text.isEmpty()) {
                mensaje("Escribe nombre de archivo")
                return@setOnClickListener
            }
            guardarArchivoSD()
        }

        //Boton leer
        leer.setOnClickListener {
            if (txtLeer.text.isEmpty()){
                mensaje("Escribe nombre de Archivo")
                return@setOnClickListener
            }
            leerArchivoSD()
        }
    }

    // Verificar que se tenga una memoria SD
    fun noSD() : Boolean{
        var estado = Environment.getExternalStorageState()
        if(estado != Environment.MEDIA_MOUNTED){
            return true
        }
        return false
    }

    // Funcion para mostrar un mensaje
    fun mensaje(m:String){
        AlertDialog.Builder(this).setTitle("ATENCIÓN").setMessage((m)).setPositiveButton("OK"){ d, i->}.show()
    }

    private fun limpiarCampos() {
        txtValor.setText("")
        txtPosicion.setText("")
        txtGuardar.setText("")
        txtLeer.setText("")
    }

    //Funcion para insertar valores en el vector
    private fun asignarVec(){
        var data = txtValor.text.toString().toInt()
        var posicion = txtPosicion.text.toString().toInt()
        vector[posicion]= data
        mensaje("INSERTADO CORRECTAMENTE")
        limpiarCampos()
    }

    // Funcion para mostrar vector
    fun mostrarVec() {
        contenido=""

        (0..9).forEach {
            contenido = contenido + vector[it]
            if(it<9){
                contenido = contenido + " , "}
        }
        mensaje(contenido)
    }//mostrarContenido


    //Funcion para leer archivo
    fun leerArchivoSD(){
        var nombreArchivo = txtLeer.text.toString()
        if(noSD()){
            mensaje("NO EXISTE MEMORIA EXTERNA")
            return
        }

        try {
            var rutaSD = Environment.getExternalStorageDirectory()
            var datosArchivo = File(rutaSD.absolutePath,nombreArchivo)

            var flujoEntrada = BufferedReader(InputStreamReader(FileInputStream(datosArchivo)))
            var data = flujoEntrada.readLine()

            mensaje(data)

            var datos = data.split(",")

            (0..9).forEach {
                vector[it] = datos[it].toInt()
            }

            flujoEntrada.close()

        }catch (error : IOException){
            mensaje(error.message.toString())
        }
    }

    //Funcion para guardar archivos
    fun guardarArchivoSD(){
        if(noSD()){
            mensaje("NO EXISTE MEMORIA EXTERNA SD")
            return
        }

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED) {
            // SI ENTRA EN ESTE IF, ES PORQUE NO TIENE LOS PERMISOS
            // EL SIGUIENTE CODIGO SOLICITA LOS PERMISOS
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 0)
        }else {

            try {
                var rutaSD = Environment.getExternalStorageDirectory()
                var data = txtGuardar.text.toString()
                var datosArchivo = File(rutaSD.absolutePath, data)
                var flujoSalida = OutputStreamWriter(FileOutputStream(datosArchivo))

                flujoSalida.write(contenido)
                flujoSalida.flush()
                flujoSalida.close()

                mensaje("¡ÉXITO! Se guardó correctamente en MEMORIA EXTERNA SD")
                limpiarCampos()

            } catch (error: IOException) {
                mensaje(error.message.toString())
            }
        }
    }
}
