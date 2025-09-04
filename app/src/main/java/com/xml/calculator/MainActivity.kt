package com.xml.calculator

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.xml.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var leftHandNum = 0.0
    private var currentExpression = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        addCallback()
    }
    fun addCallback() {
        binding.acBtn.setOnClickListener {
            reset()
        }
        binding.deleteBtn.setOnClickListener {
            if (binding.resultText.text.toString().length == 1) {
                binding.resultText.text = "0"
                return@setOnClickListener
            }
            if (binding.resultText.text.toString().last() == ' ')
            binding.resultText.text = binding.resultText.text.dropLast(2)
            binding.resultText.text = binding.resultText.text.dropLast(1)
        }
        binding.plusBtn.setOnClickListener {
            addOperator("+")
        }
        binding.minusBtn.setOnClickListener {
            addOperator("-")
        }
        binding.multiplyBtn.setOnClickListener {
            addOperator("*")
        }
        binding.divideBtn.setOnClickListener {
            addOperator("/")
        }
        binding.percentageBtn.setOnClickListener {
            val text = binding.resultText.text.toString()
            if (text.isNotEmpty() && text != "0") {
                val value = text.toDoubleOrNull()
                if (value != null) {
                    val percentValue = value / 100
                    binding.resultText.text = if (percentValue % 1 == 0.0) {
                        percentValue.toInt().toString()
                    } else {
                        percentValue.toString()
                    }
                    currentExpression = binding.resultText.text.toString()
                }
            }
        }

        binding.plusMinusBtn.setOnClickListener {
            val text = binding.resultText.text.toString()
            if (text.isNotEmpty() && text != "0") {
                if (text.startsWith("-")) {
                    binding.resultText.text = text.drop(1)
                } else {
                    binding.resultText.text = "-$text"
                }
                currentExpression = binding.resultText.text.toString()
            }
        }

        binding.equalBtn.setOnClickListener {

                val expression = currentExpression
                val result = evaluateExpression(expression)

                binding.historyText.text = expression

                val displayResult = if (result % 1 == 0.0) {
                    result.toInt().toString()
                } else {
                    result.toString()
                }

                binding.resultText.text = displayResult
                currentExpression = displayResult
        }

    }
    private fun evaluateExpression(expression: String): Double {
        val cleanExpr = expression.replace("x", "*").replace("÷", "/")

        val tokens = cleanExpr.split(" ")

        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<String>()

        for (token in tokens) {
            if (token.isBlank()) continue
            if (token in listOf("+", "-", "*", "/")) {
                operators.add(token)
            } else {
                numbers.add(token.toDouble())
            }
        }

        var index = 0
        while (index < operators.size) {
            if (operators[index] == "*" || operators[index] == "/") {
                val left = numbers[index]
                val right = numbers[index + 1]
                val result = if (operators[index] == "*") left * right else left / right

                numbers[index] = result
                numbers.removeAt(index + 1)
                operators.removeAt(index)
                index--
            }
            index++
        }

        var result = numbers[0]
        for (j in operators.indices) {
            val op = operators[j]
            val num = numbers[j + 1]
            result = when (op) {
                "+" -> result + num
                "-" -> result - num
                else -> result
            }
        }

        return result
    }

    fun reset() {
        binding.resultText.text = "0"
        binding.historyText.text = ""
        currentExpression = ""
    }
    private fun addOperator(op: String) {
        val text = binding.resultText.text.toString()
        if (text.isNotEmpty() && text.last().isDigit()) {
            binding.resultText.append(" $op ")
            currentExpression = binding.resultText.text.toString()
        }
    }
    fun onNumberClick(view: View) {
        val buttonNum = (view as? androidx.appcompat.widget.AppCompatButton)?.text
        if (binding.resultText.text.toString() == "0") {
            binding.resultText.text = buttonNum
        } else {
            binding.resultText.append(buttonNum)
        }
        currentExpression = binding.resultText.text.toString()
    }
}