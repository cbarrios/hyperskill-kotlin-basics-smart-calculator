package calculator

import kotlin.math.pow
import kotlin.system.exitProcess

val variables = mutableMapOf<String, Int>()

fun setVariable(key: String, value: Int) {
    variables[key] = value
}

fun isValidNumber(num: String): Boolean {
    val regex = Regex("[+-]?\\d+")
    return regex.matches(num)
}

fun isValidIdentifier(identifier: String): Boolean {
    val regex = Regex("[a-zA-Z]+")
    return regex.matches(identifier)
}

fun isValidAssignment(string: String): Boolean {
    val regex = Regex("[a-zA-Z]+\\s*=\\s*(-?\\d+|[a-zA-Z]+)")
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
        "/help" -> println("The program calculates addition, subtraction, multiplication, division and power of numbers and/or variables with and without parentheses")
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
        val originalLine = readLine()!!

        if (originalLine.isEmpty() || originalLine.isBlank()) {
            continue
        }

        val list = originalLine.split(" ").filter { it.isNotEmpty() }

        // consider assignments here only
        val exp = list.joinToString("")
        if ('=' in exp) {
            processAssignment(exp)
            continue
        }

        val isCalculation = isValidCalculation(originalLine)

        if (list.size == 1 && !isCalculation) {
            processSingleString(exp)
        } else {
            if (isCalculation) {
                val readyInfix = returnReadyInfix(exp)
                runCalculation(readyInfix)
            } else {
                println("Invalid expression")
            }
        }
    }
}

// Stage 7
fun isValidCalculation(expression: String): Boolean {
    val parentheses = Regex("[()]")
    val replaced = expression.replace(parentheses, " ")
    val regex = Regex("\\s*(-?\\d+|[a-zA-Z]+)\\s*((\\^|\\*|/|\\++|-+)\\s*(-?\\d+|[a-zA-Z]+)\\s*)+")
    return regex.matches(replaced)
}

fun runCalculation(readyInfix: String) {
    val postfix = infixToPostfix(readyInfix)
    if (postfix == null) {
        println("Invalid expression")
    } else {
        calculateFromPostfix(postfix)
    }
}

fun calculateFromPostfix(postfix: String) {
    val list = postfix.split(" ").filter { it.isNotEmpty() }
    val stack = ArrayDeque<Int>()
    for (e in list) {
        if (!isOperator(e)) {
            val value = getOperandValue(e)
            if (value == null) {
                return
            } else {
                stack.addFirst(value)
            }
        } else {
            if (stack.isEmpty()) break
            val n1 = stack.removeFirst()
            if (stack.isEmpty()) break
            val n2 = stack.removeFirst()
            when (getOperator(e)) {
                Operator.ADD -> stack.addFirst(n2 + n1)
                Operator.SUBTRACT -> stack.addFirst(n2 - n1)
                Operator.MULTIPLY -> stack.addFirst(n2 * n1)
                Operator.DIVIDE -> stack.addFirst(n2 / n1)
                Operator.POWER -> stack.addFirst(n2.toDouble().pow(n1).toInt())
                Operator.NULL -> Unit
            }
        }
    }

    when (stack.size) {
        1 -> {
            val result = stack.removeFirst()
            println(result)
        }
        else -> {
            println("Invalid expression")
        }
    }
}

fun isOperator(symbol: String): Boolean {
    val regex = Regex("[*+/^-]")
    return regex.matches(symbol)
}

enum class Operator {
    ADD, SUBTRACT, MULTIPLY, DIVIDE, POWER, NULL
}

fun getOperator(symbol: String): Operator {
    return when (symbol) {
        "+" -> Operator.ADD
        "-" -> Operator.SUBTRACT
        "*" -> Operator.MULTIPLY
        "/" -> Operator.DIVIDE
        "^" -> Operator.POWER
        else -> Operator.NULL
    }
}

// Assumes a valid infix:
// (1) Exactly one space between elements. Example: ( 4 - 2 ) * ( 3 - 3 ) + 2 ^ 3
fun infixToPostfix(infix: String): String? {
    val list = infix.split(" ").filter { it.isNotEmpty() }
    var postfix = ""
    val stack = ArrayDeque<String>()
    var valid = true
    main@ for (e in list) {
        if (isOperator(e)) {
            if (stack.isEmpty() || stack.first() == "(") {
                stack.addFirst(e)
            } else {
                if (((e == "*" || e == "/") && (stack.first() == "+" || stack.first() == "-")) || ((e == "^") && (stack.first() == "*" || stack.first() == "/" || stack.first() == "+" || stack.first() == "-"))) {
                     higher precedence
                    stack.addFirst(e)
                } else {
                    while (true) {
                        val operator = stack.removeFirst()
                        postfix += "$operator "
                        if (stack.isEmpty()) break
                        val top = stack.first()
                        if (top == "(" || ((top == "+" || top == "-") && (e == "*" || e == "/" || e == "^")) || ((top == "*" || top == "/") && (e == "^"))) break
                    }
                    stack.addFirst(e)
                }
            }
        } else { // operand
            if (e == "(") {
                stack.addFirst(e)
            } else if (e == ")") {
                while (true) {
                    val operator = stack.removeFirst()
                    postfix += "$operator "
                    if (stack.isEmpty()) {
                        valid = false
                        break@main
                    }
                    val top = stack.first()
                    if (top == "(") {
                        stack.removeFirst()
                        break
                    }
                }
            } else { // regular number
                postfix += "$e "
            }
        }
    }


    while (stack.isNotEmpty() && valid) {
        val operator = stack.removeFirst()
        if (operator == ")" || operator == "(") {
            valid = false
            break
        }
        postfix += "$operator "
    }
    return if (valid) postfix else null
}

// Replace consecutive '+' and/or '-' accordingly to the rules and gives one space between elements
// The incoming infix must not contain double // or ** or ^^ and must be joined to string. (one single string with no spaces)
// Returns a ready to process infix to be converted to postfix later
fun returnReadyInfix(infix: String): String {
    val r = Regex("\\++")
    val aux = infix.replace(r, "+")
    var result = ""
    var lastChar = ' '
    for ((i, c) in aux.withIndex()) {
        if (c == '-') {
            lastChar = when (lastChar) {
                ' ' -> c
                '-' -> {
                    '+'
                }
                else -> {
                    '-'
                }
            }
            if (i == aux.lastIndex) result += lastChar
        } else {
            if (lastChar != ' ') {
                result += lastChar
                lastChar = ' '
            }
            result += c
        }
    }
    result = result.replace("(", "( ")
    result = result.replace(")", " )")
    result = result.replace("+", " + ")
    result = result.replace("-", " - ")
    result = result.replace("*", " * ")
    result = result.replace("/", " / ")
    result = result.replace("^", " ^ ")
    return result
}