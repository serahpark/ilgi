package hu.ait.finalproject.data

import com.google.firebase.Timestamp

data class Book(
    var uid: String = "",
    var user: String = "",
    var postDate: Timestamp = Timestamp.now(),
    var bookTitle: String = "",
    var bookAuthor: String = "",
    var bookRating: Int = 0,
    var bookReview: String = "",
    var likes: Int = 0
)

data class BookWithId(
    var bookId: String = "",
    var book: Book
)