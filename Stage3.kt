package calculator

class Calculator {

    fun addAll(list: List<Int>) = list.reduce { acc, i -> acc + i }

}

fun main() {

    while (true) {
        val list = readLine()!!.split(" ")

        when (list.size) {
            1 -> {
                val first = list.first()
                if (first.isNotEmpty()) {
                    when(first) {
                        "/exit" -> {
                            println("Bye!")
                            break
                        }
                        "/help" -> println("The program calculates the sum of numbers")
                        else -> println(first)
                    }
                }
            }
            in 2..Int.MAX_VALUE -> {
                val integers = list.map { it.toInt() }
                val calculator = Calculator()
                val sum = calculator.addAll(integers)
                println(sum)
            }
        }

    }

}