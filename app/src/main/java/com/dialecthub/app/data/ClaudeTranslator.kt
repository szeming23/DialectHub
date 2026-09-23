package com.dialecthub.app.data

import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.core.JsonValue
import com.anthropic.errors.AnthropicException
import com.anthropic.errors.AnthropicIoException
import com.anthropic.errors.AnthropicServiceException
import com.anthropic.errors.RateLimitException
import com.anthropic.errors.UnauthorizedException
import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.OutputConfig
import com.anthropic.models.messages.StopReason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class HokkienTranslation(
    val hanji: String,
    val tailo: String,
    val pinyinStyle: String
)

sealed interface TranslationOutcome {
    data class Found(val translation: HokkienTranslation) : TranslationOutcome
    data object NoEquivalent : TranslationOutcome
    data class Failed(val message: String) : TranslationOutcome
}

/**
 * Looks up the Hokkien equivalent of an English word or phrase with the
 * user's own Claude API key. Built to spend as few tokens as possible: a
 * short system prompt, three few-shot examples that pin a one-line
 * "Hanji|Tâi-lô|pinyin-style" reply, capped input length and low effort.
 */
object ClaudeTranslator {

    const val MAX_INPUT_LENGTH = 60

    private const val MODEL = "claude-opus-5"
    private const val NO_EQUIVALENT = "?"

    private const val SYSTEM_PROMPT =
        "Translate English to Taiwanese Hokkien. Reply with one line only: Hanji|Tâi-lô|pinyin-style. " +
            "Pinyin-style respells Tâi-lô like Hanyu Pinyin: b/d/g/z unaspirated, p/t/k/c aspirated, " +
            "j/q/x before i, ⁿ for nasal vowels, no tones. No other text. " +
            "If there is no Hokkien equivalent, reply $NO_EQUIVALENT"

    // Few-shot examples, sent as prior conversation turns. Each one also
    // demonstrates a pinyin-style rule (unaspirated, aspirated, nasal).
    private val EXAMPLES = listOf(
        "thank you" to "多謝|To-siā|do-xia",
        "vegetable" to "菜|Tshài|cai",
        "how much money?" to "偌濟錢?|Guā-tsē tsînn?|gua-ze jiⁿ"
    )

    suspend fun translate(apiKey: String, english: String): TranslationOutcome = withContext(Dispatchers.IO) {
        val client = AnthropicOkHttpClient.builder().apiKey(apiKey).build()
        try {
            val params = MessageCreateParams.builder()
                .model(MODEL)
                .maxTokens(1024L)
                .system(SYSTEM_PROMPT)
                .apply {
                    EXAMPLES.forEach { (question, answer) ->
                        addUserMessage(question)
                        addAssistantMessage(answer)
                    }
                }
                .addUserMessage(english.trim().take(MAX_INPUT_LENGTH))
                .outputConfig(OutputConfig.builder().effort(OutputConfig.Effort.LOW).build())
                // If a safety classifier declines, let the API retry on a fallback model.
                .putAdditionalHeader("anthropic-beta", "server-side-fallback-2026-07-01")
                .putAdditionalBodyProperty("fallbacks", JsonValue.from("default"))
                .build()

            val response = client.messages().create(params)
            if (response.stopReason().orElse(null) == StopReason.REFUSAL) {
                TranslationOutcome.Failed("Claude declined to translate that.")
            } else {
                parseReply(response.content().mapNotNull { it.text().orElse(null)?.text() }.joinToString(""))
            }
        } catch (e: UnauthorizedException) {
            TranslationOutcome.Failed("Claude rejected the API key. Check it in Settings.")
        } catch (e: RateLimitException) {
            TranslationOutcome.Failed("Too many requests. Wait a moment and try again.")
        } catch (e: AnthropicServiceException) {
            TranslationOutcome.Failed("Claude API error (HTTP ${e.statusCode()}).")
        } catch (e: AnthropicIoException) {
            TranslationOutcome.Failed("Couldn't reach Claude. Check your internet connection.")
        } catch (e: AnthropicException) {
            TranslationOutcome.Failed("Something went wrong: ${e.message}")
        } finally {
            client.close()
        }
    }

    internal fun parseReply(reply: String): TranslationOutcome {
        val line = reply.trim().lineSequence().firstOrNull()?.trim().orEmpty()
        if (line == NO_EQUIVALENT) return TranslationOutcome.NoEquivalent

        val parts = line.split("|").map { it.trim() }
        if (parts.size != 3 || parts.any { it.isEmpty() }) {
            return TranslationOutcome.Failed("Unexpected reply from Claude: ${line.ifEmpty { "(empty)" }}")
        }
        return TranslationOutcome.Found(HokkienTranslation(hanji = parts[0], tailo = parts[1], pinyinStyle = parts[2]))
    }
}
