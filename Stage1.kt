package calculator

class Calculator {
    fun add(a: Int, b: Int) = a + b
}

fun main() {
    val list = readLine()!!.split(" ")
    val a = list[0].toInt()
    val b = list[1].toInt()
    val calculator = Calculator()
    val sum = calculator.add(a, b)
    println(sum)
}