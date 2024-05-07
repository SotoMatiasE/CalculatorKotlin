package com.example.calculator

class Operaciones {
    companion object{
        fun getOperator(operation: String): String { //obtenemos el operador de la vista
            var operator = ""

            if (operation.contains(Constantes.OPERATOR_MULTI)){
                operator = Constantes.OPERATOR_MULTI
            }else if (operation.contains(Constantes.OPERATOR_DIV)){
                operator = Constantes.OPERATOR_DIV
            }else if (operation.contains(Constantes.OPERATOR_SUM)){
                operator = Constantes.OPERATOR_SUM
            }else { //valor por defecto
                operator = Constantes.OPERATOR_NULL
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
                            penultimate == Constantes.OPERATOR_SUM || penultimate == Constantes.OPERATOR_SUB
                    )
        }
    }
}