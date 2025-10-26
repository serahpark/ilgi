package hu.ait.finalproject.ui.screen

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedAIViewModel : ViewModel() {

    // AI portion
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = "AIzaSyD5ZLSwFZyiQFqLV38vqpjYFBdbk3EGIl8",
        generationConfig = generationConfig {
            temperature = 0.7f
            topP = 0.9f
            topK = 32
            maxOutputTokens = 4096
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
        )
    )

    private val _summaries = MutableStateFlow<Map<String, String>>(emptyMap())
    val summaries: StateFlow<Map<String, String>> = _summaries.asStateFlow()
//    private val _textGenerationResult = MutableStateFlow<String?>(null)
//    val textGenerationResult = _textGenerationResult.asStateFlow()

    fun generateSummary(promptBook: String, promptAuthor: String) {
        val key = "${promptBook}_${promptAuthor}"

        if (_summaries.value.containsKey(key)) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prompt = "Generate a brief summary of the book ${promptBook} by ${promptAuthor}. Return only one summary that is no longer than 2 sentences and does not contain spoilers of the book. Do not generate any other content about this book. Unless a summary for the book is available, always return 'No summary is available for this book' and do not return any follow up questions or prompts."
                val result = generativeModel.generateContent(prompt)
                _summaries.update { it + (key to (result.text ?: "No summary is available")) }
            } catch (e: Exception) {
                _summaries.update { it + (key to "Error: ${e.message}") }
            }
        }
    }

}