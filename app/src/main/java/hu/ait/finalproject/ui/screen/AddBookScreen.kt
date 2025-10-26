package hu.ait.finalproject.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import hu.ait.finalproject.R
import hu.ait.finalproject.data.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    viewModel: AddBookViewModel = viewModel(),
    onPostSuccess: () -> Unit
) {
    var bookTitle by remember { mutableStateOf("") }
    var bookAuthor by remember { mutableStateOf("") }
    var bookRating by remember { mutableStateOf(0) }
    var bookReview by remember { mutableStateOf("") }

    var titleErrorState by rememberSaveable { mutableStateOf(false) }
    var authorErrorState by rememberSaveable { mutableStateOf(false) }
    var ratingErrorState by rememberSaveable { mutableStateOf(false) }
    var showErrorMessage by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_review_header), fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(99, 136, 191)
                ),
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_topbar),
                        contentDescription = stringResource(R.string.topbar_app_logo),
                        modifier = Modifier
                            .size(50.dp)
                            .padding(horizontal = 10.dp)
                    )
                }
            )
        },
        bottomBar = {
            OutlinedButton(
                onClick = {
                    if (bookTitle.isBlank() || bookAuthor.isBlank() || bookRating == 0) {
                        showErrorMessage = true
                    }
                    if (!showErrorMessage) {
                        viewModel.uploadBook(
                            Book(
                                Firebase.auth.currentUser!!.uid,
                                Firebase.auth.currentUser!!.email!!,
                                Timestamp.now(),
                                bookTitle,
                                bookAuthor,
                                bookRating,
                                bookReview
                            )
                        )
                        onPostSuccess()
                    }
                },
                modifier = Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth()
            ) { Text(stringResource(R.string.post), fontFamily = FontFamily.Monospace) }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = bookTitle,
                onValueChange = {
                    bookTitle = it
                    titleErrorState = it.isBlank()
                    showErrorMessage = titleErrorState || authorErrorState || ratingErrorState
                },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                isError = titleErrorState,
                trailingIcon = {
                    if (titleErrorState) {
                        Icon(Icons.Filled.Warning,
                            stringResource(R.string.error), tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            OutlinedTextField(
                value = bookAuthor,
                onValueChange = {
                    bookAuthor = it
                    authorErrorState = it.isBlank()
                    showErrorMessage = titleErrorState || authorErrorState || ratingErrorState
                },
                label = { Text(stringResource(R.string.author)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                isError = authorErrorState,
                trailingIcon = {
                    if (authorErrorState) {
                        Icon(Icons.Filled.Warning, stringResource(R.string.error), tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 12.dp)
            ) {
                Text(stringResource(R.string.rating))
                StarRating(
                    maxRating = 5,
                    rating = bookRating,
                    onRatingChanged = {
                        bookRating = it
                        ratingErrorState = it == 0
                        showErrorMessage = titleErrorState || authorErrorState || ratingErrorState
                    },
                    active = true,
                    modifier = Modifier
                )
            }

            OutlinedTextField(
                value = bookReview,
                onValueChange = { bookReview = it },
                label = { Text(stringResource(R.string.review_optional)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 5
            )

            if (showErrorMessage) {
                Text(
                    text = stringResource(R.string.error_msg),
                    color = MaterialTheme.colorScheme.error,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(top = 5.dp)
                )
            }

            when (viewModel.addBookUiState) {
                is AddBookUiState.Init -> {}
                is AddBookUiState.LoadingBookUpload -> CircularProgressIndicator()
                is AddBookUiState.BookUploadSuccess -> Text(stringResource(R.string.posted_review))
                is AddBookUiState.ErrorDuringPostUpload -> Text(
                    "Error: ${
                        (viewModel.addBookUiState as AddBookUiState.ErrorDuringPostUpload).error
                    }"
                )
            }
        }
    }
}

@Composable
fun StarRating(
    maxRating: Int,
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    active: Boolean,
    modifier: Modifier
) {
    Row(
        modifier = if (active) modifier.selectableGroup() else modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxRating) {
            val filled = i <= rating
            val icon = if (filled) Icons.Filled.Star else Icons.Default.Star
            val iconColor = if (filled) Color(24, 54, 97) else Color(175, 185, 199)
            Icon(
                imageVector = icon,
                contentDescription = stringResource(R.string.star),
                tint = iconColor,
                modifier = if (active) {
                    Modifier.selectable(
                        selected = filled,
                        onClick = {onRatingChanged(i)}
                    ) } else { Modifier }
            )

            if (i < maxRating) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}