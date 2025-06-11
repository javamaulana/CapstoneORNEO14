package com.example.currencyconverterpro.ui.main

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols // <-- PASTIKAN IMPORT INI ADA
import java.util.*

class NumberFormattingVisualTransformation : VisualTransformation {

    private val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale("in", "ID")))

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.filter { it.isDigit() }

        if (originalText.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        val number = originalText.toLongOrNull() ?: 0L
        val formattedText = formatter.format(number)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val
                        originalOffset = offset.coerceAtMost(originalText.length)
                var transformedOffset = 0
                var originalCount = 0
                for (char in formattedText) {
                    if (originalCount == originalOffset) break
                    if (char.isDigit()) {
                        originalCount++
                    }
                    transformedOffset++
                }
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                var originalOffset = 0
                for (i in 0 until offset.coerceAtMost(formattedText.length)) {
                    if (formattedText[i].isDigit()) {
                        originalOffset++
                    }
                }
                return originalOffset
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}