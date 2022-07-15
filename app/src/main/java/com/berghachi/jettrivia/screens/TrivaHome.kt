package com.berghachi.jettrivia.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.berghachi.jettrivia.components.Questions

@Composable
fun TriviaHome( viewModel: QuestionsViewModel = hiltViewModel()) = Questions(viewModel)