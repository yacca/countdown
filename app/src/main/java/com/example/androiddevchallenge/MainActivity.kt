/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        SetTime()
    }
}

enum class TimerStatus {
    STOPPED,
    STARTED
}

@Composable
fun SetTime() {
    var setTime by rememberSaveable { mutableStateOf(0L) }
    val onAddTime: (Long, TimeUnit) -> Unit = { time, unit ->
        setTime += unit.toSeconds(time)
    }
    var status by rememberSaveable { mutableStateOf(TimerStatus.STOPPED) }
    var remaining by rememberSaveable { mutableStateOf(0L) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }
    val onStart: () -> Unit = {
        timer = object : CountDownTimer(setTime * 1000, 10) {
            override fun onTick(p0: Long) {
                remaining = p0
            }

            override fun onFinish() {
                status = TimerStatus.STOPPED
            }
        }
        remaining = setTime * 1000
        timer?.start()
        status = TimerStatus.STARTED
    }
    val onStop: () -> Unit = {
        timer?.cancel()
        status = TimerStatus.STOPPED
    }
    SetTime(setTime, remaining, onAddTime, { setTime = 0 }, onStart, onStop, status)
}

@Composable
fun SetTime(
    setTime: Long,
    remaining: Long,
    onAddTime: (Long, TimeUnit) -> Unit,
    onRest: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    status: TimerStatus
) {
    Column {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(24.dp)
        ) {
            when (status) {
                TimerStatus.STOPPED -> {
                    val setHours = "%02d".format(setTime / 3600)
                    val setMinutes = "%02d".format((setTime % 3600) / 60)
                    val setSeconds = "%02d".format(setTime % 60)
                    Text(
                        text = "$setHours:$setMinutes:$setSeconds",
                        style = MaterialTheme.typography.h2.copy(fontWeight = FontWeight.Bold)
                    )
                }
                TimerStatus.STARTED -> {
                    val remainingSecs = remaining / 1000
                    val hours = "%02d".format(remainingSecs / 3600)
                    val minutes = "%02d".format((remainingSecs % 3600) / 60)
                    val seconds = "%02d".format(remainingSecs % 60)
                    val deciSecs = "%d".format(remaining % 1000 / 100)
                    Text(
                        text = "$hours:$minutes:$seconds.$deciSecs",
                        style = MaterialTheme.typography.h2.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
        val visible = status == TimerStatus.STOPPED
        Box(modifier = Modifier.height(256.dp)) {
            if (visible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ButtonsColumn(unit = TimeUnit.HOURS, onAddTime = onAddTime)
                    ButtonsColumn(unit = TimeUnit.MINUTES, onAddTime = onAddTime)
                    ButtonsColumn(unit = TimeUnit.SECONDS, onAddTime = onAddTime)
                }
            }
        }
        ActionButtons(onRest = onRest, onStart = onStart, onStop = onStop, status)
    }
}

val hourColor = Color(0xFF512DA8)
val minuteColor = Color(0xFF1976D2)
val secondColor = Color(0xFF00796B)

@Composable
fun ActionButtons(
    onRest: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    status: TimerStatus
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val buttonWidth = 144.dp
        val buttonHeight = 56.dp
        val visible = status == TimerStatus.STOPPED
        if (visible) {
            Button(
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonHeight),
                onClick = onRest
            ) {
                Text(
                    text = "RESET",
                    style = MaterialTheme.typography.h5
                )
            }
        } else {
            Spacer(modifier = Modifier.width(buttonWidth))
        }
        if (status == TimerStatus.STOPPED) {
            Button(
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonHeight),
                onClick = onStart
            ) {
                Text(
                    text = "START",
                    style = MaterialTheme.typography.h5
                )
            }
        } else {
            Button(
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonHeight),
                onClick = onStop
            ) {
                Text(
                    text = "STOP",
                    style = MaterialTheme.typography.h5
                )
            }
        }
    }
}

@Composable
fun ButtonsColumn(unit: TimeUnit, onAddTime: (Long, TimeUnit) -> Unit) {
    Column {
        IncreaseTimeButton(time = 10, unit, onAddTime = onAddTime)
        IncreaseTimeButton(time = 5, unit, onAddTime = onAddTime)
        IncreaseTimeButton(time = 1, unit, onAddTime = onAddTime)
    }
}

@Composable
fun IncreaseTimeButton(time: Long, unit: TimeUnit, onAddTime: (Long, TimeUnit) -> Unit) {
    val unitString: String
    val backgroundColor: Color
    when (unit) {
        TimeUnit.HOURS -> {
            unitString = "h"
            backgroundColor = hourColor
        }
        TimeUnit.MINUTES -> {
            unitString = "m"
            backgroundColor = minuteColor
        }
        TimeUnit.SECONDS -> {
            unitString = "s"
            backgroundColor = secondColor
        }
        else -> {
            unitString = ""
            backgroundColor = secondColor
        }
    }
    val buttonColor = ButtonDefaults.buttonColors(
        backgroundColor = backgroundColor,
        contentColor = Color.White
    )
    Box(modifier = Modifier.padding(8.dp)) {
        Button(
            colors = buttonColor,
            modifier = Modifier
                .width(96.dp)
                .height(56.dp),
            onClick = { onAddTime(time, unit) }
        ) {
            Text(
                text = "+$time$unitString",
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
