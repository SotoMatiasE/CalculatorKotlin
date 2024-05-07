package com.example.calculator

class Operaciones {
    companion object{
        fun getOperator(operation: String): String { //obtenemos el operador de la vista
            var operator = if (operation.contains(Constantes.OPERATOR_MULTI)){
                Constantes.OPERATOR_MULTI
            }else if (operation.contains(Constantes.OPERATOR_DIV)){
                Constantes.OPERATOR_DIV
            }else if (operation.contains(Constantes.OPERATOR_SUM)){
                Constantes.OPERATOR_SUM
            }else { //valor por defecto
                Constantes.OPERATOR_NULL
            }
            /*CON if ESTAMOS VALIDADNDO EL SIGNO NEGATIVO PARA CALCULOS NEGATIVOS. UNICAMENTE SE TOMA EL SEGUNDO - VALIDADNDO
            EL PRIMERO COMO NULL*/
            if (operator == Constantes.OPERATOR_NULL && operation.lastIndexOf(Constantes.OPERATOR_SUB) > 0){
                operator = Constantes.OPERATOR_SUB
            }
            return operator
        }

        //reemplazar operador
        fun canReplaceOperator(charSequence: CharSequence): Boolean {
            if (charSequence.length < 2) return false
            val lasElement = charSequence[charSequence.length - 1].toString()
            val penultimate = charSequence[charSequence.length - 2].toString()

            return (lasElement == Constantes.OPERATOR_MULTI || lasElement ==
                    Constantes.OPERATOR_DIV || lasElement == Constantes.OPERATOR_SUM) && (
                    penultimate == Constantes.OPERATOR_MULTI || penultimate == Constantes.OPERATOR_DIV ||
                            penultimate == Constantes.OPERATOR_SUM || penultimate == Constantes.OPERATOR_SUB)
        }

        fun tryResolve(operationRef: String, isFromResult: Boolean, listener: OnResolveListener) {
            if (operationRef.isEmpty()){ //si esta vacio retorna para no ejecutar de nuevo
                return
            }
            var operation = operationRef //HACEMOS ESTO POR SI HAY UN PUNTO AL FINAL QUITARLO
            //quita el punto que estes ubicado al final de un valor
            if (operation.contains(Constantes.POINT) && operation.lastIndexOf(Constantes.POINT) == operation.length -1) {
                operation = operation.substring(0, operation.length -1)
            }

            //extrare el operador +,-,*,/
            val operator = getOperator(operationRef)

            val value = divideOperation(operator, operation)

            if (value.size > 1) {
                try {
                    //PROCESAR NUMEROS
                    val numberOne = value[0]!!.toDouble()
                    val numberTwo = value[1]!!.toDouble()

                    listener.onShowResult(getResult(numberOne, operator, numberTwo))
                }catch (e: NumberFormatException){
                    if (isFromResult)listener.onShowMessage(R.string.message_num_incorrect)

                }
            }else{
                if (isFromResult && operator != Constantes.OPERATOR_NULL)
                    listener.onShowMessage(R.string.message_exp_incorrect) //SI NO TENGO OPERADORES EN PANTALLA NO NOTIFICA
            }
            /*//Snackbar es similar a Toast
            Snackbar.make(binding.root, "1:$numberOne 2:$numberTwo", Snackbar.LENGTH_SHORT).show()*/
        }

        fun divideOperation(operator: String, operation: String): Array<String?> {
            var value = arrayOfNulls<String>(0)
            //si el operador es diferente de null vamos a descartar que sea una operacion invalida
            //es decir que no contenga un operador
            if (operator != Constantes.OPERATOR_NULL){
                //verificamos si es un operador de resta
                if (operator == Constantes.OPERATOR_SUB){
                    //extraer el incice del operador -
                    val  index = operation.lastIndexOf(Constantes.OPERATOR_SUB)
                    if (index < operation.length-1) { //si index es menor puede dividirse en 2 si no esta incompleta
                        value = arrayOfNulls(2)
                        value[0] = operation.substring(0, index) //index es la posicion de -
                        value[1] = operation.substring(index+1)
                    }else {
                        value = arrayOfNulls(1)
                        value[0] = operation.substring(0, index)
                    }
                }else {
                    value = operation.split(operator).dropLastWhile { it == "" }.toTypedArray()
                }
            }
            return value
        }


        private fun getResult(numberOne: Double, operator: String, numberTow: Double): Double {
            return when (operator) {
                Constantes.OPERATOR_MULTI -> numberOne * numberTow
                Constantes.OPERATOR_DIV -> numberOne / numberTow
                Constantes.OPERATOR_SUM -> numberOne + numberTow
                else -> numberOne - numberTow //Constantes.OPERATOR_SUB
            }
        }
    }
}