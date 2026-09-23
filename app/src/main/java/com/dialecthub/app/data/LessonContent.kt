package com.dialecthub.app.data

import com.dialecthub.app.data.model.Category
import com.dialecthub.app.data.model.VocabItem

/**
 * Starter Hokkien (Bân-lâm-gú) vocabulary, bundled directly in code so the
 * app works fully offline with no parsing step. Romanization follows
 * Tâi-lô. This is intentionally a small, hand-picked set for v1 -- see the
 * README for how to extend it with new categories or swap it for a
 * data-driven source later.
 */
object LessonContent {

    val categories: List<Category> = listOf(
        Category(
            id = "greetings",
            titleEn = "Greetings",
            titleHokkien = "打招呼",
            emoji = "👋",
            items = listOf(
                VocabItem("greetings_1", "你好", "Lí hó", "Hello", "li ho"),
                VocabItem("greetings_2", "多謝", "To-siā", "Thank you", "do-xia"),
                VocabItem("greetings_3", "免客氣", "Bián kheh-khì", "You're welcome", "bian keh-ki"),
                VocabItem("greetings_4", "歹勢", "Pháinn-sè", "Sorry / excuse me", "paiⁿ-se"),
                VocabItem("greetings_5", "再會", "Tsài-huē", "Goodbye", "zai-hue"),
                VocabItem("greetings_6", "早安", "Tsá-an", "Good morning", "za-an"),
                VocabItem("greetings_7", "暗安", "Àm-an", "Good night", "am-an"),
                VocabItem("greetings_8", "是", "Sī", "Yes", "xi"),
                VocabItem("greetings_9", "毋是", "M̄-sī", "No / not so", "m-xi"),
                VocabItem("greetings_10", "你叫啥物名?", "Lí kiò siánn-mi̍h miâ?", "What is your name?", "li gio xiaⁿ-mih mia")
            )
        ),
        Category(
            id = "numbers",
            titleEn = "Numbers",
            titleHokkien = "數字",
            emoji = "🔢",
            items = listOf(
                VocabItem("numbers_1", "一", "Tsi̍t", "One", "jit"),
                VocabItem("numbers_2", "二", "Nn̄g", "Two", "nng"),
                VocabItem("numbers_3", "三", "Sann", "Three", "saⁿ"),
                VocabItem("numbers_4", "四", "Sì", "Four", "xi"),
                VocabItem("numbers_5", "五", "Gōo", "Five", "go"),
                VocabItem("numbers_6", "六", "La̍k", "Six", "lak"),
                VocabItem("numbers_7", "七", "Tshit", "Seven", "qit"),
                VocabItem("numbers_8", "八", "Peh", "Eight", "beh"),
                VocabItem("numbers_9", "九", "Káu", "Nine", "gao"),
                VocabItem("numbers_10", "十", "Tsa̍p", "Ten", "zap")
            )
        ),
        Category(
            id = "family",
            titleEn = "Family",
            titleHokkien = "家人",
            emoji = "👪",
            items = listOf(
                VocabItem("family_1", "阿爸", "A-pah", "Father", "a-bah"),
                VocabItem("family_2", "阿母", "A-bú", "Mother", "a-bu"),
                VocabItem("family_3", "阿公", "A-kong", "Grandfather", "a-gong"),
                VocabItem("family_4", "阿媽", "A-má", "Grandmother", "a-ma"),
                VocabItem("family_5", "阿兄", "A-hiann", "Older brother", "a-hiaⁿ"),
                VocabItem("family_6", "阿姊", "A-tsí", "Older sister", "a-ji"),
                VocabItem("family_7", "小弟", "Sió-tī", "Younger brother", "xio-di"),
                VocabItem("family_8", "小妹", "Sió-muē", "Younger sister", "xio-mue"),
                VocabItem("family_9", "後生", "Hāu-senn", "Son", "hao-seⁿ"),
                VocabItem("family_10", "查某囝", "Tsa-bóo-kiánn", "Daughter", "za-bo-giaⁿ")
            )
        ),
        Category(
            id = "food",
            titleEn = "Food",
            titleHokkien = "食物",
            emoji = "🍜",
            items = listOf(
                VocabItem("food_1", "飯", "Pn̄g", "Rice", "bng"),
                VocabItem("food_2", "水", "Tsuí", "Water", "zui"),
                VocabItem("food_3", "茶", "Tê", "Tea", "de"),
                VocabItem("food_4", "麵", "Mī", "Noodles", "mi"),
                VocabItem("food_5", "魚", "Hî", "Fish", "hi"),
                VocabItem("food_6", "肉", "Bah", "Meat", "bah"),
                VocabItem("food_7", "菜", "Tshài", "Vegetable", "cai"),
                VocabItem("food_8", "食", "Tsia̍h", "To eat", "jiah"),
                VocabItem("food_9", "好食", "Hó-tsia̍h", "Delicious", "ho-jiah"),
                VocabItem("food_10", "卵", "Nn̄g", "Egg", "nng")
            )
        ),
        Category(
            id = "phrases",
            titleEn = "Common Phrases",
            titleHokkien = "常用語",
            emoji = "💬",
            items = listOf(
                VocabItem("phrases_1", "偌濟錢?", "Guā-tsē tsînn?", "How much money?", "gua-ze jiⁿ"),
                VocabItem("phrases_2", "我聽無", "Guá thiann-bô", "I don't understand", "gua tiaⁿ-bo"),
                VocabItem("phrases_3", "廁所佇佗位?", "Tshek-sóo tī tó-uī?", "Where is the bathroom?", "cek-so di do-ui"),
                VocabItem("phrases_4", "我愛你", "Guá ài lí", "I love you", "gua ai li"),
                VocabItem("phrases_5", "講較慢咧", "Kóng khah bān--leh", "Please speak slower", "gong kah ban-leh"),
                VocabItem("phrases_6", "這是啥物?", "Tsit sī siánn-mi̍h?", "What is this?", "jit xi xiaⁿ-mih"),
                VocabItem("phrases_7", "鬥相共", "Tàu-sann-kāng", "Please help me", "dao-saⁿ-gang"),
                VocabItem("phrases_8", "食飽矣", "Tsia̍h-pá--ah", "I'm full (from eating)", "jiah-ba-ah"),
                VocabItem("phrases_9", "有夠好食!", "Ū-kàu hó-tsia̍h!", "So delicious!", "u-gao ho-jiah"),
                VocabItem("phrases_10", "保重", "Pó-tiōng", "Take care", "bo-diong")
            )
        )
    )

    fun findCategory(categoryId: String): Category =
        categories.first { it.id == categoryId }
}
