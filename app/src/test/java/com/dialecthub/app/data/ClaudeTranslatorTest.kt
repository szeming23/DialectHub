package com.dialecthub.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClaudeTranslatorTest {

    @Test
    fun parsesOneLineReply() {
        assertEquals(
            TranslationOutcome.Found(HokkienTranslation("多謝", "To-siā", "do-xia")),
            ClaudeTranslator.parseReply("多謝|To-siā|do-xia")
        )
    }

    @Test
    fun trimsWhitespaceAndIgnoresExtraLines() {
        assertEquals(
            TranslationOutcome.Found(HokkienTranslation("菜", "Tshài", "cai")),
            ClaudeTranslator.parseReply("  菜 | Tshài | cai \nSome extra note")
        )
    }

    @Test
    fun questionMarkMeansNoEquivalent() {
        assertEquals(TranslationOutcome.NoEquivalent, ClaudeTranslator.parseReply("?"))
    }

    @Test
    fun malformedReplyFails() {
        assertTrue(ClaudeTranslator.parseReply("Sure! The word is 多謝") is TranslationOutcome.Failed)
        assertTrue(ClaudeTranslator.parseReply("") is TranslationOutcome.Failed)
        assertTrue(ClaudeTranslator.parseReply("多謝||do-xia") is TranslationOutcome.Failed)
    }
}
