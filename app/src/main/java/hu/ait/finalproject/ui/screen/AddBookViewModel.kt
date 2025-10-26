package hu.ait.finalproject.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.finalproject.data.Book

class AddBookViewModel : ViewModel() {
    var addBookUiState: AddBookUiState by mutableStateOf(AddBookUiState.Init)

    fun uploadBook(book: Book) {
        addBookUiState = AddBookUiState.LoadingBookUpload

        var bookCollection = FirebaseFirestore.getInstance().collection("books")
        bookCollection.add(book)
            .addOnSuccessListener {
                addBookUiState = AddBookUiState.BookUploadSuccess
            }.addOnFailureListener {
                addBookUiState = AddBookUiState.ErrorDuringPostUpload(it.localizedMessage)
            }
    }
}

sealed interface AddBookUiState {
    object Init : AddBookUiState
    object LoadingBookUpload : AddBookUiState
    object BookUploadSuccess : AddBookUiState
    data class ErrorDuringPostUpload(val error: String?) : AddBookUiState
}