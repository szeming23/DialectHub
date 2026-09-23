package com.dialecthub.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dialecthub.app.viewmodel.QuizQuestion
import com.dialecthub.app.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            val questions = viewModel.questions
            if (questions == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (questions.isEmpty()) {
                NothingToReview(onDone = onFinish)
            } else if (viewModel.isFinished) {
                QuizResult(
                    scorePercent = viewModel.scorePercent,
                    score = viewModel.score,
                    total = viewModel.totalQuestions,
                    onRetry = viewModel::restart,
                    onDone = onFinish
                )
            } else {
                QuizQuestionContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun QuizQuestionContent(viewModel: QuizViewModel) {
    val question = viewModel.currentQuestion

    LinearProgressIndicator(
        progress = { (viewModel.currentIndex + 1) / viewModel.totalQuestions.toFloat() },
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = "Question ${viewModel.currentIndex + 1} of ${viewModel.totalQuestions}",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 12.dp)
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = question.vocab.hanji,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            Text(
                text = question.vocab.tailo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    Text(
        text = "What does this mean?",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 16.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        question.options.forEachIndexed { index, option ->
            AnswerButton(
                text = option,
                index = index,
                question = question,
                selectedOption = viewModel.selectedOption,
                onClick = { viewModel.selectAnswer(index) }
            )
        }
    }

    if (viewModel.selectedOption != null) {
        Button(
            onClick = viewModel::nextQuestion,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text(if (viewModel.currentIndex == viewModel.totalQuestions - 1) "See results" else "Next")
        }
    }
}

@Composable
private fun AnswerButton(
    text: String,
    index: Int,
    question: QuizQuestion,
    selectedOption: Int?,
    onClick: () -> Unit
) {
    val isSelected = selectedOption == index
    val isCorrectAnswer = index == question.correctIndex
    val showFeedback = selectedOption != null

    val containerColor = when {
        !showFeedback -> MaterialTheme.colorScheme.surfaceVariant
        isCorrectAnswer -> Color(0xFF4CAF50)
        isSelected && !isCorrectAnswer -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        !showFeedback -> MaterialTheme.colorScheme.onSurfaceVariant
        isCorrectAnswer -> Color.White
        isSelected && !isCorrectAnswer -> MaterialTheme.colorScheme.onError
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Button(
        onClick = onClick,
        enabled = !showFeedback,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}

@Composable
private fun NothingToReview(onDone: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "✅", style = MaterialTheme.typography.displayLarge)
        Text(
            text = "Nothing to review right now",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Words you've quizzed come back here when they're due.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Home")
        }
    }
}

@Composable
private fun QuizResult(
    scorePercent: Int,
    score: Int,
    total: Int,
    onRetry: () -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (scorePercent >= 70) "🎉" else "💪",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "$scorePercent%",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "You got $score out of $total correct",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )
        Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
            Text("Try Again")
        }
        TextButton(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) {
            Text("Back to Home")
        }
    }
}
