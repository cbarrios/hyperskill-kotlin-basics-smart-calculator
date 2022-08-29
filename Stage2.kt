package calculator

class Calculator {

    fun add(a: Int, b: Int) = a + b

}

fun main() {

    while (true) {
        val list = readLine()!!.split(" ")

        when (list.size) {
            1 -> {
                val first = list.first()
                if (first.isNotEmpty()) {
                    if (first == "/exit") {
                        println("Bye!")
                        break
                    }
                    println(first)
                }
            }
            2 -> {
                val a = list[0].toInt()
                val b = list[1].toInt()
                val calculator = Calculator()
                val sum = calculator.add(a, b)
                println(sum)
            }
        }

    }

}