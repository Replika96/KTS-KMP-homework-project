package org.kts.tazmin.feature.course_details.presentation.parser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier

sealed class HtmlBlock {
    data class Text(val text: AnnotatedString) : HtmlBlock()
    data class Image(val url: String) : HtmlBlock()
}

@Composable
fun HtmlText(html: String) {
    val blocks = remember(html) { parseHtmlToBlocks(html) }

    Column {
        blocks.forEach { block ->
            when (block) {

                is HtmlBlock.Text -> {
                    ClickableText(
                        text = block.text,
                        style = MaterialTheme.typography.bodyMedium,
                        onClick = { offset ->
                            block.text.getStringAnnotations("URL", offset, offset)
                                .firstOrNull()
                                ?.let {
                                    Napier.d("Open URL: ${it.item}")
                                }
                        }
                    )
                }

                is HtmlBlock.Image -> {
                    HtmlImage(block.url)
                }
            }
        }
    }
}

fun normalizeHtml(value: Any?): String {
    return when (value) {
        is String -> value
        is List<*> -> buildString {
            append("<ul>")
            value.forEach {
                append("<li>${it.toString()}</li>")
            }
            append("</ul>")
        }

        else -> ""
    }
}

@Composable
fun HtmlImage(url: String) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .widthIn(max = 300.dp),
        contentScale = ContentScale.Inside
    )
}


fun parseHtmlToBlocks(html: String): List<HtmlBlock> {
    val blocks = mutableListOf<HtmlBlock>()

    val imgRegex = Regex("<img[^>]*src=['\"]([^'\"]+)['\"][^>]*>")
    var lastIndex = 0

    imgRegex.findAll(html).forEach { match ->
        val start = match.range.first
        val end = match.range.last + 1
        val url = match.groupValues.getOrNull(1)

        if (url.isNullOrEmpty()) return@forEach

        if (start > lastIndex) {
            val textPart = html.substring(lastIndex, start)
            parseTextBlock(textPart)?.let { blocks.add(it) }
        }

        blocks.add(HtmlBlock.Image(url))
        lastIndex = end
    }

    if (lastIndex < html.length) {
        parseTextBlock(html.substring(lastIndex))?.let { blocks.add(it) }
    }

    return blocks
}

private fun parseTextBlock(text: String): HtmlBlock.Text? {
    val annotated = parseHtmlToAnnotatedString(text)
    val normalized = normalizeAnnotatedString(annotated)
    return if (normalized.text.isBlank()) null else HtmlBlock.Text(normalized)
}

fun parseHtmlToAnnotatedString(html: String): AnnotatedString {
    val cleaned = html
        .replace("\r", "")
        .replace("<br>", "\n")
        .replace("<br/>", "\n")
        .replace("</p>", "\n")
        .replace("<p>", "")

    return buildAnnotatedString {
        parseHtmlInternalSafe(cleaned, this)
    }
}

private fun parseHtmlInternalSafe(
    text: String,
    builder: AnnotatedString.Builder
) {
    var i = 0

    while (i < text.length) {

        // --- <a href="">
        if (text.startsWith("<a", i)) {
            val hrefIndex = text.indexOf("href=", i)
            if (hrefIndex == -1) {
                i += 2
                continue
            }

            val quoteStart = text.indexOfFirstQuote(hrefIndex)
            val quoteEnd = text.indexOfMatchingQuote(quoteStart)

            if (quoteStart == -1 || quoteEnd == -1) {
                i += 2
                continue
            }

            val url = text.substring(quoteStart + 1, quoteEnd)

            val tagEnd = text.indexOf(">", quoteEnd)
            val close = text.indexOf("</a>", tagEnd)

            if (tagEnd == -1 || close == -1) {
                builder.append(text[i])
                i++
                continue
            }

            val content = text.substring(tagEnd + 1, close)

            builder.pushStringAnnotation("URL", url)
            builder.withStyle(
                SpanStyle(color = Color(0xFF1E88E5))
            ) {
                parseHtmlInternalSafe(content, this)
            }
            builder.pop()

            i = close + 4
            continue
        }

        // --- <strong>
        if (text.startsWith("<strong>", i)) {
            val end = text.indexOf("</strong>", i)
            if (end != -1) {
                val content = text.substring(i + 8, end)
                builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    parseHtmlInternalSafe(content, this)
                }
                i = end + 9
                continue
            }
        }

        // --- <em>
        if (text.startsWith("<em>", i)) {
            val end = text.indexOf("</em>", i)
            if (end != -1) {
                val content = text.substring(i + 4, end)
                builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    parseHtmlInternalSafe(content, this)
                }
                i = end + 5
                continue
            }
        }

        // --- <li>
        if (text.startsWith("<li>", i)) {
            val end = text.indexOf("</li>", i)
            if (end != -1) {
                val content = text.substring(i + 4, end)
                builder.append("• ")
                parseHtmlInternalSafe(content, builder)
                builder.append("\n")
                i = end + 5
                continue
            }
        }

        // --- пропуск тегов
        if (text[i] == '<') {
            val end = text.indexOf(">", i)
            if (end != -1) {
                i = end + 1
                continue
            }
        }

        builder.append(text[i])
        i++
    }
}

private fun String.indexOfFirstQuote(start: Int): Int {
    val single = indexOf('\'', start)
    val double = indexOf('"', start)

    return listOf(single, double)
        .filter { it != -1 }
        .minOrNull() ?: -1
}

private fun String.indexOfMatchingQuote(start: Int): Int {
    if (start == -1) return -1
    val quote = this[start]
    return indexOf(quote, start + 1)
}

fun normalizeAnnotatedString(input: AnnotatedString): AnnotatedString {
    // нормализуем текст
    val normalizedText = input.text
        .replace(Regex("\n{3,}"), "\n\n")

    // создаём новый builder
    val builder = AnnotatedString.Builder()
    builder.append(normalizedText)

    // переносим span-стили
    input.spanStyles.forEach { span ->
        val end = minOf(span.end, normalizedText.length)
        if (span.start < end) {
            builder.addStyle(span.item, span.start, end)
        }
    }

    // переносим string annotations (например, URL)
    input.getStringAnnotations(0, input.length).forEach { ann ->
        val end = minOf(ann.end, normalizedText.length)
        if (ann.start < end) {
            builder.addStringAnnotation(
                tag = ann.tag,
                annotation = ann.item,
                start = ann.start,
                end = end
            )
        }
    }

    return builder.toAnnotatedString()
}

