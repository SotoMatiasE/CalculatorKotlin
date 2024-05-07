package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    //ESTE METODO PUBLICO ENVIA PROPIEDAD ONCLICK A LOS XML
    fun onClickButton(view: View){
        val valueStr = (view as Button).text.toString()
    //ESTO VA A EVALIAR LA VISTA QUE CORRESPONDE AL BOTON PULSADO
        when(view.id){
            R.id.btnDelete -> { //RECORDAR QUE LA FLECHA ES PARA ABRIR UN NUEVO BLOQUE DE CODIGO
                val length = binding.tvOperation.text.length //AVERIGUA LA LONGITUD ACTUAL DE LA OPERACION

                if (length > 0) { //si la long es mayor
                    val newOperation = binding.tvOperation.text.toString().substring(0, length - 1)
                    binding.tvOperation.text =
                        newOperation //asignamos newOperation y elimina el ultimo caracter
                }
            }
            R.id.btnClear -> { //RECORDAR QUE LA FLECHA ES PARA ABRIR UN NUEVO BLOQUE DE CODIGO
                binding.tvOperation.text = ""
                binding.tvResult.text = ""
            }
            R.id.btnResult -> { //RECORDAR QUE LA FLECHA ES PARA ABRIR UN NUEVO BLOQUE DE CODIGO
                tryResolve(binding.tvOperation.text.toString(), true)
            }
            //EL CONJUNTO DE BOTONES VAN A DETONAR UN MISMO CODIGO
            R.id.btnMulti,
            R.id.btnDiv,
            R.id.btnSum,
            R.id.btnSub -> {
                //el argumento es la operacion previa resuelta
                tryResolve(binding.tvOperation.text.toString(), false)
                        binding.tvOperation.append(valueStr)
            }
            //else ESTA PARA QUE IMPRIMA LOS TXT OSEA LOS NUMEROS
            else -> {
                binding.tvOperation.append(valueStr) //append ES UN METODO QUE AÑADE TEXTO AL FINAL EL NUEVO VALOR
            }
        }
    }

    private fun tryResolve(operationRef: String, isFromResult: Boolean) {
        if (operationRef.isEmpty()){ //si esta vacio retorna para no ejecutar de nuevo
            return
        }

        var operation = operationRef //HACEMOS ESTO POR SI HAY UN PUNTO AL FINAL QUITARLO
        //quita el punto que estes ubicado al final de un valor
        if (operation.contains(POINT) && operation.lastIndexOf(POINT) == operation.length -1) {
            operation = operation.substring(0, operation.length -1)
        }

        //extrare el operador +,-,*,/
        val operator = getOperator(operationRef)

        var value = arrayOfNulls<String>(0)
        //si el operador es diferente de null vamos a descartar que sea una operacion invalida
        //es decir que no contenga un operador
        if (operator != OPERATOR_NULL){
            //verificamos si es un operador de resta
            if (operator == OPERATOR_SUB){
                //extraer el incice del operador -
                val  index = operation.lastIndexOf(OPERATOR_SUB)
                if (index < operation.length-1) { //si index es menor puede dividirse en 2 si no esta incompleta
                    value = arrayOfNulls(2)
                    value[0] = operation.substring(0, index) //index es la posicion de -
                    value[1] = operation.substring(index+1)
                }else {
                    value = arrayOfNulls(1)
                    value[0] = operationRef.substring(0, index)
                }
            }else {
                value = operation.split(operator).toTypedArray()
            }
        }

        if (value.size > 1) {
            try {
                //PROCESAR NUMEROS
                val numberOne = value[0]!!.toDouble()
                val numberTwo = value[1]!!.toDouble()

                //ASIGANA EL RESULTADO
                binding.tvResult.text = getResult(numberOne, operator, numberTwo).toString()

                //COMPROBAMOS SI SE PUEDE COPIAR
                                  //si no esta vacio   y   si no viene de result
                if (binding.tvResult.text.isNotEmpty() && !isFromResult){
                    binding.tvOperation.text = binding.tvResult.text
                }
            }catch (e: NumberFormatException){
                if (isFromResult) showMessage()
            }
        }else{
            if (isFromResult && operator != OPERATOR_NULL) showMessage() //SI NO TENGO OPERADORES EN PANTALLA NO NOTIFICA
        }
        /*//Snackbar es similar a Toast
        Snackbar.make(binding.root, "1:$numberOne 2:$numberTwo", Snackbar.LENGTH_SHORT).show()*/
    }

    private fun getOperator(operation: String): String {
        var operator = ""

        if (operation.contains(OPERATOR_MULTI)){
            operator = OPERATOR_MULTI
        }else if (operation.contains(OPERATOR_DIV)){
           operator = OPERATOR_DIV
        }else if (operation.contains(OPERATOR_SUM)){
           operator = OPERATOR_SUM
        }else { //valor por defecto
            operator = OPERATOR_NULL
        }
        /*CON if ESTAMOS VALIDADNDO EL SIGNO NEGATIVO PARA CALCULOS NEGATIVOS. UNICAMENTE SE TOMA EL SEGUNDO - VALIDADNDO
        EL PRIMERO COMO NULL*/
        if (operator == OPERATOR_NULL && operation.lastIndexOf(OPERATOR_SUB) > 0){
            operator = OPERATOR_SUB
        }

        return operator
    }

    private fun getResult(numberOne: Double, operator: String, numberTow: Double): Double {
        var result = 0.0

        when (operator) {
            OPERATOR_MULTI -> result = numberOne * numberTow
            OPERATOR_DIV -> result = numberOne / numberTow
            OPERATOR_SUM -> result = numberOne + numberTow
            OPERATOR_SUB -> result = numberOne - numberTow
        }

        return result
    }

    private fun showMessage(){
        Snackbar.make(binding.root, getString(R.string.message_exp_incorrect), Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.llTop).show() //setAnchorView(binding.llTop) situa el mensaje arriba de los botones
    }

    companion object {
        const val OPERATOR_MULTI = "x"
        const val OPERATOR_DIV = "÷"
        const val OPERATOR_SUB= "-"
        const val OPERATOR_SUM = "+"
        const val OPERATOR_NULL = "null"
        const val POINT = "."
    }
}




















