package calculator

class Calculator {

    enum class Operation {
        ADD, SUBTRACT
    }

    private fun getOperation(string: String): Operation {
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

    fun evaluate(list: List<String>): Int {
        var result = list.first().toInt()
        for (i in 1..list.lastIndex step 2) {
            when (getOperation(list[i])) {
                Operation.ADD -> result += list[i + 1].toInt()
                Operation.SUBTRACT -> result -= list[i + 1].toInt()
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
            if (!isValidNumber(list[i])) {
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

fun main() {

    while (true) {
        val list = readLine()!!.split(" ").filter { it.isNotEmpty() }

        when (list.size) {
            1 -> {
                when (val first = list.first()) {
                    "/exit" -> {
                        println("Bye!")
                        break
                    }
                    "/help" -> println("The program calculates the sum and subtraction of numbers")
                    else -> {
                        when (first.first()) {
                            '/' -> {
                                println("Unknown command")
                                continue
                            }
                        }
                        if (isValidNumber(first)) {
                            if (first.first() == '+') {
                                println(first.substring(1))
                            } else {
                                println(first)
                            }
                        } else println("Invalid expression")
                    }
                }
            }
            2 -> println("Invalid expression")
            in 3..Int.MAX_VALUE -> {
                if (isValidExpression(list)) {
                    val calculator = Calculator()
                    val result = calculator.evaluate(list)
                    println(result)
                } else {
                    println("Invalid expression")
                }
            }
        }
    }

}