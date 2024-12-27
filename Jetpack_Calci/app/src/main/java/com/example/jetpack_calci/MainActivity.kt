package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
MaterialTheme{
    Scaffold { CalculatorApp() }
}


    }

}
    @Composable
    fun CalculatorApp() {
        var displayText by remember { mutableStateOf("0") }
        var resultText by remember { mutableStateOf("0") }
        var operatorQueue by remember { mutableStateOf(mutableListOf<String>()) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .background(color = Color(0xFF0C090A))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                DisplaySection(
                    displayText = displayText,
                    resultText = resultText,
                    modifier = Modifier
                        .weight(3f)
                        .align(Alignment.End)
                )
                ButtonGrid(
                    onButtonClick = { it -> handleButtonClick(it, operatorQueue, { displayText = it }, { resultText = it }) },
                    modifier = Modifier.weight(7f)
                )
            }
        }
    }
    private fun handleButtonClick(button : String , operationQueue : MutableList<String>, updateDisplay : (String) -> Unit, updateResult: (String) -> Unit){
        when(button) {
            "C" -> {
                operationQueue.clear()
                updateDisplay("0")
                updateResult("0")
            }
            "⌫" -> {
                if(operationQueue.isNotEmpty()){
                    operationQueue.removeAt(operationQueue.lastIndex)
                    updateDisplay(operationQueue.joinToString { "" })

                }
            }
            "=" -> {
                val isOperator = operationQueue.lastOrNull() in listOf("+", "-", "X", "/")
                if(operationQueue.isEmpty() || isOperator ) return
                val result = calculateResult(operationQueue)
                updateResult(result)
                operationQueue.clear()
                operationQueue.add(result)
                updateDisplay(result)
            }
            else -> {
                operationQueue.add(button)
                updateDisplay(operationQueue.joinToString(""))
            }
        }
    }
    private fun calculateResult(operationQueue: List<String>): String {
        return try {
            val expression = operationQueue.joinToString("").replace("X", "*")
            val result = eval(expression)
            result.toString()
        } catch (e: Exception) {
            "Error"
        }
    }

    private fun eval(expression: String): Double {
       val exp =  ExpressionBuilder(expression).build().evaluate()
        return  exp.toDouble();
    }
    @Composable
    fun DisplaySection(
        displayText: String = "0",
        resultText: String = "0",
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(10.dp)
                )
                .background(Color.Black)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Box (
                    modifier = Modifier.weight(3f),
                    contentAlignment = Alignment.BottomEnd
                ){
                    Text(
                        text = displayText,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.W400
                        ),
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
                Box(

                    modifier = Modifier.weight(1f).padding(top = 4.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        text = resultText,
                        style = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }

    @Composable
    fun ButtonGrid(onButtonClick: (String) -> Unit, modifier: Modifier = Modifier) {
        val buttons = listOf(
            listOf("C", "⌫", "/"),
            listOf("7", "8", "9", "X"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf(".", "0", "=")
        )

        Column(
            modifier = modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { button ->
                        val isSpecialButton = button == "C" || button == "="
                        CalculatorButton(
                            text = button,
                            onClick = { onButtonClick(button) },
                            isOperator = button in listOf("C", "⌫", "/", "X", "-", "+", "="),
                            modifier = Modifier.weight(if (isSpecialButton) 2f else 1f)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun CalculatorButton(
        text: String,
        onClick: () -> Unit,
        isOperator: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .padding(4.dp)
                .height(80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isOperator) Color(0xFF1E88E5) else Color(0xFF37474F),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(size = 10.dp)
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
