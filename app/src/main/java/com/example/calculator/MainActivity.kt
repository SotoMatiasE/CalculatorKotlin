package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.calculator.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Locale


private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //podemos detectar los cambios en tiempo real y pregunta si es reemplazable el operador
        binding.tvOperation.run {
            addTextChangedListener { charSequence ->
                //reemplazar el operador siempre que sea posble
                if (Operaciones.canReplaceOperator(charSequence.toString())) {
                    //extraer antes del operador y concatenarlo con el ultimo char
                    val newStr = "${text.substring(0, text.length - 2)}${
                        text.substring(text.length - 1)
                    }"
                    text = newStr
                }
            }
        }
    }

    //ESTE METODO PUBLICO ENVIA PROPIEDAD ONCLICK A LOS XML
    fun onClickButton(view: View){
        val valueStr = (view as Button).text.toString()
        val operation = binding.tvOperation.text.toString()

    //ESTO VA A EVALUAR LA VISTA QUE CORRESPONDE AL BOTON PULSADO
        when(view.id){
            R.id.btnDelete -> { //RECORDAR QUE LA FLECHA ES PARA ABRIR UN NUEVO BLOQUE DE CODIGO
                binding.tvOperation.run {
                    if (text.isNotEmpty()) text = operation.substring(0, text.length - 1)
                //AVERIGUA LA LONGITUD ACTUAL DE LA OPERACION asignamos y elimina el ultimo caracter
                }
            }
            R.id.btnClear -> { //RECORDAR QUE LA FLECHA ES PARA ABRIR UN NUEVO BLOQUE DE CODIGO
                binding.tvOperation.text = ""
                binding.tvResult.text = ""
            }
            //RECORDAR QUE LA FLECHA ES PARA ABRIR UN NUEVO BLOQUE DE CODIGO
            R.id.btnResult ->  checkOrResolve(operation, true)

            //EL CONJUNTO DE BOTONES VAN A DETONAR UN MISMO CODIGO
            R.id.btnMulti,
            R.id.btnDiv,
            R.id.btnSum,
            R.id.btnSub -> {
                //el argumento es la operacion previa resuelta
                checkOrResolve(operation, false)

                //AGREGAR OPERADOR EN CASO DE SER VALIDO
                addOperator(valueStr, operation) //esta funcion recibe argumentos de val
            }
            //validar si se puede agregar punto en un metodo
            R.id.btnPoint -> addPoint(valueStr, operation)

            //else ESTA PARA QUE IMPRIMA LOS TXT OSEA LOS NUMEROS
            else -> binding.tvOperation.append(valueStr) //append ES UN METODO QUE AÑADE TEXTO AL FINAL EL NUEVO VALOR
        }
    }

    private fun addPoint(pointStr: String, operation: String) {
        if (!operation.contains(Constantes.POINT)){
            binding.tvOperation.append(pointStr)
        } else {
            val operator = Operaciones.getOperator(operation) //valida donde si debe agregar punto

            val value = Operaciones.divideOperation(operator, operation)

            if (value.isNotEmpty()){
                val numberOne = value[0]!!
                if (value.size > 1){
                    val numberTwo = value[1]!!
                    if (numberOne.contains(Constantes.POINT) && !numberTwo.contains(Constantes.POINT)){
                        binding.tvOperation.append(pointStr)
                    }
                }else {
                    if (numberOne.contains(Constantes.POINT)){
                        binding.tvOperation.append(pointStr)
                    }
                }
            }
        }
    }
    /*ESTE METODO AYUDA A VALIDAR LOS CASOS DONDE ES POSIBLE AÑADIR EL OPERADOR QUE INTENTAMOS AGREGAR
    A LA OPERACION ACTUAL*/
    private fun addOperator(operator: String, operation: String) {
        //EXTRAER ULTIMO ELEMENTO ANTES DEL OPERADOR
        val lastElement = if(operation.isEmpty())"" else operation.substring(operation.length -1)

        //VERIFICAR SI ES UNA RESTA O NO
        if (operator == Constantes.OPERATOR_SUB){
            if (operation.isEmpty() || lastElement != Constantes.OPERATOR_SUB && lastElement != Constantes.POINT){
                binding.tvOperation.append(operator)
            }
        } else{
                if (operation.isNotEmpty() && lastElement != Constantes.POINT){
                    binding.tvOperation.append(operator)
                }
        }
    }
    private fun checkOrResolve(operation: String, isFromResult: Boolean){
        Operaciones.tryResolve(operation, isFromResult, object : OnResolveListener{
            override fun onShowResult(result: Double) {
                //ASIGANA EL RESULTADO
                binding.tvResult.text = String.format(Locale.ROOT, "%.2f", result) //"%.2f" deja 2 decimales

                //COMPROBAMOS SI SE PUEDE COPIAR
                //si no esta vacio   y   si no viene de result
                if (binding.tvResult.text.isNotEmpty() && !isFromResult){
                    binding.tvOperation.text = binding.tvResult.text
                }
            }
            override fun onShowMessage(errorRes: Int) {
                showMessage(errorRes)
            }
        })
    }
    private fun showMessage(errorRes: Int){
        Snackbar.make(binding.root, errorRes, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.llTop).show() //setAnchorView(binding.llTop) situa el mensaje arriba de los botones
    }
    
}




















