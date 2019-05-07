package cn.sinata.xldutils.utils

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan

/**
 *改变文本颜色或字体大小。字体单位是dp。
 */
class SpanBuilder(content: String) {
    private var spannableString: SpannableStringBuilder = SpannableStringBuilder(content)

    fun color(context: Context,start: Int,end: Int,colorRes: Int): SpanBuilder {
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(context,colorRes))
        spannableString.setSpan(colorSpan,start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return this
    }
    fun bgColor(context: Context,start: Int,end: Int,colorRes: Int): SpanBuilder {
        val colorSpan = BackgroundColorSpan(ContextCompat.getColor(context,colorRes))
        spannableString.setSpan(colorSpan,start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return this
    }

    fun size(start:Int,end :Int,dpSize: Int): SpanBuilder {
        val s = AbsoluteSizeSpan(dpSize,true)
        spannableString.setSpan(s,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return this
    }

    /**
     * 字体样式（正常，粗体，斜体等）
     */

    fun style(start:Int,end :Int,style: Int): SpanBuilder {
        val s = StyleSpan(style)
        spannableString.setSpan(s,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return this
    }

    fun build() : SpannableStringBuilder{
        return spannableString
    }
}