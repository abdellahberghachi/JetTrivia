package com.berghachi.jettrivia.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berghachi.jettrivia.model.QuestionItem
import com.berghachi.jettrivia.screens.QuestionsViewModel
import com.berghachi.jettrivia.util.AppColors

@Composable
fun Questions(viewModel: QuestionsViewModel) {

    val questions = viewModel.data.value.data?.toMutableList()
    val questionIndex = remember {
        mutableStateOf(0)
    }



    if (viewModel.data.value.loading == true) {
        CircularProgressIndicator()
    } else {

        val question = try {
            questions?.get(questionIndex.value)
        } catch (ex: Exception) {
            null
        }

        if (questions != null) {
            QuestionDisplay(question!!, questionIndex, viewModel) {
                questionIndex.value = questionIndex.value + 1
            }
        }

    }
}

//@Preview
@Composable
fun QuestionDisplay(
    questionItem: QuestionItem,
    questionIndex: MutableState<Int>,
    questionsViewModel: QuestionsViewModel,
    onNextClicked: (Int) -> Unit
) {

    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    val choicesState = remember(questionItem) {
        questionItem.choices.toMutableList()
    }

    val answerState = remember(questionItem) {
        mutableStateOf<Int?>(null)

    }

    val correctAnswerState = remember(questionItem) {
        mutableStateOf<Boolean?>(null)

    }

    val updateAnswer: (Int) -> Unit = remember(questionItem) {
        {
            answerState.value = it
            correctAnswerState.value = choicesState[it] == questionItem.answer
        }

    }


    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = AppColors.mDarkPurple
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            if (questionIndex.value >= 3) ShowProgress(score = questionIndex.value)

            QuestionTracker(
                counter = questionIndex.value,
                questionsViewModel.getTotalQuestionCount()
            )
            DottedLine(pathEffect = pathEffect)

            Column {
                Text(
                    text = questionItem.question,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(alignment = Alignment.Start)
                        .fillMaxHeight(0.3f),
                    fontSize = 17.sp,
                    color = AppColors.mOffWhite,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )

                choicesState.forEachIndexed { index, answerText ->
                    Row(
                        modifier = Modifier
                            .padding(3.dp)
                            .fillMaxWidth()
                            .height(45.dp)
                            .background(Color.Transparent)
                            .border(
                                width = 4.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColors.mOffDarkPurple,
                                        AppColors.mOffDarkPurple
                                    )
                                ),
                                shape = RoundedCornerShape(15.dp)
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = answerState.value == index,
                            onClick = { updateAnswer(index) },
                            colors = RadioButtonDefaults
                                .colors(
                                    selectedColor = if (correctAnswerState.value == true && index == answerState.value) {
                                        Color.Green.copy(alpha = 0.2f)
                                    } else {
                                        Color.Red.copy(alpha = 0.2f)
                                    }
                                )
                        ) //end rb

                        val annotatedString = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Light,
                                    color = if (correctAnswerState.value == true && index == answerState.value) {
                                        Color.Green
                                    } else if (correctAnswerState.value == false && index == answerState.value) {
                                        Color.Red
                                    } else {
                                        AppColors.mOffWhite
                                    },
                                    fontSize = 17.sp
                                )
                            ) {

                                append(answerText)
                            }
                        }
                        Text(text = annotatedString, modifier = Modifier.padding(6.dp))

                    }

                }
                Button(
                    onClick = { onNextClicked(questionIndex.value) },
                    modifier = Modifier
                        .padding(3.dp)
                        .align(alignment = CenterHorizontally),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.mLightBlue)
                ) {
                    Text(
                        text = "Next",
                        modifier = Modifier.padding(4.dp),
                        color = AppColors.mOffWhite,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}


@Composable
fun QuestionTracker(counter: Int = 0, outOff: Int = 0) {
    Text(text = buildAnnotatedString {
        withStyle(style = ParagraphStyle(textIndent = TextIndent.None)) {
            withStyle(
                style = SpanStyle(
                    color = AppColors.mLightGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 27.sp
                )
            ) {
                append("Question $counter/")

                withStyle(
                    style = SpanStyle(
                        color = AppColors.mLightGray,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp
                    )
                ) {
                    append("$outOff")
                }
            }

        }
    }, modifier = Modifier.padding(20.dp))
}

@Composable
fun DottedLine(pathEffect: PathEffect) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = AppColors.mLightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }

}

@Composable
fun ShowProgress(score: Int = 12) {

    val gradient = Brush.linearGradient(
        listOf(
            Color(0xFFF95075),
            Color(0xFFBE6BE5)
        )
    )


    val progressFactor by remember(score) {
        mutableStateOf(score * 0.005f)

    }


    Row(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(45.dp)
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        AppColors.mLightPurple,
                        AppColors.mLightPurple
                    )
                ),
                shape = RoundedCornerShape(34.dp)
            ) .clip(shape = RoundedCornerShape(34.dp))
            .background(color = Color.Transparent), verticalAlignment = CenterVertically
    ) {
        Button(
            contentPadding = PaddingValues(1.dp),
            onClick = { },
            modifier = Modifier
                .fillMaxWidth(progressFactor)
                .background(brush = gradient),
            enabled = false,
            elevation = null,
            colors = buttonColors(
                backgroundColor = Color.Transparent,
                disabledBackgroundColor = Color.Transparent
            )
        ) {
            Text(
                text = (score * 10).toString(),
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(23.dp))
                    .fillMaxHeight(0.87f)
                    .fillMaxWidth()
                    .padding(6.dp),
                color = AppColors.mOffWhite,
                textAlign = TextAlign.Center
            )
        }

    }
}