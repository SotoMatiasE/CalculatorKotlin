package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.calculator.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //podemos detectar los cambios en tiempo real
        binding.tvOperation.addTextChangedListener { charSequence ->
            //reemplazar el operador siempre que sea posble
            if (canReplaceOperator(charSequence.toString())){
                val length = binding.tvOperation.text.length
                //extraer antes del operador y concatenarlo con el ultimo char
                val newOperation = binding.tvOperation.text.toString().substring(0, length - 2) +
                    binding.tvOperation.text.toString().substring(length -1)
                binding.tvOperation.text = newOperation

            }
        }

    }
    private fun canReplaceOperator(charSequence: CharSequence): Boolean {
        if (charSequence.length < 2) return false
        val lasElement = charSequence[charSequence.length - 1].toString()
        val penultimate = charSequence[charSequence.length - 2].toString()

        return (lasElement == OPERATOR_MULTI || lasElement ==
                OPERATOR_DIV || lasElement == OPERATOR_SUM) && (
                        penultimate == OPERATOR_MULTI || penultimate == OPERATOR_DIV ||
                        penultimate == OPERATOR_SUM || penultimate == OPERATOR_SUB
                        )
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

                //AGREGAR OPERADOR EN CASO DE SER VALIDO
                val operator = valueStr
                val operation = binding.tvOperation.text.toString()
                addOperator(operator, operation) //esta funcion recibe argumentos de val
            }
            R.id.btnPoint -> {
                val operation = binding.tvOperation.text.toString()
                //validar si se puede agregar punto en un metodo
                addPoint(valueStr, operation)
            }
            //else ESTA PARA QUE IMPRIMA LOS TXT OSEA LOS NUMEROS
            else -> {
                binding.tvOperation.append(valueStr) //append ES UN METODO QUE AÑADE TEXTO AL FINAL EL NUEVO VALOR
            }
        }
    }

    private fun addPoint(pointStr: String, operation: String) {
        if (!operation.contains(POINT)){
            binding.tvOperation.append(pointStr)
        } else {
            val operator = getOperator(operation) //valida donde si debe agregar punto

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
                        value[0] = operation.substring(0, index)
                    }
                }else {
                    value = operation.split(operator).toTypedArray()
                }
            }
            if (value.size > 0){
                val numberOne = value[0]!!
                if (value.size > 1){
                    val numberTwo = value[1]!!
                    if (numberOne.contains(POINT) && !numberTwo.contains(POINT)){
                        binding.tvOperation.append(pointStr)
                    }
                }else {
                    if (numberOne.contains(POINT)){
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
        if (operator == OPERATOR_SUB){
            if (operation.isEmpty() || lastElement != OPERATOR_SUB && lastElement != POINT){
                binding.tvOperation.append(operator)
            }
        } else{
                if (!operation.isEmpty() && lastElement != POINT){
                    binding.tvOperation.append(operator)
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




















