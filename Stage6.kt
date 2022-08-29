package calculator

import kotlin.system.exitProcess

class Calculator {

    enum class Operation {
        ADD, SUBTRACT
    }

    fun getOperation(string: String): Operation {
        return when (val size = string.length) {
            1 -> when (string.first()) {
                '+' -> Operation.ADD
                else -> Operation.SUBTRACT
            }
            else -> when (string.first()) {
                '+' -> Operation.ADD
                else -> if (size % 2 == 0) Operation.ADD else Operation.SUBTRACT
            }
        }
    }

    // updated for stage 6
    fun evaluate(list: List<String>): Int? {
        val first = list.first()
        var result = getOperandValue(first) ?: return null

        for (i in 1..list.lastIndex step 2) {
            when (getOperation(list[i])) {
                Operation.ADD -> {
                    result += getOperandValue(list[i + 1]) ?: return null
                }
                Operation.SUBTRACT -> {
                    result -= getOperandValue(list[i + 1]) ?: return null
                }
            }
        }
        return result
    }
}

fun isValidExpression(list: List<String>): Boolean {
    if (list.size % 2 == 0) return false
    var flow = false
    for (i in 0..list.lastIndex) {
        if (!flow) {
            if (!isValidNumber(list[i]) && !isValidIdentifier(list[i])) { // identifier added for stage 6
                return false
            }
            flow = true
            continue
        }
        if (list[i].all { it == '+' } || list[i].all { it == '-' }) {
            flow = false
            continue
        } else {
            return false
        }
    }
    return true
}

fun isValidNumber(num: String): Boolean {
    val regex = Regex("[+-]?\\d+")
    return regex.matches(num)
}

// Stage 6
val variables = mutableMapOf<String, Int>()

fun setVariable(key: String, value: Int) {
    variables[key] = value
}

fun isValidIdentifier(identifier: String): Boolean {
    val regex = Regex("[a-zA-Z]+")
    return regex.matches(identifier)
}

fun isValidAssignment(string: String): Boolean {
    val regex = Regex("[a-zA-Z]+\\s*=\\s*(\\d+|[a-zA-Z]+)")
    return regex.matches(string)
}

fun getVariableValue(variable: String): Int? {
    val value = variables[variable]
    return if (value == null) {
        println("Unknown variable")
        null
    } else {
        value
    }
}

fun getOperandValue(operand: String): Int? {
    return if (isValidNumber(operand)) {
        operand.toInt()
    } else {
        getVariableValue(operand)
    }
}

fun processSingleString(first: String) {
    when (first) {
        "/exit" -> {
            println("Bye!")
            exitProcess(0)
        }
        "/help" -> println("The program calculates the sum and subtraction of numbers and/or variables")
        else -> {
            if (first.first() == '/') {
                println("Unknown command")
            } else {
                when {
                    isValidNumber(first) -> {
                        if (first.first() == '+') {
                            println(first.substring(1))
                        } else {
                            println(first)
                        }
                    }
                    isValidIdentifier(first) -> {
                        getVariableValue(first)?.let {
                            println(it)
                        }
                    }
                    else -> {
                        println("Invalid identifier") // expression ?
                    }
                }
            }
        }
    }
}

fun processCalculation(list: List<String>) {
    if (isValidExpression(list)) {
        val calculator = Calculator()
        val result = calculator.evaluate(list)
        result?.let { println(it) }
    } else {
        println("Invalid expression")
    }
}

fun processAssignment(exp: String) {
    if (isValidAssignment(exp)) {
        val second = exp.substringAfter("=")
        if (isValidIdentifier(second)) {
            getVariableValue(second)?.let {
                val first = exp.substringBefore("=")
                setVariable(first, it)
            }
        } else {
            val first = exp.substringBefore("=")
            setVariable(first, second.toInt())
        }
    } else {
        val first = exp.substringBefore("=")
        if (!isValidIdentifier(first)) {
            println("Invalid identifier")
        } else {
            println("Invalid assignment")
        }
    }
}

fun main() {

    while (true) {
        val list = readLine()!!.split(" ").filter { it.isNotEmpty() }

        // consider assignments here only
        val exp = list.joinToString("")
        if ('=' in exp) {
            processAssignment(exp)
            continue
        }

        when (list.size) {
            1 -> processSingleString(list.first())
            2 -> println("Invalid expression")
            in 3..Int.MAX_VALUE -> processCalculation(list)
        }
    }

}