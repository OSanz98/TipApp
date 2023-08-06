package sanzlimited.com.tipapp

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sanzlimited.com.tipapp.components.InputField
import sanzlimited.com.tipapp.ui.theme.TipAppTheme
import sanzlimited.com.tipapp.util.calculateTotalPerPerson
import sanzlimited.com.tipapp.util.calculateTotalTip
//import sanzlimited.com.tipapp.widgets.RoundIconButton
import sanzlimited.com.tipapp.widgets.RoundedIconButton

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                TipCalculatorApp()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit){
    TipAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun TipCalculatorApp() {
    Surface(modifier = Modifier.padding(12.dp)) {
        Column {
            MainContent()
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent() {
    val splitByPeople = rememberSaveable {
        mutableStateOf(1)
    }
    val totalTip = rememberSaveable {
        mutableStateOf(0.0)
    }
    val totalPerPerson = rememberSaveable {
        mutableStateOf(0.0)
    }
    val totalBillsState = rememberSaveable {
        mutableStateOf("")
    }
    val validState = rememberSaveable(totalBillsState.value){
        totalBillsState.value.trim().isNotEmpty()
    }
    var sliderPosition = rememberSaveable {
        mutableStateOf(0f)
    }

    val configuration = LocalConfiguration.current
    when(configuration.orientation){
        Configuration.ORIENTATION_LANDSCAPE -> {
            BillForm(totalPerPerson = totalPerPerson, splitByPeople = splitByPeople, totalTip = totalTip, totalBillsState = totalBillsState, validState = validState, sliderPosition = sliderPosition, isLandscape = true)
        }
        else -> {
            BillForm(totalPerPerson = totalPerPerson, splitByPeople = splitByPeople, totalTip = totalTip, totalBillsState = totalBillsState, validState = validState, sliderPosition = sliderPosition, isLandscape = false)
        }
    }



}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(12.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
//            shape = CircleShape.copy(all = CornerSize(12.dp))
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
            ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per Person",
                style = MaterialTheme.typography.titleLarge
                )
            Text(text = "$$total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun BillForm(modifier: Modifier = Modifier, range: IntRange = 1..100, totalPerPerson: MutableState<Double>, splitByPeople: MutableState<Int>, totalTip: MutableState<Double>, totalBillsState: MutableState<String>, validState: Boolean, sliderPosition: MutableState<Float>, isLandscape: Boolean, onValueChange: (String) -> Unit = {}){
//    rememberSaveable will store its state even on

    val tipPercentage = (sliderPosition.value * 100).toInt()
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(modifier = Modifier
        .padding(top = 10.dp)
        .verticalScroll(enabled = isLandscape, state = rememberScrollState())

    ) {
        TopHeader(totalPerPerson = totalPerPerson.value)

        Surface(modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
            shape = CircleShape.copy(all = CornerSize(12.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                InputField(
                    valueState = totalBillsState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if(!validState) return@KeyboardActions
                        onValueChange(totalBillsState.value.trim())
                        keyboardController?.hide()
                    }
                )
                if(validState){
                    Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
                        Text("Split",
                            modifier = Modifier.align(
                                alignment = Alignment.CenterVertically
                            ))
                        Spacer(modifier = Modifier.width(120.dp))
                        Row(modifier = Modifier.padding(horizontal = 3.dp), horizontalArrangement = Arrangement.End){
                            RoundedIconButton(imageVector = Icons.Default.Remove, onClick = {
                                if (splitByPeople.value > 1) splitByPeople.value -= 1 else 1
                                totalPerPerson.value = calculateTotalPerPerson(totalBill = totalBillsState.value.toDouble(), splitBy = splitByPeople.value, tipPercentage = tipPercentage)
                            })
                            Text(text = "${splitByPeople.value}", modifier = Modifier
                                .align(alignment = CenterVertically)
                                .padding(start = 9.dp, end = 9.dp))
                            RoundedIconButton(imageVector = Icons.Default.Add, elevation = 4.dp, onClick = {
                                if (splitByPeople.value < range.last) {
                                    splitByPeople.value += 1
                                    totalPerPerson.value = calculateTotalPerPerson(totalBill = totalBillsState.value.toDouble(), splitBy = splitByPeople.value, tipPercentage = tipPercentage)
                                }
                            })
                        }
                    }
                    Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp), horizontalArrangement = Arrangement.End) {
                        Text(text = "Tip", modifier = Modifier.align(alignment = CenterVertically))
                        Spacer(modifier = Modifier.width(200.dp))
                        Text(text = "$${totalTip.value}", modifier = Modifier.align(alignment = CenterVertically))
                    }

                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
                        Text(text = "$tipPercentage %")
                        Spacer(modifier = Modifier.height(14.dp))
                        Slider(value = sliderPosition.value, onValueChange = { newVal ->
                            sliderPosition.value = newVal
                            totalTip.value = calculateTotalTip(totalBill = totalBillsState.value.toDouble(), tipPercentage = tipPercentage)
                            totalPerPerson.value = calculateTotalPerPerson(totalBill = totalBillsState.value.toDouble(), splitBy = splitByPeople.value, tipPercentage = tipPercentage)
                        },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            steps = 5,
                            onValueChangeFinished = {
                                Log.d("Finished", "BillForm: $tipPercentage")
                            }
                        )

                    }
                } //end of isValid if statement
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    TipAppTheme {
        MyApp {
            Text("Hello again")
        }
    }

}