package hu.ait.finalproject.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import hu.ait.finalproject.R
import hu.ait.finalproject.data.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel = viewModel(),
    aiViewModel: FeedAIViewModel = viewModel(),
    onNewBookClick: () -> Unit = {}
) {
    val bookListState = viewModel.booksList().collectAsState(initial = FeedUiState.Init)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.feed_screen_header), color = Color.White, fontWeight = FontWeight.Bold)},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(99, 136, 191)),
                navigationIcon = {
                    Image(
                    painter = painterResource(id = R.drawable.logo_topbar),
                    contentDescription = stringResource(R.string.topbar_app_logo),
                    modifier = Modifier.size(50.dp).padding(horizontal = 10.dp)
                )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewBookClick) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.add),
                    tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 5.dp)) {
            if (bookListState.value is FeedUiState.Init) {
                Text(stringResource(R.string.initializing), modifier = Modifier.padding(20.dp))
            }
            else if (bookListState.value is FeedUiState.Loading) {
                CircularProgressIndicator()
            } else if (bookListState.value is FeedUiState.Error) {
                Text(stringResource(R.string.error))
            }
            else if (bookListState.value is FeedUiState.Success) {
                LazyColumn {
                    items((bookListState.value as FeedUiState.Success).bookList) {
                        BookCard(book = it.book,
                            onRemoveItem = {
                                viewModel.deleteBook(it.bookId)
                            },
                            onLikeItem = {
                                viewModel.likeBook(it.bookId)
                            },
                            currentUserId = Firebase.auth.currentUser!!.uid,
                            feedAIViewModel = aiViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    onRemoveItem: () -> Unit,
    onLikeItem: () -> Unit,
    currentUserId: String = "",
    feedAIViewModel: FeedAIViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val key = "${book.bookTitle}_${book.bookAuthor}"

    val summaries by feedAIViewModel.summaries.collectAsState()
    val summary = summaries[key]

    LaunchedEffect (key) {
        feedAIViewModel.generateSummary(book.bookTitle, book.bookAuthor)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(204, 225, 255),
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        modifier = Modifier.padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = book.bookTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = book.bookAuthor,
                        fontStyle = FontStyle.Italic,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                    StarRating(
                        maxRating = 5,
                        rating = book.bookRating,
                        onRatingChanged = {},
                        active = false,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    if (book.bookReview.isNotBlank()) {
                        Text(
                            text = book.bookReview,
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    IconButton(
                        onClick = { onLikeItem() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            contentDescription = stringResource(R.string.like),
                            tint = Color(24, 54, 97)
                        )
                    }
                    Text(
                        text = book.likes.toString(),
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {
                            expanded = !expanded
                        }
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.expand_or_close)
                        )
                    }

                    if (currentUserId.equals(book.uid)) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete),
                            modifier = Modifier.clickable {
                                onRemoveItem()
                            },
                            tint = Color(83, 101, 128)
                        )
                    }
                }
            }

            if (expanded) {
                Text(text = stringResource(R.string.ai_summary) + (summary?: stringResource(R.string.generating)), modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}