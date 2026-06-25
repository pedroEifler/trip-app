package com.example.trip.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

/**
 * Lightweight Markdown renderer for the small subset of Markdown returned by the
 * Gemini API (headers, bold, italic, inline code, bullet and numbered lists).
 *
 * It intentionally avoids external dependencies and only covers the formatting
 * commonly produced by the LLM, so the itinerary is shown nicely instead of as
 * raw Markdown text such as `## Roteiro` or `**Tipo**`.
 */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val lines = markdown.trim().lines()
    Column(modifier = modifier) {
        lines.forEach { rawLine ->
            val trimmed = rawLine.trim()
            when {
                trimmed.isBlank() ->
                    Spacer(Modifier.height(6.dp))

                trimmed == "---" || trimmed == "***" || trimmed == "___" ->
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))

                trimmed.startsWith("#### ") -> MarkdownHeader(
                    text = trimmed.removePrefix("#### "),
                    style = MaterialTheme.typography.titleSmall
                )

                trimmed.startsWith("### ") -> MarkdownHeader(
                    text = trimmed.removePrefix("### "),
                    style = MaterialTheme.typography.titleMedium
                )

                trimmed.startsWith("## ") -> MarkdownHeader(
                    text = trimmed.removePrefix("## "),
                    style = MaterialTheme.typography.titleLarge
                )

                trimmed.startsWith("# ") -> MarkdownHeader(
                    text = trimmed.removePrefix("# "),
                    style = MaterialTheme.typography.headlineSmall
                )

                trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("+ ") ->
                    MarkdownBullet(
                        marker = "•",
                        content = trimmed.drop(2)
                    )

                else -> {
                    val numbered = NUMBERED_LIST_REGEX.find(trimmed)
                    if (numbered != null) {
                        MarkdownBullet(
                            marker = numbered.groupValues[1] + ".",
                            content = trimmed.substring(numbered.range.last + 1)
                        )
                    } else {
                        Text(
                            text = parseInlineMarkdown(trimmed),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarkdownHeader(text: String, style: TextStyle) {
    Text(
        text = parseInlineMarkdown(text),
        style = style,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun MarkdownBullet(marker: String, content: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$marker ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = parseInlineMarkdown(content),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private val NUMBERED_LIST_REGEX = Regex("^(\\d+)\\.\\s+")

/**
 * Converts inline Markdown (`**bold**`, `*italic*`, `` `code` ``) into an
 * [AnnotatedString] with the appropriate [SpanStyle]s.
 */
private fun parseInlineMarkdown(text: String): AnnotatedString = buildAnnotatedString {
    appendInline(text)
}

private fun AnnotatedString.Builder.appendInline(text: String) {
    var i = 0
    while (i < text.length) {
        when {
            text.startsWith("**", i) -> {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        appendInline(text.substring(i + 2, end))
                    }
                    i = end + 2
                    continue
                }
            }
            text.startsWith("__", i) -> {
                val end = text.indexOf("__", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        appendInline(text.substring(i + 2, end))
                    }
                    i = end + 2
                    continue
                }
            }
            text.startsWith("`", i) -> {
                val end = text.indexOf("`", i + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                    continue
                }
            }
            text[i] == '*' || text[i] == '_' -> {
                val marker = text[i]
                val end = text.indexOf(marker, i + 1)
                if (end != -1 && end > i + 1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        appendInline(text.substring(i + 1, end))
                    }
                    i = end + 1
                    continue
                }
            }
        }
        append(text[i])
        i++
    }
}

