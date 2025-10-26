package hu.ait.finalproject.ui.screen

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import hu.ait.finalproject.data.Book
import hu.ait.finalproject.data.BookWithId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class FeedViewModel : ViewModel() {

    fun booksList() = callbackFlow {

        val snapshotListener = Firebase.firestore.collection("books")
            .orderBy("postDate", Query.Direction.DESCENDING)
            .addSnapshotListener() { snapshot, e ->
                val response = if (snapshot != null) {
                    val bookList = snapshot.toObjects(Book::class.java)
                    val bookWithIdList = mutableListOf<BookWithId>()

                    bookList.forEachIndexed { index, book ->
                        bookWithIdList.add(BookWithId(snapshot.documents[index].id, book))
                    }
                    FeedUiState.Success(bookWithIdList)
                } else {
                    FeedUiState.Error(e?.localizedMessage)
                }
                trySend(response)
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun likeBook(bookId: String) {
        FirebaseFirestore.getInstance().collection("books")
            .document(bookId)
            .update("likes", FieldValue.increment(1))
    }

    fun deleteBook(bookId: String) {
        FirebaseFirestore.getInstance().collection("books")
            .document(bookId).delete()
    }

}

sealed interface FeedUiState {
    object Init : FeedUiState
    object Loading: FeedUiState
    data class Success(val bookList: List<BookWithId>) : FeedUiState
    data class Error(val error: String?) : FeedUiState
}