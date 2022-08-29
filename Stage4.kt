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
                    else -> println(first)
                }
            }
            in 3..Int.MAX_VALUE -> {
                val calculator = Calculator()
                val result = calculator.evaluate(list)
                println(result)
            }
        }

    }

}